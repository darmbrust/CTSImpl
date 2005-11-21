package org.hl7.CTSMAPI.refImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.hl7.CTSMAPI.BadlyFormedMatchText;
import org.hl7.CTSMAPI.CTSVersionId;
import org.hl7.CTSMAPI.ConceptId;
import org.hl7.CTSMAPI.InvalidExpansionContext;
import org.hl7.CTSMAPI.NoApplicableDesignationFound;
import org.hl7.CTSMAPI.NoApplicableValueSet;
import org.hl7.CTSMAPI.QualifiersNotSupported;
import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.SubsumptionNotSupported;
import org.hl7.CTSMAPI.TimeoutError;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.UnknownApplicationContextCode;
import org.hl7.CTSMAPI.UnknownCodeSystem;
import org.hl7.CTSMAPI.UnknownConceptCode;
import org.hl7.CTSMAPI.UnknownLanguage;
import org.hl7.CTSMAPI.UnknownMatchAlgorithm;
import org.hl7.CTSMAPI.UnknownVocabularyDomain;
import org.hl7.CTSMAPI.UnrecognizedQualifier;
import org.hl7.CTSMAPI.ValidateCodeReturn;
import org.hl7.CTSMAPI.ValidationDetail;
import org.hl7.CTSMAPI.ValueSetExpansion;
import org.hl7.cts.types.BL;
import org.hl7.cts.types.CD;
import org.hl7.cts.types.CR;
import org.hl7.cts.types.CS;
import org.hl7.cts.types.CV;
import org.hl7.cts.types.ST;
import org.hl7.cts.types.UID;
import org.hl7.utility.CTSConstants;
import org.hl7.utility.MAPIExpansionContext;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Constructors;
import edu.mayo.informatics.cts.utility.Utility;
import edu.mayo.informatics.cts.utility.Utility.SQLConnectionInfo;

/**
 * <pre>
 *  Title: RuntimeOperationsImpl 
 *  Description: A reference implementation of RuntimeOperationsImpl. 
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
 * @version 1.0 - cvs $Revision: 1.67 $ checked in on $Date: 2005/10/14 15:44:11 $
 */
public class RuntimeOperationsImpl implements RuntimeOperations
{
    private String             booleanWrapper_;
    // mysql, postgres, and msaccess do not play nice with booleans, some
    // require them to be wrapped with "'" while others don't.

    private SQLStatements      queries_;

    public final static Logger logger                    = Logger.getLogger("org.hl7.MAPI_Runtime");

    private final ST[]         supportedMatchAlgorithms_ = Constructors.stArray(
                                                                                new String[]{"IdenticalIgnoreCase",
            "StartsWithIgnoreCase", "EndsWithIgnoreCase", "ContainsPhraseIgnoreCase"});

    private Hashtable          validateCodeReturnCodes   = new Hashtable();

    /**
     * Create a new Runtime Operations object using the current values of CTSConstants.
     * @return
     * @throws UnexpectedError
     */
    static public synchronized RuntimeOperations _interface() throws UnexpectedError
    {
        RuntimeOperationsImpl svc = new RuntimeOperationsImpl(null, null, null, null, false, false);
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
            String password, boolean loadProps,  boolean initLogger) throws UnexpectedError

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
            throw new UnexpectedError(Constructors.stm("Problem parsing the the XML connection info. "
                    + e.toString()
                    + " "
                    + (e.getCause() == null ? ""
                            : e.getCause().toString())));
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
            logger.debug("CTSMAPI.RuntimeOperationImpl Constructor called");
            queries_ = SQLStatements.instance(null, null, null, null);
            if (queries_.isAccess())
            {
                booleanWrapper_ = "";
            }
            else
            {
                booleanWrapper_ = "'";
            }
            populateValidateCodeReturns();
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
            if (queries_.isAccess())
            {
                booleanWrapper_ = "";
            }
            else
            {
                booleanWrapper_ = "'";
            }
            populateValidateCodeReturns();
        }
        catch (Exception e)
        {
            logger.error("BrowserOperationsImpl Constructor error", e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    public ST getServiceName() throws UnexpectedError
    {
        logger.debug("getServiceName called");
        try
        {
            return Constructors.stm("Mayo CTS Mapi Runtime");
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    public ST getServiceVersion() throws UnexpectedError
    {
        logger.debug("getServiceVersion called");
        try
        {
            return Constructors.stm(CTSConstants.FULL_VERSION);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    public ST getServiceDescription() throws UnexpectedError
    {
        logger.debug("getServiceDescription called");
        try
        {
            return Constructors.stm("This is the Mayo Implementation of the CTS "
                    + CTSConstants.MAJOR_VERSION
                    + "."
                    + CTSConstants.MINOR_VERSION
                    + " Final Draft.");
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    public CTSVersionId getCTSVersion() throws UnexpectedError
    {
        logger.debug("CTSVersion called");
        try
        {
            CTSVersionId verID = new CTSVersionId();
            verID.setMajor(Constructors.intfm(Integer.parseInt(CTSConstants.MAJOR_VERSION)));
            verID.setMinor(Constructors.intfm(Integer.parseInt(CTSConstants.MINOR_VERSION)));
            return verID;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    public ST getHL7ReleaseVersion() throws UnexpectedError
    {
        logger.debug("HL7ReleaseVersion called");
        StringBuffer resultToReturn = new StringBuffer();
        PreparedStatement getHL7ReleaseVersion = null;
        try
        {
            getHL7ReleaseVersion = queries_.checkOutStatement(queries_.GET_HL7_RELEASE_VERSION);
            ResultSet results = getHL7ReleaseVersion.executeQuery();
            if (results.next())
            {
                resultToReturn.append(results.getString("modelID") + " (");
                resultToReturn.append(results.getString("lastModifiedDate") + ")");
            }
            return Constructors.stm(resultToReturn.toString());
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getHL7ReleaseVersion);
        }
    }

    public BL subsumes(CD parentCode, CD childCode) throws SubsumptionNotSupported, QualifiersNotSupported,
            UnknownConceptCode, UnknownCodeSystem, UnrecognizedQualifier, UnexpectedError
    {
        logger.debug("subsumes called");
        // We choose not to support Qualifiers at this time.

        PreparedStatement getTargetsOfSourceCode = null;
        try
        {
            if (parentCode == null
                    || childCode == null
                    || parentCode.getCodeSystem() == null
                    || childCode.getCodeSystem() == null
                    || parentCode.getCode() == null
                    || childCode.getCode() == null)
            {
                throw new UnknownCodeSystem();
            }

            if (!parentCode.getCodeSystem().equals(childCode.getCodeSystem()))
            {
                throw new SubsumptionNotSupported();
            }

            if ((parentCode.getQualifiers() != null && parentCode .getQualifiers().length > 0)
                    || (childCode.getQualifiers() != null && childCode.getQualifiers().length > 0))
            {
                throw new QualifiersNotSupported();
            }

            if (!doesCodeSystemExist(parentCode.getCodeSystem(), "%"))
            {
                throw new UnknownCodeSystem(Constructors.uidm(parentCode.getCodeSystem()));
            }

            ConceptId targetConceptId = Constructors.conceptIdm(childCode.getCodeSystem(), childCode.getCode());
            if (!doesCodeExist(targetConceptId))
            {
                throw new UnknownConceptCode(targetConceptId);
            }

            if (parentCode.getCode().equals(childCode.getCode()))
            {
                return Constructors.blm(true);
            }

            getTargetsOfSourceCode = queries_.checkOutStatement(queries_.GET_TARGETS_OF_SOURCE_CODE);
            getTargetsOfSourceCode.setString(1, parentCode.getCodeSystem());
            getTargetsOfSourceCode.setString(2, parentCode.getCode());
            ResultSet results = getTargetsOfSourceCode.executeQuery();
            ArrayList temp = new ArrayList();
            while (results.next())
            {
                temp.add(results.getString("targetCode"));
            }
            if (temp.size() == 0)
            {
                ConceptId conceptId = Constructors.conceptIdm(parentCode.getCodeSystem(), parentCode.getCode());
                if (!doesCodeExist(conceptId))
                {
                    throw new UnknownConceptCode(conceptId);
                }
            }
            return Constructors.blm(subsumesHelper(temp, childCode, parentCode.getCodeSystem()));
        }
        catch (SubsumptionNotSupported e)
        {
            throw e;
        }
        catch (QualifiersNotSupported e)
        {
            throw e;
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        // catch (UnrecognizedQualifier e)
        // {
        // throw e;
        // }
        catch (Exception e)
        {
            logger.error("Error", e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getTargetsOfSourceCode);
        }
    }

    private boolean subsumesHelper(ArrayList codes, CD targetCode, String sourceCodeSystem) throws UnexpectedError
    {
        if (codes.size() == 0)
            return false;
        for (int i = 0; i < codes.size(); i++)
        {
            if (codes.get(i).equals(targetCode.getCode()))
            {
                return true;
            }
        }
        PreparedStatement getTargetsOfSourceCode = null;
        try
        {
            getTargetsOfSourceCode = queries_.checkOutStatement(queries_.GET_TARGETS_OF_SOURCE_CODE);
            for (int i = 0; i < codes.size(); i++)
            {
                ArrayList temp = new ArrayList();

                getTargetsOfSourceCode.setString(1, sourceCodeSystem);
                getTargetsOfSourceCode.setString(2, (String) codes.get(i));
                ResultSet results = getTargetsOfSourceCode.executeQuery();
                while (results.next())
                {
                    temp.add(results.getString("targetCode"));
                }

                if (subsumesHelper(temp, targetCode, sourceCodeSystem))
                    return true;
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.getMessage() + e.toString()));
        }
        finally
        {
            queries_.checkInPreparedStatement(getTargetsOfSourceCode);
        }
        return false; // This isn't used.....
    }

    public ST[] getSupportedMatchAlgorithms() throws UnexpectedError
    {
        try
        {
            return supportedMatchAlgorithms_;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    public ST[] getSupportedVocabularyDomains(ST matchText, ST matchAlgorithm_code, int timeout, int sizeLimit)
            throws UnexpectedError, TimeoutError, UnknownMatchAlgorithm, BadlyFormedMatchText
    {
        long startTime = System.currentTimeMillis();
        logger.debug("supportedVocabularyDomains called");
        ArrayList supportedVocabularyDomains = new ArrayList();
        PreparedStatement getVocabularyDomains = null;
        try
        {
            getVocabularyDomains = queries_.checkOutStatement(queries_.GET_VOCABULARY_DOMAINS);
            try
            {
                int temp = timeout / 1000;
                if (temp <= 0 && timeout > 0)
                {
                    temp = 1;
                }
                getVocabularyDomains.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            getVocabularyDomains.setMaxRows(sizeLimit);
            getVocabularyDomains.setString(1, wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code));
            ResultSet results = getVocabularyDomains.executeQuery();
            while (results.next())
            {
                supportedVocabularyDomains.add(Constructors.stm(results.getString("vocDomain")));
            }
            return (ST[]) supportedVocabularyDomains.toArray(new ST[supportedVocabularyDomains.size()]);
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
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        catch (Exception ex)
        {
            logger.error(ex);
            throw new UnexpectedError(Constructors.stm(ex.toString() + " " + (ex.getCause() == null ? ""
                    : ex.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getVocabularyDomains);
        }
    }

    // Question relating to subsumes and validate code:
    // When comparing these two functions they seem to perform a similar validation yet their signatures are very
    // different. I guess I'm concerned that there is something going on here that I don't really understand.

    // My understanding is that validateCode checks to see if a certain concept is contained within a vocabulary system.
    // Subsumes checks to see if 2 concepts are contained within a vocabulary system and one is in fact the child of the
    // other.
    // I am confused because
    // - validateCode contains a vocabulary domain parameter, since "codeToValidate" must have a code system id isn't
    // this repetitive?
    // - validateCode contains an activeConceptsOnly flag, shouldn't subsumes have this flag too.. even if it's
    // optional?

    // Answer (from Harold):
    // Vocabulary doman is not the same thing as code system. A vocabulary domain plus an optional context maps to a set
    // of concept codes. What makes this confusing is that the codes managed by HL7 tend to align vocabulary domains,
    // value sets and concept codes very closely. This is particularily confusing in the area of structural codes. To
    // contrive an example, let’s say that you have an attribute named “patient gender”, which is connected to the
    // “administrative gender” vocabulary domain.
    // Administrative gender could be defined as:
    // The administrativeGender value set in the US
    // The CanadianAdministativeGender value set in Canada
    // The administrativeGender value set consists of all of the concept codes in the HL7 AdministrativeGender “code
    // system”
    // The CanadianAdministrativeGender consists of all of the direct children of SNOMED-CT concept 365873007 (gender
    // finding) except 407375002 (surgically transgendered).
    // Were you to call validateCode with vocabularyDomain=”administrative gender”, codedAttributeValue=”&snomedCt /
    // 248152002 / female”, applicationContexts=”Canada”
    // You wouldn’t get any errors
    //
    // Were the call vocabularyDomain=”administrative gender”, codedAttributeValue=”&snomedCt / 248152002 / female”,
    // applicationContexts=null
    // You would get (at least) one error: E005 – Concept Code not Valid for Vocabulary Domain
    // The call vocabularyDomain=”administrative gender”, codedAttributeValue=”&snomedCt / 248152001 / female”,
    // applicationContexts=null
    // Would return (at least) one error: E0002 – Concept Code not valid for Code System
    // The primary purpose behind “subsumes” is subsumption checking. The rest of the error conditions are included in
    // the call because the result is really trinary:
    // Yes, code a subsumes code b
    // No, code a doesn’t subsume code b
    // I don’t know because I don’t recognize one or both of the concept codes, the code systems, the qualifiers or
    // something else about the statement.
    //
    // While, technially, you could use “subsumes” (or several other calls for that matter) to validate concept codes,
    // there would be no guarantee that it would be efficient.
    // ActiveOnly:
    //
    // The current CTS model is a bit fuzzy when it comes to the relationship between “active/retired” concepts and
    // their participation in relationships. Relationships really need to be marked the same way, but we decided to wait
    // on this little detail because of the complexity that it added to the model. At the moment, it is up to the
    // terminology authors to decide whether an active concept can subsume a retired concept and visa-versa. I suppose
    // that it would have made sense to put the control in the hands of the users, however. An “activeOnly” flag would
    // have allowed them to say only active concepts / relationships count, and false would have said any assertion that
    // existed at some point in history counts.

    public ValidateCodeReturn validateCode(ST vocabularyDomain_name, CD codeToValidate, ST applicationContext_code,
            BL activeConceptsOnly, BL errorCheckOnly) throws UnexpectedError, UnknownApplicationContextCode,
            UnknownVocabularyDomain, NoApplicableValueSet
    {
        PreparedStatement getVocabularyDomainId = null;
        try
        {

            logger.debug("ValidateCode called - domain: "
                    + vocabularyDomain_name.getV()
                    + " code: "
                    + (codeToValidate != null ? codeToValidate.getCode()
                            : "")
                    + " codeSystem: "
                    + (codeToValidate != null ? codeToValidate.getCodeSystem()
                            : "")
                    + " codeSystemName: "
                    + (codeToValidate != null ? codeToValidate.getCodeSystemName()
                            : "")
                    + " codeSystemVersion: "
                    + (codeToValidate != null ? codeToValidate.getCodeSystemVersion()
                            : "")
                    + " DisplayName: "
                    + (codeToValidate != null ? codeToValidate.getDisplayName()
                            : "")
                    + " activeOnly: "
                    + activeConceptsOnly
                    + " errorCheckOnly: "
                    + errorCheckOnly);

            ValidateCodeReturn resultsToReturn = new ValidateCodeReturn();
            ArrayList validationDetails = new ArrayList();

            String valueSetId = "";

            // TODO support active concepts only - the current implementation only supports validating active concepts.

            if (applicationContext_code != null
                    && applicationContext_code.getV() != null
                    && applicationContext_code.getV().length() > 0
                    && !applicationContext_code.getV().equals("%"))
            {
                // throws an exception if it is not.
                this.isApplicationContextValid(applicationContext_code);
            }
            else
            {
                applicationContext_code = Constructors.stm("%");
            }

            // validate the vocabularyDomain name
            if (vocabularyDomain_name == null
                    || vocabularyDomain_name.getV() == null
                    || vocabularyDomain_name.getV().length() == 0
                    || !isVocabularyDomainNameValid(vocabularyDomain_name))
            {
                throw new UnknownVocabularyDomain(vocabularyDomain_name);
            }

            getVocabularyDomainId = queries_.checkOutStatement(queries_.GET_VOCABULARY_DOMAIN_ID);
            getVocabularyDomainId.setString(1, vocabularyDomain_name.getV());
            getVocabularyDomainId.setString(2, applicationContext_code.getV());
            ResultSet results = getVocabularyDomainId.executeQuery();
            if (results.next())
            // Want the first result
            {
                valueSetId = results.getString("definedbyvalueset");
            }
            else
            {
                throw new NoApplicableValueSet();
            }

            validateCodeHelper(valueSetId, vocabularyDomain_name, applicationContext_code, activeConceptsOnly,
                               errorCheckOnly, codeToValidate, validationDetails);

            resultsToReturn.setDetail((ValidationDetail[]) validationDetails
                    .toArray(new ValidationDetail[validationDetails.size()]));

            int numErrors = 0;
            int numWarnings = 0;
            for (int i = 0; i < validationDetails.size(); i++)
            {
                if (((ValidationDetail) validationDetails.get(i)).getError_id().getV().startsWith("E"))
                {
                    numErrors++;
                }
                else
                {
                    numWarnings++;
                }
            }
            resultsToReturn.setNErrors(Constructors.intfm(numErrors));
            resultsToReturn.setNWarnings(Constructors.intfm(numWarnings));
            return resultsToReturn;
        }
        catch (UnknownApplicationContextCode e)
        {
            throw e;
        }
        catch (UnknownVocabularyDomain e)
        {
            throw e;
        }
        catch (NoApplicableValueSet e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));

        }
        finally
        {
            queries_.checkInPreparedStatement(getVocabularyDomainId);
        }

    }

    private void validateCodeHelper(String valueSetId, ST vocabularyDomain_name, ST applicationContext_code,
            BL activeConceptsOnly, BL errorCheckOnly, CD codeToValidate, ArrayList validationDetails)
            throws UnexpectedError, NoApplicableValueSet, UnknownVocabularyDomain, UnknownApplicationContextCode
    {
        try
        {
            boolean isCS = false;
            if (codeToValidate == null)
            {
                validationDetails.add(makeValidationDetail("E013", codeToValidate));
                // this is pretty much fatal...
                return;
            }

            isCS = this.isDataTypeAllowedForVocDomain(vocabularyDomain_name.getV(), "%CS%");
            if (codeToValidate.getCode() == null || codeToValidate.getCode().length() == 0)
            {
                validationDetails.add(makeValidationDetail("E013", codeToValidate));
            }
            else if ((codeToValidate.getCodeSystem() == null || codeToValidate.getCodeSystem().length() == 0) && !isCS)
            { // only have a missing code system error if there is a concept code, the code system is missing,
                // and the code system is required for this type.
                validationDetails.add(makeValidationDetail("E012", codeToValidate));
            }

            String codeSystem = codeToValidate.getCodeSystem();
            if ((codeSystem == null || codeSystem.length() == 0) && !isCS)
            {
                validationDetails.add(makeValidationDetail("E001", codeToValidate));
                return;
            }
            else if (codeSystem == null || codeSystem.length() == 0)
            {
                codeSystem = "%";
            }

            String conceptCode = codeToValidate.getCode();
            if (conceptCode == null || conceptCode.length() == 0)
            {
                validationDetails.add(makeValidationDetail("E002", codeToValidate));
                return;
            }

            // per Harold:
            // This doesn't work for activeOnly = false, because we don't currently carry inactive
            // concepts in the nested value set table.
            //
            // Lookup by valueSet cc & cs
            //
            // Hit - happy :-)
            // Miss
            //
            // 1) Lookup by vs & cs
            // Hit
            // Is cc in cs?
            // Y E005
            // N E002
            // Miss
            // Is cs recognized?
            // Y E003
            // N E001

            if (!doesCodeExistInCodeSystem(valueSetId, codeSystem, conceptCode))
            {
                if (doesCodeExistInCodeSystem(valueSetId, codeSystem, "%"))
                {
                    if (doesCodeExistInCodeSystem("%", codeSystem, conceptCode))
                    {
                        validationDetails.add(makeValidationDetail("E005", codeToValidate));
                    }
                    else
                    {
                        validationDetails.add(makeValidationDetail("E002", codeToValidate));
                    }
                }
                else
                {
                    if (doesCodeExistInCodeSystem("%", codeSystem, "%"))
                    {
                        validationDetails.add(makeValidationDetail("E003", codeToValidate));
                    }
                    else
                    {
                        validationDetails.add(makeValidationDetail("E001", codeToValidate));
                    }
                }
            }

            CS rationale = codeToValidate.getCodingRationale();
            if (rationale != null && rationale.getCode().length() != 0 && !isCodingRationaleValid(rationale.getCode()))
            {
                // empty is allowed, but if they do supply one, validate it.
                validationDetails.add(makeValidationDetail("E014", codeToValidate));
            }

            CD[] translations = codeToValidate.getTranslation();
            if (translations != null && translations.length > 0)
            {
                // translations only supported by types CD and CE
                if (!isDataTypeAllowedForVocDomain(vocabularyDomain_name.getV(), "%CD%")
                        && !isDataTypeAllowedForVocDomain(vocabularyDomain_name.getV(), "%CE%"))
                {
                    validationDetails.add(makeValidationDetail("E007", codeToValidate));
                }
            }

            CR[] qualifiers = codeToValidate.getQualifiers();
            if (qualifiers != null && qualifiers.length > 0)
            {
                // qualifiers only supported by type CD
                if (!isDataTypeAllowedForVocDomain(vocabularyDomain_name.getV(), "%CD%"))
                {
                    validationDetails.add(makeValidationDetail("E006", codeToValidate));
                }
                for (int i = 0; i < qualifiers.length; i++)
                {
                    if (qualifiers[i] != null)
                    {
                        // A CV is just a CD with missing stuff... put it into a CD and recurse
                        CV qualifierTemp = qualifiers[i].getValue();
                        CD qualifierTemp2 = new CD();
                        qualifierTemp2.setCode(qualifierTemp.getCode());
                        qualifierTemp2.setCodeSystem(qualifierTemp.getCodeSystem());
                        qualifierTemp2.setCodeSystemName(qualifierTemp.getCodeSystemName());
                        qualifierTemp2.setCodeSystemVersion(qualifierTemp.getCodeSystemVersion());
                        qualifierTemp2.setCodingRationale(qualifierTemp.getCodingRationale());
                        qualifierTemp2.setDisplayName(qualifierTemp.getDisplayName());
                        qualifierTemp2.setOriginalText(qualifierTemp.getOriginalText());
                        ValidateCodeReturn temp = validateCode(vocabularyDomain_name, qualifierTemp2,
                                                               applicationContext_code, activeConceptsOnly,
                                                               errorCheckOnly);
                        ValidationDetail[] details = temp.getDetail();
                        for (int j = 0; j < details.length; j++)
                        {
                            validationDetails.add(details[i]);
                        }
                    }
                }
            }

            if (!errorCheckOnly.isV())
            {
                if (isCS)
                {
                    if (!(codeToValidate.getCodeSystem() == null || codeToValidate.getCodeSystem().length() == 0)
                            || !(codeToValidate.getCodeSystemName() == null || codeToValidate.getCodeSystemName()
                                    .length() == 0)
                            || !(codeToValidate.getCodeSystemVersion() == null || codeToValidate.getCodeSystemVersion()
                                    .length() == 0))
                    {
                        validationDetails.add(makeValidationDetail("W001", codeToValidate));
                    }
                }
                else
                {

                    if (!doesCodeSystemExist(codeToValidate.getCodeSystem(), codeToValidate.getCodeSystemName()))
                    {
                        validationDetails.add(makeValidationDetail("W002", codeToValidate));
                    }
                }

                if (codeToValidate.getCodeSystemVersion() != null
                        && codeToValidate.getCodeSystemVersion().length() > 0
                        && !isCodeSystemVersionValid(codeToValidate.getCodeSystem(), codeToValidate
                                .getCodeSystemVersion()))
                {
                    validationDetails.add(makeValidationDetail("W003", codeToValidate));
                }

                if (codeToValidate.getDisplayName() != null
                        && codeToValidate.getDisplayName().length() > 0
                        && !doesDesignationExist("%", codeToValidate.getCode(), codeToValidate.getDisplayName()))
                {
                    validationDetails.add(makeValidationDetail("W004", codeToValidate));
                }

                if (translations != null && translations.length > 0)
                {
                    boolean translationFound = false;
                    for (int i = 0; i < translations.length; i++)
                    {
                        if (translations[i] != null
                                && translations[i].getCodingRationale() != null
                                && translations[i].getCodingRationale().getCode() != null
                                && translations[i].getCodingRationale().getCode().equals("HL7"))
                        {
                            translationFound = true;
                            break;
                        }
                    }
                    if (!translationFound)
                    {
                        validationDetails.add(makeValidationDetail("W005", codeToValidate));
                    }
                }

            }

            // TODO set error4, and 8 through 11, Warning 6 when necessary
        }

        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (UnknownApplicationContextCode e)
        {
            throw e;
        }
        catch (UnknownVocabularyDomain e)
        {
            throw e;
        }
        catch (NoApplicableValueSet e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));

        }
    }

    private ValidationDetail makeValidationDetail(String errorId, CD code)
    {
        ValidationDetail current = new ValidationDetail();
        current.setCodeInError(code);
        current.setError_id(Constructors.stm(errorId));
        current.setErrorText((ST) this.validateCodeReturnCodes.get(errorId));
        if (errorId.startsWith("E"))
        {
            current.setIsError(Constructors.blm(true));
        }
        else
        {
            current.setIsError(Constructors.blm(false));
        }
        return current;
    }

    public ValidateCodeReturn validateTranslation(ST parm1, CD parm2, ST parm3, BL parm4, BL parm5)
            throws UnexpectedError, org.hl7.CTSMAPI.UnknownVocabularyDomain, UnknownApplicationContextCode
    {
        // TODO Implement this org.hl7.CTSMAPI.RuntimeOperations method */
        throw new java.lang.UnsupportedOperationException("Method validateTranslation() not yet implemented.");
    }

    public CD translateCode(ST parm1, CD parm2, UID param5, ST parm3) throws UnexpectedError,
            org.hl7.CTSMAPI.UnableToTranslate, org.hl7.CTSMAPI.UnknownCodeSystem,
            org.hl7.CTSMAPI.UnknownApplicationContextCode, org.hl7.CTSMAPI.UnknownVocabularyDomain
    {
        // TODO Implement this org.hl7.CTSMAPI.RuntimeOperations method */
        throw new java.lang.UnsupportedOperationException("Method translateCode() not yet implemented.");
    }

    public BL areEquivalent(CD parm1, CD parm2) throws org.hl7.CTSMAPI.SubsumptionNotSupported,
            org.hl7.CTSMAPI.QualifiersNotSupported, org.hl7.CTSMAPI.UnknownConceptCode,
            org.hl7.CTSMAPI.UnknownCodeSystem, org.hl7.CTSMAPI.UnrecognizedQualifier, UnexpectedError
    {
        // TODO Implement this org.hl7.CTSMAPI.RuntimeOperations method */
        throw new java.lang.UnsupportedOperationException("Method areEquivalent() not yet implemented.");
    }

    public CD fillInDetails(CD codeToFillIn, ST displayLanguage_code) throws UnknownLanguage, UnexpectedError,
            UnknownConceptCode, UnknownCodeSystem, NoApplicableDesignationFound
    {
        logger.debug("fillInDetails called - Code: " + (codeToFillIn == null ? "null"
                : codeToFillIn.getCode()) + " CodeSystem: " + (codeToFillIn == null ? "null"
                : codeToFillIn.getCodeSystem()) + " language: " + (displayLanguage_code == null ? "null"
                : displayLanguage_code.getV()));

        PreparedStatement getCodeDetails = null;
        try
        {
            if (codeToFillIn == null
                    || codeToFillIn.getCodeSystem() == null
                    || codeToFillIn.getCodeSystem().length() == 0)
            {
                throw new UnknownCodeSystem();
            }
            if (codeToFillIn.getCode() == null || codeToFillIn.getCode().length() == 0)
            {
                throw new UnknownConceptCode();
            }
            if (displayLanguage_code == null
                    || displayLanguage_code.getV() == null
                    || displayLanguage_code.getV().length() == 0)
            {
                throw new UnknownLanguage();
            }

            getCodeDetails = queries_.checkOutStatement(queries_.GET_CODE_DETAILS);
            getCodeDetails.setString(1, codeToFillIn.getCodeSystem());
            getCodeDetails.setString(2, displayLanguage_code.getV());
            getCodeDetails.setString(3, codeToFillIn.getCode());
            ResultSet results = getCodeDetails.executeQuery();
            if (results.next())
            // Should return 0 or 1 rows... more than 1 is probably an error
            {
                codeToFillIn.setCodeSystemName(results.getString("codeSystemName"));
                codeToFillIn.setCodeSystemVersion(results.getString("releaseId"));
                codeToFillIn.setDisplayName(results.getString("designation"));
            }
            else
            {
                // Didn't get any results, need to throw an exception.

                if (!doesCodeSystemExist(codeToFillIn.getCodeSystem(), "%"))
                {
                    throw new UnknownCodeSystem(Constructors.uidm(codeToFillIn.getCodeSystem()));
                }

                ConceptId temp = new ConceptId();
                temp.setCodeSystem_id(Constructors.uidm(codeToFillIn.getCodeSystem()));
                temp.setConcept_code(Constructors.stm(codeToFillIn.getCode()));

                if (!doesCodeExist(temp))
                {
                    throw new UnknownConceptCode(temp);
                }

                if (!isLanguageValidForCodeSystem(codeToFillIn.getCodeSystem(), displayLanguage_code.getV()))
                {
                    throw new UnknownLanguage(displayLanguage_code);
                }

                else
                {
                    throw new NoApplicableDesignationFound(codeToFillIn, displayLanguage_code);
                }
            }
            return codeToFillIn;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownLanguage e)
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
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getCodeDetails);
        }
    }

    public ValueSetExpansion[] lookupValueSetExpansion(ST vocabularyDomain_name, ST applicationContext_code,
            ST language_code, BL expandAll, int timeout, int sizeLimit) throws UnknownLanguage, UnexpectedError,
            UnknownApplicationContextCode, UnknownVocabularyDomain, NoApplicableValueSet, TimeoutError
    {
        long startTime = System.currentTimeMillis();
        logger.debug("listValidCodes called - domain: "
                + vocabularyDomain_name.getV()
                + " language: "
                + (language_code == null ? ""
                        : language_code.getV())
                + " expandAll: "
                + expandAll.isV());

        if (applicationContext_code != null
                && applicationContext_code.getV() != null
                && applicationContext_code.getV().length() > 0)
        {
            // This throws an exception if it is not valid.
            isApplicationContextValid(applicationContext_code);
        }
        else
        {
            applicationContext_code = Constructors.stm("%");
        }

        // validate the vocabularyDomain name
        if (vocabularyDomain_name == null
                || vocabularyDomain_name.getV() == null
                || vocabularyDomain_name.getV().length() == 0
                || !isVocabularyDomainNameValid(vocabularyDomain_name))
        {
            throw new UnknownVocabularyDomain(vocabularyDomain_name);
        }

        ValueSetExpansion[] resultsToReturn = null;
        String valueSetId = "";

        PreparedStatement getVocabularyDomainId = null;
        PreparedStatement blankStatement = null;
        try
        {
            getVocabularyDomainId = queries_.checkOutStatement(queries_.GET_VOCABULARY_DOMAIN_ID);
            getVocabularyDomainId.setString(1, vocabularyDomain_name.getV());
            getVocabularyDomainId.setString(2, applicationContext_code.getV());
            ResultSet results = getVocabularyDomainId.executeQuery();
            if (results.next())
            // Want the first result
            {
                valueSetId = results.getString("definedbyvalueset");
            }
            else
            {
                throw new NoApplicableValueSet();
            }

            // validate the language
            if (language_code == null || !isLanguageValidForValueSetId(valueSetId, language_code.getV()))
            {
                throw new UnknownLanguage(language_code);
            }

            StringBuffer sql = new StringBuffer();
            sql
                    .append("SELECT VOC_nested_value_set.baseValueSetId, VOC_nested_value_set.nestingDepth, VOC_nested_value_set.nestedValueSetId, VOC_nested_value_set.codeSystem, VOC_nested_value_set.conceptCode, VOC_nested_value_set.nestedValueSetName, VOC_nested_value_set.isSelectable, VOC_nested_value_set.entryType, VCS_concept_designation.designation"
                            + " FROM VCS_concept_designation RIGHT JOIN VOC_nested_value_set"
                            + " ON (VCS_concept_designation.internalId = VOC_nested_value_set.internalId)"
                            + " WHERE ((VOC_nested_value_set.basevaluesetid='"
                            + valueSetId
                            + "')"
                            + " AND (VCS_concept_designation.language='"
                            + language_code.getV()
                            + "'"
                            + " Or VCS_concept_designation.language Is Null)"
                            + " AND (VCS_concept_designation.preferredforlanguage="
                            + booleanWrapper_
                            + "true"
                            + booleanWrapper_
                            + " Or VCS_concept_designation.preferredforlanguage Is Null)"
                            + " AND (VOC_nested_value_set.nestingdepth>=1)");
            if (!expandAll.isV())
            {
                sql.append(" AND (VOC_nested_value_set.nestingdepth<4)");
                // I'm getting the 1's 2s and 3s because I need the 3's to
                // find out if the 2s can expand
            }
            sql.append(") ORDER BY VOC_nested_value_set.huid");
            logger.debug("Generated SQL " + sql.toString());

            blankStatement = queries_.getArbitraryStatement(sql.toString());
            try
            {
                int temp = timeout / 1000;
                if (temp <= 0 && timeout > 0)
                {
                    temp = 1;
                }
                blankStatement.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            // Don't set a size limit on this one, the query returns more than I use - I discard some
            // in the post query filtering.
            results = blankStatement.executeQuery();
            resultsToReturn = codeSetExpansionHelper(results, !expandAll.isV(), 3, language_code.getV(), null, timeout,
                                                     sizeLimit, startTime);
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getVocabularyDomainId);
            queries_.checkInPreparedStatement(blankStatement);
        }
        return resultsToReturn;
    }

    /**
     * This method builds the results from a resultSet. It is protected so it can be used from BrowserOperationsImpl.
     * 
     * @param results - The result set to iterate
     * @param oneLevel - Return one level or all levels
     * @param maxNestingDepthPlusOne - One greater than the max nesting depth to return (ignored if oneLevel if false)
     * @param language - The language
     * @return - The CodeSetExpansion[]
     */
    private ValueSetExpansion[] codeSetExpansionHelper(ResultSet results, boolean oneLevel, int maxNestingDepthPlusOne,
            String language, String nestedValueSetId, int timeout, int sizeLimit, long startTime)
            throws UnexpectedError, TimeoutError
    {
        logger.debug("codeSetExpansionHelper called - oneLevel: "
                + oneLevel
                + "  maxNestingDepth - "
                + maxNestingDepthPlusOne);
        ArrayList resultsToReturn = new ArrayList();
        ValueSetExpansion valueSetExpansion;
        try
        {
            while (results.next())
            {
                valueSetExpansion = new ValueSetExpansion();
                valueSetExpansion.setDisplayName(Constructors.stm(results.getString("designation")));
                valueSetExpansion.setPathLength(Constructors.intfm(results.getInt("nestingDepth")));
                valueSetExpansion.setConcept_id(Constructors.conceptIdm(results.getString("codeSystem"), results
                        .getString("conceptCode")));
                valueSetExpansion.setValueSet(Constructors.valueSetDescriptor(results.getString("nestedValueSetId"),
                                                                              results.getString("nestedValueSetName")));
                valueSetExpansion.setNodeType_code(Constructors.stm(results.getString("entryType")));
                if (resultsToReturn.size() > 0)
                {
                    if ((oneLevel)
                            && ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1)).getPathLength()
                                    .getV() == maxNestingDepthPlusOne)
                    {
                        // If the last one added is depth
                        // maxNestingDepthPlusOne, and they only wanted top
                        // level, strip it.
                        resultsToReturn.remove(resultsToReturn.size() - 1);
                    }
                    else
                    {
                        // Set the can expand on the previous one added.
                        if (valueSetExpansion.getPathLength().getV() > ((ValueSetExpansion) resultsToReturn
                                .get(resultsToReturn.size() - 1)).getPathLength().getV())
                        {
                            ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1))
                                    .setIsExpandable(Constructors.blm(true));
                        }
                        else
                        {
                            ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1))
                                    .setIsExpandable(Constructors.blm(false));
                            ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1))
                                    .setExpansionContext(new byte[]{});
                        }
                    }
                }
                valueSetExpansion.setExpansionContext(new MAPIExpansionContext(results.getString("baseValueSetId"),
                        valueSetExpansion.getValueSet().getValueSet_id().getV(), valueSetExpansion.getPathLength()
                                .getV(), language, timeout, sizeLimit).toString().getBytes());
                resultsToReturn.add(valueSetExpansion);

                timeup(startTime, timeout);
            }
            if (resultsToReturn.size() > 0)
            {
                if ((oneLevel)
                        && ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1)).getPathLength().getV() == maxNestingDepthPlusOne)
                {
                    // If the last one added is depth maxNestingDepthPlusOne,
                    // and they only wanted top level, strip it.
                    resultsToReturn.remove(resultsToReturn.size() - 1);
                }
                else
                {
                    // Set the can Expand on the last code set - it has to be
                    // false
                    ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1)).setIsExpandable(Constructors
                            .blm(false));
                    ((ValueSetExpansion) resultsToReturn.get(resultsToReturn.size() - 1))
                            .setExpansionContext(new byte[]{});
                }
            }
            if (nestedValueSetId != null)
            // special case for expandValueSetExpansionContext method
            {
                /*
                 * The expandValueSetExpansionContext method always expands by one level. Therefore, the only nesting
                 * depths I return should be maxNestingDepthPlusOne - 1.
                 */
                for (int i = 0; i < resultsToReturn.size(); i++)
                {
                    if (((ValueSetExpansion) resultsToReturn.get(i)).getValueSet().getValueSet_id().getV()
                            .equals(nestedValueSetId))
                    {
                        // If the valueSet Id is the one we want, I need to
                        // remove the current item - I want
                        // to keep the expanded code(s) - not the code we were
                        // expanding.
                        // I also need to keep all of the following codes until
                        // the nesting depth drops again.
                        int temp = ((ValueSetExpansion) resultsToReturn.get(i)).getPathLength().getV();
                        resultsToReturn.remove(i);
                        for (int j = 0; i + j < resultsToReturn.size(); j++)
                        {
                            if (temp != ((ValueSetExpansion) resultsToReturn.get(i + j)).getPathLength().getV())
                            {
                                // Don't remove all of the items with a greater
                                // path length than the one we
                                // were looking for. (do nothing)
                            }
                            else
                            {
                                // If the path links were equal, then we are
                                // done saving results. All others should be
                                // removed.
                                i = i + j - 1;
                                break;
                            }
                        }
                    }
                    else
                    {
                        resultsToReturn.remove(i);
                        i--;
                    }
                    timeup(startTime, timeout);
                }
            }
            if (sizeLimit != 0 && resultsToReturn.size() > sizeLimit)
            {
                // trim the size if it is to big.
                // it would be quicker to stop adding things if it got too big,
                // but the way it is written makes that hard to do...
                resultsToReturn = new ArrayList(resultsToReturn.subList(0, sizeLimit));
            }
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        return (ValueSetExpansion[]) resultsToReturn.toArray(new ValueSetExpansion[resultsToReturn.size()]);
    }

    public ValueSetExpansion[] expandValueSetExpansionContext(byte[] expansionContext) throws InvalidExpansionContext,
            UnexpectedError, TimeoutError
    {
        long startTime = System.currentTimeMillis();
        logger.debug("ExpandValidCodeNode called - expansionContext: " + expansionContext);
        ValueSetExpansion[] resultsToReturn = null;
        MAPIExpansionContext myExpansionContext;
        try
        {
            myExpansionContext = MAPIExpansionContext.getExpansionContext(expansionContext);
        }
        catch (Exception e)
        {
            throw new InvalidExpansionContext();
        }

        PreparedStatement blankStatement = null;
        try
        {
            StringBuffer sql = new StringBuffer();
            sql
                    .append("SELECT VOC_nested_value_set.baseValueSetId, VOC_nested_value_set.nestingDepth, VOC_nested_value_set.nestedValueSetId, VOC_nested_value_set.codeSystem, VOC_nested_value_set.conceptCode, VOC_nested_value_set.nestedValueSetName, VOC_nested_value_set.isSelectable, VOC_nested_value_set.entryType, VCS_concept_designation.designation"
                            + " FROM VCS_concept_designation RIGHT JOIN VOC_nested_value_set"
                            + " ON (VCS_concept_designation.internalId = VOC_nested_value_set.internalId)"
                            + " WHERE ((VOC_nested_value_set.basevaluesetid='"
                            + myExpansionContext.baseValueSetId_
                            + "')"
                            + " AND (VCS_concept_designation.language='"
                            + myExpansionContext.language_
                            + "'"
                            + " Or VCS_concept_designation.language Is Null)"
                            + " AND (VCS_concept_designation.preferredforlanguage="
                            + booleanWrapper_
                            + "true"
                            + booleanWrapper_
                            + " Or VCS_concept_designation.preferredforlanguage Is Null)"
                            + " AND (VOC_nested_value_set.nestingdepth >= "
                            + myExpansionContext.nestingDepth_
                            + ")");
            sql.append(" AND (VOC_nested_value_set.nestingdepth < " + (myExpansionContext.nestingDepth_ + 3) + ")");
            sql.append(") ORDER BY VOC_nested_value_set.huid");
            logger.debug("Generated SQL " + sql.toString());

            blankStatement = queries_.getArbitraryStatement(sql.toString());
            try
            {
                int temp = myExpansionContext.timeout_ / 1000;
                if (temp <= 0 && myExpansionContext.timeout_ > 0)
                {
                    temp = 1;
                }
                blankStatement.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            // Don't set a size limit on this one, the query returns more than I use - I discard some
            // in the post query filtering.
            ResultSet results = blankStatement.executeQuery();
            resultsToReturn = codeSetExpansionHelper(results, true, myExpansionContext.nestingDepth_ + 2,
                                                     myExpansionContext.language_,
                                                     myExpansionContext.nestedValueSetId_, myExpansionContext.timeout_,
                                                     myExpansionContext.sizeLimit_, startTime);
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, myExpansionContext.timeout_);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(blankStatement);
        }
        return resultsToReturn;
    }

    private boolean doesDesignationExist(String codeSystemId, String code, String designation) throws UnexpectedError
    {
        logger.debug("doesDesignationExist called - codeSystemId: "
                + codeSystemId
                + " code: "
                + code
                + " designation: "
                + designation);
        PreparedStatement doesDesignationExist = null;
        try
        {
            doesDesignationExist = queries_.checkOutStatement(queries_.DOES_DESIGNATION_EXIST);
            doesDesignationExist.setString(1, codeSystemId);
            doesDesignationExist.setString(2, designation);
            doesDesignationExist.setString(3, code);
            ResultSet results = doesDesignationExist.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(doesDesignationExist);
        }
        return false;
    }

    private boolean doesCodeSystemExist(String codeSystemId, String codeSystemName) throws UnexpectedError
    {
        logger.debug("doesCodeSystemExist called - codeSystemId: " + codeSystemId);
        PreparedStatement doesCodeSystemExist = null;
        try
        {
            doesCodeSystemExist = queries_.checkOutStatement(queries_.DOES_CODE_SYSTEM_EXIST);
            doesCodeSystemExist.setString(1, codeSystemId);
            doesCodeSystemExist.setString(2, codeSystemName);
            ResultSet results = doesCodeSystemExist.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(doesCodeSystemExist);
        }
        return false;
    }

    private boolean doesCodeExist(ConceptId code) throws UnexpectedError
    {
        logger.debug("doesCodeExist called - code.codeSystemId: "
                + code.getCodeSystem_id().getV()
                + " code.code: "
                + code.getConcept_code().getV());

        PreparedStatement doesConceptCodeExist = null;
        try
        {
            doesConceptCodeExist = queries_.checkOutStatement(queries_.DOES_CONCEPT_CODE_EXIST);
            doesConceptCodeExist.setString(1, code.getCodeSystem_id().getV());
            doesConceptCodeExist.setString(2, code.getConcept_code().getV());
            ResultSet results = doesConceptCodeExist.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(doesConceptCodeExist);
        }
        return false;
    }

    private boolean isDataTypeAllowedForVocDomain(String vocDomainName, String dataType) throws UnexpectedError
    {
        // TODO this method is not 100% correct (according to Harold)
        logger.debug("isCodingSchemeRequiredForVocDomain - vocDomain: " + vocDomainName + " datatype - " + dataType);

        PreparedStatement isDataTypeAllowedForVocDomain = null;
        try
        {
            isDataTypeAllowedForVocDomain = queries_.checkOutStatement(queries_.IS_DATA_TYPE_ALLOWED_FOR_VOC_DOMAIN);
            isDataTypeAllowedForVocDomain.setString(1, vocDomainName);
            isDataTypeAllowedForVocDomain.setString(2, dataType);
            ResultSet results = isDataTypeAllowedForVocDomain.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isDataTypeAllowedForVocDomain);
        }
        return false;
    }

    private boolean isLanguageValidForValueSetId(String valueSetId, String language) throws UnexpectedError
    {
        logger.debug("isLanguageValidForValueSetId called - valueSetId: " + valueSetId + " language: " + language);

        PreparedStatement doesLanguageExistForValueSet = null;
        try
        {
            doesLanguageExistForValueSet = queries_.checkOutStatement(queries_.DOES_LANGUAGE_EXIST_FOR_VALUESET);
            doesLanguageExistForValueSet.setString(1, valueSetId);
            doesLanguageExistForValueSet.setString(2, language);
            ResultSet results = doesLanguageExistForValueSet.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;

            else
                return false;

        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(doesLanguageExistForValueSet);
        }
    }

    private boolean isCodingRationaleValid(String rationale) throws UnexpectedError
    {
        logger.debug("isCodingRationaleValid called - rationale: " + rationale);

        PreparedStatement isCodingRationaleValid = null;
        try
        {
            isCodingRationaleValid = queries_.checkOutStatement(queries_.IS_CODING_RATIONALE_VALID);
            isCodingRationaleValid.setString(1, rationale);
            ResultSet results = isCodingRationaleValid.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;

            else
                return false;

        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isCodingRationaleValid);
        }
    }

    private boolean isCodeSystemVersionValid(String codeSystem, String version) throws UnexpectedError
    {
        logger.debug("isCodeSystemVersionValid called - codeSystem: " + codeSystem);

        PreparedStatement isCodeSystemVersionValid = null;
        try
        {
            isCodeSystemVersionValid = queries_.checkOutStatement(queries_.IS_CODE_SYSTEM_VERSION_VALID);
            isCodeSystemVersionValid.setString(1, codeSystem);
            isCodeSystemVersionValid.setString(2, version);
            ResultSet results = isCodeSystemVersionValid.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;

            else
                return false;

        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isCodeSystemVersionValid);
        }
    }

    private boolean isLanguageValidForCodeSystem(String codeSystemId, String language) throws UnexpectedError
    {
        logger.debug("isLanguageValidForCodeSystem called - codeSystemId: " + codeSystemId + " language: " + language);

        PreparedStatement doesLanguageExistForCodeSystem = null;
        try
        {
            doesLanguageExistForCodeSystem = queries_.checkOutStatement(queries_.DOES_LANGUAGE_EXIST_FOR_CODE_SYSTEM);
            doesLanguageExistForCodeSystem.setString(1, codeSystemId);
            doesLanguageExistForCodeSystem.setString(2, language);
            ResultSet results = doesLanguageExistForCodeSystem.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
                return true;

            else
                return false;

        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(doesLanguageExistForCodeSystem);
        }
    }

    private boolean doesCodeExistInCodeSystem(String baseValueSetId, String codeSystemId, String conceptCode)
            throws UnexpectedError
    {
        logger.debug("doesCodeExistInCodeSystem called - codeSystemId: "
                + baseValueSetId
                + " language: "
                + codeSystemId);

        PreparedStatement doesCodeExistInCodeSystem = null;
        try
        {
            doesCodeExistInCodeSystem = queries_.checkOutStatement(queries_.DOES_CODE_EXIST_IN_CODE_SYSTEM);
            doesCodeExistInCodeSystem.setString(1, baseValueSetId);
            doesCodeExistInCodeSystem.setString(2, codeSystemId);
            doesCodeExistInCodeSystem.setString(3, conceptCode);
            ResultSet results = doesCodeExistInCodeSystem.executeQuery();
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
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(doesCodeExistInCodeSystem);
        }
    }

    private boolean isApplicationContextValid(ST applicationContextCode) throws UnexpectedError,
            UnknownApplicationContextCode
    {
        PreparedStatement isApplicationContextValid = null;
        try
        {
            isApplicationContextValid = queries_.checkOutStatement(queries_.IS_APPLICATION_CONTEXT_VALID);
            logger.debug("Validating application context code " + applicationContextCode.getV());
            isApplicationContextValid.setString(1, applicationContextCode.getV());
            ResultSet results = isApplicationContextValid.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
            {
                return true;
            }
            else
            {
                throw new UnknownApplicationContextCode(applicationContextCode);
            }
        }
        catch (UnknownApplicationContextCode e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isApplicationContextValid);
        }
    }

    private boolean isVocabularyDomainNameValid(ST vocabularyDomain_name) throws UnknownVocabularyDomain,
            UnexpectedError
    {
        PreparedStatement isVocabularyDomainNameValid = null;
        try
        {
            isVocabularyDomainNameValid = queries_.checkOutStatement(queries_.IS_VOCABULARY_DOMAIN_NAME_VALID);
            logger.debug("Validating vocabulary domain name " + vocabularyDomain_name.getV());
            isVocabularyDomainNameValid.setString(1, vocabularyDomain_name.getV());
            ResultSet results = isVocabularyDomainNameValid.executeQuery();
            // one and only one result
            results.next();
            if (results.getInt("found") > 0)
            {
                return true;
            }
            else
            {
                throw new UnknownVocabularyDomain(vocabularyDomain_name);
            }
        }
        catch (UnknownVocabularyDomain e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isVocabularyDomainNameValid);
        }
    }

    private String wrapSearchStringForAlgorithm(ST searchString, ST algorithm) throws UnknownMatchAlgorithm
    {
        if (searchString == null || searchString.getV() == null || searchString.getV().length() == 0)
        {
            searchString = Constructors.stm("%");
        }
        if (algorithm == null || algorithm.getV() == null || algorithm.getV().length() == 0)
        {
            return searchString.getV();
        }
        else if (algorithm.getV().equals("IdenticalIgnoreCase"))
        {
            return searchString.getV();
        }
        else if (algorithm.getV().equals("StartsWithIgnoreCase"))
        {
            return searchString.getV() + "%";
        }
        else if (algorithm.getV().equals("EndsWithIgnoreCase"))
        {
            return "%" + searchString.getV();
        }
        else if (algorithm.getV().equals("ContainsPhraseIgnoreCase"))
        {
            return "%" + searchString.getV() + "%";
        }
        else
        {
            throw new UnknownMatchAlgorithm(algorithm);
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

    private void populateValidateCodeReturns()
    {
        validateCodeReturnCodes.put("E001", Constructors.stm("Unknown code system"));
        validateCodeReturnCodes.put("E002", Constructors.stm("Invalid concept code for code system"));
        validateCodeReturnCodes.put("E003", Constructors.stm("Code system not valid for vocabulary domain"));
        validateCodeReturnCodes.put("E004", Constructors.stm("Concept code is not active"));
        validateCodeReturnCodes.put("E005", Constructors.stm("Concept code is not valid for vocabulary domain"));
        validateCodeReturnCodes.put("E006", Constructors.stm("Qualifiers not allowed for data type"));
        validateCodeReturnCodes.put("E007", Constructors.stm("Translation not allowed for data type"));
        validateCodeReturnCodes.put("E008", Constructors.stm("Invalid role name for vocabulary domain"));
        validateCodeReturnCodes.put("E009", Constructors.stm("Role name must be supplied for vocabulary domain"));
        validateCodeReturnCodes.put("E010", Constructors.stm("Invalid value for qualifier"));
        validateCodeReturnCodes.put("E011", Constructors.stm("Invalid translation"));
        validateCodeReturnCodes.put("E012", Constructors.stm("Missing code system"));
        validateCodeReturnCodes.put("E013", Constructors.stm("Missing concept code"));
        validateCodeReturnCodes.put("E014", Constructors.stm("Unknown coding rationale"));

        validateCodeReturnCodes.put("W001", Constructors.stm("Code system not allowed in CS data type"));
        validateCodeReturnCodes.put("W002", Constructors.stm("Code system name doesn't match code system"));
        validateCodeReturnCodes.put("W003", Constructors.stm("Unknown code system version"));
        validateCodeReturnCodes.put("W004", Constructors.stm("Display name incorrect for concept code"));
        validateCodeReturnCodes.put("W005", Constructors.stm("No HL7 translation present"));
        validateCodeReturnCodes.put("W006", Constructors.stm("Concept code is not active"));

    }
}