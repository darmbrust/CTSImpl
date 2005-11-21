package org.hl7.CTSVAPI.refImpl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.hl7.CTSVAPI.BadlyFormedMatchText;
import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.CTSVersionId;
import org.hl7.CTSVAPI.CodeSystemIdAndVersions;
import org.hl7.CTSVAPI.CompleteCodedConceptDescription;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.ConceptRelationship;
import org.hl7.CTSVAPI.InvalidExpansionContext;
import org.hl7.CTSVAPI.NoApplicableDesignationFound;
import org.hl7.CTSVAPI.RelatedCode;
import org.hl7.CTSVAPI.StringAndLanguage;
import org.hl7.CTSVAPI.TimeoutError;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownConceptCode;
import org.hl7.CTSVAPI.UnknownLanguageCode;
import org.hl7.CTSVAPI.UnknownMatchAlgorithm;
import org.hl7.CTSVAPI.UnknownMimeTypeCode;
import org.hl7.CTSVAPI.UnknownPropertyCode;
import org.hl7.CTSVAPI.UnknownRelationshipCode;
import org.hl7.CTSVAPI.lucene.LuceneSearch;
import org.hl7.utility.CTSConstants;
import org.hl7.utility.ObjectCache;
import org.hl7.utility.VAPIExpansionContext;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Utility;
import edu.mayo.informatics.cts.utility.Utility.LDAPConnectionInfo;
import edu.mayo.informatics.cts.utility.lexGrid.SchemaConstants;
import edu.mayo.informatics.lexgrid.convert.utility.DBUtility;
import edu.mayo.mir.utility.AutoReconnectDirContext;
import edu.mayo.mir.utility.StringArrayUtility;

/**
 * <pre>
 * Title:        BrowserOperationsImpl
 * Description:  A reference implementation of BrowserOperationsImpl.
 * Copyright: (c) 2002, 2003, 2004 Mayo Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must include
 *    the following acknowledgment:
 *       "This product includes software developed by Mayo Clinic Division of Biomedical
 *        Informatics Research (http://informatics.mayo.edu/)."
 *    Alternately, this acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Mayo", "Mayo Clinic", "Mayo Foundation", or "LexGrid" must not be used 
 *    to endorse or promote products derived from this software without prior written
 *    permission. For written permission, please contact the author or copyright holder.
 *
 * 5. Products derived from this software may not be called "LexGrid", nor may
 *    "LexGrid" or "Mayo" appear in their name, without prior written permission of the
 *    author or copyright holder.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL MAYO CLINIC OR OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 * 
 * 
 * 
 * <H5>Bugs, to be done, notes, etc.</H5>
 * <UL>
 * <LI>There are still several todo items in this class.</li>
 * </UL>
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust </A>
 * @version 1.0 - cvs $Revision: 1.88 $ checked in on $Date: 2005/10/14 15:44:11 $
 */
public class BrowserOperationsImpl implements org.hl7.CTSVAPI.BrowserOperations
{
    private AutoReconnectDirContext rootContext_;
    private AutoReconnectDirContext codingSchemesContext_;
    private SearchControls          searchControls;
    private NameParser              nameParser;
    private RuntimeOperationsImpl   roi_                      = null;
    private final String            rootRelationNode_         = "@";
    private ObjectCache             objectCache_              = ObjectCache.instance();
    private LuceneSearch            luceneSearch_             = null;

    private String[]                supportedMatchAlgorithms_ = null;

    public final static Logger      logger                    = Logger.getLogger("org.hl7.VAPI_Browser");

   
    /**
     * Create a BrowserOperations object - using values currently in CTSConstants.
     * 
     * @return
     * @throws UnexpectedError
     */
    static public synchronized BrowserOperations _interface() throws UnexpectedError

    {
        BrowserOperationsImpl svc = new BrowserOperationsImpl(null, null, null, null, false, false);
        return (BrowserOperations) svc;
    }
    
    /**
     * Return a reference to a new BrowserOperations.
     * 
     * @param userName - the name to use to connect to the service - if null, loaded from properties
     * @param userPassword - the password used to connect to the service - if null, loaded from properties
     * @param address - The address of the service to connect to - if null, loaded from properties
     * @param service - The Service to connect to - if null, loaded from properties
     * @return reference or null if unable to construct it
     * @throws NamingException - if unable to connect to service
     */
    static public synchronized BrowserOperations _interface(String userName, String userPassword, String address,
            String service, boolean loadProperties, boolean initLogger) throws UnexpectedError

    {
        BrowserOperationsImpl svc = new BrowserOperationsImpl(userName, userPassword, address, service, loadProperties, initLogger);
        return (BrowserOperations) svc;
    }

    /**
     * Return a reference to a new Browser Operations. Parses the connection information from an XML string.
     * 
     * @param xmlLexGridLDAPString The XML String.
     * @param loadProperties whether or not to load the properties from the default properties file.
     * @param initLogger whether or not to init the logger.  Only done if loadProperties is set to true.
     * @return reference or null if unable to construct it
     * @throws UnexpectedError if something goes wrong.
     */
    static public synchronized BrowserOperations _interface(String xmlLexGridLDAPString, String username,
            String password, boolean loadProperties, boolean initLogger) throws UnexpectedError

    {
        BrowserOperationsImpl svc;
        try
        {
            LDAPConnectionInfo temp = Utility.parseLDAPXML(xmlLexGridLDAPString);
            if (username == null || username.length() == 0)
            {
                username = temp.username;
            }
            if (password == null || password.length() == 0)
            {
                password = temp.password;
            }
            svc = new BrowserOperationsImpl(username, password, temp.address, temp.service, loadProperties, initLogger);
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError("Problem parsing the the XML connection info. "
                    + e.toString()
                    + " "
                    + (e.getCause() == null ? ""
                            : e.getCause().toString()));
        }

        return (BrowserOperations) svc;
    }

    /**
     * This method is here for AXIS to work properly. End users should use the _interface method.
     */
    public BrowserOperationsImpl()
    {
        try
        {

            CTSConfigurator._instance().initialize();
            this.rootContext_ = initContext(null, null, null, null);
            this.codingSchemesContext_ = (AutoReconnectDirContext) rootContext_.lookup("dc=CodingSchemes");
            searchControls = new SearchControls();
            this.nameParser = codingSchemesContext_.getNameParser(codingSchemesContext_.getNameInNamespace());
            roi_ = new RuntimeOperationsImpl(null, null, null, null, false, false);
        }
        catch (Exception e)
        {
            logger.error("Constructor error", e);
        }
    }

    protected BrowserOperationsImpl(String userName, String userPassword, String address, String service,
            boolean loadProperties, boolean initLogger) throws UnexpectedError
    {
        try
        {
            if (loadProperties)
            {
                CTSConfigurator._instance().initialize(initLogger);
            }
            this.rootContext_ = initContext(userName, userPassword, address, service);
            this.codingSchemesContext_ = (AutoReconnectDirContext) rootContext_.lookup("dc=CodingSchemes");
            searchControls = new SearchControls();
            this.nameParser = codingSchemesContext_.getNameParser(codingSchemesContext_.getNameInNamespace());
            roi_ = new RuntimeOperationsImpl(userName, userPassword, address, service, false, false);
        }
        catch (Exception e)
        {
            logger.error("Constructor error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public CTSVersionId getCTSVersion() throws UnexpectedError
    {
        CTSVersionId verID;
        try
        {
            logger.debug("getCTSVersion Called");
            verID = new CTSVersionId();
            verID.setMajor(Short.parseShort(CTSConstants.MAJOR_VERSION));
            verID.setMinor(Short.parseShort(CTSConstants.MINOR_VERSION));
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.getMessage());
        }

        return verID;
    }

    public String getServiceDescription() throws UnexpectedError
    {
        logger.debug("getServiceDescription called.");
        try
        {
            return "This is the Mayo Implementation of the CTS " + CTSConstants.MAJOR_VERSION + "." + CTSConstants.MINOR_VERSION + " Final Draft, running against LDAP.";
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceName() throws java.rmi.RemoteException, UnexpectedError
    {
        logger.debug("getServicename called.");
        try
        {
            return "Mayo CTS Vapi (ldap) Browser";
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceVersion() throws UnexpectedError
    {
        logger.debug("getServiceVersion called.");
        try
        {
            return CTSConstants.FULL_VERSION;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String[] getSupportedMatchAlgorithms() throws UnexpectedError
    {
        try
        {
            if (supportedMatchAlgorithms_ == null)
            {
                ArrayList temp = new ArrayList();
                temp.add("IdenticalIgnoreCase");
                temp.add("StartsWithIgnoreCase");
                temp.add("EndsWithIgnoreCase");
                temp.add("ContainsPhraseIgnoreCase");

                if (CTSConstants.LUCENE_SEARCH_ENABLED.getValue())
                {
                    temp.add("LuceneQuery");
                }
                if (CTSConstants.LUCENE_NORM_SEARCH_ENABLED.getValue())
                {
                    temp.add("NormalizedLuceneQuery");
                }
                if (CTSConstants.LUCENE_DOUBLE_METAPHONE_SEARCH_ENABLED.getValue())
                {
                    temp.add("DoubleMetaphoneLuceneQuery");
                }

                supportedMatchAlgorithms_ = (String[]) temp.toArray(new String[temp.size()]);
            }
            return supportedMatchAlgorithms_;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public RelatedCode[] expandCodeExpansionContext(byte[] contextToExpand) throws InvalidExpansionContext,
            UnexpectedError, TimeoutError
    {
        long startTime = System.currentTimeMillis();
        ConceptId tempC = new ConceptId();

        VAPIExpansionContext temp;
        try
        {
            temp = new VAPIExpansionContext(new String(contextToExpand));
            tempC.setCodeSystem_id(temp.sourceCodeSystemId_);
            tempC.setConcept_code(temp.conceptCode_);
        }
        catch (Exception e)
        {
            throw new InvalidExpansionContext();
        }

        try
        {
            if (temp.forward_)
            {
                return expandCodeForward(tempC, temp.directDescendantsOnly_, temp.language_, temp.defaultLanguage_,
                                         temp.ldapName_, temp.nestingDepth_, temp.defaultTargetCodeSystemId_, true,
                                         temp.timeout_, temp.sizeLimit_, startTime);
            }
            else
            {
                return expandCodeReverse(tempC, temp.directDescendantsOnly_, temp.language_, temp.defaultLanguage_,
                                         temp.ldapName_, temp.nestingDepth_, temp.defaultTargetCodeSystemId_, true,
                                         temp.timeout_, temp.sizeLimit_, startTime);
            }
        }
        catch (UnexpectedError e1)
        {
            throw e1;
        }
        catch (TimeoutError e1)
        {
            throw e1;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }

    }

    public RelatedCode[] lookupCodeExpansion(ConceptId expandConcept_id, String relationship_code,
            boolean sourceToTarget, boolean directRelationsOnly, String designationLanguage_code, int timeout,
            int sizeLimit) throws UnknownRelationshipCode, UnknownConceptCode, UnknownCodeSystem, UnknownLanguageCode,
            UnexpectedError, TimeoutError
    {
        // TODO handle cycles according to spec... currently doesn't.
        try
        {
            long methodStartTime = System.currentTimeMillis();

            if (expandConcept_id.getConcept_code() != null
                    && expandConcept_id.getConcept_code().equals(this.rootRelationNode_))
            {
                throw new UnknownConceptCode(expandConcept_id.getConcept_code());
            }

            String baseName = getLDAPNameForCodingSchemeId(expandConcept_id.getCodeSystem_id());

            String defaultLanguage = roi_.getDefaultLanguage(baseName);

            // Validate that the language (if supplied) is valid
            if ((designationLanguage_code != null)
                    && (designationLanguage_code.length() > 0)
                    && !designationLanguage_code.equals(defaultLanguage))
            {
                if (!verifyAttribute(baseName, "supportedLanguage", designationLanguage_code))
                {
                    throw new UnknownLanguageCode(designationLanguage_code);
                }
            }
            else
            {
                designationLanguage_code = defaultLanguage;
            }

            String name = "association=" + relationship_code + ",dc=Relations," + baseName;

            boolean isTransitive = false;
            String targetCodingScheme = "";

            try
            {

                // Find out if this association is transitive, and get the
                // target coding scheme
                Attributes attributes = getAttributes(name, new String[]{SchemaConstants.isTransitive,
                        SchemaConstants.targetCodingScheme});

                if (attributes.get(SchemaConstants.isTransitive) != null)
                    isTransitive = new Boolean((String) attributes.get(SchemaConstants.isTransitive).get())
                            .booleanValue();

                if (attributes.get(SchemaConstants.targetCodingScheme) != null)
                {
                    //map it back to an oid
                    targetCodingScheme = getIdForRelationshipCodeSystemName(baseName, (String) attributes.get(SchemaConstants.targetCodingScheme).get());
                }
                if (targetCodingScheme.length() == 0)
                {
                    targetCodingScheme = expandConcept_id.getCodeSystem_id();
                }

            }
            catch (NameNotFoundException nameNotFoundException)
            {
                // if they sent in hasSubtype and we don't support it
                // return ALL of the concept codes. will send NULL for the
                // subNameToUse code as a flag...
                if (((expandConcept_id.getConcept_code() == null) || (expandConcept_id.getConcept_code().length() == 0))
                        && relationship_code.equalsIgnoreCase("hasSubtype"))
                {

                    return expandCodeForward(expandConcept_id, directRelationsOnly, designationLanguage_code,
                                             defaultLanguage, null, 1, null, false, timeout, sizeLimit, methodStartTime);
                }
                else
                {
                    throw new UnknownRelationshipCode(relationship_code);
                }

            }

            if (sourceToTarget)
            {
                return expandCodeForward(expandConcept_id, directRelationsOnly, designationLanguage_code,
                                         defaultLanguage, name, 1, targetCodingScheme, isTransitive, timeout,
                                         sizeLimit, methodStartTime);
            }
            else
            {
                return expandCodeReverse(expandConcept_id, directRelationsOnly, designationLanguage_code,
                                         defaultLanguage, name, 1, targetCodingScheme, isTransitive, timeout,
                                         sizeLimit, methodStartTime);
            }
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (UnknownRelationshipCode e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    private RelatedCode[] expandCodeForward(ConceptId codeToExpand, boolean directDescendantsOnly, String language,
            String defaultLanguage, String subNameToUse, int pathLength, String targetCodingScheme,
            boolean isTransitive, int timeout, int sizeLimit, long startTime) throws UnknownConceptCode,
            UnexpectedError, TimeoutError
    {
        try
        {

            ArrayList resultsToReturn = new ArrayList();
            logger.debug("Private helper - expandCodeForward called");

            if ((codeToExpand.getConcept_code() == null) || (codeToExpand.getConcept_code().length() == 0))
            {
                if (subNameToUse == null)
                // This is the flag for return ALL codes for a scheme. No
                // hierarchy.
                {
                    return expandCodeReturnAll(codeToExpand, timeout, sizeLimit, startTime);
                }
                else
                {
                    // otherwise, we need to get the next set of codes from the
                    // special rootRelationNode_ node.
                    // use a new conceptId to not mess up the input one

                    codeToExpand = new ConceptId(codeToExpand.getCodeSystem_id(), rootRelationNode_);
                }
            }

            // Get a targets for a source
            if ((isTransitive == false) || (directDescendantsOnly == true))
            // if the association is non-transitive, or they ask for a
            // direct only search
            {
                resultsToReturn = expandCodeForwardHelper(codeToExpand, subNameToUse, pathLength, targetCodingScheme,
                                                          language, defaultLanguage, isTransitive, timeout, sizeLimit,
                                                          startTime);
            }
            else
            {
                // look for A rel B, B rel C ==> A rel C - they want a
                // recursive search.
                resultsToReturn = expandCodeForwardRecursive(1, subNameToUse, codeToExpand, language, defaultLanguage,
                                                             targetCodingScheme, timeout, sizeLimit, startTime);
            }

            if (resultsToReturn.size() == 0
                    && !codeToExpand.getConcept_code().equals(this.rootRelationNode_)
                    && !roi_.isConceptIdValid(codeToExpand, true))
            {
                throw new UnknownConceptCode(codeToExpand.getConcept_code());
            }

            return (RelatedCode[]) resultsToReturn.toArray(new RelatedCode[resultsToReturn.size()]);
        }

        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    private ArrayList expandCodeForwardRecursive(int recursionDepth, String subName, ConceptId codeToExpand,
            String language, String defaultLanguage, String targetCodingScheme, int timeout, int sizeLimit,
            long startTime) throws UnknownConceptCode, TimeoutError, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();

        if (recursionDepth > CTSConstants.MAX_SYSTEMIZATION_ASSOCIATION_RECURSION.getValue())
        {
            // base case 1
            return resultsToReturn;
        }
        ArrayList temp = expandCodeForwardHelper(codeToExpand, subName, recursionDepth, targetCodingScheme, language,
                                                 defaultLanguage, true, timeout, sizeLimit, startTime);

        for (int i = 0; i < temp.size(); i++)
        {
            if (resultsToReturn.size() >= sizeLimit && sizeLimit != 0)
            {
                logger.warn("Skipping results due to sizeLimit");
                break;
            }
            resultsToReturn.add(temp.get(i));

            ConceptId tempConceptId = new ConceptId();
            tempConceptId.setCodeSystem_id(codeToExpand.getCodeSystem_id());
            tempConceptId.setConcept_code(((RelatedCode) temp.get(i)).getConcept_code());
            ArrayList deeperResults = expandCodeForwardRecursive(recursionDepth + 1, subName, tempConceptId, language,
                                                                 defaultLanguage, targetCodingScheme, timeout,
                                                                 sizeLimit, startTime);
            for (int j = 0; j < deeperResults.size(); j++)
            {
                if (resultsToReturn.size() >= sizeLimit && sizeLimit != 0)
                {
                    logger.warn("Skipping results due to sizeLimit");
                    break;
                }
                resultsToReturn.add(deeperResults.get(j));
            }

            if (System.currentTimeMillis() - startTime > timeout && timeout != 0)
            {
                throw new TimeoutError();
            }
        }
        return resultsToReturn;
    }

    private ArrayList expandCodeForwardHelper(ConceptId conceptCode, String name, int pathLength,
            String targetCodingScheme, String language, String defaultLanguage, boolean isTransitive, int timeout,
            int sizeLimit, long startTime) throws TimeoutError, UnexpectedError
    {
        ArrayList results = new ArrayList();
        try
        {
            String filter = "(objectClass=" + SchemaConstants.associationTarget + ")";
            String tempName = DBUtility.escapeLdapDN(SchemaConstants.sourceConcept
                    + "="
                    + conceptCode.getConcept_code()
                    + ", "
                    + name);
            logger.debug("The expandCodeForward filter is: " + filter);
            logger.debug("The expandCodeForward name is: " + tempName);

            int timeLeft = timeout - (int) (System.currentTimeMillis() - startTime);
            if (timeout == 0)
            {
                timeLeft = 0;
            }
            else if (timeLeft <= 0)
            {
                throw new TimeoutError();
            }

            setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, timeLeft, sizeLimit, false, new String[]{
                    SchemaConstants.targetConcept, "targetCodingScheme"});

            NamingEnumeration tempEnum;
            try
            {
                tempEnum = codingSchemesContext_.search(tempName, filter, searchControls);
            }
            catch (NameNotFoundException nameNotFoundException)
            {
                if (conceptCode.getConcept_code().equals(rootRelationNode_))
                {
                    throw new UnexpectedError("This coding scheme is missing the "
                            + rootRelationNode_
                            + " code.  Cannot expand.");
                }
                else
                {
                    return results;
                }
            }

            SearchResult sRes;
            String currentItem;
            RelatedCode relatedCode;

            while (tempEnum.hasMore())
            {
                relatedCode = new RelatedCode();
                sRes = (SearchResult) tempEnum.next();
                Attributes attributes = sRes.getAttributes();

                currentItem = (String) (sRes.getAttributes().get(SchemaConstants.targetConcept)).get();
                relatedCode.setConcept_code(currentItem);
                relatedCode.setPathLength((short) pathLength);

                // get the designation
                ConceptId conceptId = new ConceptId();
                conceptId.setConcept_code(currentItem);

                //if there is a targetCodingScheme on the node, it overrides the default.
                if (attributes.get("targetCodingScheme") != null)
                {
                    String localTargetCodingScheme = (String) attributes.get("targetCodingScheme").get();
                    if (localTargetCodingScheme != null && localTargetCodingScheme.length() > 0)
                    {
                        targetCodingScheme = getIdForRelationshipCodeSystemName(getLDAPNameForCodingSchemeId(conceptCode.getCodeSystem_id()), localTargetCodingScheme);
                    }
                }
                conceptId.setCodeSystem_id(targetCodingScheme);
                try
                {
                    String tempname = name.substring(name.indexOf("codingScheme="));
                    StringAndLanguage stringAndLanguage = roi_.lookupDesignationQuick(tempname, conceptId, language,
                                                                                      defaultLanguage);

                    relatedCode.setDesignation(stringAndLanguage.getText());
                }
                catch (NoApplicableDesignationFound e2)
                {
                    relatedCode.setDesignation("");
                }
                catch (Exception e)
                {
                    logger.error("", e);
                }

                // Get the qualifiers
                relatedCode.setRelationQualifiers(roi_.getAssociationQualifiers(sRes.getName()
                        + ","
                        + tempName));

                relatedCode.setCanExpand(false);
                relatedCode.setExpansionContext("".getBytes());

                // find out if it can expand
                if (isTransitive)
                {
                    String temp = DBUtility.escapeLdapDN("sourceConcept=" + relatedCode.getConcept_code() + "," + name);

                    try
                    {
                        codingSchemesContext_.getAttributes(temp, new String[]{});
                        // if this succeeds, then it can expand.
                        relatedCode.setCanExpand(true);
                        relatedCode.setExpansionContext(new VAPIExpansionContext(true, true, null, name, null,
                                conceptCode.getCodeSystem_id(), pathLength + 1, language, defaultLanguage, relatedCode
                                        .getConcept_code(), targetCodingScheme, timeout, sizeLimit).toString()
                                .getBytes());

                    }
                    catch (Exception e1)
                    {
                        // can't expand
                    }
                }

                results.add(relatedCode);
            }

        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (SizeLimitExceededException e)
        {
            // do nothing
            logger.warn(e);
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (TimeLimitExceededException e)
        {
            logger.error("The search exceeded the allotted time", e);
            throw new TimeoutError();
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return results;
    }

    private RelatedCode[] expandCodeReturnAll(ConceptId codeToExpand, int timeout, int sizeLimit, long startTime)
            throws TimeoutError, UnknownCodeSystem, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();
        try
        {
            String name = getLDAPNameForCodingSchemeId(codeToExpand.getCodeSystem_id());

            String filter = "(objectClass=" + SchemaConstants.codedEntry + ")";
            String tempName = DBUtility.escapeLdapDN("dc=Concepts, " + name);
            logger.debug("The expandCodeReturnAll filter is: " + filter);
            logger.debug("The expandCodeReturnAll name is: " + tempName);

            int timeLeft = timeout - (int) (System.currentTimeMillis() - startTime);
            if (timeout == 0)
            {
                timeLeft = 0;
            }
            else if (timeLeft <= 0)
            {
                throw new TimeoutError();
            }

            setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, timeLeft, sizeLimit, false, new String[]{
                    "conceptCode", "entityDescription"});

            NamingEnumeration temp;
            temp = codingSchemesContext_.search(tempName, filter, searchControls);

            SearchResult sRes;
            String currentItem;
            RelatedCode relatedCode;

            while (temp.hasMore())
            {
                relatedCode = new RelatedCode();
                sRes = (SearchResult) temp.next();
                Attributes attributes = sRes.getAttributes();

                currentItem = (String) (attributes.get("conceptCode")).get();
                relatedCode.setConcept_code(currentItem);
                relatedCode.setDesignation((String) (attributes.get("entityDescription")).get());

                // these will always be this...
                relatedCode.setCanExpand(false);
                relatedCode.setExpansionContext(new byte[0]);
                relatedCode.setPathLength((short) 1);
                relatedCode.setRelationQualifiers(new String[] {});

                resultsToReturn.add(relatedCode);
            }
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (SizeLimitExceededException e)
        {
            // do nothing
            logger.warn(e);
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (TimeLimitExceededException e)
        {
            logger.error("The search exceeded the allotted time", e);
            throw new TimeoutError();
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }

        return (RelatedCode[]) resultsToReturn.toArray(new RelatedCode[resultsToReturn.size()]);
    }

    private RelatedCode[] expandCodeReverse(ConceptId codeToExpand, boolean directDescendantsOnly, String language,
            String defaultLanguage, String subNameToUse, int pathLength, String targetCodingScheme,
            boolean isTransitive, int timeout, int sizeLimit, long startTime) throws UnknownConceptCode,
            UnexpectedError, TimeoutError
    {
        logger.debug("Private helper - expandCodeReverse called");
        ArrayList resultsToReturn = new ArrayList();

        try
        {
            if ((codeToExpand.getConcept_code() == null) || (codeToExpand.getConcept_code().length() == 0))
            {// Want all of the bottom level hits.
                throw new java.lang.UnsupportedOperationException("Getting the bottom nodes is not in the spec.");
            }
            else
            {
                if ((isTransitive == false) || (directDescendantsOnly == true))
                // if the association is non-transitive, or they ask for a
                // direct only search
                {
                    resultsToReturn = expandCodeReverseHelper(codeToExpand, subNameToUse, pathLength,
                                                              targetCodingScheme, language, defaultLanguage,
                                                              isTransitive, timeout, sizeLimit, startTime);
                }

                else
                {
                    // look for A rel B, B rel C ==> A rel C - they want a
                    // recursive search.
                    resultsToReturn = expandCodeReverseRecursive(pathLength, subNameToUse, codeToExpand, language,
                                                                 defaultLanguage, targetCodingScheme, timeout,
                                                                 sizeLimit, startTime);
                }

                if (resultsToReturn.size() == 0)
                {
                    if (!roi_.isConceptIdValid(codeToExpand, true))
                    {
                        throw new UnknownConceptCode(codeToExpand.getConcept_code());
                    }
                }

            }

            return (RelatedCode[]) resultsToReturn.toArray(new RelatedCode[resultsToReturn.size()]);
        }

        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (TimeoutError e)
        {
            throw e;
        }

        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    private ArrayList expandCodeReverseRecursive(int recursionDepth, String subName, ConceptId codeToExpand,
            String language, String defaultLanguage, String targetCodingScheme, int timeout, int sizeLimit,
            long startTime) throws TimeoutError, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();

        if (recursionDepth > CTSConstants.MAX_SYSTEMIZATION_ASSOCIATION_RECURSION.getValue())
        {
            // base case 1
            return resultsToReturn;
        }

        ArrayList temp = expandCodeReverseHelper(codeToExpand, subName, recursionDepth, targetCodingScheme, language,
                                                 defaultLanguage, true, timeout, sizeLimit, startTime);
        for (int i = 0; i < temp.size(); i++)
        {
            if (resultsToReturn.size() >= sizeLimit && sizeLimit != 0)
            {
                logger.warn("Skipping results due to sizeLimit");
                break;
            }
            resultsToReturn.add(temp.get(i));

            ConceptId tempConcept = new ConceptId();
            tempConcept.setCodeSystem_id(codeToExpand.getCodeSystem_id());
            tempConcept.setConcept_code(((RelatedCode) temp.get(i)).getConcept_code());

            ArrayList deeperResults = expandCodeReverseRecursive(recursionDepth + 1, subName, tempConcept, language,
                                                                 defaultLanguage, targetCodingScheme, timeout,
                                                                 sizeLimit, startTime);
            for (int j = 0; j < deeperResults.size(); j++)
            {
                if (resultsToReturn.size() >= sizeLimit && sizeLimit != 0)
                {
                    logger.warn("Skipping results due to sizeLimit");
                    break;
                }
                resultsToReturn.add(deeperResults.get(j));
            }

            if (System.currentTimeMillis() - startTime > timeout && timeout != 0)
            {
                throw new TimeoutError();
            }
        }

        return resultsToReturn;
    }

    private ArrayList expandCodeReverseHelper(ConceptId conceptCode, String name, int pathLength,
            String targetCodingScheme, String language, String defaultLanguage, boolean isTransitive, int timeout,
            int sizeLimit, long startTime) throws TimeoutError, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();
        try
        {

            String filter = "(&(objectClass="
                    + SchemaConstants.associationTarget
                    + ")(targetConcept="
                    + conceptCode.getConcept_code()
                    + "))";

            logger.debug("The expandCodeReverse filter is: " + filter);
            logger.debug("The expandCodeReverse name is: " + name);

            int timeLeft = timeout - (int) (System.currentTimeMillis() - startTime);
            if (timeout == 0)
            {
                timeLeft = 0;
            }
            else if (timeLeft <= 0)
            {
                throw new TimeoutError();
            }

            setSearchControls(searchControls, SearchControls.SUBTREE_SCOPE, timeLeft, sizeLimit, false, new String[]{});

            NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);

            SearchResult sRes;
            String currentItem;
            RelatedCode relatedCode;

            while (results.hasMore())
            {
                relatedCode = new RelatedCode();
                sRes = (SearchResult) results.next();

                currentItem = sRes.getName();
                currentItem = currentItem.substring(currentItem.lastIndexOf("sourceConcept=")
                        + "sourceConcept=".length());

                if (currentItem.equals(this.rootRelationNode_))
                {
                    continue;
                }
                relatedCode.setConcept_code(currentItem);
                relatedCode.setPathLength((short) pathLength);

                // get the designation
                ConceptId conceptId = new ConceptId();
                conceptId.setConcept_code(currentItem);
                conceptId.setCodeSystem_id(conceptCode.getCodeSystem_id());

                //check to see if there is an alternate sourceId - if so, override the default
                Attributes sourceCodingSchemeAttr = codingSchemesContext_.getAttributes("sourceConcept="
                        + currentItem
                        + ","
                        + name, new String[]{"sourceCodingScheme"});

                if (sourceCodingSchemeAttr.get("sourceCodingScheme") != null)
                {
                    String tempSourceCodingScheme = (String) sourceCodingSchemeAttr.get("sourceCodingScheme").get();
                    if (tempSourceCodingScheme != null && tempSourceCodingScheme.length() > 0)
                    {
                        // if one exists, use that instead of the default.
                        conceptId.setCodeSystem_id(getIdForRelationshipCodeSystemName(getLDAPNameForCodingSchemeId(conceptCode.getCodeSystem_id()), tempSourceCodingScheme));
                    }
                }

                try
                {
                    String tempname = name.substring(name.indexOf("codingScheme="));
                    StringAndLanguage stringAndLanguage = roi_.lookupDesignationQuick(tempname, conceptId, language,
                                                                                      defaultLanguage);
                    relatedCode.setDesignation(stringAndLanguage.getText());
                }
                catch (NoApplicableDesignationFound e2)
                {
                    relatedCode.setDesignation("");
                }

                // Get the qualifiers
                relatedCode.setRelationQualifiers(roi_.getAssociationQualifiers(sRes.getName()
                        + ","
                        + name));

                // find out if it can expand

                relatedCode.setCanExpand(false);
                relatedCode.setExpansionContext("".getBytes());

                if (isTransitive)
                {
                    logger.debug("Finding out if the codes can expand");
                    filter = "(&(objectClass="
                            + SchemaConstants.associationTarget
                            + ")(targetConcept="
                            + relatedCode.getConcept_code()
                            + "))";

                    timeLeft = timeout - (int) (System.currentTimeMillis() - startTime);
                    if (timeout == 0)
                    {
                        timeLeft = 0;
                    }
                    else if (timeLeft <= 0)
                    {
                        throw new TimeoutError();
                    }

                    setSearchControls(searchControls, SearchControls.SUBTREE_SCOPE, timeLeft,
                                      CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false, new String[]{});

                    NamingEnumeration temp = codingSchemesContext_.search(name, filter, searchControls);
                    if (temp.hasMore())
                    {
                        relatedCode.setCanExpand(true);
                        relatedCode.setExpansionContext(new VAPIExpansionContext(false, true, null, name, null,
                                conceptId.getCodeSystem_id(), pathLength + 1, language, defaultLanguage, relatedCode
                                        .getConcept_code(), targetCodingScheme, timeout, sizeLimit).toString()
                                .getBytes());
                    }
                }

                resultsToReturn.add(relatedCode);
            }

        }
        catch (SizeLimitExceededException e)
        {
            // do nothing
            logger.warn(e);
        }
        catch (TimeLimitExceededException e)
        {
            logger.error("The search exceeded the allotted time", e);
            throw new TimeoutError();
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return resultsToReturn;

    }

    public CodeSystemIdAndVersions[] getSupportedCodeSystems(int timeout, int sizeLimit) throws TimeoutError,
            UnexpectedError
    {
        return roi_.getSupportedCodeSystems(timeout, sizeLimit);
    }

    public CompleteCodedConceptDescription lookupCompleteCodedConcept(ConceptId concept_id) throws UnknownConceptCode,
            UnknownCodeSystem, UnexpectedError
    {
        try
        {
            // the root concept isn't actually a concept
            if (concept_id.getConcept_code().equals(this.rootRelationNode_))
            {
                throw new UnknownConceptCode(concept_id.getConcept_code());
            }
            CompleteCodedConceptDescription cccd = new CompleteCodedConceptDescription();

            String name = getLDAPNameForCodingSchemeId(concept_id.getCodeSystem_id());
            String filter = "";

            // Need to get the default systemization for later use - use a cached version if available.

            String systemization = getDefaultSystemization(name);

            // Set the code
            cccd.setConcept_id(concept_id);

            NamingEnumeration innerResults;

            // set designated by
            cccd.setDesignatedBy(lookupDesignations(concept_id, null, null, null));

            try
            {
                // Set isActive, version
                Attributes attributes = codingSchemesContext_.getAttributes("conceptCode="
                        + concept_id.getConcept_code()
                        + ", dc=Concepts, "
                        + name, new String[]{"isActive"});
                if (attributes.get("isActive") != null)
                {
                    cccd.setConceptStatus_code((String) attributes.get("isActive").get());
                }
                else
                {
                    cccd.setConceptStatus_code("true");
                }

                attributes = getAttributes(name, new String[]{"representsVersion"});

                if (attributes.get("representsVersion") != null)
                {
                    cccd.setCodeSystem_version((String) attributes.get("representsVersion").get());
                }
                else
                {
                    cccd.setCodeSystem_version("");
                }

            }
            catch (NamingException e2)
            {
                throw new UnknownConceptCode(concept_id.getConcept_code());
            }

            // set the hasProperty
            cccd
                    .setHasProperties(lookupProperties(concept_id, null, null, null, null,
                                                                                  null));
            // Set SourceFor
            if (systemization.length() > 0)
            {
                try
                {
                    setSearchControls(searchControls, SearchControls.SUBTREE_SCOPE, CTSConstants.LONG_SEARCH_TIMEOUT
                            .intValue(), CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false,
                                      new String[]{"sourceCodingScheme"});

                    filter = "(sourceConcept=" + concept_id.getConcept_code() + ")";

                    ArrayList targets = new ArrayList();
                    try
                    {
                        innerResults = codingSchemesContext_.search("dc=" + systemization + ", " + name, filter,
                                                                    searchControls);
                        while (innerResults.hasMore())
                        {
                            // inside of source for, build the relatedConcepts
                            // list
                            SearchResult currentSource = (SearchResult) innerResults.next();
                            if (currentSource.getAttributes().get("sourceCodingScheme") != null)
                            {
                                // if it has a sourceCodingScheme attribute that does not equal the current coding
                                // scheme, skip it.
                                String conceptSourceCodingScheme = (String) currentSource.getAttributes()
                                        .get("sourceCodingScheme").get();
                                if (conceptSourceCodingScheme != null
                                        && conceptSourceCodingScheme.length() > 0
                                        && !concept_id.getCodeSystem_id().equals(getIdForRelationshipCodeSystemName(name, conceptSourceCodingScheme)))
                                {
                                    continue;
                                    // don't add this one.
                                }
                            }

                            NamingEnumeration innerInnerResults = null;
                            try
                            {
                                setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE,
                                                  CTSConstants.SHORT_SEARCH_TIMEOUT.intValue(),
                                                  CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false, new String[]{
                                                          "targetConcept", "targetCodingScheme"});
                                filter = "(objectClass=associationTarget)";

                                innerInnerResults = codingSchemesContext_.search(currentSource.getName()
                                        + ", dc="
                                        + systemization
                                        + ","
                                        + name, filter, searchControls);

                                while (innerInnerResults.hasMore())
                                {
                                    SearchResult currentTarget = (SearchResult) innerInnerResults.next();
                                    ConceptRelationship currentRelationship = new ConceptRelationship();
                                    currentRelationship.setRelationship_code(nameParser.parse(currentSource.getName())
                                            .get(0).substring("association=".length()));
                                    ConceptId temp = new ConceptId();
                                    temp.setConcept_code((String) currentTarget.getAttributes().get("targetConcept")
                                            .get());

                                    String association = nameParser.parse(currentSource.getName()).get(0);

                                    String targetCodingScheme = "";
                                    // if there is a localTargetCodingScheme - use it.
                                    if (currentTarget.getAttributes().get("targetCodingScheme") != null)
                                    {
                                        targetCodingScheme = (String) currentTarget.getAttributes()
                                                .get("targetCodingScheme").get();
                                    }
                                    
                                    //if the targetCodingScheme isn't defined, try setting it to the default one.
                                    if (targetCodingScheme.length() == 0)
                                    {
                                        targetCodingScheme = getDefaultTargetCodingScheme(association
                                                + ", dc="
                                                + systemization
                                                + ","
                                                + name);
                                    }
                                    
                                    // if if hasn't been defined at all, then its the same as the codingScheme we are in.
                                    if (targetCodingScheme.length() == 0)
                                    {
                                        targetCodingScheme = concept_id.getCodeSystem_id();
                                    }
                                    else
                                    {
                                        //map it back to an oid
                                        targetCodingScheme = getIdForRelationshipCodeSystemName(name, targetCodingScheme);
                                    }

                                    temp.setCodeSystem_id(targetCodingScheme);

                                    currentRelationship.setSourceConcept_id(concept_id);
                                    currentRelationship.setTargetConcept_id(temp);

                                    // get the qualifiers
                                    currentRelationship.setRelationQualifiers(roi_
                                            .getAssociationQualifiers(currentTarget.getName()
                                                    + ","
                                                    + currentSource.getName()
                                                    + ", dc="
                                                    + systemization
                                                    + ", "
                                                    + name));

                                    targets.add(currentRelationship);
                                }
                            }
                            catch (NamingException e1)
                            {
                                // ok I think
                            }
                            innerInnerResults.close();

                        }

                        innerResults.close();
                    }
                    catch (NameNotFoundException e)
                    {
                        // no relationships
                    }

                    cccd.setSourceFor((ConceptRelationship[]) targets
                            .toArray(new ConceptRelationship[targets.size()]));
                }
                catch (NamingException e1)
                {
                    logger.error("Problems building sourceFor", e1);
                }

                // Set targetOf

                try
                {
                    setSearchControls(searchControls, SearchControls.SUBTREE_SCOPE, CTSConstants.LONG_SEARCH_TIMEOUT
                            .intValue(), CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false,
                                      new String[]{"targetCodingScheme"});

                    filter = "(targetConcept=" + concept_id.getConcept_code() + ")";
                    ArrayList sources = new ArrayList();

                    try
                    {
                        innerResults = codingSchemesContext_.search("dc=" + systemization + ", " + name, filter,
                                                                    searchControls);

                        while (innerResults.hasMore())
                        {
                            SearchResult currentSource = (SearchResult) innerResults.next();

                            ConceptRelationship currentRelationship = new ConceptRelationship();
                            currentRelationship.setRelationship_code(nameParser.parse(currentSource.getName()).get(0)
                                    .substring("association=".length()));
                            // There can only be one source for a target....

                            ConceptId temp = new ConceptId();

                            // get the target coding scheme
                            String association = nameParser.parse(currentSource.getName()).get(0);

                            String targetCodingScheme = "";
                            // First, set the targetCodingScheme to the default.
                            Attributes attributesTemp = getAttributes(association
                                    + ", dc="
                                    + systemization
                                    + ","
                                    + name, new String[]{SchemaConstants.targetCodingScheme});

                            if (attributesTemp.get(SchemaConstants.targetCodingScheme) != null)
                            {
                                targetCodingScheme = (String) attributesTemp.get(SchemaConstants.targetCodingScheme)
                                        .get();
                            }

                            // if there is a localTargetCodingScheme - override the default with it.
                            if (currentSource.getAttributes().get("targetCodingScheme") != null)
                            {
                                targetCodingScheme = (String) currentSource.getAttributes().get("targetCodingScheme")
                                        .get();
                            }

                            // if for some reason, still no coding scheme set it to the sourcecoding scheme
                            if (targetCodingScheme.length() == 0)
                            {
                                targetCodingScheme = concept_id.getCodeSystem_id();
                            }

                            if (!targetCodingScheme.equals(concept_id.getCodeSystem_id()))
                            {
                                // don't add this one - in target Of - targets are only valid if they are in the
                                // same coding scheme as the source.
                                continue;
                            }

                            temp.setConcept_code(nameParser.parse(currentSource.getName()).get(1)
                                    .substring("sourceConcept=".length()));
                            
                            if (temp.getConcept_code().equals(this.rootRelationNode_))
                            {
                                // dont add this one, its invalid.
                                continue;
                            }
                            
                            String sourceConceptId = concept_id.getCodeSystem_id();
                            
                            // check to see if there is an alternate sourceId
                            Attributes sourceCodingSchemeAttr = codingSchemesContext_.getAttributes("sourceConcept="
                                    + temp.getConcept_code()
                                    + ","
                                    + association
                                    + ", dc="
                                    + systemization
                                    + ","
                                    + name, new String[]{"sourceCodingScheme"});

                            if (sourceCodingSchemeAttr.get("sourceCodingScheme") != null)
                            {
                                String tempSourceCodingScheme = (String) sourceCodingSchemeAttr
                                        .get("sourceCodingScheme").get();
                                if (tempSourceCodingScheme != null && tempSourceCodingScheme.length() > 0)
                                {
                                    // if one exists, use that instead of the default.
                                    sourceConceptId = getIdForRelationshipCodeSystemName(name, tempSourceCodingScheme);
                                }
                            }

                            temp.setCodeSystem_id(sourceConceptId);
                            
                            currentRelationship.setSourceConcept_id(temp);
                            currentRelationship.setTargetConcept_id(concept_id);

                            // get the qualifiers
                            currentRelationship.setRelationQualifiers(roi_
                                    .getAssociationQualifiers(currentSource.getName()
                                            + ", dc="
                                            + systemization
                                            + ", "
                                            + name));

                            sources.add(currentRelationship);
                        }
                        innerResults.close();
                    }
                    catch (NameNotFoundException e)
                    {
                        // no relationships
                    }

                    cccd.setTargetOf((ConceptRelationship[]) sources
                            .toArray(new ConceptRelationship[sources.size()]));
                }
                catch (NamingException e3)
                {
                    logger.error("Problem getting target of", e3);
                }
            }
            else
            {
                cccd.setSourceFor(new ConceptRelationship[] {});
                cccd.setTargetOf(new ConceptRelationship[] {});
            }

            return cccd;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public ConceptId[] lookupConceptCodesByDesignation(String codeSystem_id, String matchText,
            String matchAlgorithm_code, String language_code, boolean activeConceptsOnly, int timeout, int sizeLimit)
            throws BadlyFormedMatchText, UnknownCodeSystem, UnknownLanguageCode, UnexpectedError, TimeoutError,
            UnknownMatchAlgorithm
    {
        logger.debug("getConceptCodesByDesignation called: codeSystem: "
                + codeSystem_id
                + " matchText: "
                + matchText
                + " matchAlgorithm_code "
                + matchAlgorithm_code
                + " language_code "
                + language_code
                + " activeConceptsOnly: "
                + activeConceptsOnly
                + " timeout: "
                + timeout
                + " sizeLimit: "
                + sizeLimit);

        Hashtable resultsToReturn = new Hashtable();
        // using a hashtable so I don't return duplicates

        try
        {

            String name = getLDAPNameForCodingSchemeId(codeSystem_id);
            String filter = "";

            String defaultLanguage = roi_.getDefaultLanguage(name);

            String languageFilterPart;
            String textFilterPart;
            String baseFilterPart;


            // Validate that the language (if supplied) is valid
            if ((language_code != null) && (language_code.length() > 0) && !language_code.equals(defaultLanguage))
            {
                if (!verifyAttribute(name, "supportedLanguage", language_code))
                {
                    throw new UnknownLanguageCode(language_code);
                }
            }

            
            if (matchAlgorithm_code != null && matchAlgorithm_code.endsWith("LuceneQuery"))
            {
                //'*' is allowed in EVS mode for lucene queries 
                int loc = name.indexOf("=");
                String codingSchemeName = "";
                if (name != "*" && loc > 0)
                {
                    codingSchemeName = name.substring(name.indexOf("=") + 1);
                }
                else
                {
                    codingSchemeName = name;
                }
                return getLuceneSearch().luceneLookupConceptCodesByDesignation(codingSchemeName,
                                                                                                  matchText,
                                                                                                  matchAlgorithm_code,
                                                                                                  language_code,
                                                                                                  activeConceptsOnly,
                                                                                                  timeout, sizeLimit);
            }

            // Figure out the language filter part of the query
            languageFilterPart = generateLanguageFilterString(language_code, defaultLanguage, true);

            // Figure out the match text part of the query
            textFilterPart = generateFilterForProperty("text", wrapSearchStringForAlgorithm(matchText,
                                                                                            matchAlgorithm_code));

            if (languageFilterPart.length() > 0)
                baseFilterPart = "(&(property=textualPresentation)" + languageFilterPart + ")";
            else
                baseFilterPart = "(property=textualPresentation)";
            // }

            filter = "(&" + textFilterPart + baseFilterPart + ")";

            setSearchControls(searchControls, SearchControls.SUBTREE_SCOPE, timeout, sizeLimit * 5, false,
                              new String[]{"language"});
            //make the sizelimit bigger, because it will usually match on multiple 
            //designations per concept, and I will end up returning less concepts than the limit requested in that case.


            NamingEnumeration results;

            logger.debug("Filter = " + filter);
            logger.debug("name = " + "dc=Concepts," + name);
            results = codingSchemesContext_.search("dc=Concepts, " + name, filter, searchControls);

            try
            {
                while (results.hasMore())
                {
                    if (sizeLimit != 0 && resultsToReturn.size() == sizeLimit)
                    {
                        break;
                    }
                    SearchResult sRes = (SearchResult) results.next();
                    ConceptId conceptId = new ConceptId();
                    // Set the code
                    conceptId.setConcept_code(nameParser.parse(sRes.getName()).get(0)
                            .substring("conceptCode=".length()));
                    if (conceptId.getConcept_code().equals(this.rootRelationNode_))
                    {
                        // skip root relation node
                        continue;
                    }
                    if (activeConceptsOnly)
                    {
                        Attributes attributes = codingSchemesContext_.getAttributes(nameParser.parse(sRes.getName())
                                .get(0)
                                + ", dc=Concepts,"
                                + name, new String[]{"isActive"});
                        if (attributes.get("isActive") != null
                                && !((String) attributes.get("isActive").get()).equalsIgnoreCase("true"))
                        {
                            // skip it if its not active
                            continue;
                        }
                    }
                    conceptId.setCodeSystem_id(codeSystem_id);
                    resultsToReturn.put(conceptId.getConcept_code(), conceptId);

                }
            }
            catch (SizeLimitExceededException e1)
            {
                logger.warn("Results exceeded limit.");
            }
        }
        catch (SizeLimitExceededException e)
        {
            // do nothing
            logger.warn(e);
        }
        catch (TimeLimitExceededException e)
        {
            logger.error("The search exceeded the allotted time", e);
            throw new TimeoutError();
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (NamingException e)
        {
            throw new BadlyFormedMatchText(matchText);
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (BadlyFormedMatchText e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return (ConceptId[]) resultsToReturn.values().toArray(new ConceptId[resultsToReturn.size()]);
    }

    public ConceptId[] lookupConceptCodesByProperty(String codeSystem_id, String matchText, String matchAlgorithm_code,
            String language_code, boolean activeConceptsOnly, String[] properties, String[] mimeTypes, int timeout,
            int sizeLimit) throws UnknownMimeTypeCode, BadlyFormedMatchText, UnknownCodeSystem, UnknownLanguageCode,
            UnknownPropertyCode, UnexpectedError, UnknownMatchAlgorithm, TimeoutError
    {
        logger.debug("lookupConceptCodesByProperty called - codeSystem_id: "
                + codeSystem_id
                + " matchText: "
                + matchText
                + " matchAlgorithm_code "
                + matchAlgorithm_code
                + " language_code: "
                + language_code
                + " activeConceptOnly: "
                + activeConceptsOnly
                + " properties: "
                + StringArrayUtility.stringArrayToString(properties)
                + " mimeTypes: "
                + StringArrayUtility.stringArrayToString(mimeTypes)
                + " timeout: "
                + timeout
                + " sizeLimit: "
                + sizeLimit);

        Hashtable resultsToReturn = new Hashtable();
        // using a hash table to get rid of duplicates
        try
        {

            // First, locate to the proper coding scheme (and validate its
            // existance)
            String name = getLDAPNameForCodingSchemeId(codeSystem_id);
            String filter = "";
            String defaultLanguage;

            defaultLanguage = roi_.getDefaultLanguage(name);
            // Validate that the language (if supplied) is valid
            if ((language_code != null) && (language_code.length() > 0) && !language_code.equals(defaultLanguage))
            {
                if (!verifyAttribute(name, "supportedLanguage", language_code))
                {
                    throw new UnknownLanguageCode(language_code);
                }
            }

            // Validate that the properties (if supplied) are valid
            if ((properties != null) && (properties.length > 0))
            {
                for (int i = 0; i < properties.length; i++)
                {
                    if (!verifyAttribute(name, "supportedProperty", properties[i]))
                    {
                        throw new UnknownPropertyCode(properties[i]);
                    }
                }
            }

            // Validate that the mime types (if supplied) are valid
            if ((mimeTypes != null) && (mimeTypes.length > 0))
            {
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    if (!verifyAttribute(name, "supportedFormat", mimeTypes[i]))
                    {
                        throw new UnknownMimeTypeCode(mimeTypes[i]);
                    }
                }
            }
            
            if (matchAlgorithm_code != null && matchAlgorithm_code.endsWith("LuceneQuery"))
            {
                //'*' is allowed in EVS mode for lucene queries 
                int loc = name.indexOf("=");
                String codingSchemeName = "";
                if (name != "*" && loc > 0)
                {
                    codingSchemeName = name.substring(name.indexOf("=") + 1);
                }
                else
                {
                    codingSchemeName = name;
                }
                return getLuceneSearch().luceneLookupConceptCodesByProperty(codingSchemeName,
                                                                                               matchText,
                                                                                               matchAlgorithm_code,
                                                                                               language_code,
                                                                                               activeConceptsOnly,
                                                                                               properties, mimeTypes,
                                                                                               timeout, sizeLimit);
            }

            String languageFilterPart;
            String textFilterPart;
            String baseFilterPart;
            String mimeTypePart;
            String propertyPart;

            languageFilterPart = generateLanguageFilterString(language_code, defaultLanguage, true);
            
            // Figure out the match text part of the query
            textFilterPart = generateFilterForProperty("text", wrapSearchStringForAlgorithm(matchText,
                                                                                            matchAlgorithm_code));
            if (textFilterPart.length() == 0)
            {
                textFilterPart = "(text=*)";
            }

            // set up the mimeTypePart of the query
            mimeTypePart = generateFilterForProperty("presentationFormat", mimeTypes);

            // set up the propertyPart of the query
            propertyPart = generateFilterForProperty("property", properties);

            baseFilterPart = "(objectClass=propertyClass)";

            filter = "(&" + languageFilterPart + textFilterPart + baseFilterPart + mimeTypePart + propertyPart + ")";
            name = "dc=Concepts, " + name;
            setSearchControls(searchControls, SearchControls.SUBTREE_SCOPE, timeout, sizeLimit * 5, false, new String[]{});
            //make the sizelimit bigger, because it will usually match on multiple 
            //designations per concept, and I will end up returning less concepts than the limit requested in that case.
            
            logger.debug("filter is: " + filter);

            NamingEnumeration tempEnum = codingSchemesContext_.search(name, filter, searchControls);

            while (tempEnum.hasMore())
            {
                if (sizeLimit != 0 && resultsToReturn.size() == sizeLimit)
                {
                    break;
                }
                ConceptId temp = new ConceptId();
                SearchResult sRes = (SearchResult) tempEnum.next();
                temp.setCodeSystem_id(codeSystem_id);
                String tempConcept = sRes.getName();
                temp.setConcept_code(tempConcept.substring(tempConcept.indexOf("conceptCode=")
                        + "conceptCode=".length()));
                if (temp.getConcept_code().equals(this.rootRelationNode_))
                {
                    // skip root relation node
                    continue;
                }
                if (activeConceptsOnly)
                {
                    Attributes attributes = codingSchemesContext_.getAttributes(nameParser.parse(sRes.getName()).get(0)
                            + ","
                            + name, new String[]{"isActive"});
                    if (attributes.get("isActive") != null
                            && !((String) attributes.get("isActive").get()).equalsIgnoreCase("true"))
                    {
                        // skip it if its not active
                        continue;
                    }
                }
                resultsToReturn.put(temp.getConcept_code(), temp);
            }

        }
        catch (SizeLimitExceededException e)
        {
            // do nothing
            logger.warn(e);
        }
        catch (TimeLimitExceededException e)
        {
            logger.error("The search exceeded the allotted time", e);
            throw new TimeoutError();
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (UnknownMimeTypeCode e)
        {
            throw e;
        }
        catch (UnknownPropertyCode e)
        {
            throw e;
        }
        catch (NamingException e)
        {
            throw new BadlyFormedMatchText(matchText);
        }
        catch (BadlyFormedMatchText e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return (ConceptId[]) resultsToReturn.values().toArray(new ConceptId[resultsToReturn.size()]);
    }

    public ConceptDesignation[] lookupDesignations(ConceptId conceptCode, String matchText, String matchAlgorithm_code,
            String languageCode) throws UnknownConceptCode, BadlyFormedMatchText, UnknownCodeSystem,
            UnknownLanguageCode, UnexpectedError, UnknownMatchAlgorithm
    {
        logger.debug("getDesignations called: conceptCode.conceptCode: " + (conceptCode == null ? "null"
                : (conceptCode.getConcept_code() == null ? "null"
                        : conceptCode.getConcept_code())) + " conceptCode.id: " + (conceptCode == null ? "null"
                : (conceptCode.getCodeSystem_id() == null ? "null"
                        : conceptCode.getCodeSystem_id())) + " languageCode: " + (languageCode == null ? "null"
                : languageCode) + " matchText: " + matchText + " matchAlgorithm: " + matchAlgorithm_code);

        try
        {
            String name = "";
            String filter = "";

            String defaultLanguage;
            
            if (conceptCode == null)
            {
                throw new UnknownCodeSystem("Null");
            }

            name = getLDAPNameForCodingSchemeId(conceptCode.getCodeSystem_id());
            
            if (conceptCode.getConcept_code() == null || conceptCode.getConcept_code().length() == 0)
            {
                throw new UnknownConceptCode("null");
            }
            
            defaultLanguage = roi_.getDefaultLanguage(name);

            // Validate that the language (if supplied) is valid
            if ((languageCode != null) && (languageCode.length() > 0) && !languageCode.equals(defaultLanguage))
            {
                if (!verifyAttribute(name, "supportedLanguage", languageCode))
                {
                    throw new UnknownLanguageCode(languageCode);
                }
            }
            
            if (matchAlgorithm_code != null && matchAlgorithm_code.endsWith("LuceneQuery"))
            {
                int loc = name.indexOf("=");
                String codingSchemeName = "";
                if (loc > 0)
                {
                    codingSchemeName = name.substring(name.indexOf("=") + 1);
                }
                else
                {
                    codingSchemeName = name;
                }
                ConceptDesignation[] temp = getLuceneSearch().luceneLookupDesignations(codingSchemeName, conceptCode.getConcept_code(), matchText,
                                                                         matchAlgorithm_code, languageCode);
                
                if (temp == null || temp.length == 0)
                {
                    if (!roi_.isConceptIdValid(conceptCode, false))
                    {
                        throw new UnknownConceptCode(conceptCode.getConcept_code());
                    }
                }
                return temp;
            }

            String languageFilterPart;
            String textFilterPart;
            String baseFilterPart;

            // Figure out the language filter part of the query
            languageFilterPart = generateLanguageFilterString(languageCode, defaultLanguage, false);

            // Figure out the match text part of the query
            textFilterPart = generateFilterForProperty("text", wrapSearchStringForAlgorithm(matchText,
                                                                                            matchAlgorithm_code));

            baseFilterPart = "(property=textualPresentation)";

            filter = "(&" + languageFilterPart + textFilterPart + baseFilterPart + ")";

            name = "conceptCode=" + conceptCode.getConcept_code() + ", dc=Concepts, " + name;
            setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                    .intValue(), CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false, new String[]{"text",
                    "isPreferred", "language"});

            ArrayList designationsToReturn = new ArrayList();
            try
            {
                NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);

                while (results.hasMore())
                {
                    SearchResult sRes = (SearchResult) results.next();
                    ConceptDesignation currentDesignation = new ConceptDesignation();

                    currentDesignation.setDesignation((String) sRes.getAttributes().get("text").get());
                    currentDesignation.setPreferredForLanguage(sRes.getAttributes().get("isPreferred") == null ? false
                            : ((sRes.getAttributes().get("isPreferred")).get()).equals("TRUE"));

                    if (sRes.getAttributes().get("language") == null)
                        currentDesignation.setLanguage_code(defaultLanguage);
                    else
                        currentDesignation.setLanguage_code((String) (sRes.getAttributes().get("language").get()));

                    designationsToReturn.add(currentDesignation);
                }
            }
            catch (NamingException e)
            {
                try
                {
                    codingSchemesContext_.getAttributes(name);
                    // if this works, it means the match text must be bad
                }
                catch (Exception e1)
                {
                    throw new UnknownConceptCode(conceptCode.getConcept_code());
                }
                throw new BadlyFormedMatchText(matchText);

            }

            return (ConceptDesignation[]) designationsToReturn.toArray(new ConceptDesignation[designationsToReturn
                    .size()]);
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (BadlyFormedMatchText e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public ConceptProperty[] lookupProperties(ConceptId concept_id, String[] properties, String matchText,
            String matchAlgorithm_code, String language_code, String[] mimeTypes) throws UnknownMimeTypeCode,
            UnknownConceptCode, BadlyFormedMatchText, UnknownCodeSystem, UnknownLanguageCode, UnknownPropertyCode,
            UnexpectedError, UnknownMatchAlgorithm
    {
        logger.debug("getProperties called: concept_id:conceptCode"
                + (concept_id == null ? "null"
                        : (concept_id.getConcept_code() == null ? "null"
                                : concept_id.getConcept_code()))
                + " ConceptId.codeSystemId: "
                + (concept_id == null ? "null"
                        : (concept_id.getCodeSystem_id() == null ? "null"
                                : concept_id.getCodeSystem_id()))
                + " properties: "
                + StringArrayUtility.stringArrayToString(properties)
                + " textFilter "
                + (matchText == null ? "null"
                        : matchText)
                + (matchAlgorithm_code == null ? "null"
                        : matchAlgorithm_code)
                + " language: "
                + (language_code == null ? "null"
                        : language_code)
                + " mimeTypes: "
                + StringArrayUtility.stringArrayToString(mimeTypes));
        try
        {
            ArrayList resultsToReturn = new ArrayList();
            
            if (concept_id == null)
            {
                throw new UnknownCodeSystem("Null");
            }

            // First, locate to the proper coding scheme (and validate its
            // existance)
            String name = getLDAPNameForCodingSchemeId(concept_id.getCodeSystem_id());
            
            String filter = "";
            String defaultLanguage;

            defaultLanguage = roi_.getDefaultLanguage(name);

            // Partially validate the concept code
            if (concept_id.getConcept_code() == null || concept_id.getConcept_code().length() == 0)
            {
                throw new UnknownConceptCode(concept_id.getConcept_code());
            }

            // Validate that the language (if supplied) is valid
            if ((language_code != null) && (language_code.length() > 0) && !language_code.equals(defaultLanguage))
            {
                if (!verifyAttribute(name, "supportedLanguage", language_code))
                {
                    throw new UnknownLanguageCode(language_code);
                }
            }

            // Validate that the properties (if supplied) are valid
            if ((properties != null) && (properties.length > 0))
            {
                for (int i = 0; i < properties.length; i++)
                {
                    if (!verifyAttribute(name, "supportedProperty", properties[i]))
                    {
                        throw new UnknownPropertyCode(properties[i]);
                    }
                }
            }

            // Validate that the mime types (if supplied) are valid
            if ((mimeTypes != null) && (mimeTypes.length > 0))
            {
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    if (!verifyAttribute(name, "supportedFormat", mimeTypes[i]))
                    {
                        throw new UnknownMimeTypeCode(mimeTypes[i]);
                    }
                }
            }
            
            if (matchAlgorithm_code != null && matchAlgorithm_code.endsWith("LuceneQuery"))
            {
                int loc = name.indexOf("=");
                String codingSchemeName = "";
                if (loc > 0)
                {
                    codingSchemeName = name.substring(name.indexOf("=") + 1);
                }
                else
                {
                    codingSchemeName = name;
                }
                ConceptProperty[] temp = getLuceneSearch().luceneLookupProperties(codingSchemeName, concept_id.getConcept_code(), properties, matchText,
                                                                         matchAlgorithm_code, language_code, mimeTypes);
                
                if (temp == null || temp.length == 0)
                {
                    if (!roi_.isConceptIdValid(concept_id, false))
                    {
                        throw new UnknownConceptCode(concept_id.getConcept_code());
                    }
                }
                return temp;
            }

            String languageFilterPart;
            String textFilterPart;
            String baseFilterPart;
            String mimeTypePart;
            String propertyPart;

            // Figure out the language filter part of the query
            languageFilterPart = generateLanguageFilterString(language_code, defaultLanguage, false);

            // Figure out the match text part of the query
            textFilterPart = generateFilterForProperty("text", wrapSearchStringForAlgorithm(matchText,
                                                                                            matchAlgorithm_code));

            // set up the mimeTypePart of the query
            mimeTypePart = generateFilterForProperty("presentationFormat", mimeTypes);

            // set up the propertyPart of the query
            propertyPart = generateFilterForProperty("property", properties);

            baseFilterPart = "(objectClass=propertyClass)";

            filter = "(&" + languageFilterPart + textFilterPart + baseFilterPart + mimeTypePart + propertyPart + ")";
            name = "conceptCode=" + concept_id.getConcept_code() + ", dc=Concepts, " + name;
            setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                    .intValue(), CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false, new String[]{"text",
                    "presentationFormat", "property", "language"});

            logger.debug("filter is: " + filter);

            NamingEnumeration tempEnum = codingSchemesContext_.search(name, filter, searchControls);

            while (tempEnum.hasMore())
            {
                ConceptProperty temp = new ConceptProperty();
                SearchResult sRes = (SearchResult) tempEnum.next();
                temp.setLanguage_code((sRes.getAttributes().get("language") == null ? defaultLanguage
                        : (String) sRes.getAttributes().get("language").get()));
                temp.setMimeType_code((sRes.getAttributes().get("presentationFormat") == null ? ""
                        : (String) sRes.getAttributes().get("presentationFormat").get()));
                temp.setProperty_code((sRes.getAttributes().get("property") == null ? ""
                        : (String) sRes.getAttributes().get("property").get()));
                temp.setPropertyValue((sRes.getAttributes().get("text") == null ? ""
                        : (String) sRes.getAttributes().get("text").get()));
                resultsToReturn.add(temp);

            }

            return (ConceptProperty[]) resultsToReturn.toArray(new ConceptProperty[resultsToReturn.size()]);
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (NameNotFoundException e)
        {
            throw new UnknownConceptCode(concept_id.getConcept_code());
        }
        catch (NamingException e)
        {
            throw new BadlyFormedMatchText(matchText);
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (UnknownMimeTypeCode e)
        {
            throw e;
        }
        catch (UnknownPropertyCode e)
        {
            throw e;
        }
        catch (BadlyFormedMatchText e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    private Attributes getAttributes(String name, String[] attributesToGet) throws NamingException
    {
        Attributes attributes = null;
        String keyName = name + "getAttributes";

        if (objectCache_.get(keyName) instanceof Attributes)
        {
            attributes = (Attributes) objectCache_.get(keyName);
        }

        if (attributes == null)
        {
            attributes = codingSchemesContext_.getAttributes(name, attributesToGet);
            objectCache_.put(keyName, attributes);
        }

        return attributes;
    }

    /*
     * Get the ldap name for a coding scheme id
     * 
     * @param codeSystem_id The local name to look up 
     * @return 
     * @throws UnknownCodeSystem
     */

    private String getLDAPNameForCodingSchemeId(String codeSystem_id) throws UnknownCodeSystem
    {
        return roi_.getLDAPNameForCodingSchemeId(codeSystem_id);
    }
    
    /*
     * Turn a code system name (from a relationship) into the ID.  Goes through the supportedCodeSystems
     * map.
     */
    protected String getIdForRelationshipCodeSystemName(String ldapName, String targetCodeSystemName) throws UnexpectedError
    {
        try
        {
            if (targetCodeSystemName == null || targetCodeSystemName.length() == 0)
            {
                throw new UnexpectedError("No target code system name specified");
            }
            
            String keyName = targetCodeSystemName + "_targetCodeSystemIdLookup_";
            if (objectCache_.get(keyName) instanceof String)
            {
                return (String) objectCache_.get(keyName);
            }
            else
            {
                String filter = "(objectClass=codingSchemeClass)";

                setSearchControls(searchControls, SearchControls.OBJECT_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                        .intValue(), 1, false, new String[]{"supportedCodingScheme"});

                NamingEnumeration results = codingSchemesContext_.search(ldapName, filter, searchControls);
                
                if (results.hasMore())
                {
                    SearchResult sRes = (SearchResult) results.next();
                    Attribute attrib = sRes.getAttributes().get("supportedCodingScheme");
                    
                    Enumeration temp = attrib.getAll();
                    while (temp.hasMoreElements())
                    {
                        String urn = (String)temp.nextElement();
                        int namePos = urn.indexOf(" ");
                        if (namePos > 0)
                        {
                            String namePart = urn.substring(namePos + 1);
                            if (namePart.equals(targetCodeSystemName))
                            {
                                //cut off the leading stuff
                                urn = urn.substring(0, namePos);
                                int temp2 = urn.lastIndexOf(':');
                                if (temp2 > 0 && ((temp2 + 1) <= urn.length()))
                                {
                                    urn = urn.substring(temp2 + 1);
                                }
                                objectCache_.put(keyName, urn);
                                return urn;
                            }
                        }
                    }
                }
                throw new UnexpectedError("Database is missing required supportedCodingScheme Attributes entry that maps target coding scheme to the URN");
            }
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    /*
     * Generate a filter string for an array of values
     * 
     * @param attributeToFilter the attribute to filter on @param value The values of the attributes @return
     */
    private String generateFilterForProperty(String attributeToFilter, String[] value)
    {
        StringBuffer result = new StringBuffer();
        if (value != null && value.length > 0)
        {
            result.append("(|");
            for (int i = 0; i < value.length; i++)
            {
                if (value[i] != null && value[i].length() > 0)
                {
                    result.append("(" + attributeToFilter + "=" + value[i] + ")");
                }
            }
            result.append(")");
            return result.toString();
        }
        else
        {
            return "";
        }
    }

    /*
     * Generate a filter string for a property
     * 
     * @param attributeToFilter @param value @return
     */
    private String generateFilterForProperty(String attributeToFilter, String value)
    {
        if ((value != null) && (value.length() > 0))
        {
            return "(" + attributeToFilter + "=" + value + ")";
        }
        else
        { // no value specified
            return "";
        }
    }

    private String wrapSearchStringForAlgorithm(String searchString, String algorithm) throws UnknownMatchAlgorithm
    {
        if (algorithm == null || algorithm.length() == 0)
        {
            return searchString;
        }
        else if (algorithm.equals("IdenticalIgnoreCase"))
        {
            return searchString;
        }
        else if (algorithm.equals("StartsWithIgnoreCase"))
        {
            return searchString + "*";
        }
        else if (algorithm.equals("EndsWithIgnoreCase"))
        {
            return "*" + searchString;
        }
        else if (algorithm.equals("ContainsPhraseIgnoreCase"))
        {
            return "*" + searchString + "*";
        }
        else
        {
            throw new UnknownMatchAlgorithm(algorithm);
        }
    }

    /*
     * Helper method to generate the code for a language filter
     * 
     * @param language The language passed in @param defaultLanguage the default language for the coding scheme @return
     * the filter text
     */
    private String generateLanguageFilterString(String language, String defaultLanguage, boolean matchMoreGeneral)
    {
        String wildCard = "";
        if (matchMoreGeneral)
        {
            wildCard = "*";
        }
        
        if ((language != null) && (language.length()) > 0 && !(language.equals(defaultLanguage)))
        { // language specified other than default
            return "(language=" + language + wildCard + ")";
        }
        else if ((language != null) && (language.length()) > 0 && (language.equals(defaultLanguage)))
        { // specifed default language
            return "(|(!(language=*))(language=" + defaultLanguage + wildCard + "))";
        }
        else
        { // no language specified
            return "";
        }

    }
    
    private String getDefaultTargetCodingScheme(String name) throws NamingException
    {
        String target = "";

        String keyName = name + "_targetCodingScheme_";
        if (objectCache_.get(keyName) instanceof String)
        {
            target = (String) objectCache_.get(keyName);
        }
        if (target == null)
        {
            Attributes attributesTemp = getAttributes(name, new String[]{SchemaConstants.targetCodingScheme});

            if (attributesTemp.get(SchemaConstants.targetCodingScheme) != null)
            {
                target = (String) attributesTemp.get(SchemaConstants.targetCodingScheme).get();
                objectCache_.put(keyName, target);
            }
            else
            {
                target = "";
            }
        }
        
        return target;
    }
    
    /*
     * Get the default systemization for the codingscheme rooted at "name"
     */
    private String getDefaultSystemization(String name)
    {
        String systemization = null;
        String keyName = name + "systemization";

        if (objectCache_.get(keyName) instanceof String)
        {
            systemization = (String) objectCache_.get(keyName);
        }
        
        if (systemization == null)
        {
            try
            {
                setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                        .intValue(), 1, false, new String[]{"dc"});

                String filter = "(&(objectClass=relations)(isNative=TRUE))";
                NamingEnumeration temp = codingSchemesContext_.search(name, filter, searchControls);
                
                if (temp.hasMore())
                {
                    systemization = (String) ((SearchResult) temp.next()).getAttributes().get("dc").get();
                }
                else 
                {
                    systemization = "";
                }
                temp.close();
                objectCache_.put(keyName, systemization);
            }
            catch (NamingException ex)
            {
                //not going to make this an error - just a warning - could mean there are just no relationships
                logger.warn("Could not determine native systemization", ex);
                systemization = "";
            }
        }

        return systemization;
    }

    /*
     * Method to verify the availablility of an attribute in a position. removes urn's as necessary, and caches results.
     * 
     * @param ldapName The ldap subname to use @param attributeName The attribute to verify @param attributeValue What
     * the attribute should be 
     * @return true if exists, false otherwise unless it throws a: @throws NamingException
     * usually when the name was invalid
     */
    private boolean verifyAttribute(String ldapName, String attributeName, String attributeValue)
            throws NamingException
    {
        //'*' is allowed for ldapName when you are in EVSMode - so we can't validate attributes.
        //just return true.  This is used by the lucene plugins.
        if (CTSConstants.EVSModeEnabled.getValue() && ldapName.equals("*"))
        {
            return true;
        }
        
        String keyName = ldapName + ":" + attributeName + ":" + attributeValue;

        Boolean result = (Boolean) objectCache_.get(keyName);
        if (result != null)
        {
            return result.booleanValue();
        }

        String filter = "(" + attributeName + "=*)";
        setSearchControls(searchControls, SearchControls.OBJECT_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT.intValue(), 1,
                          false, new String[]{attributeName});

        NamingEnumeration tempEnum = codingSchemesContext_.search(ldapName, filter, searchControls);
        if (tempEnum.hasMore())
        {
            SearchResult sRes = (SearchResult) tempEnum.next();
            Attributes attributes = sRes.getAttributes();
            NamingEnumeration attribEnum = attributes.get(attributeName).getAll();

            while (attribEnum.hasMore())
            {
                String current = (String) attribEnum.nextElement();
                if (current.equals(attributeValue))
                {
                    objectCache_.put(keyName, new Boolean(true));
                    return true;
                }
                else
                {
                    // supported things have this format now - only care about value after space.. urn:sdjfkdslf VALUE
                    int temp = current.indexOf(" ");
                    if (temp > 0)
                    {
                        current = current.substring(temp + 1, current.length());
                        if (current.equals(attributeValue))
                        {
                            objectCache_.put(keyName, new Boolean(true));
                            return true;
                        }
                    }
                }
            }
        }
        objectCache_.put(keyName, new Boolean(false));
        return false;
    }
    
    private LuceneSearch getLuceneSearch() throws UnexpectedError
    {
        if (luceneSearch_ == null)
        {
            String[] codeSystems = null;
            try
            {
                CodeSystemIdAndVersions[] csidv = getSupportedCodeSystems(0, 0);
                codeSystems = new String[csidv.length];
                for (int i = 0; i < csidv.length; i++)
                {
                    codeSystems[i] = csidv[i].getCodeSystem_name();
                }
            }
            catch (TimeoutError e)
            {
                // can't happen.
            }
            luceneSearch_ = new LuceneSearch(CTSConstants.LUCENE_INDEX_LOCATION.getValue(), codeSystems);
        }
        return luceneSearch_;
    }
    

    /**
     * Given a searchControls Object, and the values that you wish to set, this method sets those values. This method is
     * used because many of the classes have a single searchControls object that is reused, and not all searches require
     * all of these controls to be set. By using this method, you are requred to set each control to what is should be
     * for this search.
     * 
     * @param scope
     * @param timeout
     * @param limit
     * @param returningObjFlag
     * @param attributes
     * @see SearchControls
     */
    private static void setSearchControls(SearchControls searchControls, int scope, int timeout, long limit,
            boolean returningObjFlag, String[] attributes)
    {
        searchControls.setSearchScope(scope);
        searchControls.setTimeLimit(timeout);
        searchControls.setCountLimit(limit);
        searchControls.setDerefLinkFlag(false);
        searchControls.setReturningObjFlag(returningObjFlag);
        searchControls.setReturningAttributes(attributes);
    }

    /**
     * Connect to the ldap server. Any parameter not supplied will be read from the config file.
     * 
     * @param userName - optional
     * @param userPassword - optional
     * @param address - optionl
     * @param service - optional
     * @return the connection
     * @throws NamingException
     */
    private AutoReconnectDirContext initContext(String userName, String userPassword, String address, String service)
            throws NamingException
    {
        if ((address == null) || (address.length() == 0))
        {
            address = CTSConstants.VAPI_LDAP_ADDRESS.toString();
        }

        if ((service == null) || (service.length() == 0))
        {
            service = CTSConstants.VAPI_LDAP_SERVICE.toString();
        }

        if ((userName == null) || (userName.length() == 0))
        {
            userName = CTSConstants.VAPI_LDAP_USERNAME.toString();
        }
        if ((userPassword == null) || (userPassword.length() == 0))
        {
            userPassword = CTSConstants.VAPI_LDAP_PASSWORD.toString();
        }

        if (!address.endsWith("/") && !service.startsWith("/"))
        {
            service = "/" + service;
        }

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, address + "" + service);
        env.put(Context.REFERRAL, "follow");
        env.put("java.naming.ldap.derefAliases", "never");

        // these only work in 1.4... and beyond
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        env.put("com.sun.jndi.ldap.connect.timeout", "3000");

        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, userPassword);

        // We have to put in every object type that we want returned as a
        // byte[] in this format....
        env.put("java.naming.ldap.attributes.binary", SchemaConstants.blob); // + "
        // " +
        // SchemaConstants.formalRules);
        AutoReconnectDirContext context = new AutoReconnectDirContext(env, false);
        return context;
    }

}