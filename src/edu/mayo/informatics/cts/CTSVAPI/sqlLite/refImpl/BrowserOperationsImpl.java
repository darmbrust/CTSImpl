/*
 * Copyright: (c) 2002-2006 Mayo Foundation for Medical Education and
 * Research (MFMER).  All rights reserved.  MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, the trade names, 
 * trademarks, service marks, or product names of the copyright holder shall
 * not be used in advertising, promotion or otherwise in connection with
 * this Software without prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

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

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.CTSConstants;
import edu.mayo.informatics.cts.utility.Utility;
import edu.mayo.informatics.cts.utility.VAPIExpansionContext;
import edu.mayo.informatics.cts.utility.Utility.SQLConnectionInfo;
import edu.mayo.mir.utility.StringArrayUtility;

/**
 * A reference implementation of BrowserOperations that uses a sql backend instead of ldap.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class BrowserOperationsImpl implements BrowserOperations
{
    private final String[]        supportedMatchAlgorithms_ = new String[]{"IdenticalIgnoreCase",
            "StartsWithIgnoreCase", "EndsWithIgnoreCase", "ContainsPhraseIgnoreCase"};

    private SQLStatements         queries_;
    private RuntimeOperationsImpl roi_                      = null;
    private final String          rootRelationNode_         = "@";

    public final static Logger    logger                    = Logger.getLogger("edu.mayo.informatics.cts.VAPI_sqlLite_Browser");

    
    /**
     * Create a new BrowserOperations object using the current values of CTSConstants
     * @return
     * @throws UnexpectedError
     */
    static public synchronized BrowserOperations _interface() throws UnexpectedError
    {
        BrowserOperationsImpl svc = new BrowserOperationsImpl(null, null, null, null, false, false);
        return (BrowserOperations) svc;
    }
    
    /**
     * This is the method you want to use to instantiate a local RuntimeOperations object.
     * 
     * @param userName loaded from props if null
     * @param userPassword loaded from props if null
     * @param server loaded from props if null
     * @param driver loaded from props if null
     * @param initLogger whether or not to init the logger and load props.
     * @return
     */
    static public synchronized BrowserOperations _interface(String userName, String userPassword, String server,
            String driver, boolean loadProps, boolean initLogger) throws UnexpectedError
    {
        BrowserOperationsImpl svc = new BrowserOperationsImpl(userName, userPassword, server, driver, loadProps, initLogger);
        return (BrowserOperations) svc;
    }

    /**
     * Return a reference to a new Browser Operations. Parses the connection information from an XML string.
     * 
     * @param xmlLexGridLDAPString The XML String.
     * @param initLogger whether or not to init the logger.
     * @return reference or null if unable to construct it
     * @throws UnexpectedError if something goes wrong.
     */
    static public synchronized BrowserOperations _interface(String xmlLexGridSQLString, String username,
            String password, boolean loadProps, boolean initLogger) throws UnexpectedError

    {
        BrowserOperationsImpl svc;
        try
        {
            SQLConnectionInfo temp = Utility.parseSQLXML(xmlLexGridSQLString);
            if (username == null || username.length() == 0)
            {
                username = temp.username;
            }
            if (password == null || password.length() == 0)
            {
                password = temp.password;
            }
            svc = new BrowserOperationsImpl(username, password, temp.server, temp.driver, loadProps, initLogger);
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
     * This method is here for AXIS. Use the _interface method to instantiate this object locally.
     */
    public BrowserOperationsImpl()
    {
        try
        {
            CTSConfigurator._instance().initialize();
            logger.debug("CTSVAPI.BrowserOperationImpl SQL Constructor called");
            queries_ = SQLStatements.instance(null, null, null, null);
            roi_ = new RuntimeOperationsImpl(null, null, null, null, false, false);
        }
        catch (Exception e)
        {
            logger.error("BrowserOperationsImpl Constructor error", e);
        }
    }

    protected BrowserOperationsImpl(String username, String password, String server, String driver, 
            boolean loadProps, boolean initLogger)
            throws UnexpectedError
    {
        try
        {
            if (loadProps)
            {
                CTSConfigurator._instance().initialize(initLogger);
            }
            logger.debug("CTSVAPI.BrowserOperationImpl Constructor called");
            queries_ = SQLStatements.instance(username, password, server, driver);
            roi_ = new RuntimeOperationsImpl(username, password, server, driver, false, false);
        }
        catch (Exception e)
        {
            logger.error("BrowserOperationsImpl Constructor error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String[] getSupportedMatchAlgorithms() throws UnexpectedError
    {
        try
        {
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
            tempC.setCodeSystem_id(null);
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
                return expandCodeForward(tempC, temp.directDescendantsOnly_, temp.relationship_, temp.language_,
                                         temp.defaultLanguage_, temp.codeSystemName_, temp.nestingDepth_, true,
                                         temp.timeout_, temp.sizeLimit_, startTime);
            }
            else
            {
                return expandCodeReverse(tempC, temp.directDescendantsOnly_, temp.relationship_, temp.language_,
                                         temp.defaultLanguage_, temp.codeSystemName_, temp.nestingDepth_, true,
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
        // TODO Handle cycles according to spec
        PreparedStatement getAssociationProperties = null;
        try
        {
            long methodStartTime = System.currentTimeMillis();

            if (expandConcept_id.getConcept_code() != null
                    && expandConcept_id.getConcept_code().equals(this.rootRelationNode_))
            {
                throw new UnknownConceptCode(expandConcept_id.getConcept_code());
            }

            String codeSystemName = roi_.getNameForCodeSystemId(expandConcept_id.getCodeSystem_id());

            String defaultLanguage = roi_.getDefaultLanguage(codeSystemName);

            // Validate that the language (if supplied) is valid
            if (designationLanguage_code != null && designationLanguage_code.length() != 0)
            {
                roi_.validateLanguageCode(codeSystemName, designationLanguage_code);
            }
            else
            {
                designationLanguage_code = roi_.getDefaultLanguage(codeSystemName);
            }

            // validate the association
            String[] supportedAssociations = roi_.getSupportedProperties(codeSystemName, "Association");

            boolean associationSupported = false;
            for (int i = 0; i < supportedAssociations.length; i++)
            {
                if (supportedAssociations[i].equals(relationship_code))
                {
                    associationSupported = true;
                    break;
                }
            }
            if (!associationSupported)
            {
                // this is a special case, if they don't pass in a source code, and ask for hasSubtype, and
                // we don't support hasSubtype, then return all codes.
                if (((expandConcept_id.getConcept_code() == null) || (expandConcept_id.getConcept_code().length() == 0))
                        && relationship_code.equals("hasSubtype"))
                {
                    // set the relationship to null as a flag...
                    return expandCodeForward(expandConcept_id, directRelationsOnly, null, designationLanguage_code,
                                             defaultLanguage, codeSystemName, 1, false, timeout, sizeLimit,
                                             methodStartTime);
                }
                else
                {
                    throw new UnknownRelationshipCode(relationship_code);
                }
            }

            getAssociationProperties = queries_.checkOutStatement(queries_.GET_ASSOCIATION_PROPERTIES);
            getAssociationProperties.setString(1, relationship_code);
            getAssociationProperties.setString(2, codeSystemName);
            ResultSet results = getAssociationProperties.executeQuery();
            // there should be one and only one result - if not, we will just fail and toss the unexpected error.
            results.next();
 
            boolean isTransitive = SQLStatements.getBooleanResult(results, "isTransitive"); 
 
            //dont hold this open
            queries_.checkInPreparedStatement(getAssociationProperties);

            if (sourceToTarget)
            {
                return expandCodeForward(expandConcept_id, directRelationsOnly, relationship_code,
                                         designationLanguage_code, defaultLanguage, codeSystemName, 1, isTransitive,
                                         timeout, sizeLimit, methodStartTime);
            }
            else
            {
                return expandCodeReverse(expandConcept_id, directRelationsOnly, relationship_code,
                                         designationLanguage_code, defaultLanguage, codeSystemName, 1, isTransitive,
                                         timeout, sizeLimit, methodStartTime);
            }
        }
        catch (UnknownConceptCode e)
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
        catch (UnknownRelationshipCode e)
        {
            throw e;
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getAssociationProperties);
        }
    }

    private RelatedCode[] expandCodeForward(ConceptId codeToExpand, boolean directDescendantsOnly, String relationship,
            String language, String defaultLanguage, String codingSchemeName, int pathLength, boolean isTransitive,
            int timeout, int sizeLimit, long startTime) throws UnknownConceptCode, UnexpectedError, TimeoutError
    {
        try
        {

            ArrayList resultsToReturn = new ArrayList();
            logger.debug("Private helper - expandCodeForward called");

            if ((codeToExpand.getConcept_code() == null) || (codeToExpand.getConcept_code().length() == 0))
            {
                if (relationship == null)
                // This is the flag for return ALL codes for a scheme. No
                // hierarchy.
                {
                    return expandCodeReturnAll(codingSchemeName, timeout, sizeLimit, startTime);
                }
                else
                {
                    // otherwise, we need to get the next set of codes from the
                    // special rootRelationNode_ node.
                    //create a new on rather then changing the code on the input code system.
                    codeToExpand = new ConceptId(codeToExpand.getCodeSystem_id(), rootRelationNode_);
                    
                   // codeToExpand.setConcept_code(rootRelationNode_);
                }
            }

            // Get a targets for a source
            if ((isTransitive == false) || (directDescendantsOnly == true))
            // if the association is non-transitive, or they ask for a
            // direct only search
            {
                resultsToReturn = expandCodeForwardHelper(codeToExpand.getConcept_code(), codingSchemeName,
                                                          relationship, pathLength, language, defaultLanguage,
                                                          isTransitive, timeout, sizeLimit, startTime);
            }
            else
            {
                // look for A rel B, B rel C ==> A rel C - they want a
                // recursive search.
                resultsToReturn = expandCodeForwardRecursive(1, codingSchemeName, relationship, codeToExpand
                        .getConcept_code(), language, defaultLanguage, timeout, sizeLimit, startTime);
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

    private ArrayList expandCodeForwardHelper(String conceptCode, String codingSchemeName, String relationship,
            int pathLength, String language, String defaultLanguage, boolean isTransitive, int timeout, int sizeLimit,
            long startTime) throws TimeoutError, UnexpectedError
    {
        ArrayList results = new ArrayList();
        PreparedStatement getChildrenOfConcept = null;
        PreparedStatement checkForChildren = null;
        try
        {
            int timeLeft = timeout - (int) (System.currentTimeMillis() - startTime);
            if (timeout == 0)
            {
                timeLeft = 0;
            }
            else if (timeLeft <= 0)
            {
                throw new TimeoutError();
            }

            getChildrenOfConcept = queries_.checkOutStatement(queries_.GET_SOURCE_FOR);
            checkForChildren = queries_.checkOutStatement(queries_.GET_SOURCE_FOR);

            getChildrenOfConcept.setString(1, codingSchemeName);
            getChildrenOfConcept.setString(2, conceptCode);
            getChildrenOfConcept.setString(3, relationship);

            setTimeout(getChildrenOfConcept, timeout);
            getChildrenOfConcept.setMaxRows(sizeLimit);

            ResultSet queryResults = getChildrenOfConcept.executeQuery();

            RelatedCode relatedCode;

            while (queryResults.next())
            {
                relatedCode = new RelatedCode();

                String currentConceptCode = queryResults.getString("targetConceptCode");
                relatedCode.setConcept_code(currentConceptCode);
                relatedCode.setPathLength((short) pathLength);

                // get the designation
                ConceptId conceptId = new ConceptId();
                conceptId.setConcept_code(currentConceptCode);
                conceptId.setCodeSystem_id(roi_
                        .getIdForRelationshipCodeSystemName(codingSchemeName, queryResults.getString("targetCodingSchemeName")));
                try
                {
                    StringAndLanguage stringAndLanguage = roi_.lookupDesignation(conceptId, language);

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

                // Get the qualifiers - no qualfiers in cts-lite.
                relatedCode.setRelationQualifiers(new String[] {});

                relatedCode.setCanExpand(false);
                relatedCode.setExpansionContext("".getBytes());

                // find out if it can expand
                if (isTransitive)
                {
                    checkForChildren.setString(1, codingSchemeName);
                    checkForChildren.setString(2, relatedCode.getConcept_code());
                    checkForChildren.setString(3, relationship);

                    ResultSet temp = checkForChildren.executeQuery();
                    if (temp.next())
                    {
                        relatedCode.setCanExpand(true);
                        relatedCode.setExpansionContext(new VAPIExpansionContext(true, true, relationship, null,
                                codingSchemeName, null, pathLength + 1, language, defaultLanguage, relatedCode
                                        .getConcept_code(), null, timeout, sizeLimit).toString().getBytes());
                    }
                }
                results.add(relatedCode);
            }
        }
        catch (SQLException e)
        {
            timeup(startTime, timeout);
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
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
        finally
        {
            queries_.checkInPreparedStatement(checkForChildren);
            queries_.checkInPreparedStatement(getChildrenOfConcept);
        }
        return results;
    }

    private ArrayList expandCodeForwardRecursive(int recursionDepth, String codingSchemeName, String relationship,
            String codeToExpand, String language, String defaultLanguage, int timeout, int sizeLimit, long startTime)
            throws UnknownConceptCode, TimeoutError, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();

        if (recursionDepth > CTSConstants.MAX_SYSTEMIZATION_ASSOCIATION_RECURSION.getValue())
        {
            // base case 1
            return resultsToReturn;
        }
        ArrayList temp = expandCodeForwardHelper(codeToExpand, codingSchemeName, relationship, recursionDepth,
                                                 language, defaultLanguage, true, timeout, sizeLimit, startTime);

        for (int i = 0; i < temp.size(); i++)
        {
            if (resultsToReturn.size() >= sizeLimit && sizeLimit != 0)
            {
                logger.warn("Skipping results due to sizeLimit");
                break;
            }
            resultsToReturn.add(temp.get(i));

            ArrayList deeperResults = expandCodeForwardRecursive(recursionDepth + 1, codingSchemeName, relationship,
                                                                 ((RelatedCode) temp.get(i)).getConcept_code(),
                                                                 language, defaultLanguage, timeout, sizeLimit,
                                                                 startTime);
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

    private RelatedCode[] expandCodeReverse(ConceptId codeToExpand, boolean directDescendantsOnly, String relationship,
            String language, String defaultLanguage, String codeSystemName, int pathLength, boolean isTransitive,
            int timeout, int sizeLimit, long startTime) throws UnknownConceptCode, UnexpectedError, TimeoutError
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
                    resultsToReturn = expandCodeReverseHelper(codeToExpand.getConcept_code(), codeSystemName,
                                                              relationship, pathLength, language, defaultLanguage,
                                                              isTransitive, timeout, sizeLimit, startTime);
                }

                else
                {
                    // look for A rel B, B rel C ==> A rel C - they want a
                    // recursive search.
                    resultsToReturn = expandCodeReverseRecursive(pathLength, codeSystemName, relationship, codeToExpand
                            .getConcept_code(), language, defaultLanguage, timeout, sizeLimit, startTime);
                }

                if (resultsToReturn.size() == 0)
                {
                    // expandCodeExpansionContext doesn't populate the codeSystem id. Its assumed that its concepts
                    // should be valid.
                    if (codeToExpand.getCodeSystem_id() != null
                            && codeToExpand.getCodeSystem_id().length() > 0
                            && !roi_.isConceptIdValid(codeToExpand, true))
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

    private ArrayList expandCodeReverseRecursive(int recursionDepth, String codeSystemName, String relationship,
            String codeToExpand, String language, String defaultLanguage, int timeout, int sizeLimit, long startTime)
            throws TimeoutError, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();

        if (recursionDepth > CTSConstants.MAX_SYSTEMIZATION_ASSOCIATION_RECURSION.getValue())
        {
            // base case 1
            return resultsToReturn;
        }

        ArrayList temp = expandCodeReverseHelper(codeToExpand, codeSystemName, relationship, recursionDepth, language,
                                                 defaultLanguage, true, timeout, sizeLimit, startTime);
        for (int i = 0; i < temp.size(); i++)
        {
            if (resultsToReturn.size() >= sizeLimit && sizeLimit != 0)
            {
                logger.warn("Skipping results due to sizeLimit");
                break;
            }
            resultsToReturn.add(temp.get(i));

            ArrayList deeperResults = expandCodeReverseRecursive(recursionDepth + 1, codeSystemName, relationship,
                                                                 ((RelatedCode) temp.get(i)).getConcept_code(),
                                                                 language, defaultLanguage, timeout, sizeLimit,
                                                                 startTime);
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

    private ArrayList expandCodeReverseHelper(String conceptCode, String codingSchemeName, String relationship,
            int pathLength, String language, String defaultLanguage, boolean isTransitive, int timeout, int sizeLimit,
            long startTime) throws TimeoutError, UnexpectedError
    {
        ArrayList results = new ArrayList();
        PreparedStatement getParentsOfConcept = null;
        PreparedStatement checkForParents = null;
        try
        {
            int timeLeft = timeout - (int) (System.currentTimeMillis() - startTime);
            if (timeout == 0)
            {
                timeLeft = 0;
            }
            else if (timeLeft <= 0)
            {
                throw new TimeoutError();
            }

            getParentsOfConcept = queries_.checkOutStatement(queries_.GET_TARGET_OF);
            checkForParents = queries_.checkOutStatement(queries_.GET_TARGET_OF);

            getParentsOfConcept.setString(1, codingSchemeName);
            getParentsOfConcept.setString(2, conceptCode);
            getParentsOfConcept.setString(3, relationship);

            setTimeout(getParentsOfConcept, timeout);
            getParentsOfConcept.setMaxRows(sizeLimit);

            ResultSet queryResults = getParentsOfConcept.executeQuery();

            RelatedCode relatedCode;

            while (queryResults.next())
            {
                relatedCode = new RelatedCode();

                String currentConceptCode = queryResults.getString("sourceConceptCode");
                if (currentConceptCode.equals(this.rootRelationNode_))
                {
                    continue;
                }

                relatedCode.setConcept_code(currentConceptCode);
                relatedCode.setPathLength((short) pathLength);

                // get the designation
                ConceptId conceptId = new ConceptId();
                conceptId.setConcept_code(currentConceptCode);
                conceptId.setCodeSystem_id(roi_.getIdForCodeSystemName(codingSchemeName));
                try
                {
                    StringAndLanguage stringAndLanguage = roi_.lookupDesignation(conceptId, language);

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

                // Get the qualifiers - no qualfiers in cts-lite.
                relatedCode.setRelationQualifiers(new String[] {});

                relatedCode.setCanExpand(false);
                relatedCode.setExpansionContext("".getBytes());

                // find out if it can expand
                if (isTransitive)
                {
                    checkForParents.setString(1, codingSchemeName);
                    checkForParents.setString(2, relatedCode.getConcept_code());
                    checkForParents.setString(3, relationship);

                    ResultSet temp = checkForParents.executeQuery();
                    if (temp.next())
                    {
                        relatedCode.setCanExpand(true);
                        relatedCode.setExpansionContext(new VAPIExpansionContext(false, true, relationship, null,
                                codingSchemeName, null, pathLength + 1, language, defaultLanguage, relatedCode
                                        .getConcept_code(), null, timeout, sizeLimit).toString().getBytes());
                    }
                }
                results.add(relatedCode);
            }
        }
        catch (SQLException e)
        {
            timeup(startTime, timeout);
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
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
        finally
        {
            queries_.checkInPreparedStatement(checkForParents);
            queries_.checkInPreparedStatement(getParentsOfConcept);
        }
        return results;
    }

    private RelatedCode[] expandCodeReturnAll(String codeSystemName, int timeout, int sizeLimit, long startTime)
            throws TimeoutError, UnexpectedError
    {
        ArrayList resultsToReturn = new ArrayList();
        PreparedStatement getAllConcepts = null;
        try
        {
            getAllConcepts = queries_.checkOutStatement(queries_.GET_ALL_CONCEPTS);
            getAllConcepts.setString(1, codeSystemName);

            setTimeout(getAllConcepts, timeout);
            getAllConcepts.setMaxRows(sizeLimit);

            ResultSet results = getAllConcepts.executeQuery();

            RelatedCode relatedCode;
            while (results.next())
            {
                relatedCode = new RelatedCode();
                relatedCode.setConcept_code(results.getString("conceptCode"));
                relatedCode.setDesignation(results.getString("conceptName"));

                // these will always be this...
                relatedCode.setCanExpand(false);
                relatedCode.setExpansionContext(new byte[0]);
                relatedCode.setPathLength((short) 1);
                relatedCode.setRelationQualifiers(new String[] {});

                resultsToReturn.add(relatedCode);
                timeup(startTime, timeout);
            }
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            timeup(startTime, timeout);
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (Exception e)
        {
            logger.debug("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getAllConcepts);
        }

        return (RelatedCode[]) resultsToReturn.toArray(new RelatedCode[resultsToReturn.size()]);
    }

    public CodeSystemIdAndVersions[] getSupportedCodeSystems(int timeout, int sizeLimit) throws UnexpectedError,
            TimeoutError
    {
        return roi_.getSupportedCodeSystems(timeout, sizeLimit);
    }

    public CompleteCodedConceptDescription lookupCompleteCodedConcept(ConceptId concept_id) throws UnknownConceptCode,
            UnknownCodeSystem, UnexpectedError
    {
        PreparedStatement getCodeDetails = null;
        PreparedStatement getSourceFor = null;
        PreparedStatement getTargetOf = null;
        try
        {
            CompleteCodedConceptDescription result = new CompleteCodedConceptDescription();

            if (concept_id == null || concept_id.getCodeSystem_id() == null)
            {
                throw new UnknownCodeSystem(null);
            }

            // the root concept isn't actually a concept
            if (concept_id.getConcept_code() == null || concept_id.getConcept_code().equals(this.rootRelationNode_))
            {
                throw new UnknownConceptCode(concept_id.getConcept_code());
            }

            String codeSystemName = roi_.getNameForCodeSystemId(concept_id.getCodeSystem_id());

            getCodeDetails = queries_.checkOutStatement(queries_.GET_CODE_DETAILS);
            getCodeDetails.setString(1, codeSystemName);
            getCodeDetails.setString(2, concept_id.getConcept_code());

            ResultSet queryResults = getCodeDetails.executeQuery();
            if (queryResults.next())
            {
                result.setCodeSystem_version(queryResults.getString("representsVersion"));
                result.setConcept_id(concept_id);
                result.setConceptStatus_code(queryResults.getString("conceptStatus"));
            }
            else
            {
                throw new UnknownConceptCode(concept_id.getConcept_code());
            }
            
            //don't hold open
            queries_.checkInPreparedStatement(getCodeDetails);

            // get the designations - this also validates the code (and code system)
            result.setDesignatedBy(lookupDesignations(concept_id, null, null, null));

            // get the properties
            result.setHasProperties(lookupProperties(concept_id, null, null, null, null, null));

            // get the source for
            ArrayList sourceFor = new ArrayList();

            getSourceFor = queries_.checkOutStatement(queries_.GET_SOURCE_FOR_ALL);
            getSourceFor.setString(1, codeSystemName);
            getSourceFor.setString(2, concept_id.getConcept_code());

            queryResults = getSourceFor.executeQuery();
            while (queryResults.next())
            {
                ConceptRelationship temp = new ConceptRelationship();
                temp.setRelationship_code(queryResults.getString("association"));
                temp.setSourceConcept_id(concept_id);

                ConceptId tempConceptId = new ConceptId();
                tempConceptId.setCodeSystem_id(roi_.getIdForRelationshipCodeSystemName(codeSystemName, queryResults
                        .getString("targetCodingSchemeName")));
                tempConceptId.setConcept_code(queryResults.getString("targetConceptCode"));
                temp.setTargetConcept_id(tempConceptId);
                sourceFor.add(temp);
            }

            result.setSourceFor((ConceptRelationship[]) sourceFor
                    .toArray(new ConceptRelationship[sourceFor.size()]));
            
            //don't hold open
            queries_.checkInPreparedStatement(getSourceFor);

            // get the target of
            ArrayList targetOf = new ArrayList();

            getTargetOf = queries_.checkOutStatement(queries_.GET_TARGET_OF_ALL);
            getTargetOf.setString(1, codeSystemName);
            getTargetOf.setString(2, concept_id.getConcept_code());

            queryResults = getTargetOf.executeQuery();
            while (queryResults.next())
            {
                String sourceConceptCode = queryResults.getString("sourceConceptCode");
                if (sourceConceptCode.equals(rootRelationNode_))
                {
                    //Don't return this node
                    continue;
                }
                ConceptRelationship temp = new ConceptRelationship();
                temp.setRelationship_code(queryResults.getString("association"));
                ConceptId tempTargetConceptId = new ConceptId();
                tempTargetConceptId.setCodeSystem_id(roi_.getIdForRelationshipCodeSystemName(codeSystemName, queryResults
                        .getString("targetCodingSchemeName")));
                tempTargetConceptId.setConcept_code(concept_id.getConcept_code());
                temp.setTargetConcept_id(tempTargetConceptId);

                ConceptId tempConceptId = new ConceptId();
                tempConceptId.setCodeSystem_id(concept_id.getCodeSystem_id());
                tempConceptId.setConcept_code(sourceConceptCode);
                temp.setSourceConcept_id(tempConceptId);
                targetOf.add(temp);
            }

            result.setTargetOf((ConceptRelationship[]) targetOf
                    .toArray(new ConceptRelationship[targetOf.size()]));

            return result;
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
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getCodeDetails);
            queries_.checkInPreparedStatement(getTargetOf);
            queries_.checkInPreparedStatement(getSourceFor);
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

        long startTime = System.currentTimeMillis();
        Hashtable resultsToReturn = new Hashtable();
        PreparedStatement getCodesByDesignation = null;
        try
        {
            String codeSystemName = roi_.getNameForCodeSystemId(codeSystem_id);

            if (language_code != null && language_code.length() != 0)
            {
                roi_.validateLanguageCode(codeSystemName, language_code);
            }

            StringBuffer query = new StringBuffer();

            query.append("SELECT conceptProperty.conceptCode");
            query.append(" FROM conceptProperty");
            if (activeConceptsOnly)
            {
                query.append(" INNER JOIN codedEntry");
                query.append(" ON conceptProperty.conceptCode = codedEntry.conceptCode");
                query.append(" AND conceptProperty.codingSchemeName = codedEntry.codingSchemeName");
            }
            query.append(" WHERE conceptProperty.codingSchemeName=?");
            query.append(" AND conceptProperty.property='textualPresentation'");
            
            if (matchText != null && matchText.length() > 0)
            {
                query.append(" AND conceptProperty.propertyText {LIKE} ?");
            }
            
            if (language_code != null && language_code.length() != 0)
            {
                if (language_code.equals(roi_.getDefaultLanguage(codeSystemName)))
                {
                    // default language is not required in the tables
                    query
                            .append(" AND (conceptProperty.language {LIKE} ? OR conceptProperty.language is null OR conceptProperty.language='')");
                }
                else
                {
                    query.append(" AND conceptProperty.language {LIKE} ?");
                }
            }
            if (activeConceptsOnly)
            {
                query.append(" AND codedEntry.isActive <> {false}");
            }

            getCodesByDesignation = queries_.getArbitraryStatement(queries_.modifySQL(query.toString()));

            setTimeout(getCodesByDesignation, timeout);
            getCodesByDesignation.setMaxRows(sizeLimit* 5);  //make it bigger, because it will usually match on multiple 
            //designations per concept, and I will end up returning less concepts than the limit requested in that case.


            int i = 1;
            getCodesByDesignation.setString(i++, codeSystemName);
            if (matchText != null && matchText.length() > 0)
            {
                getCodesByDesignation.setString(i++, wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code));
            }
            if (language_code != null && language_code.length() != 0)
            {
                //DB2 case sensitivity issues require this
                if (queries_.isDB2())
                {
                    getCodesByDesignation.setString(i++, language_code.toUpperCase() + "%");
                }
                else
                {
                    getCodesByDesignation.setString(i++, language_code + "%");
                }
            }

            ResultSet results = getCodesByDesignation.executeQuery();
            while (results.next())
            {
                if (sizeLimit != 0 && resultsToReturn.size() == sizeLimit)
                {
                    break;
                }
                ConceptId tempConceptId = new ConceptId();
                tempConceptId.setCodeSystem_id(codeSystem_id);
                tempConceptId.setConcept_code(results.getString("conceptCode"));
                if (tempConceptId.getConcept_code().equals(this.rootRelationNode_))
                {
                    // skip root relation node
                    continue;
                }

                resultsToReturn.put(tempConceptId.getConcept_code(), tempConceptId);
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
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            // throw new BadlyFormedMatchText(matchText.getV());
            // TODO throw badly formed match text when necessary
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (Exception e)
        {
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getCodesByDesignation);
        }

        // using a hashtable so I don't return duplicates
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

        long startTime = System.currentTimeMillis();
        Hashtable resultsToReturn = new Hashtable();
        PreparedStatement getCodesByProperty = null;
        try
        {
            String codeSystemName = roi_.getNameForCodeSystemId(codeSystem_id);

            if (language_code != null && language_code.length() != 0)
            {
                roi_.validateLanguageCode(codeSystemName, language_code);
            }

            if (properties != null)
            {
                for (int i = 0; i < properties.length; i++)
                {
                    if (!validAttribute(codeSystemName, "Property", properties[i]))
                    {
                        throw new UnknownPropertyCode(properties[i]);
                    }
                }
            }
            if (mimeTypes != null)
            {
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    if (!validAttribute(codeSystemName, "Format", mimeTypes[i]))
                    {
                        throw new UnknownMimeTypeCode(mimeTypes[i]);
                    }
                }
            }

            StringBuffer query = new StringBuffer();

            query.append("SELECT conceptProperty.conceptCode FROM conceptProperty");
            if (activeConceptsOnly)
            {
                query
                        .append(" INNER JOIN codedEntry ON conceptProperty.conceptCode = codedEntry.conceptCode AND conceptProperty.codingSchemeName = codedEntry.codingSchemeName");
            }
            query.append(" WHERE conceptProperty.codingSchemeName = ?");
            if (matchText != null && matchText.length() > 0)
            {
                query.append(" AND conceptProperty.propertyText {LIKE} ?");
            }
            if (language_code != null && language_code.length() != 0)
            {
                if (language_code.equals(roi_.getDefaultLanguage(codeSystemName)))
                {
                    // if it is the default language, it is not required in the database.
                    query
                            .append(" AND (conceptProperty.language {LIKE} ? OR conceptProperty.language is null OR conceptProperty.language='')");
                }
                else
                {
                    query.append(" AND conceptProperty.language {LIKE} ?");
                }
            }
            if (activeConceptsOnly)
            {
                query.append(" AND codedEntry.isActive <> {false}");
            }
            if (mimeTypes != null && mimeTypes.length != 0)
            {
                query.append(" AND (");
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    query.append("conceptProperty.format=?");
                    if (i + 1 < mimeTypes.length)
                    {
                        query.append(" OR ");
                    }
                }
                query.append(")");
            }
            if (properties != null && properties.length != 0)
            {
                query.append(" AND (");
                for (int i = 0; i < properties.length; i++)
                {
                    query.append("conceptProperty.property=?");
                    if (i + 1 < properties.length)
                    {
                        query.append(" OR ");
                    }
                }
                query.append(")");
            }

            getCodesByProperty = queries_.getArbitraryStatement(queries_.modifySQL(query.toString()));

            // set the parameters - these if statements need to match the above precisely...
            int i = 1;
            getCodesByProperty.setString(i++, codeSystemName);
            if (matchText != null && matchText.length() > 0)
            {
                getCodesByProperty.setString(i++, wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code));
            }
            if (language_code != null && language_code.length() != 0)
            {
                //DB2 case sensitivity issues require this
                if (queries_.isDB2())
                {
                    getCodesByProperty.setString(i++, language_code.toUpperCase() + "%");
                }
                else
                {
                    getCodesByProperty.setString(i++, language_code + "%");
                }
            }
            if (mimeTypes != null && mimeTypes.length != 0)
            {
                for (int j = 0; j < mimeTypes.length; j++)
                {
                    getCodesByProperty.setString(i++, mimeTypes[j]);
                }
            }
            if (properties != null && properties.length != 0)
            {
                for (int j = 0; j < properties.length; j++)
                {
                    getCodesByProperty.setString(i++, properties[j]);
                }
            }

            setTimeout(getCodesByProperty, timeout);
            getCodesByProperty.setMaxRows(sizeLimit* 5);  //make it bigger, because it will usually match on multiple 
            //designations per concept, and I will end up returning less concepts than the limit requested in that case.


            ResultSet results = getCodesByProperty.executeQuery();
            while (results.next())
            {
                if (sizeLimit != 0 && resultsToReturn.size() == sizeLimit)
                {
                    break;
                }
                ConceptId tempConceptId = new ConceptId();
                tempConceptId.setCodeSystem_id(codeSystem_id);
                tempConceptId.setConcept_code(results.getString("conceptCode"));
                if (tempConceptId.getConcept_code().equals(this.rootRelationNode_))
                {
                    // skip root relation node
                    continue;
                }

                resultsToReturn.put(tempConceptId.getConcept_code(), tempConceptId);
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            // throw new BadlyFormedMatchText(matchText.getV());
            // TODO throw badly formed match text when necessary
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (UnknownMimeTypeCode e)
        {
            throw e;
        }
        catch (UnknownPropertyCode e)
        {
            throw e;
        }
        catch (UnexpectedError e)
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
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getCodesByProperty);
        }

        // using a hashtable so I don't return duplicates
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

        PreparedStatement getDesignations = null;
        ArrayList resultsToReturn = new ArrayList();

        try
        {
            if (conceptCode == null)
            {
                throw new UnknownCodeSystem("null");
            }

            String codeSystemName = roi_.getNameForCodeSystemId(conceptCode.getCodeSystem_id());
            
            if (conceptCode.getConcept_code() == null || conceptCode.getConcept_code().length() == 0)
            {
                throw new UnknownConceptCode("null");
            }

            if (languageCode != null && languageCode.length() != 0)
            {
                roi_.validateLanguageCode(codeSystemName, languageCode);
            }

            StringBuffer query = new StringBuffer(
                    "SELECT conceptProperty.language, conceptProperty.propertyText, conceptProperty.isPreferred"
                            + " FROM conceptProperty"
                            + " WHERE conceptProperty.codingSchemeName=?"
                            + " AND conceptProperty.conceptCode=?"
                            + " AND conceptProperty.property='textualPresentation'");
            if (languageCode != null && languageCode.length() != 0)
            {
                if (languageCode.equals(roi_.getDefaultLanguage(codeSystemName)))
                {
                    query
                            .append(" AND (conceptProperty.language=? OR conceptProperty.language is null OR conceptProperty.language='')");
                }
                else
                {
                    query.append(" AND conceptProperty.language=?");
                }
            }

            if (matchText != null && matchText.length() > 0)
            {
                query.append(" AND conceptProperty.propertyText {LIKE} ?");
            }

            getDesignations = queries_.getArbitraryStatement(queries_.modifySQL(query.toString()));
            int i = 1;
            getDesignations.setString(i++, codeSystemName);
            getDesignations.setString(i++, conceptCode.getConcept_code());
            if (languageCode != null && languageCode.length() != 0)
            {
                getDesignations.setString(i++, languageCode);
            }
            if (matchText != null && matchText.length() > 0)
            {
                getDesignations.setString(i++, wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code));
            }

            ResultSet results = getDesignations.executeQuery();

            while (results.next())
            {
                ConceptDesignation conceptDesignation = new ConceptDesignation();
                String language = results.getString("language");
                if (language == null || language.length() == 0)
                {
                    language = roi_.getDefaultLanguage(codeSystemName);
                }
                String propertyText = results.getString("propertyText");
                boolean isPreferred = SQLStatements.getBooleanResult(results, "isPreferred"); 

                conceptDesignation.setDesignation(propertyText);
                conceptDesignation.setLanguage_code(language);
                conceptDesignation.setPreferredForLanguage(isPreferred);

                resultsToReturn.add(conceptDesignation);
            }

            if (resultsToReturn.size() == 0)
            {
                if (!roi_.isConceptIdValid(conceptCode, false))
                {
                    throw new UnknownConceptCode(conceptCode.getConcept_code());
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            // throw new BadlyFormedMatchText(matchText.getV());
            // TODO throw badly formed match text when necessary
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
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
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getDesignations);
        }

        return (ConceptDesignation[]) resultsToReturn.toArray(new ConceptDesignation[resultsToReturn.size()]);
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

        ArrayList resultsToReturn = new ArrayList();
        PreparedStatement getProperties = null;
        try
        {
            if (concept_id == null)
            {
                throw new UnknownCodeSystem("null");
            }
            String codeSystemName = roi_.getNameForCodeSystemId(concept_id.getCodeSystem_id());
            
            if (concept_id.getConcept_code() == null || concept_id.getConcept_code().length() == 0)
            {
                throw new UnknownConceptCode("null");
            }

            if (language_code != null && language_code.length() != 0)
            {
                roi_.validateLanguageCode(codeSystemName, language_code);
            }

            if (properties != null)
            {
                for (int i = 0; i < properties.length; i++)
                {
                    if (!validAttribute(codeSystemName, "Property", properties[i]))
                    {
                        throw new UnknownPropertyCode(properties[i]);
                    }
                }
            }
            if (mimeTypes != null)
            {
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    if (!validAttribute(codeSystemName, "Format", mimeTypes[i]))
                    {
                        throw new UnknownMimeTypeCode(mimeTypes[i]);
                    }
                }
            }

            // build the query
            StringBuffer query = new StringBuffer();
            query
                    .append("SELECT conceptProperty.property, conceptProperty.propertyText, conceptProperty.language, conceptProperty.format");
            query.append(" FROM conceptProperty");
            query.append(" WHERE conceptProperty.codingSchemeName=?");
            query.append(" AND conceptProperty.conceptCode=?");
            if (properties != null && properties.length > 0)
            {
                query.append(" AND (");
                for (int i = 0; i < properties.length; i++)
                {
                    query.append(" conceptProperty.property=?");
                    if (i + 1 < properties.length)
                    {
                        query.append(" OR");
                    }
                }
                query.append(")");
            }

            if (matchText != null && matchText.length() > 0)
            {
                query.append(" AND conceptProperty.propertyText {LIKE} ?");
            }

            if (language_code != null && language_code.length() != 0)
            {
                if (language_code.equals(roi_.getDefaultLanguage(codeSystemName)))
                {
                    query
                            .append(" AND (conceptProperty.language=? OR conceptProperty.language is null OR conceptProperty.language='')");
                }
                else
                {
                    query.append(" AND conceptProperty.language=?");
                }
            }

            if (mimeTypes != null && mimeTypes.length > 0)
            {
                query.append(" AND (");
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    query.append(" conceptProperty.format=?");
                    if (i + 1 < mimeTypes.length)
                    {
                        query.append(" OR");
                    }
                }
                query.append(")");
            }

            // set the parameters
            logger.debug(query.toString());
            getProperties = queries_.getArbitraryStatement(queries_.modifySQL(query.toString()));
            int j = 1;
            getProperties.setString(j++, codeSystemName);
            getProperties.setString(j++, concept_id.getConcept_code());
            if (properties != null)
            {
                for (int i = 0; i < properties.length; i++)
                {
                    getProperties.setString(j++, properties[i]);
                }
            }
            if (matchText != null && matchText.length() > 0)
            {
                getProperties.setString(j++, wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code));
            }

            if (language_code != null && language_code.length() != 0)
            {
                getProperties.setString(j++, language_code);
            }

            if (mimeTypes != null)
            {
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    getProperties.setString(j++, mimeTypes[i]);
                }
            }

            ResultSet results = getProperties.executeQuery();

            while (results.next())
            {
                ConceptProperty conceptProperty = new ConceptProperty();
                conceptProperty.setLanguage_code(results.getString("language"));
                if (conceptProperty.getLanguage_code() == null || conceptProperty.getLanguage_code().length() == 0)
                {
                    conceptProperty.setLanguage_code(roi_.getDefaultLanguage(codeSystemName));
                }
                conceptProperty.setMimeType_code(results.getString("format"));
                conceptProperty.setProperty_code(results.getString("property"));
                conceptProperty.setPropertyValue(results.getString("propertyText"));

                resultsToReturn.add(conceptProperty);
            }
            if (resultsToReturn.size() == 0)
            {
                if (!roi_.isConceptIdValid(concept_id, false))
                {
                    throw new UnknownConceptCode(concept_id.getConcept_code());
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            // throw new BadlyFormedMatchText(matchText.getV());
            // TODO throw badly formed match text when necessary
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (UnexpectedError e)
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
        catch (UnknownPropertyCode e)
        {
            throw e;
        }
        catch (UnknownMimeTypeCode e)
        {
            throw e;
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getProperties);
        }
        return (ConceptProperty[]) resultsToReturn.toArray(new ConceptProperty[resultsToReturn.size()]);
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
            return "This is the Mayo Implementation of the CTS " + CTSConstants.MAJOR_VERSION + "." + CTSConstants.MINOR_VERSION + " Final Draft, against the SQL lite backend.";
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
            return "Mayo CTS Vapi SQLLite Browser";
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

    private String wrapSearchStringForAlgorithm(String searchString, String algorithm) throws UnknownMatchAlgorithm
    {
        if (algorithm == null || algorithm.length() == 0)
        {
            return searchString;
        }
        
        String temp = searchString;
        //DB2 case insensitive queries require uppercasing 
        if (queries_.isDB2())
        {
            temp = temp.toUpperCase();
        }
        
        if (algorithm.equals("IdenticalIgnoreCase"))
        {
            return temp;
        }
        else if (algorithm.equals("StartsWithIgnoreCase"))
        {
            return temp + "%";
        }
        else if (algorithm.equals("EndsWithIgnoreCase"))
        {
            return "%" + temp;
        }
        else if (algorithm.equals("ContainsPhraseIgnoreCase"))
        {
            return "%" + temp + "%";
        }
        else
        {
            throw new UnknownMatchAlgorithm(algorithm);
        }
    }

    private void setTimeout(PreparedStatement statement, int timeout)
    {
        try
        {
            int temp = timeout / 1000;
            if (temp <= 0 && timeout > 0)
            {
                // the min timeout on a sql query is one second. And it takes seconds, not milliseconds.
                temp = 1;
            }
            statement.setQueryTimeout(temp);
        }
        catch (SQLException e1)
        {
            logger.info("Timeout not implemented on database");
        }
    }

    private void timeup(long startTime, long timeout) throws TimeoutError
    {
        if (timeout == 0)
        {
            return;
        }
        if (System.currentTimeMillis() - timeout > startTime)
        {
            throw new TimeoutError();
        }

    }

    private boolean validAttribute(String codingScheme, String attributeName, String attributeValue)
            throws UnexpectedError
    {
        PreparedStatement isAttributeValid = null;
        try
        {
            isAttributeValid = queries_.checkOutStatement(queries_.IS_PROPERTY_VALID);
            isAttributeValid.setString(1, codingScheme);
            isAttributeValid.setString(2, attributeName);
            isAttributeValid.setString(3, attributeValue);

            ResultSet results = isAttributeValid.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(isAttributeValid);
        }
    }
}