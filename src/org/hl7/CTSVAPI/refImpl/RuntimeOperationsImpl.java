package org.hl7.CTSVAPI.refImpl;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.TimeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.hl7.CTSVAPI.CTSVersionId;
import org.hl7.CTSVAPI.CodeSystemIdAndVersions;
import org.hl7.CTSVAPI.CodeSystemInfo;
import org.hl7.CTSVAPI.CodeSystemNameIdMismatch;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.NoApplicableDesignationFound;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.StringAndLanguage;
import org.hl7.CTSVAPI.TimeoutError;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownConceptCode;
import org.hl7.CTSVAPI.UnknownLanguageCode;
import org.hl7.CTSVAPI.UnknownRelationQualifier;
import org.hl7.CTSVAPI.UnknownRelationshipCode;
import org.hl7.utility.CTSConstants;
import org.hl7.utility.ObjectCache;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Utility;
import edu.mayo.informatics.cts.utility.Utility.LDAPConnectionInfo;
import edu.mayo.informatics.cts.utility.lexGrid.SchemaConstants;
import edu.mayo.informatics.lexgrid.convert.utility.DBUtility;
import edu.mayo.mir.utility.AutoReconnectDirContext;
import edu.mayo.mir.utility.StringArrayUtility;
import edu.mayo.mir.utility.parameter.IntParameter;

/**
 * <pre>
 * Title:        RuntimeOperationsImpl
 * Description:  A reference implementation of RuntimeOperationsImpl.
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
 * <H5>Bugs, to be done, notes, etc.</H5>
 * <UL>
 * <LI>There are still several todo items in this class.</li>
 * </UL>
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust </A>
 * @version 1.0 - cvs $Revision: 1.65 $ checked in on $Date: 2005/10/14 15:44:11 $
 */

public class RuntimeOperationsImpl implements RuntimeOperations
{
    private AutoReconnectDirContext  codingSchemesContext_;
    private SearchControls           searchControls;

    private ObjectCache              objectCache_                = ObjectCache.instance();

    public static final IntParameter MAX_CODES_RELATED_RECURSION = new IntParameter(10);

    public final static Logger       logger                      = Logger.getLogger("org.hl7.VAPI_Runtime");

    /**
     * Create a new RuntimeOperations object using the current values of CTSConstants.
     * @return
     * @throws UnexpectedError
     */
    static public synchronized RuntimeOperations _interface() throws UnexpectedError

    {
        RuntimeOperationsImpl svc = new RuntimeOperationsImpl(null, null, null, null, false, false);
        return (RuntimeOperations) svc;
    }
    
    /**
     * Return a reference to a new RuntimeOperations.
     * 
     * @param userName - the name to use to connect to the service - if null, loaded from properties
     * @param userPassword - the password used to connect to the service - if null, loaded from properties
     * @param address - The address of the service to connect to - if null, loaded from properties
     * @param service - The Service to connect to - if null, loaded from properties
     * @return reference or null if unable to construct it
     * @throws NamingException - if unable to connect to service
     */
    static public synchronized RuntimeOperations _interface(String userName, String userPassword, String address,
            String service, boolean loadProperties, boolean initLogger) throws UnexpectedError

    {
        RuntimeOperationsImpl svc = new RuntimeOperationsImpl(userName, userPassword, address, service, loadProperties, initLogger);
        return (RuntimeOperations) svc;
    }

    /**
     * Return a reference to a new Runtime Operations. Parses the connection information from an XML string.
     * 
     * @param xmlLexGridLDAPString The XML String.
     * @param initLogger whether or not to init the logger.
     * @return reference or null if unable to construct it
     * @throws UnexpectedError if something goes wrong.
     */
    static public synchronized RuntimeOperations _interface(String xmlLexGridLDAPString, String username,
            String password, boolean loadProperties, boolean initLogger) throws UnexpectedError

    {
        RuntimeOperationsImpl svc;
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
            svc = new RuntimeOperationsImpl(username, password, temp.address, temp.service, loadProperties, initLogger);
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

        return (RuntimeOperations) svc;
    }

    /**
     * This method is only public for things like the SOAP server. You should use the static _interface method to get a
     * RuntimeOperationsImpl.
     *  
     */
    public RuntimeOperationsImpl()
    {
        // Initialize the service manager
        try
        {
            CTSConfigurator._instance().initialize();

            this.codingSchemesContext_ = initContext(null, null, null, null);
            searchControls = new SearchControls();
            //this.nameParser =
            // context.getNameParser(context.getNameInNamespace());
        }
        catch (Exception e)
        {
            logger.error("Constructor error", e);
            //System.exit(0);
        }
    }

    protected RuntimeOperationsImpl(String userName, String userPassword, String address, String service,
            boolean loadProperties, boolean initLogger) throws UnexpectedError
    {
        // Initialize the service manager
        try
        {
            if (loadProperties)
            {
                CTSConfigurator._instance().initialize(initLogger);
            }
            this.codingSchemesContext_ = initContext(userName, userPassword, address, service);
            searchControls = new SearchControls();
            //this.nameParser =
            // context.getNameParser(context.getNameInNamespace());
        }
        catch (Exception e)
        {
            logger.error("Constructor error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceName() throws UnexpectedError
    {
        logger.debug("getServicename called.");
        try
        {
            return "Mayo CTS Vapi (Ldap) Runtime";
        }
        catch (Exception e)
        {
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
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public CTSVersionId getCTSVersion() throws UnexpectedError
    {
        logger.debug("getCTSVersion Called");
        try
        {
            CTSVersionId verID = new CTSVersionId();
            verID.setMajor(Short.parseShort(CTSConstants.MAJOR_VERSION));
            verID.setMinor(Short.parseShort(CTSConstants.MINOR_VERSION));
            return verID;
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceDescription() throws UnexpectedError
    {
        logger.debug("getServiceDescription called.");
        try
        {
            return "This is the Mayo Implementation of the CTS " + CTSConstants.MAJOR_VERSION + "." + CTSConstants.MINOR_VERSION + " Final Draft running against LDAP.";
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public CodeSystemInfo lookupCodeSystemInfo(String codeSystem_id, String codeSystem_name) throws UnexpectedError,
            CodeSystemNameIdMismatch, UnknownCodeSystem
    {
        try
        {
            ArrayList temp = new ArrayList();

            String name = "";
            String filter = "";

            setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                    .intValue(), 1, false, new String[]{"localName", "codingScheme", "formalName",
                    SchemaConstants.entityDescription, "releaseId", "supportedLanguage", "supportedProperty",
                    "supportedAssociation", "supportedFormat", "copyright", "supportedAssociationQualifier", "registeredName"});

            SearchResult sRes;

            if (codeSystem_id != null && codeSystem_id.length() > 0)
            {
                name = "";
                filter = "(&(objectClass=codingSchemeClass)(|(registeredName=urn:iso:" + codeSystem_id + ")(localName=" + codeSystem_id + ")))";

                NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);
                if (!results.hasMore())
                    throw new UnknownCodeSystem(codeSystem_id);

                sRes = (SearchResult) results.next();
            }

            else
            {
                name = "";
                filter = "(&(objectClass=codingSchemeClass)(codingScheme=" + codeSystem_name + "))";

                NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);

                if (!results.hasMore())
                    throw new UnknownCodeSystem();

                sRes = (SearchResult) results.next();

            }

            Attributes attributes = sRes.getAttributes();

            String _id = this.getIdForCodeSystem(attributes);
            String _name = (String) attributes.get("codingScheme").get();
            String _copyright = (attributes.get("copyright") != null ? (String) attributes.get("copyright").get()
                    : "");

            if (codeSystem_name != null && codeSystem_name.length() != 0 && !_name.equalsIgnoreCase(codeSystem_name))
            {
                throw new CodeSystemNameIdMismatch(codeSystem_id, codeSystem_name);
            }
            if (codeSystem_id != null && codeSystem_id.length() != 0 && !_id.equalsIgnoreCase(codeSystem_id))
            {
                throw new CodeSystemNameIdMismatch(codeSystem_id, codeSystem_name);
            }

            String _fullname = attributes.get("formalName") == null ? ""
                    : (String) attributes.get("formalName").get();
            String _description = (attributes.get(SchemaConstants.entityDescription) == null ? ""
                    : (String) attributes.get(SchemaConstants.entityDescription).get());

            //Get the languages
            NamingEnumeration languages = attributes.get("supportedLanguage").getAll();
            temp.clear();
            while (languages.hasMore())
            {
                temp.add(removeURN((String) languages.next()));
            }
            String[] _languages = (String[]) temp.toArray(new String[temp.size()]);
            
            String ldapName = sRes.getName();
            if (ldapName.startsWith("\"") && ldapName.endsWith("\""))
            {
                //there is a strange case, where the ldapName contains the "/" character, 
                //the returning string is encased in '"' characters.
                ldapName = ldapName.substring(1, ldapName.length() - 1);
            }
            
            String defaultLanguage = getDefaultLanguage(DBUtility.escapeLdapDN(ldapName));
            
            //move the default language to the front of the list if it is not there.
            if (!_languages[0].equals(defaultLanguage))
            {
                String tempLang = _languages[0];
                _languages[0] = defaultLanguage;
                for (int i = 1; i < _languages.length; i++)
                {
                    if (_languages[i].equals(defaultLanguage))
                    {
                        _languages[i] = tempLang;
                        break;
                    }
                }
            }

            //Get the relations
            temp.clear();
            Attribute tempAssociation = attributes.get("supportedAssociation");
            if (tempAssociation != null)
            {
                NamingEnumeration relations = tempAssociation.getAll();

                while (relations.hasMore())
                {
                    String relation = (String) relations.next();
                    temp.add(removeURN(relation));
                }
            }
            String[] _relations = (String[]) temp.toArray(new String[temp.size()]);

            //Get the properties
            NamingEnumeration properties = attributes.get("supportedProperty").getAll();
            temp.clear();
            while (properties.hasMore())
            {
                temp.add(removeURN((String) properties.next()));
            }
            String[] _properties = (String[]) temp.toArray(new String[temp.size()]);

            //get the release versions
            filter = "(objectClass=codingSchemeVersion)";
            name = "dc=Versions, codingScheme=" + _name;
            temp.clear();

            try
            {
                NamingEnumeration versions = codingSchemesContext_.search(name, filter, searchControls);
                while (versions.hasMore())
                {
                    SearchResult innerSearchResult = (SearchResult) versions.next();
                    temp.add(innerSearchResult.getName().substring("codingSchemeVersion=".length()));
                }
            }
            catch (Exception ex)
            {
                //              no versions
            }
            String[] _revisedinreleases = (String[]) temp.toArray(new String[temp.size()]);

            //mime types
            NamingEnumeration mimeTypes = attributes.get("supportedFormat").getAll();
            temp.clear();
            while (mimeTypes.hasMore())
            {
                temp.add(removeURN((String) mimeTypes.next()));
            }
            String[] _mimeTypes = (String[]) temp.toArray(new String[temp.size()]);

            //          Get the relation Qualifiers
            temp.clear();
            tempAssociation = attributes.get("supportedAssociationQualifier");
            if (tempAssociation != null)
            {
                NamingEnumeration relations = tempAssociation.getAll();

                while (relations.hasMore())
                {
                    String relation = (String) relations.next();
                    temp.add(removeURN(relation));
                }
            }
            String[] _relationQualifiers = (String[]) temp.toArray(new String[temp.size()]);

            CodeSystemIdAndVersions codeSystemIdAndVersions = new CodeSystemIdAndVersions();
            codeSystemIdAndVersions.setCodeSystem_id(_id);
            codeSystemIdAndVersions.setCodeSystem_name(_name);
            codeSystemIdAndVersions.setCodeSystem_versions(_revisedinreleases);
            codeSystemIdAndVersions.setCopyright(_copyright);
            CodeSystemInfo theItem = new CodeSystemInfo();
            theItem.setCodeSystem(codeSystemIdAndVersions);
            theItem.setFullName(_fullname);
            theItem.setCodeSystemDescription(_description);
            theItem.setSupportedLanguages(_languages);

            theItem.setSupportedRelations(_relations);
            theItem.setSupportedRelationQualifiers(_relationQualifiers);
            theItem.setSupportedProperties(_properties);
            theItem.setSupportedMimeTypes(_mimeTypes);

            return theItem;

        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (NamingException e)
        {
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public CodeSystemIdAndVersions[] getSupportedCodeSystems(int timeout, int sizeLimit) throws TimeoutError,
            UnexpectedError
    {
        logger.debug("supportedSystems called - timeout: " + timeout + " sizeLimit: " + sizeLimit);

        String filter = "(objectClass=codingSchemeClass)";
        String name = "";

        setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, timeout, sizeLimit, false, new String[]{
                "localName", "representsVersion", "codingScheme", "language", "copyright", "registeredName"});

        ArrayList resultsToReturn = new ArrayList();

        try
        {
            NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);
            while (results.hasMore())
            {
                CodeSystemIdAndVersions codeSystemIdAndVersions = new CodeSystemIdAndVersions();

                SearchResult result = (SearchResult) results.next();
                Attributes attributes = result.getAttributes();

                if (attributes.get("codingScheme") != null)
                {
                    codeSystemIdAndVersions.setCodeSystem_name((String) attributes.get("codingScheme").get());
                }
                else
                {
                    codeSystemIdAndVersions.setCodeSystem_name("ERROR: Coding Scheme MISSING!!");
                }

                codeSystemIdAndVersions.setCodeSystem_id(getIdForCodeSystem(attributes));

                codeSystemIdAndVersions.setCopyright((attributes.get("copyright") != null ? (String) attributes
                        .get("copyright").get()
                        : ""));

                if (attributes.get("representsVersion") != null)
                {
                    codeSystemIdAndVersions.setCodeSystem_versions(
                            new String[]{((String) attributes.get("representsVersion").get())});
                }
                else
                {
                    codeSystemIdAndVersions.setCodeSystem_versions(new String[]{});
                }
                resultsToReturn.add(codeSystemIdAndVersions);
            }
        }
        catch (SizeLimitExceededException e)
        {
            //do nothing
            logger.warn(e);
        }
        catch (TimeLimitExceededException e)
        {
            logger.error("The search exceeded the allotted time", e);
            throw new TimeoutError();
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return (CodeSystemIdAndVersions[]) resultsToReturn.toArray(new CodeSystemIdAndVersions[resultsToReturn.size()]);

    }

    public boolean isConceptIdValid(ConceptId concept_id, boolean activeConceptsOnly) throws UnknownCodeSystem,
            UnexpectedError
    {
        try
        {
            String name = getLDAPNameForCodingSchemeId(concept_id.getCodeSystem_id());
            name = "conceptCode=" + concept_id.getConcept_code() + ", dc=Concepts, " + name;
            Attributes attributes = codingSchemesContext_.getAttributes(name, new String[]{"isActive"});
            if (activeConceptsOnly)
            {
                if (attributes.get("isActive") == null)
                    return true;

                String active = (String) attributes.get("isActive").get();
                if (!active.equalsIgnoreCase("true"))
                    return false;
            }
            return true;

        }
        catch (NamingException e)
        {
            return false;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public StringAndLanguage lookupDesignation(ConceptId concept_id, String language_code)
            throws org.hl7.CTSVAPI.UnknownConceptCode, org.hl7.CTSVAPI.UnknownCodeSystem,
            org.hl7.CTSVAPI.UnknownLanguageCode, NoApplicableDesignationFound, UnexpectedError
    {
        try
        {
            LanguageHelperReturn temp = languageHelper(concept_id.getCodeSystem_id(), language_code);

            return lookupDesignationQuick(temp.codeSystemLdapName, concept_id, temp.language, temp.defaultLanguage);
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (NoApplicableDesignationFound e)
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

    /*
     * This implementes lookupDesignation, but it doesn't validate the language or coding scheme. This is used so
     * BrowserOperations methods that call lookupDesignations a lot don't have to waste time validating pieces that are
     * know to be valid.
     */
    protected StringAndLanguage lookupDesignationQuick(String name, ConceptId concept_id, String language,
            String defaultLanguage) throws UnknownConceptCode, NoApplicableDesignationFound, UnexpectedError
    {
        try
        {
            String filter = "";

            name = "conceptCode=" + DBUtility.escapeLdapCode(concept_id.getConcept_code()) + ", dc=Concepts, " + name;

            language += "-";
            // language codes may have subparts... if we don't match on the full
            // code, strip off everything after the last hyphen, and try again.

            StringAndLanguage stringAndLanguage = new StringAndLanguage();
            boolean notFound = true;
            while (notFound)
            {
                if (language.indexOf("-") == -1)
                {
                    throw new NoApplicableDesignationFound(concept_id, language);
                }
                language = language.substring(0, language.lastIndexOf("-"));

                String languageFilterPart = "";
                if ((language != null) && (language.length()) > 0 && !(language.equals(defaultLanguage)))
                {
                    //language specified other than default
                    languageFilterPart = "(language=" + language + ")";
                }
                else if ((language != null) && (language.length()) > 0 && (language.equals(defaultLanguage)))
                { //specifed default language
                    languageFilterPart = "(|(!(language=*))(language=" + defaultLanguage + "))";
                }

                try
                {
                    //not specifying object class for performance reasons... queries that specify the object class
                    //are at least 3 times slower than queries that don't specify the object class.
                    filter = languageFilterPart;
                    setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, 0, 0, false, new String[]{"text",
                            "language", "property", "isPreferred"});

                    NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);

                    String bestValue = null;
                    String bestLang = null;
                    
                    while (results.hasMore())
                    {
                        Attributes attributes = ((SearchResult) results.next()).getAttributes();
                        Attribute property = attributes.get("property");
                        if (property != null && ((String) property.get()).equals("textualPresentation"))
                        {
                            String currentValue = (String) attributes.get("text").get();
                            Attribute temp = (Attribute) attributes.get("language");
                            String currentLanguage = temp == null ? "" : (String) temp.get();
                            if (currentLanguage == null || currentLanguage.length() == 0)
                            {
                                currentLanguage = defaultLanguage;
                            }
                            
                            temp = (Attribute)attributes.get("isPreferred");
                            boolean isPref = temp == null ? false : new Boolean((String)temp.get()).booleanValue();
                            
                            if (isPref)
                            {
                                stringAndLanguage.setText(currentValue);
                                stringAndLanguage.setLanguage_code(currentLanguage);
                                notFound = false;
                                break;
                            }
                            
                            if (bestValue == null || currentValue.compareToIgnoreCase(bestValue) < 0)
                            {
                                bestValue = currentValue;
                                bestLang = currentLanguage;
                            }
                        }
                    }
//                  if we found one above, return it.
                    if (bestValue != null)
                    {
                        stringAndLanguage.setText(bestValue);
                        stringAndLanguage.setLanguage_code(bestLang);
                        notFound = false;
                        break;
                    }

                }
                catch (NamingException e)
                {
                    throw new UnknownConceptCode(concept_id.getCodeSystem_id());
                }
            }
            return stringAndLanguage;
        }
        catch (NoApplicableDesignationFound e)
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
    }

    /**
     * This method takes in a codeSystemId, and a language. It validates the language, and the code system id. It then
     * returns the default language for the code system, and the ldap name of the code system.
     * 
     * @param codeSystemId
     * @param language
     * @return @throws UnknownCodeSystem
     * @throws UnknownLanguageCode
     */
    private LanguageHelperReturn languageHelper(String codeSystemId, String language) throws UnknownCodeSystem,
            UnknownLanguageCode, UnexpectedError
    {
        logger.debug("LanguageHelper called with codeSystemId - " + codeSystemId + " language - " + language);
        String name = "";
        String filter = "(&(objectClass=codingSchemeClass)(|(registeredName=urn:iso:" + codeSystemId + ")(localName=" + codeSystemId + ")))";
        String defaultLanguage = "";
        logger.debug("languageHelper filter - " + filter);

        try
        {
            
            setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                    .intValue(), 1, false, new String[]{"supportedLanguage", "defaultLanguage"});

            NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);
            if (!results.hasMore())
            {
                throw new UnknownCodeSystem(codeSystemId);
            }

            SearchResult sRes = (SearchResult) results.next();
            name = sRes.getName();
            
            if (name.startsWith("\"") && name.endsWith("\""))
            {
                //there is a strange case, where the ldapName contains the "/" character, 
                //the returning string is encased in '"' characters.
                name = name.substring(1, name.length() - 1);
            }
            
            name = DBUtility.escapeLdapDN(name);
            
            defaultLanguage = (String) sRes.getAttributes().get("defaultLanguage").get();
            
            if (language == null || language.length() == 0)
            {
                language = defaultLanguage;
            }
            
            NamingEnumeration languages = sRes.getAttributes().get("supportedLanguage").getAll();
            boolean foundLanguage = false;
            while (languages.hasMoreElements())
            {
                String currentLanguage = removeURN((String) languages.next());
                if (currentLanguage.equalsIgnoreCase(language))
                {
                    foundLanguage = true;
                    break;
                }
                else if (language.lastIndexOf("-") != -1)
                {
                    //chop off any extensions to the language, and try again
                    // for a match.
                    String temp = language;
                    while (temp.lastIndexOf("-") != -1)
                    {
                        temp = temp.substring(0, temp.lastIndexOf("-"));
                        if (currentLanguage.equalsIgnoreCase(temp))
                        {
                            foundLanguage = true;
                            break;
                        }
                    }
                    if (foundLanguage)
                    {
                        break;
                    }
                }

            }
            if (!foundLanguage)
            {
                throw new UnknownLanguageCode(language);
            }
        }
        catch (NamingException e)
        {
            logger.error(e);
            throw new UnknownCodeSystem(codeSystemId);
        }
        catch (UnknownLanguageCode e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        LanguageHelperReturn result = new LanguageHelperReturn();
        result.codeSystemLdapName = name;
        result.defaultLanguage = defaultLanguage;
        result.language = language;
        return result;
    }

    public boolean areCodesRelated(String codeSystem_id, String source_code, String target_code,
            String relationship_code, String[] relationQualifiers, boolean directRelationsOnly)
            throws UnknownRelationshipCode, UnknownCodeSystem, UnexpectedError, UnknownConceptCode,
            UnknownRelationQualifier

    {
        logger.debug("areCodesRelated called: codeSystem="
                + codeSystem_id
                + " sourceCode="
                + source_code
                + " targetCode="
                + target_code
                + "relationshipCode="
                + relationship_code
                + "relationshipQualifiers="
                + StringArrayUtility.stringArrayToString(relationQualifiers)
                + " directRelationsOnly="
                + directRelationsOnly);

        try
        {
            String name = getLDAPNameForCodingSchemeId(codeSystem_id);
            ArrayList validQualifiers = new ArrayList();
            Attributes attributes;

            ConceptId temp = new ConceptId();
            temp.setCodeSystem_id(codeSystem_id);
            temp.setConcept_code(source_code);

            if (!isConceptIdValid(temp, true))
            {
                throw new UnknownConceptCode(source_code);
            }

            temp.setConcept_code(target_code);
            if (!isConceptIdValid(temp, true))
            {
                throw new UnknownConceptCode(target_code);
            }

            if (relationQualifiers != null && relationQualifiers.length > 0)
            {
                attributes = codingSchemesContext_.getAttributes(name, new String[]{"supportedAssociationQualifier"});
                if (attributes.get("supportedAssociationQualifier") != null)
                {
                    NamingEnumeration qualifiers = attributes.get("supportedAssociationQualifier").getAll();
                    while (qualifiers.hasMoreElements())
                    {
                        validQualifiers.add(removeURN((String) qualifiers.nextElement()));
                    }
                }

                for (int i = 0; i < relationQualifiers.length; i++)
                {
                    boolean found = false;
                    for (int j = 0; j < validQualifiers.size(); j++)
                    {
                        if (relationQualifiers[i].equalsIgnoreCase((String) validQualifiers.get(j)))
                        {
                            found = true;
                            break;
                        }
                    }
                    if (found == false)
                    {
                        throw new UnknownRelationQualifier(relationQualifiers[i]);
                    }
                }
            }

            name = "association=" + relationship_code + ", dc=Relations, " + name;

            boolean isTransitive = false;
            boolean isSymmetric = false;
            boolean isReflexive = false;

            try
            {
                //Find out if this association is transitive.
                attributes = codingSchemesContext_.getAttributes(name, new String[]{SchemaConstants.isTransitive,
                        "isSymmetric", "isReflexive"});
            }
            catch (NameNotFoundException nameNotFoundException)
            {
                throw new UnknownRelationshipCode(relationship_code);
            }

            if (attributes.get(SchemaConstants.isTransitive) != null)
            {
                isTransitive = new Boolean((String) attributes.get(SchemaConstants.isTransitive).get()).booleanValue();
            }

            if (attributes.get("isSymmetric") != null)
            {
                isSymmetric = new Boolean((String) attributes.get("isSymmetric").get()).booleanValue();
            }

            if (attributes.get("isReflexive") != null)
            {
                isReflexive = new Boolean((String) attributes.get("isReflexive").get()).booleanValue();
            }

            if (source_code.equals(target_code))
            {
                if (isReflexive)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }

            boolean useRecursion = false;
            if (!directRelationsOnly && isTransitive)
            {
                useRecursion = true;
            }

            boolean result = areCodesRelatedRecursive(source_code, target_code, 0, name, relationQualifiers,
                                                      useRecursion);

            //if its not related forward, check if it is related backward (if allowed)
            if (!result && isSymmetric)
            {
                result = areCodesRelatedRecursive(target_code, source_code, 0, name, relationQualifiers, useRecursion);
            }

            return result;

        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (UnknownRelationshipCode e)
        {
            throw e;
        }
        catch (UnknownRelationQualifier e)
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

    protected String[] getAssociationQualifiers(String ldapdn) throws NamingException
    {
        setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT.intValue(),
                          CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false, new String[]{"associationQualifier"});
        String filter = "(associationQualifier=*)";

        ArrayList resultsToReturn = new ArrayList();
        NamingEnumeration results = codingSchemesContext_.search(DBUtility.escapeLdapDN(ldapdn), filter, searchControls);
        while (results.hasMore())
        {
            SearchResult sRes = (SearchResult) results.next();
            if (sRes.getAttributes().get("associationQualifier") != null)
            {
                resultsToReturn.add((String) sRes.getAttributes().get("associationQualifier").get());
            }
        }
        results.close();
        return (String[]) resultsToReturn.toArray(new String[resultsToReturn.size()]);
    }

    private boolean areCodesRelatedRecursive(String parentCode, String childCode, int recursionDepth, String name,
            String[] relationQualifiers, boolean useRecursion) throws NamingException, UnexpectedError
    {
        logger.debug("areCodesRelatedRecurisve called: parentCode="
                + parentCode
                + " childConcept="
                + childCode
                + " recursionDepth="
                + recursionDepth
                + " name="
                + name
                + " useRecursion= "
                + useRecursion);
        if (recursionDepth > MAX_CODES_RELATED_RECURSION.getValue())
        {
            throw new UnexpectedError("Hit the limit for the recursion depth");
        }

        //check if there is a direct match

        String subName = "targetConcept=" + childCode + ", " + "sourceConcept=" + parentCode + ", " + name;

        try
        {
            String[] associationQualifiers = getAssociationQualifiers(subName); 
            // Now we know the target exists (since we did a search on subName... if they specified
            // qualifiers, check that they all match.   
            
            if (relationQualifiers != null && relationQualifiers.length > 0)
            {
                for (int i = 0; i < relationQualifiers.length; i++)
                {
                    boolean found = false;
                    for (int j = 0; j < associationQualifiers.length; j++)
                    {
                        if (((String) associationQualifiers[j]).equalsIgnoreCase(relationQualifiers[i]))
                        {
                            found = true;
                        }
                    }
                    if (found == false)
                    {
                        //If we didn't find one of the requested qualifiers in the valid qualifiers
                        //return false
                        return false;
                    }
                }
            }
            // If we get here, there was a direct match, and all specified qualifiers are met.
            return true;
        }
        catch (NameNotFoundException e)
        {
            // no direct match. Check recursively (if allowed)
            if (useRecursion)
            {
                String filter = "(objectClass=associationTarget)";
                subName = "sourceConcept=" + parentCode + ", " + name;
                setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                        .intValue(), CTSConstants.SEARCH_RESULT_LIMIT.intValue(), false,
                                  new String[]{SchemaConstants.targetConcept});
                NamingEnumeration results;
                try
                {
                    results = codingSchemesContext_.search(subName, filter, searchControls);
                    while (results.hasMore())
                    {
                        String code = (String) ((SearchResult) results.next()).getAttributes().get("targetConcept")
                                .get();
                        boolean result = areCodesRelatedRecursive(code, childCode, recursionDepth + 1, name,
                                                                  relationQualifiers, true);
                        if (result)
                        {
                            return true;
                        }

                    }
                    // if we get here, and haven't found one yet, then it is false
                    return false;
                }
                catch (NameNotFoundException nameNotFoundException)
                {
                    return false;
                }
            }
            else
            {
                return false;
            }

        }
    }

    protected String getLDAPNameForCodingSchemeId(String codeSystem_id) throws UnknownCodeSystem
    {
        if (codeSystem_id == null || codeSystem_id.length() == 0)
        {
            throw new UnknownCodeSystem("Null");
        }
        
        // In EVS mode, wildcard is supported (in certain cases) - pass it along.
        //The lucene search algorithm makes use of this.
        if (CTSConstants.EVSModeEnabled.getValue() && codeSystem_id.equals("*"))
        {
            return "*";
        }
        else if (codeSystem_id.equals("*"))
        {
            throw new UnknownCodeSystem(codeSystem_id);
        }
        
        String ldapName = null;
        String keyName = codeSystem_id + "baseName";

        if (objectCache_.get(keyName) instanceof String)
        {
            ldapName = (String) objectCache_.get(keyName);
        }

        if (ldapName == null)
        {

            try
            {
                String name = "";
                String filter = "(&(objectClass=codingSchemeClass)(|(registeredName=urn:iso:" + codeSystem_id + ")(localName=" + codeSystem_id + ")))";

                setSearchControls(searchControls, SearchControls.ONELEVEL_SCOPE, CTSConstants.SHORT_SEARCH_TIMEOUT
                        .intValue(), 1, false, new String[]{});

                NamingEnumeration results = codingSchemesContext_.search(name, filter, searchControls);
                SearchResult sRes = (SearchResult) results.next();
                ldapName = sRes.getName();
                
                if (ldapName.startsWith("\"") && ldapName.endsWith("\""))
                {
                    //there is a strange case, where the ldapName contains the "/" character, 
                    //the returning string is encased in '"' characters.
                    ldapName = ldapName.substring(1, ldapName.length() - 1);
                }
                
                ldapName = DBUtility.escapeLdapDN(ldapName);
                
                objectCache_.put(keyName, ldapName);
            }
            catch (Exception e)
            {
                throw new UnknownCodeSystem(codeSystem_id);
            }
        }
        return ldapName;

    }

    private String getIdForCodeSystem(Attributes attributes) throws NamingException, UnexpectedError
    {
        if (attributes.get("localName") != null)
        {
            NamingEnumeration temp = attributes.get("localName").getAll();
            while (temp.hasMoreElements())
            {
                String localName = (String) temp.nextElement();
                for (int i = 0; i < localName.length(); i++)
                {
                    if (localName.charAt(i) != '.' && !Character.isDigit(localName.charAt(i)))
                    {
                        break;
                    }
                    if (i == localName.length() - 1)
                    {
                        return localName;
                    }
                }
            }
        }
        //fall back on the registeredName
        if (attributes.get("registeredName") != null)
        {
            String registeredName = (String)attributes.get("registeredName").get();
            int temp = registeredName.lastIndexOf(':');
            if (temp > 0 && ((temp + 1) <= registeredName.length()))
            {
                registeredName = registeredName.substring(temp + 1);
            }
            return registeredName;
        }
        //if we get here, error out.
        throw new UnexpectedError("Cannot determine code system id");
    }

    /*
     * Method to get and cache the defaultLanguage for a codingScheme.
     */
    protected String getDefaultLanguage(String ldapName) throws NamingException
    {
        String defaultLanguage = null;
        
        if (CTSConstants.EVSModeEnabled.getValue() && ldapName.equals("*"))
        {
            //In evs mode, the ldapName may be set to '*' in certain cases, and it is valid.
            //means we can't figure out a language, so just return (without error)
            //this is used by the lucene search stuff
            return defaultLanguage;
        }
        
        String keyName = ldapName + "defaultLanguageLookup";

        if (objectCache_.get(keyName) instanceof String)
        {
            defaultLanguage = (String) objectCache_.get(keyName);
        }

        if (defaultLanguage == null)
        {
            defaultLanguage = ((String) codingSchemesContext_.getAttributes(ldapName, new String[]{"defaultLanguage"})
                    .get("defaultLanguage").get());
            objectCache_.put(keyName, defaultLanguage);
        }

        return defaultLanguage;
    }
    
    /*
     * some attributes in ldap now have the urn mashed in the attribute. Need to remove them.
     */
    private String removeURN(String attribute)
    {
        int temp = attribute.indexOf(" ");
        if (temp > 0)
        {
            return attribute.substring(temp + 1, attribute.length());
        }
        else
        {
            return attribute;
        }
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

        if (!address.endsWith("/"))
        {
            address = address + "/";
        }

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, address + "dc=CodingSchemes," + service);
        env.put(Context.REFERRAL, "follow");
        env.put("java.naming.ldap.derefAliases", "never");
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        //these only work in 1.4... and beyond
        env.put("com.sun.jndi.ldap.connect.timeout", "3000");
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, userPassword);
        //We have to put in every object type that we want returned as a
        // byte[] in this format....
        //env.put("java.naming.ldap.attributes.binary", SchemaConstants.blob);
        // // + " " +
        // SchemaConstants.formalRules);
        AutoReconnectDirContext context_ = new AutoReconnectDirContext(env, false);

        return context_;
    }

    private class LanguageHelperReturn
    {
        String language;
        String defaultLanguage;
        String codeSystemLdapName;
    }

}