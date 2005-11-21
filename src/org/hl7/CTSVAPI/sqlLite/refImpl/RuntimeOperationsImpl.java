package org.hl7.CTSVAPI.sqlLite.refImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import edu.mayo.informatics.cts.utility.Utility.SQLConnectionInfo;
import edu.mayo.mir.utility.StringArrayUtility;
import edu.mayo.mir.utility.parameter.IntParameter;

/**
 * <pre>
 * Title:        RuntimeOperationsImpl
 * Description:  A reference implementation of RuntimeOperationsImpl that uses a sql backend instead of ldap.
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust </A>
 * @version 1.0 - cvs $Revision: 1.17 $ checked in on $Date: 2005/10/14 15:44:08 $
 */

public class RuntimeOperationsImpl implements RuntimeOperations
{
    public final static Logger       logger                      = Logger.getLogger("org.hl7.VAPI_sqlLite_Runtime");
    public static final IntParameter MAX_CODES_RELATED_RECURSION = new IntParameter(10);

    private SQLStatements            queries_;

    private ObjectCache              objectCache_;

    /**
     * Create a new RuntimeOperations object using the current values of CTSConstants.
     * @return
     * @throws UnexpectedError
     */
    static public synchronized RuntimeOperations _interface() throws UnexpectedError
    {
        RuntimeOperationsImpl svc = new RuntimeOperationsImpl(null, null,  null, null, false, false);
        return (RuntimeOperations) svc;
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
    static public synchronized RuntimeOperations _interface(String userName, String userPassword, String server,
            String driver, boolean loadProps, boolean initLogger) throws UnexpectedError
    {
        RuntimeOperationsImpl svc = new RuntimeOperationsImpl(userName, userPassword, server, driver, loadProps, initLogger);
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
    static public synchronized RuntimeOperations _interface(String xmlLexGridSQLString, String username,
            String password, boolean loadProps, boolean initLogger) throws UnexpectedError

    {
        RuntimeOperationsImpl svc;
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
            svc = new RuntimeOperationsImpl(username, password, temp.server, temp.driver, loadProps, initLogger);
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
     * This method is here for AXIS. Use the _interface method to instantiate this object locally.
     */
    public RuntimeOperationsImpl()
    {
        try
        {
            CTSConfigurator._instance().initialize();
            logger.debug("CTSMAPI.RuntimeOperationImpl SQL Constructor called");
            queries_ = SQLStatements.instance(null, null, null, null);
            objectCache_ = ObjectCache.instance();
        }
        catch (Exception e)
        {
            logger.error("BrowserOperationsImpl Constructor error", e);
        }
    }

    protected RuntimeOperationsImpl(String username, String password, String server, String driver, 
            boolean loadProps, boolean initLogger)
            throws UnexpectedError
    {
        try
        {
            if (loadProps)
            {
                CTSConfigurator._instance().initialize(initLogger);
            }
            logger.debug("CTSMAPI.RuntimeOperationImpl Constructor called");
            queries_ = SQLStatements.instance(username, password, server, driver);
            objectCache_ = ObjectCache.instance();
        }
        catch (Exception e)
        {
            logger.error("RuntimeOperationsImpl Constructor error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public boolean areCodesRelated(String codeSystem_id, String source_code, String target_code,
            String relationship_code, String[] relationQualifiers, boolean directRelationsOnly)
            throws UnknownRelationshipCode, UnexpectedError, UnknownConceptCode, UnknownCodeSystem,
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

        PreparedStatement getAssociationProperties = null;
        try
        {
            String codeSystemName = getNameForCodeSystemId(codeSystem_id);

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
                throw new UnknownRelationQualifier("This implementation does not support relationship qualifiers");
            }

            String[] supportedAssociations = getSupportedProperties(codeSystemName, "Association");

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
                throw new UnknownRelationshipCode(relationship_code);
            }

            getAssociationProperties = queries_.checkOutStatement(queries_.GET_ASSOCIATION_PROPERTIES);
            getAssociationProperties.setString(1, relationship_code);
            getAssociationProperties.setString(2, codeSystemName);
            ResultSet results = getAssociationProperties.executeQuery();
            // there should be one and only one result - if not, we will just fail and toss the unexpected error.
            results.next();
            
            boolean isTransitive = SQLStatements.getBooleanResult(results, "isTransitive");
            boolean isSymmetric = SQLStatements.getBooleanResult(results, "isSymmetric");
            boolean isReflexive = SQLStatements.getBooleanResult(results, "isReflexive");
            
            //don't hold open
            queries_.checkInPreparedStatement(getAssociationProperties);

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

            boolean result = areCodesRelatedRecursive(source_code, target_code, 0, codeSystemName, relationship_code,
                                                      useRecursion);

            // if its not related forward, check if it is related backward (if allowed)
            if (!result && isSymmetric)
            {
                result = areCodesRelatedRecursive(target_code, source_code, 0, codeSystemName, relationship_code,
                                                  useRecursion);
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
        finally
        {
            queries_.checkInPreparedStatement(getAssociationProperties);
        }
    }

    private boolean areCodesRelatedRecursive(String parentCode, String childCode, int recursionDepth,
            String codeSystemName, String association, boolean useRecursion) throws UnexpectedError
    {
        logger.debug("areCodesRelatedRecurisve called: parentCode="
                + parentCode
                + " childConcept="
                + childCode
                + " recursionDepth="
                + recursionDepth
                + " name="
                + codeSystemName
                + " useRecursion= "
                + useRecursion);

        PreparedStatement checkDirectMatch = null;
        PreparedStatement getTargetsOfSource = null;
        try
        {
            if (recursionDepth > MAX_CODES_RELATED_RECURSION.getValue())
            {
                throw new UnexpectedError("Hit the limit for the recursion depth");
            }

            // check if there is a direct match
            checkDirectMatch = queries_.checkOutStatement(queries_.DOES_DIRECT_RELATION_EXIST);
            checkDirectMatch.setString(1, codeSystemName);
            checkDirectMatch.setString(2, parentCode);
            checkDirectMatch.setString(3, childCode);
            checkDirectMatch.setString(4, association);

            ResultSet results = checkDirectMatch.executeQuery();
            // one and only one row
            results.next();
            if (results.getInt("found") > 0)
            {
                return true;
            }
            // otherwise....

            //don't hold open
            queries_.checkInPreparedStatement(checkDirectMatch);
            
            // no direct match. Check recursively (if allowed)
            if (useRecursion)
            {
                // check if there is a direct match
                getTargetsOfSource = queries_.checkOutStatement(queries_.GET_TARGETS_OF_SOURCE);
                getTargetsOfSource.setString(1, codeSystemName);
                getTargetsOfSource.setString(2, parentCode);
                getTargetsOfSource.setString(3, association);

                results = getTargetsOfSource.executeQuery();
                while (results.next())
                {
                    boolean recursionResult = areCodesRelatedRecursive(results.getString(1), childCode,
                                                                       recursionDepth + 1, codeSystemName, association,
                                                                       true);
                    if (recursionResult)
                    {
                        return true;
                    }
                }
                // If we get here, that means none of the recursions returned true. Not related.
                return false;
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
            queries_.checkInPreparedStatement(checkDirectMatch);
            queries_.checkInPreparedStatement(getTargetsOfSource);
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
            return "This is the Mayo Implementation of the CTS "
                    + CTSConstants.MAJOR_VERSION
                    + "."
                    + CTSConstants.MINOR_VERSION
                    + " Final Draft, against the SQL Lite backend.";
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceName() throws UnexpectedError
    {
        logger.debug("getServicename called.");
        try
        {
            return "Mayo CTS Vapi SQL Lite Runtime";
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

    public CodeSystemIdAndVersions[] getSupportedCodeSystems(int timeout, int sizeLimit) throws UnexpectedError,
            TimeoutError
    {
        long startTime = System.currentTimeMillis();
        PreparedStatement getCodeSystemDetails = null;
        try
        {
            logger.debug("getSupportedCodeSystems called - timout: " + timeout + " sizeLimit: " + sizeLimit);
            getCodeSystemDetails = queries_.checkOutStatement(queries_.GET_CODE_SYSTEM_DETAILS);
            try
            {
                int temp = timeout / 1000;
                if (temp <= 0 && timeout > 0)
                {
                    temp = 1;
                }
                getCodeSystemDetails.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            getCodeSystemDetails.setMaxRows(sizeLimit);

            getCodeSystemDetails.setString(1, "%");
            getCodeSystemDetails.setString(2, "%");

            ResultSet results = getCodeSystemDetails.executeQuery();
            ArrayList resultsToReturn = new ArrayList();

            while (results.next())
            {
                CodeSystemIdAndVersions temp = new CodeSystemIdAndVersions();

                temp.setCodeSystem_id(results.getString("CodingSchemeId"));
                temp.setCodeSystem_name(results.getString("CodingSchemeName"));
                temp
                        .setCodeSystem_versions(new String[]{results
                                .getString("representsVersion")});
                temp.setCopyright(results.getString("copyright"));
                resultsToReturn.add(temp);

                timeup(startTime, timeout);
            }

            return (CodeSystemIdAndVersions[]) resultsToReturn.toArray(new CodeSystemIdAndVersions[resultsToReturn
                    .size()]);

        }
        catch (SQLException e)
        {
            logger.error(e);
            // throw the timeout exception if appropriate
            timeup(startTime, timeout);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (TimeoutError e)
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
            queries_.checkInPreparedStatement(getCodeSystemDetails);
        }
    }

    public boolean isConceptIdValid(ConceptId concept_id, boolean activeConceptsOnly) throws UnexpectedError,
            UnknownCodeSystem
    {
        PreparedStatement isConceptValid = null;
        try
        {
            if (concept_id == null || concept_id.getCodeSystem_id() == null)
            {
                throw new UnknownCodeSystem("null");
            }

            String codeSystemName = getNameForCodeSystemId(concept_id.getCodeSystem_id());

            isConceptValid = queries_.checkOutStatement(queries_.IS_CONCEPT_VALID);

            isConceptValid.setString(1, codeSystemName);
            isConceptValid.setString(2, concept_id.getConcept_code());
            if (activeConceptsOnly)
            {
                SQLStatements.setBooleanOnPreparedStatment(isConceptValid, 3, new Boolean(true));
                SQLStatements.setBooleanOnPreparedStatment(isConceptValid, 4, new Boolean(true));
            }
            else
            {
                SQLStatements.setBooleanOnPreparedStatment(isConceptValid, 3, new Boolean(true));
                SQLStatements.setBooleanOnPreparedStatment(isConceptValid, 4, new Boolean(false));
            }

            ResultSet results = isConceptValid.executeQuery();

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
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
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
            queries_.checkInPreparedStatement(isConceptValid);
        }

    }

    public CodeSystemInfo lookupCodeSystemInfo(String codeSystem_id, String codeSystem_name) throws UnexpectedError,
            CodeSystemNameIdMismatch, UnknownCodeSystem
    {
        PreparedStatement getCodeSystemDetails = null;
        try
        {
            getCodeSystemDetails = queries_.checkOutStatement(queries_.GET_CODE_SYSTEM_DETAILS);
            ResultSet results;

            boolean resultsHasNext = false;

            if (codeSystem_id != null && codeSystem_id.length() > 0)
            {
                getCodeSystemDetails.setString(1, "%");
                getCodeSystemDetails.setString(2, codeSystem_id);

                results = getCodeSystemDetails.executeQuery();

                resultsHasNext = results.next();

                // sometimes sqlLite has a registered name - try more leniant search
                if (!resultsHasNext)
                {
                    getCodeSystemDetails.setString(1, "%");
                    getCodeSystemDetails.setString(2, "%" + codeSystem_id + "%");

                    results = getCodeSystemDetails.executeQuery();
                    resultsHasNext = results.next();
                }
            }

            else
            {
                if (codeSystem_name == null || codeSystem_name.length() == 0)
                {
                    throw new UnknownCodeSystem(codeSystem_id);
                }
                getCodeSystemDetails.setString(1, codeSystem_name);
                getCodeSystemDetails.setString(2, "%");

                results = getCodeSystemDetails.executeQuery();
                resultsHasNext = results.next();
            }

            if (resultsHasNext)
            {

                String _id = results.getString("CodingSchemeId");
                String _name = results.getString("CodingSchemeName");

                if (codeSystem_name != null
                        && codeSystem_name.length() != 0
                        && !_name.equalsIgnoreCase(codeSystem_name))
                {
                    throw new CodeSystemNameIdMismatch(codeSystem_id, codeSystem_name);
                }
                // sometimes sqllite has a registered name, dont throw this error as long as the id
                // sent in is contained in the registeredname
                if (codeSystem_id != null && codeSystem_id.length() != 0 && _id.indexOf(codeSystem_id) == -1)
                {
                    throw new CodeSystemNameIdMismatch(codeSystem_id, codeSystem_name);
                }

                CodeSystemIdAndVersions codeSystemIdAndVersions = new CodeSystemIdAndVersions();
                codeSystemIdAndVersions.setCodeSystem_id(_id);
                codeSystemIdAndVersions.setCodeSystem_name(_name);
                codeSystemIdAndVersions.setCodeSystem_versions(new String[]{results
                        .getString("representsVersion")});
                codeSystemIdAndVersions.setCopyright(results.getString("copyright"));
                CodeSystemInfo theItem = new CodeSystemInfo();
                theItem.setCodeSystem(codeSystemIdAndVersions);
                theItem.setFullName(results.getString("formalName"));
                theItem.setCodeSystemDescription(results.getString("entityDescription"));
                
                String[] supportedLanguages = getSupportedProperties(_name, "Language");
                String defaultLanguage = getDefaultLanguage(_name);
                
                //move the default language to the front of the list if it is not there.
                if (!supportedLanguages[0].equals(defaultLanguage))
                {
                    String temp = supportedLanguages[0];
                    supportedLanguages[0] = defaultLanguage;
                    for (int i = 1; i < supportedLanguages.length; i++)
                    {
                        if (supportedLanguages[i].equals(defaultLanguage))
                        {
                            supportedLanguages[i] = temp;
                            break;
                        }
                    }
                }
                
                theItem.setSupportedLanguages(supportedLanguages);
                theItem.setSupportedRelations(getSupportedProperties(_name, "Association"));
                theItem.setSupportedRelationQualifiers(
                        getSupportedProperties(_name, "AssociationQualifier"));
                theItem.setSupportedProperties(getSupportedProperties(_name, "Property"));
                theItem.setSupportedMimeTypes(getSupportedProperties(_name, "Format"));

                results.close();

                return theItem;
            }
            else
            {
                throw new UnknownCodeSystem(codeSystem_id);
            }

        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (CodeSystemNameIdMismatch e)
        {
            throw e;
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
        finally
        {
            queries_.checkInPreparedStatement(getCodeSystemDetails);
        }
    }

    protected String[] getSupportedProperties(String codeSystem, String property) throws UnexpectedError
    {
        PreparedStatement getCodeSystemProperties = null;
        try
        {
            getCodeSystemProperties = queries_.checkOutStatement(queries_.GET_CODE_SYSTEM_SUPPORTED_PROPERTYS);

            getCodeSystemProperties.setString(1, codeSystem);
            getCodeSystemProperties.setString(2, property);
            ResultSet results = getCodeSystemProperties.executeQuery();

            ArrayList temp = new ArrayList();
            while (results.next())
            {
                temp.add(results.getString("supportedAttributeValue"));
            }
            results.close();

            return (String[]) temp.toArray(new String[temp.size()]);
        }
        catch (Exception e)
        {
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getCodeSystemProperties);
        }
    }

    protected String getNameForCodeSystemId(String codeSystem) throws UnexpectedError, UnknownCodeSystem
    {
        PreparedStatement getCodeSystemName = null;
        try
        {
            if (codeSystem == null || codeSystem.length() == 0)
            {
                throw new UnknownCodeSystem("null");
            }
            
            String keyName = codeSystem + "codeSystemNameLookup";
            if (objectCache_.get(keyName) instanceof String)
            {
                return (String) objectCache_.get(keyName);
            }
            else
            {
                getCodeSystemName = queries_.checkOutStatement(queries_.GET_CODE_SYSTEM_NAME);
                if (queries_.isDB2())
                {
                    getCodeSystemName.setString(1, codeSystem.toUpperCase());
                }
                else
                {
                    getCodeSystemName.setString(1, codeSystem);
                }
                ResultSet results = getCodeSystemName.executeQuery();
                if (results.next())
                {
                    String temp = results.getString("codingSchemeName");
                    objectCache_.put(keyName, temp);
                    return temp;
                }
                else
                {
                    // some are prefixed with localname, etc. just try a more leniant search.
                    if (queries_.isDB2())
                    {
                        getCodeSystemName.setString(1, "%" + codeSystem.toUpperCase() + "%");
                    }
                    else
                    {
                        getCodeSystemName.setString(1, "%" + codeSystem + "%");
                    }
                    results = getCodeSystemName.executeQuery();
                    if (results.next())
                    {
                        String temp = results.getString("codingSchemeName");
                        objectCache_.put(keyName, temp);
                        return temp;
                    }
                    else
                    {
                        throw new UnknownCodeSystem(codeSystem);
                    }
                }
            }
        }
        catch (UnknownCodeSystem e)
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
            queries_.checkInPreparedStatement(getCodeSystemName);
        }
    }

    protected String getIdForCodeSystemName(String codeSystemName) throws UnexpectedError, UnknownCodeSystem
    {
        PreparedStatement getCodeSystemId = null;
        try
        {
            String keyName = codeSystemName + "_codeSystemIdLookup_";
            if (objectCache_.get(keyName) instanceof String)
            {
                return (String) objectCache_.get(keyName);
            }
            else
            {

                if (codeSystemName == null)
                {
                    throw new UnknownCodeSystem("null");
                }

                String id = "";

                getCodeSystemId = queries_.checkOutStatement(queries_.GET_CODE_SYSTEM_ID);
                getCodeSystemId.setString(1, codeSystemName);
                ResultSet results = getCodeSystemId.executeQuery();
                if (results.next())
                {
                    id = results.getString("codingSchemeId");
                }
                else
                {
                    // some are prefixed with urn:iso:, etc. just try a more leniant search.
                    getCodeSystemId.setString(1, "%" + codeSystemName + "%");
                    results = getCodeSystemId.executeQuery();
                    if (results.next())
                    {
                        id = results.getString("codingSchemeId");
                    }
                    else
                    {
                        throw new UnknownCodeSystem(codeSystemName);
                    }
                }
                int temp = id.lastIndexOf(':');
                if (temp > 0 && ((temp + 1) <= id.length()))
                {
                    id = id.substring(temp + 1);
                }
                objectCache_.put(keyName, id);
                return id;
            }
        }
        catch (UnknownCodeSystem e)
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
            queries_.checkInPreparedStatement(getCodeSystemId);
        }
    }
    
    protected String getIdForRelationshipCodeSystemName(String codeSystemName, String targetCodeSystemName) throws UnexpectedError
    {
        PreparedStatement getCodeSystemId = null;
        try
        {
            if (targetCodeSystemName == null || targetCodeSystemName.length() == 0)
            {
                throw new UnexpectedError("No target code system name specified");
            }
            if (codeSystemName.equals(targetCodeSystemName))
            {
                return getIdForCodeSystemName(targetCodeSystemName);
            }
            
            String keyName = targetCodeSystemName + "_targetCodeSystemIdLookup_";
            if (objectCache_.get(keyName) instanceof String)
            {
                return (String) objectCache_.get(keyName);
            }
            else
            {
                getCodeSystemId = queries_.checkOutStatement(queries_.GET_ID_FOR_RELATIONSHIP_CODE_SYSTEM_NAME);
                getCodeSystemId.setString(1, codeSystemName);
                getCodeSystemId.setString(2, targetCodeSystemName);
                ResultSet results = getCodeSystemId.executeQuery();
                if (results.next())
                {
                    String urn = results.getString("urn");
                    if (urn == null || urn.length() == 0)
                    {
                        throw new UnexpectedError("Database is missing required codingSchemeSupportedAttributes entry that maps target coding scheme (" + targetCodeSystemName + ") to the URN");
                    }
                    // cut off the leading stuff
                    
                    int temp = urn.lastIndexOf(':');
                    if (temp > 0 && ((temp + 1) <= urn.length()))
                    {
                        urn = urn.substring(temp + 1);
                    }
                    objectCache_.put(keyName, urn);
                    return urn;
                    
                }
                else
                {
                    throw new UnexpectedError("Database is missing required codingSchemeSupportedAttributes entry that maps target coding scheme (" + targetCodeSystemName + ") to the URN");
                }
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
        finally
        {
            queries_.checkInPreparedStatement(getCodeSystemId);
        }
    }

    protected String getDefaultLanguage(String codeSystemName) throws UnexpectedError, UnknownCodeSystem
    {
        PreparedStatement getDefaultLanguage = null;

        try
        {
            String keyName = codeSystemName + "defaultLanguageLookup";
            if (objectCache_.get(keyName) instanceof String)
            {
                return (String) objectCache_.get(keyName);
            }
            else
            {

                getDefaultLanguage = queries_.checkOutStatement(queries_.GET_DEFAULT_LANGUAGE);
                getDefaultLanguage.setString(1, codeSystemName);
                ResultSet results = getDefaultLanguage.executeQuery();
                if (results.next())
                {
                    String temp = results.getString("defaultLanguage");
                    objectCache_.put(keyName, temp);
                    return temp;
                }
                else
                {
                    throw new UnknownCodeSystem(codeSystemName);
                }
            }
        }
        catch (UnknownCodeSystem e)
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
            queries_.checkInPreparedStatement(getDefaultLanguage);
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

    public StringAndLanguage lookupDesignation(ConceptId concept_id, String language_code) throws UnexpectedError,
            UnknownConceptCode, UnknownCodeSystem, UnknownLanguageCode, NoApplicableDesignationFound
    {
        PreparedStatement getDesignation = null;
        PreparedStatement getDesignationNull = null;

        try
        {
            if (concept_id == null || concept_id.getCodeSystem_id() == null)
            {
                throw new UnknownCodeSystem("null");
            }

            // this throws unknownCodeSystem if necessary.
            String codeSystemName = getNameForCodeSystemId(concept_id.getCodeSystem_id());
            
            String defaultLanguage = getDefaultLanguage(codeSystemName);
            
            if (language_code == null || language_code.length() == 0)
            {
                language_code = defaultLanguage;
            }
            
            // this throws unknownLanguageCode if necessary
            validateLanguageCode(codeSystemName, language_code);

            if (concept_id.getConcept_code() == null || concept_id.getConcept_code().length() == 0)
            {
                throw new UnknownConceptCode("missing concept code");
            }

            getDesignation = queries_.checkOutStatement(queries_.GET_DESIGNATION);
            getDesignationNull = queries_.checkOutStatement(queries_.GET_DESIGNATION_NULL_STRING);

            String language = language_code + "-";

            // iterate over the language code, stripping everything after the last '-' each time
            // to get to the base level code.
            while (language.lastIndexOf("-") != -1)
            {
                language = language.substring(0, language.lastIndexOf('-'));
                getDesignation.setString(1, codeSystemName);
                getDesignation.setString(2, concept_id.getConcept_code());
                getDesignation.setString(3, language);

                ResultSet results = getDesignation.executeQuery();

                String bestLang = null;
                String bestValue = null;

                while (results.next())
                {
                    String currentLang = results.getString("language");
                    if (currentLang == null || currentLang.length() == 0)
                    {
                        currentLang = defaultLanguage;
                    }
                    String currentValue = results.getString("propertyText");
                    boolean currentIsPref = SQLStatements.getBooleanResult(results, "isPreferred");

                    if (currentIsPref)
                    {
                        StringAndLanguage resultToReturn = new StringAndLanguage();
                        resultToReturn.setLanguage_code(currentLang);
                        resultToReturn.setText(currentValue);
                        return resultToReturn;
                    }

                    // not preferred, keep it around (if current store is null or greater than it)
                    if (bestValue == null || currentValue.compareToIgnoreCase(bestValue) < 0)
                    {
                        bestValue = currentValue;
                        bestLang = currentLang;
                    }
                }
                // if we found one above, return it.
                if (bestValue != null)
                {
                    StringAndLanguage resultToReturn = new StringAndLanguage();
                    resultToReturn.setLanguage_code(bestLang);
                    resultToReturn.setText(bestValue);
                    return resultToReturn;
                }

                // if there are no results above, see if the language equals the default language
                // if it does, try searching for the concept with a null or blank language code.
                if (defaultLanguage.equals(language))
                {
                    getDesignationNull.setString(1, codeSystemName);
                    getDesignationNull.setString(2, concept_id.getConcept_code());

                    results = getDesignationNull.executeQuery();

                    while (results.next())
                    {
                        String currentLang = defaultLanguage;
                        String currentValue = results.getString("propertyText");
                        boolean currentIsPref = SQLStatements.getBooleanResult(results, "isPreferred");
                        
                        if (currentIsPref)
                        {
                            StringAndLanguage resultToReturn = new StringAndLanguage();
                            resultToReturn.setLanguage_code(currentLang);
                            resultToReturn.setText(currentValue);
                            return resultToReturn;
                        }
                        
                        //not preferred, keep it around (if current store is null or greater than it)
                        if (bestValue == null || currentValue.compareToIgnoreCase(bestValue) < 0)
                        {
                            bestValue = currentValue;
                            bestLang = currentLang;
                        }
                    }
                    //if we found one above, return it.
                    if (bestValue != null)
                    {
                        StringAndLanguage resultToReturn = new StringAndLanguage();
                        resultToReturn.setLanguage_code(bestLang);
                        resultToReturn.setText(bestValue);
                        return resultToReturn;
                    }
                }
            }
            // if we get here, the code/language combination could not be found. decide which error to throw.
            if (isConceptIdValid(concept_id, false))
            {
                throw new NoApplicableDesignationFound();
            }
            else
            {
                throw new UnknownConceptCode(concept_id.getConcept_code());
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
            logger.error("Error:", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getDesignation);
            queries_.checkInPreparedStatement(getDesignationNull);
        }
    }

    protected void validateLanguageCode(String codeSystemName, String language_code) throws UnexpectedError,
            UnknownLanguageCode
    {
        PreparedStatement validateLanguageCode = null;
        try
        {
            if (language_code == null)
            {
                throw new UnknownLanguageCode(null);
            }
            String keyName = codeSystemName + language_code + "validateLanguageCode";
            if (objectCache_.get(keyName) instanceof String)
            {
                String result = (String) objectCache_.get(keyName);
                if (!result.equals("VALID"))
                {
                    throw new UnknownLanguageCode(language_code);
                }
            }
            else
            {

                validateLanguageCode = queries_.checkOutStatement(queries_.IS_PROPERTY_VALID);
                String language = language_code + "-";

                // iterate over the language code, stripping everything after the last '-' each time
                // to get to the base level code.
                while (language.lastIndexOf("-") != -1)
                {
                    language = language.substring(0, language.lastIndexOf('-'));
                    validateLanguageCode.setString(1, codeSystemName);
                    validateLanguageCode.setString(2, "Language");
                    validateLanguageCode.setString(3, language);

                    ResultSet results = validateLanguageCode.executeQuery();

                    // one and only one result
                    results.next();
                    if (results.getInt("found") > 0)
                    {
                        objectCache_.put(keyName, "VALID");
                        return;
                    }

                }
                // if we got here without returning, the langauge must not be valid.
                objectCache_.put(keyName, "NOTVALID");
                throw new UnknownLanguageCode(language_code);
            }
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
            queries_.checkInPreparedStatement(validateLanguageCode);
        }
    }
}