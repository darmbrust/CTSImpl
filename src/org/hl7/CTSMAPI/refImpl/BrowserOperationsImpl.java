package org.hl7.CTSMAPI.refImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.hl7.CTSMAPI.BadlyFormedMatchText;
import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.CTSVersionId;
import org.hl7.CTSMAPI.CodeSystemDescriptor;
import org.hl7.CTSMAPI.CodeSystemInfo;
import org.hl7.CTSMAPI.CodeSystemNameIdMismatch;
import org.hl7.CTSMAPI.CodeSystemRegistration;
import org.hl7.CTSMAPI.ConceptId;
import org.hl7.CTSMAPI.FullValueSetDescription;
import org.hl7.CTSMAPI.NoApplicableValueSet;
import org.hl7.CTSMAPI.RIMAttributeId;
import org.hl7.CTSMAPI.RIMCodedAttribute;
import org.hl7.CTSMAPI.TimeoutError;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.UnknownApplicationContextCode;
import org.hl7.CTSMAPI.UnknownCodeSystem;
import org.hl7.CTSMAPI.UnknownConceptCode;
import org.hl7.CTSMAPI.UnknownMatchAlgorithm;
import org.hl7.CTSMAPI.UnknownValueSet;
import org.hl7.CTSMAPI.UnknownVocabularyDomain;
import org.hl7.CTSMAPI.ValueSetCodeReference;
import org.hl7.CTSMAPI.ValueSetConstructor;
import org.hl7.CTSMAPI.ValueSetDescription;
import org.hl7.CTSMAPI.ValueSetDescriptor;
import org.hl7.CTSMAPI.ValueSetNameIdMismatch;
import org.hl7.CTSMAPI.VocabularyDomainDescription;
import org.hl7.CTSMAPI.VocabularyDomainValueSet;
import org.hl7.cts.types.BL;
import org.hl7.cts.types.ST;
import org.hl7.cts.types.UID;
import org.hl7.utility.CTSConstants;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Constructors;
import edu.mayo.informatics.cts.utility.Utility;
import edu.mayo.informatics.cts.utility.Utility.SQLConnectionInfo;

/**
 * <pre>
 * Title: BrowserOperationsImpl 
 * Description: A reference implementation of BrowserOperationsImpl. 
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
 * @version 1.0 - cvs $Revision: 1.76 $ checked in on $Date: 2005/10/14 15:44:10 $
 */
public class BrowserOperationsImpl implements BrowserOperations
{
    public final static Logger logger                    = Logger.getLogger("org.hl7.MAPI_Browser");
    private SQLStatements      queries_;

    private final ST[]         supportedMatchAlgorithms_ = Constructors.stArray(new String[]{"IdenticalIgnoreCase",
            "StartsWithIgnoreCase", "EndsWithIgnoreCase", "ContainsPhraseIgnoreCase"});

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
     * If you are using this library locally, this is the method you want to instantiate a BrowserOperations object.
     * 
     * @param username if null, loaded from props
     * @param password if null, loaded from props
     * @param server if null, loaded from props
     * @param driver if null, loaded from props
     * @param initLogger whether or not to initialize log4j.
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
    static public synchronized BrowserOperations _interface(String xmlLexGridSQLString, String username, String password, 
            boolean loadProps, boolean initLogger)
            throws UnexpectedError

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
            throw new UnexpectedError(Constructors.stm("Problem parsing the the XML connection info. " + e.toString() + " "
                    + (e.getCause() == null ? ""
                            : e.getCause().toString())));
        }
        return (BrowserOperations) svc;
    }

    /**
     * This has to be public for AXIS to work properly. It should not be used.
     */
    public BrowserOperationsImpl()
    {
        try
        {
            CTSConfigurator._instance().initialize();
            logger.debug("CTSMAPI.BrowserOperationImpl Constructor called");
            queries_ = SQLStatements.instance(null, null, null, null);

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
            logger.debug("CTSMAPI.BrowserOperationImpl Constructor called");
            queries_ = SQLStatements.instance(username, password, server, driver);
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
        logger.debug("BrowserName called");
        try
        {
            return Constructors.stm("Mayo CTS Mapi Browser");
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
        logger.debug("ServiceVersion called");
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

    public CTSVersionId getCTSVersion() throws UnexpectedError
    {
        logger.debug("getCTSVersion called");
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
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getHL7ReleaseVersion);
        }
        return Constructors.stm(resultToReturn.toString());
    }

    public ST getServiceDescription() throws UnexpectedError
    {
        logger.debug("browserDescription called");
        try
        {
            return Constructors.stm("This is the Mayo Implementation of the CTS " + CTSConstants.MAJOR_VERSION + "." + CTSConstants.MINOR_VERSION + " Final Draft.");
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
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

    public RIMCodedAttribute[] getSupportedAttributes(ST matchText, ST matchAlgorithm_code, int timeout, int sizeLimit)
            throws BadlyFormedMatchText, UnknownMatchAlgorithm, TimeoutError, UnexpectedError
    {
        logger.debug("supportedAttributes called");
        RIMCodedAttribute rimCodedAttribute;
        RIMAttributeId rimAttributeId;
        ArrayList resultsToReturn = new ArrayList();
        long startTime = System.currentTimeMillis();
        PreparedStatement getActiveAttributes = null;
        try
        {
            getActiveAttributes = queries_.checkOutStatement(queries_.GET_ACTIVE_ATTRIBUTES);
            try
            {
                int temp = timeout / 1000;
                if (temp <= 0 && timeout > 0)
                {
                    temp = 1;
                }
                getActiveAttributes.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            getActiveAttributes.setMaxRows(sizeLimit);
            getActiveAttributes.setString(1, wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code));
            ResultSet results = getActiveAttributes.executeQuery();
            timeup(startTime, timeout);

            while (results.next())
            {
                rimCodedAttribute = new RIMCodedAttribute();
                rimAttributeId = new RIMAttributeId();
                rimAttributeId.setAttribute_name(Constructors.stm(results.getString("attName")));
                rimAttributeId.setClass_name(Constructors.stm(results.getString("classname")));
                rimAttributeId.setModel_id(Constructors.stm(results.getString("modelId")));
                rimCodedAttribute.setRIMAttribute_id(rimAttributeId);
                rimCodedAttribute.setCodingStrength_code(Constructors.stm(results.getString("vocStrength")));
                rimCodedAttribute.setVocabularyDomain_name(Constructors.stm(results.getString("vocDomain")));
                rimCodedAttribute.setDataType_code(Constructors.stm(results.getString("attDataType")));
                resultsToReturn.add(rimCodedAttribute);
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            //throw new BadlyFormedMatchText(matchText);
            //TODO throw badly formed match text when necessary
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (UnknownMatchAlgorithm e)
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
            queries_.checkInPreparedStatement(getActiveAttributes);
        }

        return (RIMCodedAttribute[]) resultsToReturn.toArray(new RIMCodedAttribute[resultsToReturn.size()]);
    }

    public ST[] getSupportedVocabularyDomains(ST matchText, ST matchAlgorithm_code, int timeout, int sizeLimit)
            throws BadlyFormedMatchText, UnknownMatchAlgorithm, TimeoutError, UnexpectedError
    {
        logger.debug("supportedVocabularyDomains called");
        long startTime = System.currentTimeMillis();
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
            timeup(startTime, timeout);
            while (results.next())
            {
                supportedVocabularyDomains.add(Constructors.stm(results.getString("vocDomain")));
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            //throw new BadlyFormedMatchText(matchText.getV());
            //TODO throw badly formed match text when necessary
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        catch (TimeoutError e)
        {
            throw e;
        }
        catch (UnknownMatchAlgorithm e)
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
            queries_.checkInPreparedStatement(getVocabularyDomains);
        }
        return (ST[]) supportedVocabularyDomains.toArray(new ST[supportedVocabularyDomains.size()]);
    }

    public ValueSetDescriptor[] getSupportedValueSets(ST matchText, ST matchAlgorithm_code, int timeout, int sizeLimit)
            throws BadlyFormedMatchText, UnknownMatchAlgorithm, TimeoutError, UnexpectedError
    {
        logger.debug("supportedValueSets called");
        return getMatchedValueSets("%", wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code), timeout,
                                   sizeLimit);
    }

    private ValueSetDescriptor[] getMatchedValueSets(String valueSetId, String valueSetName, int timeout, int sizeLimit)
            throws UnexpectedError, TimeoutError
    {
        logger.debug("getMatchedValueSets called - valueSetId: " + valueSetId);
        long startTime = System.currentTimeMillis();
        ArrayList supportedValueSets = new ArrayList();
        if (valueSetId == null || valueSetId.length() == 0)
        {
            valueSetId = "%";
        }
        PreparedStatement getSupportedValueSetsMinimal = null;
        try
        {
            getSupportedValueSetsMinimal = queries_.checkOutStatement(queries_.GET_SUPPORTED_VALUE_SETS_MINIMAL);
            try
            {
                int temp = timeout / 1000;
                if (temp <= 0 && timeout > 0)
                {
                    temp = 1;
                }
                getSupportedValueSetsMinimal.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            getSupportedValueSetsMinimal.setMaxRows(sizeLimit);
            getSupportedValueSetsMinimal.setString(1, valueSetId);
            getSupportedValueSetsMinimal.setString(2, valueSetName);
            ResultSet results = getSupportedValueSetsMinimal.executeQuery();
            timeup(startTime, timeout);
            while (results.next())
            {
                supportedValueSets.add(Constructors.valueSetDescriptor(results.getString("valueSetId"), results
                        .getString("valueSetName")));
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            //throw new BadlyFormedMatchText(matchText.getV());
            //TODO throw badly formed match text when necessary
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getSupportedValueSetsMinimal);
        }
        return (ValueSetDescriptor[]) supportedValueSets.toArray(new ValueSetDescriptor[supportedValueSets.size()]);
    }

    public CodeSystemDescriptor[] getSupportedCodeSystems(ST matchText, ST matchAlgorithm_code, int timeout,
            int sizeLimit) throws BadlyFormedMatchText, UnknownMatchAlgorithm, TimeoutError, UnexpectedError
    {
        logger.debug("supportedCodeSystems called");

        try
        {
            return codeSystemsHelper("%", wrapSearchStringForAlgorithm(matchText, matchAlgorithm_code), timeout,
                                     sizeLimit); //Their all active

        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (UnknownMatchAlgorithm e)
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
            //throw new BadlyFormedMatchText(matchText.getV());
            //TODO throw badly formed match text when necessary
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
    }

    private CodeSystemDescriptor[] codeSystemsHelper(String codeSystemId, String codeSystemName, int timeout,
            int sizeLimit) throws UnexpectedError, UnknownCodeSystem, CodeSystemNameIdMismatch, TimeoutError
    {
        logger.debug("codeSystemsHelper called: codeSystemStr = " + (codeSystemName == null ? "null"
                : codeSystemName));
        long startTime = System.currentTimeMillis();

        ArrayList resultsToReturn = new ArrayList();
        CodeSystemDescriptor codeSystemDescriptor;
        if ((codeSystemName == null) || (codeSystemName.length() == 0))
            codeSystemName = "%";
        //If they don't supply either, its an error.
        if (((codeSystemName == null) || (codeSystemName.length() == 0))
                && ((codeSystemId == null) || (codeSystemId.length() == 0)))
        {
            throw new UnknownCodeSystem(Constructors.uidm(codeSystemId));
        }
        //If one or the other is missing, its ok.
        if ((codeSystemName == null) || (codeSystemName.length() == 0))
        {
            codeSystemName = "%";
        }
        if ((codeSystemId == null) || (codeSystemId.length() == 0))
        {
            codeSystemId = "%";
        }

        PreparedStatement getActiveCodeSystems = null;
        try
        {
            getActiveCodeSystems = queries_.checkOutStatement(queries_.GET_ACTIVE_CODE_SYSTEMS);
            try
            {
                int temp = timeout / 1000;
                if (temp <= 0 && timeout > 0)
                {
                    temp = 1;
                }
                getActiveCodeSystems.setQueryTimeout(temp);
            }
            catch (SQLException e1)
            {
                logger.info("Timeout not implemented on database");
            }
            getActiveCodeSystems.setMaxRows(sizeLimit);
            getActiveCodeSystems.setString(1, codeSystemName);
            getActiveCodeSystems.setString(2, codeSystemId);
            ResultSet results = getActiveCodeSystems.executeQuery();
            timeup(startTime, timeout);
            while (results.next())
            {
                codeSystemDescriptor = new CodeSystemDescriptor();
                codeSystemDescriptor.setCodeSystem_id(Constructors.uidm(results.getString("codeSystemId")));
                codeSystemDescriptor.setCodeSystem_name(Constructors.stm(results.getString("codeSystemName")));
                codeSystemDescriptor.setCopyright(Constructors.stm(results.getString("copyrightNotice")));
                codeSystemDescriptor.setAvailableReleases(Constructors.stArray(new String[]{results
                        .getString("releaseId")}));
                resultsToReturn.add(codeSystemDescriptor);
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            timeup(startTime, timeout);
            //throw new BadlyFormedMatchText(matchText.getV());
            //TODO throw badly formed match text when necessary
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getActiveCodeSystems);
        }

        if (resultsToReturn.size() == 0)
        {
            //If either of them was missing and there were no results...
            // error.
            if (codeSystemName.equals("%") || codeSystemId.equals("%"))
            {
                throw new UnknownCodeSystem(Constructors.uidm(codeSystemId));
            }
            // They were both supplied, found nothing. Try again with only the
            // valueSetId.
            try
            {
                getActiveCodeSystems = queries_.checkOutStatement(queries_.GET_ACTIVE_CODE_SYSTEMS);
                try
                {
                    int temp = timeout / 1000;
                    if (temp <= 0 && timeout > 0)
                    {
                        temp = 1;
                    }
                    getActiveCodeSystems.setQueryTimeout(temp);
                }
                catch (SQLException e1)
                {
                    logger.info("Timeout not implemented on database");
                }
                getActiveCodeSystems.setMaxRows(sizeLimit);
                getActiveCodeSystems.setString(1, "%");
                getActiveCodeSystems.setString(2, codeSystemId);
                ResultSet results = getActiveCodeSystems.executeQuery();
                if (results.next())
                {
                    //Found results when just using codeSystemId
                    throw new CodeSystemNameIdMismatch(Constructors.uidm(codeSystemId), Constructors.stm(codeSystemName));
                }
                else
                //didn't find anything this way either.
                {
                    throw new UnknownCodeSystem(Constructors.uidm(codeSystemId));
                }
            }
            catch (SQLException e)
            {
                logger.error(e);
                timeup(startTime, timeout);
                //throw new BadlyFormedMatchText(matchText.getV());
                //TODO throw badly formed match text when necessary
                throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                        : e.getCause().toString())));
            }
            finally
            {
                queries_.checkInPreparedStatement(getActiveCodeSystems);
            }
        }
        return (CodeSystemDescriptor[]) resultsToReturn.toArray(new CodeSystemDescriptor[resultsToReturn.size()]);
    }

    public VocabularyDomainDescription lookupVocabularyDomain(ST domain)
            throws org.hl7.CTSMAPI.UnknownVocabularyDomain, UnexpectedError
    {
        logger.debug("lookupVocabularyDomain called: domain = " + (domain == null ? "null"
                : domain.getV()));
        try
        {
            VocabularyDomainDescription[] vocabularyDomains = lookupVocabularyDomainHelper(domain.getV());
            if ((vocabularyDomains == null) || vocabularyDomains.length != 1)
                throw new UnknownVocabularyDomain(domain);
            else
                return vocabularyDomains[0];
        }
        catch (UnexpectedError e)
        {
            throw e;
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
    }

    private VocabularyDomainDescription[] lookupVocabularyDomainHelper(String vocDomain) throws UnexpectedError
    {
        logger.debug("lookupVocabularyDomainHelper called: vocDomain = " + (vocDomain == null ? "null"
                : vocDomain));
        if ((vocDomain == null) || (vocDomain.length() == 0))
            vocDomain = "%";
        VocabularyDomainDescription vocabularyDomain;
        ArrayList resultsToReturn = new ArrayList();
        RIMCodedAttribute rimAttribute;
        RIMAttributeId rimAttributeId;
        VocabularyDomainValueSet vocabularyDomainValueSet;
        PreparedStatement getVocabularyDomains = null;
        PreparedStatement getBasisOfDomains = null;
        PreparedStatement getRimAttribute = null;
        PreparedStatement getValueSet = null;
        try
        {
            getVocabularyDomains = queries_.checkOutStatement(queries_.GET_VOCABULARY_DOMAINS);
            getBasisOfDomains = queries_.checkOutStatement(queries_.GET_BASIS_OF_DOMAINS);
            getRimAttribute = queries_.checkOutStatement(queries_.GET_RIM_ATTRIBUTE);
            getValueSet = queries_.checkOutStatement(queries_.GET_VALUE_SET);

            getVocabularyDomains.setString(1, vocDomain);
            ResultSet results = getVocabularyDomains.executeQuery();
            while (results.next())
            {
                vocabularyDomain = new VocabularyDomainDescription();
                vocabularyDomain.setVocabularyDomain_name(Constructors.stm(results.getString("vocDomain")));
                vocabularyDomain.setDescription(Constructors.stm(results.getString("description")));
                vocabularyDomain.setRestrictsDomain_name(Constructors.stm(results.getString("restrictsDomain")));

                getBasisOfDomains.setString(1, vocabularyDomain.getVocabularyDomain_name().getV());
                ResultSet innerResults = getBasisOfDomains.executeQuery();
                ArrayList basisOfDomains = new ArrayList();
                while (innerResults.next())
                {
                    basisOfDomains.add(Constructors.stm(innerResults.getString("vocDomain")));
                }
                vocabularyDomain.setBasisOfDomains((ST[]) basisOfDomains.toArray(new ST[basisOfDomains.size()]));
                ArrayList rimAttributes = new ArrayList();

                getRimAttribute.setString(1, vocabularyDomain.getVocabularyDomain_name().getV());
                innerResults = getRimAttribute.executeQuery();

                while (innerResults.next())
                {
                    rimAttribute = new RIMCodedAttribute();
                    rimAttributeId = new RIMAttributeId();
                    rimAttributeId.setAttribute_name(Constructors.stm(innerResults.getString("attName")));
                    rimAttributeId.setClass_name(Constructors.stm(innerResults.getString("classname")));
                    rimAttributeId.setModel_id(Constructors.stm(innerResults.getString("modelId")));
                    rimAttribute.setRIMAttribute_id(rimAttributeId);
                    rimAttribute.setCodingStrength_code(Constructors.stm(innerResults.getString("vocStrength")));
                    rimAttribute.setVocabularyDomain_name(Constructors.stm(innerResults.getString("vocDomain")));
                    rimAttribute.setDataType_code(Constructors.stm(innerResults.getString("attDataType")));
                    rimAttributes.add(rimAttribute);
                }
                vocabularyDomain.setConstrainsAttributes((RIMCodedAttribute[]) rimAttributes
                        .toArray(new RIMCodedAttribute[rimAttributes.size()]));
                ArrayList valueSetAndContextsArrayList = new ArrayList();

                getValueSet.setString(1, vocabularyDomain.getVocabularyDomain_name().getV());
                innerResults = getValueSet.executeQuery();
                while (innerResults.next())
                {
                    vocabularyDomainValueSet = new VocabularyDomainValueSet();
                    vocabularyDomainValueSet.setDefinedByValueSet(Constructors.valueSetDescriptor(innerResults
                            .getString("definedByvalueset"), innerResults.getString("valueSetName")));
                    vocabularyDomainValueSet.setApplicationContext_code(Constructors.stm(innerResults
                            .getString("appliesInContext")));
                    valueSetAndContextsArrayList.add(vocabularyDomainValueSet);
                }
                vocabularyDomain.setRepresentedByValueSets((VocabularyDomainValueSet[]) valueSetAndContextsArrayList
                        .toArray(new VocabularyDomainValueSet[valueSetAndContextsArrayList.size()]));
                resultsToReturn.add(vocabularyDomain);
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getBasisOfDomains);
            queries_.checkInPreparedStatement(getRimAttribute);
            queries_.checkInPreparedStatement(getValueSet);
            queries_.checkInPreparedStatement(getVocabularyDomains);

        }
        return (VocabularyDomainDescription[]) resultsToReturn.toArray(new VocabularyDomainDescription[resultsToReturn
                .size()]);
    }

    public FullValueSetDescription lookupValueSet(UID valueSetId, ST valueSetName)
            throws org.hl7.CTSMAPI.UnknownValueSet, UnexpectedError, ValueSetNameIdMismatch
    {
        logger.debug("lookupValueSet called: valueSetId - " + (valueSetId == null ? "null"
                : valueSetId.getV()) + "valueSetName - " + (valueSetName == null ? "null"
                : valueSetName.getV()));
        ValueSetDescription[] valueSetDescriptions = buildValueSetDescriptions((valueSetId == null ? null
                : valueSetId.getV()), (valueSetName == null ? null
                : valueSetName.getV()));
        FullValueSetDescription codeSets = new FullValueSetDescription();

        PreparedStatement getCodeReferences = null;
        PreparedStatement getUsedToDefineCodeSets = null;
        PreparedStatement getConstructedUsingCodeSets = null;
        try
        {
            getCodeReferences = queries_.checkOutStatement(queries_.GET_CODE_REFERENCES);
            getUsedToDefineCodeSets = queries_.checkOutStatement(queries_.GET_USED_TO_DEFINE_CODE_SETS);
            getConstructedUsingCodeSets = queries_.checkOutStatement(queries_.GET_CONSTRUCTED_USING_CODE_SETS);

            codeSets.setDescription(valueSetDescriptions[0]);
            getCodeReferences.setString(1, valueSetDescriptions[0].getIdAndName().getValueSet_id().getV());
            codeSets.setReferencesCodes(buildCodeReferences(getCodeReferences.executeQuery()));
            getUsedToDefineCodeSets.setString(1, valueSetDescriptions[0].getIdAndName().getValueSet_id().getV());
            codeSets.setUsedToDefine(buildCodeSetDescriptor(getUsedToDefineCodeSets.executeQuery()));
            getConstructedUsingCodeSets.setString(1, valueSetDescriptions[0].getIdAndName().getValueSet_id().getV());
            codeSets.setConstructedUsingValueSets(buildCodeSetConstructors(getConstructedUsingCodeSets.executeQuery()));
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getCodeReferences);
            queries_.checkInPreparedStatement(getConstructedUsingCodeSets);
            queries_.checkInPreparedStatement(getUsedToDefineCodeSets);
        }
        return codeSets;
    }

    private ValueSetDescription[] buildValueSetDescriptions(String valueSetId, String valueSetName)
            throws UnknownValueSet, UnexpectedError, ValueSetNameIdMismatch
    {
        logger.debug("buildValueSetDescriptions called: codeSetString - " + (valueSetName == null ? "null"
                : valueSetName));
        ArrayList resultsToReturn = new ArrayList();
        ValueSetDescription valueSetDescription;
        //If they don't supply either, its an error.
        if (((valueSetName == null) || (valueSetName.length() == 0))
                && ((valueSetId == null) || (valueSetId.length() == 0)))
        {
            ValueSetDescriptor temp = new ValueSetDescriptor();
            temp.setValueSet_id(Constructors.uidm(valueSetName));
            temp.setValueSet_name(Constructors.stm(valueSetName));
            throw new UnknownValueSet(temp);
        }
        //If one or the other is missing, its ok.
        if ((valueSetName == null) || (valueSetName.length() == 0))
        {
            valueSetName = "%";
        }
        if ((valueSetId == null) || (valueSetId.length() == 0))
        {
            valueSetId = "%";
        }

        PreparedStatement getSupportedValueSets = null;
        try
        {
            getSupportedValueSets = queries_.checkOutStatement(queries_.GET_SUPPORTED_VALUE_SETS);
            getSupportedValueSets.setString(1, valueSetName);
            getSupportedValueSets.setString(2, valueSetId);
            ResultSet results = getSupportedValueSets.executeQuery();
            while (results.next())
            {
                String valueSetIdRet = results.getString("valueSetId");
                String valueSetNameRet = results.getString("valueSetName");
                valueSetDescription = new ValueSetDescription();
                valueSetDescription.setIdAndName(Constructors.valueSetDescriptor(valueSetIdRet, valueSetNameRet));
                valueSetDescription.setAllCodes(Constructors.blm(results.getBoolean("allCodes")));
                try
                {
                    valueSetDescription.setBasedOnCodeSystem(codeSystemsHelper(results.getString("basedOnCodeSystem"),
                                                                               "%", 0, 0)[0]);
                }
                catch (Exception e)
                {
                    logger.error("There was an error getting a code set descriptor for a code set.", e);
                }
                valueSetDescription.setDefiningExpression(Constructors.stm(results.getString("definingExpression")));
                valueSetDescription.setDescription(Constructors.stm(results.getString("description")));
                valueSetDescription.setHead_code(Constructors.stm(results.getString("includeHeadCode")));
                resultsToReturn.add(valueSetDescription);
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getSupportedValueSets);
        }
        if (resultsToReturn.size() == 0)
        {
            //If either of them was missing and there were no results...
            // error.
            if (valueSetName.equals("%") || valueSetId.equals("%"))
            {
                ValueSetDescriptor temp = new ValueSetDescriptor();
                temp.setValueSet_id(Constructors.uidm(valueSetName));
                temp.setValueSet_name(Constructors.stm(valueSetName));
                throw new UnknownValueSet(temp);
            }
            // They were both supplied, found nothing. Try again with only the
            // valueSetId.
            try
            {
                getSupportedValueSets = queries_.checkOutStatement(queries_.GET_SUPPORTED_VALUE_SETS);
                getSupportedValueSets.setString(1, "%");
                getSupportedValueSets.setString(2, valueSetId);
                ResultSet results = getSupportedValueSets.executeQuery();
                if (results.next())
                {
                    //Found results when just using valueSetId
                    throw new ValueSetNameIdMismatch(Constructors.uidm(valueSetId), Constructors.stm(valueSetName));
                }
                else
                //didn't find anything this way either.
                {
                    ValueSetDescriptor temp = new ValueSetDescriptor();
                    temp.setValueSet_id(Constructors.uidm(valueSetName));
                    temp.setValueSet_name(Constructors.stm(valueSetName));
                    throw new UnknownValueSet(temp);
                }
            }
            catch (SQLException e)
            {
                logger.error(e);
                throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                        : e.getCause().toString())));
            }
            finally
            {
                queries_.checkInPreparedStatement(getSupportedValueSets);
            }
        }
        return (ValueSetDescription[]) resultsToReturn.toArray(new ValueSetDescription[resultsToReturn.size()]);
    }

    private ValueSetConstructor[] buildCodeSetConstructors(ResultSet results) throws SQLException, UnexpectedError
    {
        ArrayList codeSetConstructors = new ArrayList();
        ValueSetConstructor codeSetConstructor;
        while (results.next())
        {
            codeSetConstructor = new ValueSetConstructor();
            codeSetConstructor.setIncludeHeadCode(Constructors.blm(results.getBoolean("includeHeadCode")));
            String valueSetId = results.getString("includesOrExcludesSet");
            codeSetConstructor.setIncludedValueSet(Constructors.valueSetDescriptor(valueSetId,
                                                                                   getNameForValueSetId(valueSetId)));
            codeSetConstructors.add(codeSetConstructor);
        }
        return (ValueSetConstructor[]) codeSetConstructors.toArray(new ValueSetConstructor[codeSetConstructors.size()]);
    }

    private ValueSetDescriptor[] buildCodeSetDescriptor(ResultSet results) throws SQLException, UnexpectedError
    {
        ArrayList codeSetConstructors = new ArrayList();
        ValueSetDescriptor valueSetDescriptor;
        while (results.next())
        {
            valueSetDescriptor = new ValueSetDescriptor();
            valueSetDescriptor.setValueSet_id(Constructors.uidm(results.getString("usedToBuildValueSet")));
            valueSetDescriptor.setValueSet_name(Constructors.stm(getNameForValueSetId(valueSetDescriptor
                    .getValueSet_id().getV())));
            codeSetConstructors.add(valueSetDescriptor);
        }
        return (ValueSetDescriptor[]) codeSetConstructors.toArray(new ValueSetDescriptor[codeSetConstructors.size()]);
    }

    private String getNameForValueSetId(String valueSetId) throws UnexpectedError
    {
        PreparedStatement getNameForValueSetId = null;
        try
        {
            getNameForValueSetId = queries_.checkOutStatement(queries_.GET_NAME_FOR_VALUE_SET_ID);
            getNameForValueSetId.setString(1, valueSetId);
            ResultSet temp = getNameForValueSetId.executeQuery();
            if (temp.next())
            {
                return temp.getString("valueSetName");
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(getNameForValueSetId);
        }
        return null;
    }

    private ValueSetCodeReference[] buildCodeReferences(ResultSet results) throws SQLException
    {
        ArrayList codeReferences = new ArrayList();
        ValueSetCodeReference codeReference;
        while (results.next())
        {
            codeReference = new ValueSetCodeReference();
            codeReference.setIncludeReferencedCode(Constructors.blm(results.getBoolean("includeReferencedCode")));
            codeReference.setLeafOnly(Constructors.blm(results.getBoolean("leafOnly")));
            codeReference.setReferenced_code(Constructors.stm(results.getString("referencesCodedTerm")));
            codeReference.setRelationship_code(Constructors.stm(results.getString("relationship")));
            codeReferences.add(codeReference);
        }
        return (ValueSetCodeReference[]) codeReferences.toArray(new ValueSetCodeReference[codeReferences.size()]);
    }

    public CodeSystemInfo lookupCodeSystem(UID codeSystemID, ST codeSystemName)
            throws org.hl7.CTSMAPI.CodeSystemNameIdMismatch, org.hl7.CTSMAPI.UnknownCodeSystem, UnexpectedError
    {
        logger.debug("lookupCodeSystem called: codeSystemID = " + (codeSystemID == null ? "null"
                : codeSystemID.getV()) + " codeSystemName = " + (codeSystemName == null ? "null"
                : codeSystemName.getV()));

        CodeSystemInfo csi = new CodeSystemInfo();

        PreparedStatement getRegisteredCodeSystems = null;
        try
        {
            CodeSystemDescriptor[] codeSystem = codeSystemsHelper(codeSystemID.getV(), codeSystemName.getV(), 0, 0);
            if ((codeSystem == null) || codeSystem.length != 1)
                throw new UnknownCodeSystem(codeSystemID);
            if (!codeSystem[0].getCodeSystem_name().getV().equalsIgnoreCase(codeSystemName.getV()))
            {
                throw new CodeSystemNameIdMismatch(codeSystemID, codeSystemName);
            }

            csi.setDescription(codeSystem[0]);
            CodeSystemRegistration csr = new CodeSystemRegistration();

            getRegisteredCodeSystems = queries_.checkOutStatement(queries_.GET_REGISTERED_CODE_SYSTEMS);

            getRegisteredCodeSystems.setString(1, codeSystemID.getV());
            ResultSet results = getRegisteredCodeSystems.executeQuery();
            if (results.next())
            {
                boolean external = results.getBoolean("isExternal");
                if (external)
                {
                    csr.setCodeSystemType_code(Constructors.stm("E"));
                }
                else
                {
                    csr.setCodeSystemType_code(Constructors.stm("I"));
                }
                csr.setPublisher(Constructors.stm(results.getString("publisher")));
                csr.setInUMLS(Constructors.blm(results.getBoolean("inUMLS")));
                csr.setLicensingInformation(Constructors.stm(results.getString("licensingInformation")));
                csr.setSponsor(Constructors.stm(results.getString("sponsor")));
                csr.setSystemSpecificLocatorInfo(Constructors.stm(results.getString("systemSpecificLocatorInfo")));
                csr.setVersionReportingMethod(Constructors.stm(results.getString("versionReportingMethod")));

                csi.setRegistrationInfo(csr);
            }
        }
        catch (SQLException e)
        {
            logger.error("Problem getting the CodeSystemRegistration details", e);
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (CodeSystemNameIdMismatch e)
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
            queries_.checkInPreparedStatement(getRegisteredCodeSystems);
        }

        return csi;
    }

    public ValueSetDescriptor lookupValueSetForDomain(ST vocabularyDomain_name, ST applicationContext_code)
            throws org.hl7.CTSMAPI.NoApplicableValueSet, org.hl7.CTSMAPI.UnknownApplicationContextCode,
            org.hl7.CTSMAPI.UnknownVocabularyDomain, UnexpectedError
    {
        logger.debug("lookupValueSetForDomain called: vocabularyDomain_name - "
                + (vocabularyDomain_name == null ? "null"
                        : vocabularyDomain_name.getV()) + " applicationContext_code - "
                + (applicationContext_code == null ? "null"
                        : applicationContext_code.getV() + ""));
        if (vocabularyDomain_name == null)
        {
            throw new UnknownVocabularyDomain();
        }

        PreparedStatement getDefinedByValueSet = null;
        try
        {
            getDefinedByValueSet = queries_.checkOutStatement(queries_.GET_DEFINED_BY_VALUE_SET);
            String vocabularyDomainName = vocabularyDomain_name.getV();
            String applicationContextCode = applicationContext_code == null ? ""
                    : applicationContext_code.getV();
            getDefinedByValueSet.setString(1, vocabularyDomainName);
            getDefinedByValueSet.setString(2, applicationContextCode);
            ResultSet results = getDefinedByValueSet.executeQuery();
            if (results.next())
            {
                String valueSetId = results.getString("definedbyValueSet");
                return getMatchedValueSets(valueSetId, "%", 0, 0)[0];
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
            queries_.checkInPreparedStatement(getDefinedByValueSet);
        }

        //if we get here... no results... find out why...
        PreparedStatement isVocabularyDomainValid = null;
        try
        {
            isVocabularyDomainValid = queries_.checkOutStatement(queries_.IS_VOCABULARY_DOMAIN_VALID);
            isVocabularyDomainValid.setString(1, vocabularyDomain_name.getV());
            ResultSet results = isVocabularyDomainValid.executeQuery();
            results.next(); //always one and only one result
            if (results.getInt("found") == 0)
            {
                throw new UnknownVocabularyDomain(vocabularyDomain_name);
            }
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isVocabularyDomainValid);
        }
        if (applicationContext_code != null && applicationContext_code.getV() != null
                && applicationContext_code.getV().length() != 0)
        {
            try
            {
                isCodeInValueSet(null, Constructors.stm("RealmOfUse"), Constructors.blm(true), Constructors
                        .conceptIdm("2.16.840.1.113883.5.147", applicationContext_code.getV()));
            }
            catch (UnknownConceptCode e)
            {
                throw new UnknownApplicationContextCode(applicationContext_code);
            }
            catch (UnknownCodeSystem e)
            {
                throw new UnknownApplicationContextCode(applicationContext_code);
            }
            catch (Exception e)
            {
                logger.error("Error validateing applicationContextCode", e);
                throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                        : e.getCause().toString())));
            }
        }
        //Code must be valid if we got here, just didn't find any.
        throw new NoApplicableValueSet(vocabularyDomain_name, applicationContext_code);
    }

    private boolean isConceptValid(ConceptId codeToValidate) throws UnexpectedError, UnknownConceptCode,
            UnknownCodeSystem
    {
        logger.debug("isConcepValid called - codeToValidate.code: "
                + (codeToValidate.getConcept_code() == null ? "null"
                        : codeToValidate.getConcept_code().getV()) + " codeToValidate.id: "
                + (codeToValidate.getCodeSystem_id() == null ? "null"
                        : codeToValidate.getCodeSystem_id().getV()));

        PreparedStatement isConceptValid = null;
        try
        {
            isConceptValid = queries_.checkOutStatement(queries_.IS_CONCEPT_VALID);
            isConceptValid.setString(1, codeToValidate.getCodeSystem_id().getV());
            isConceptValid.setString(2, codeToValidate.getConcept_code().getV());
            ResultSet results = isConceptValid.executeQuery();
            results.next();
            if (results.getInt("found") > 0)
            {
                results.close();
                return true;
            }
            else
            {
                results.close();
                isConceptValid.setString(1, codeToValidate.getCodeSystem_id().getV());
                isConceptValid.setString(2, "%");
                results = isConceptValid.executeQuery();
                results.next();
                if (results.getInt("found") > 0)
                {
                    throw new UnknownConceptCode(codeToValidate);
                }
                else
                {
                    throw new UnknownCodeSystem(codeToValidate.getCodeSystem_id());
                }
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
        catch (Exception e)
        {
            logger.error("Error validateing applicationContextCode", e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        finally
        {
            queries_.checkInPreparedStatement(isConceptValid);
        }
    }

    public BL isCodeInValueSet(UID valueSetId, ST valueSetName, BL includeHeadCode, ConceptId codeToValidate)
            throws UnknownValueSet, ValueSetNameIdMismatch, UnknownConceptCode, UnknownCodeSystem, UnexpectedError
    {
        logger.debug("isCodeInValueSet called - valueSetId: " + (valueSetId == null ? "null"
                : valueSetId.getV()) + " valueSetName: " + (valueSetName == null ? "null"
                : valueSetName.getV()) + " includeHeadCode: " + includeHeadCode.isV() + " codeToValidate.codeSystem: "
                + (codeToValidate.getCodeSystem_id() == null ? "null"
                        : codeToValidate.getCodeSystem_id().getV()) + " codeToValidate.code: "
                + (codeToValidate.getConcept_code() == null ? "null"
                        : codeToValidate.getConcept_code().getV()));

        PreparedStatement isCodeInValueSet = null;
        PreparedStatement doesValueSetExist = null;
        PreparedStatement doesCodeSystemExist = null;
        try
        {
            isConceptValid(codeToValidate);
            //This throws exceptions if it is not
            String valueSetIdTemp = valueSetId == null ? null
                    : valueSetId.getV();
            String valueSetNameTemp = valueSetName == null ? null
                    : valueSetName.getV();
            if ((valueSetIdTemp == null || valueSetIdTemp.length() == 0)
                    && (valueSetNameTemp == null || valueSetNameTemp.length() == 0))
            { //both null is an error
                ValueSetDescriptor temp = new ValueSetDescriptor();
                temp.setValueSet_id(valueSetId);
                temp.setValueSet_name(valueSetName);
                throw new UnknownValueSet(temp);
            }
            if (valueSetIdTemp == null || valueSetIdTemp.length() == 0)
            {
                valueSetIdTemp = "%";
            }
            if (valueSetNameTemp == null || valueSetNameTemp.length() == 0)
            {
                valueSetNameTemp = "%";
            }

            isCodeInValueSet = queries_.checkOutStatement(queries_.IS_CODE_IN_VALUE_SET);
            isCodeInValueSet.setString(1, valueSetNameTemp);
            isCodeInValueSet.setString(2, valueSetIdTemp);
            isCodeInValueSet.setString(3, codeToValidate.getCodeSystem_id().getV());
            isCodeInValueSet.setString(4, codeToValidate.getConcept_code().getV());
            if (includeHeadCode.isV())
                isCodeInValueSet.setInt(5, 1);
            else
                isCodeInValueSet.setInt(5, 2);
            ResultSet results = isCodeInValueSet.executeQuery();
            results.next(); //always one row in result
            if (results.getInt("found") == 0)
            {
                doesValueSetExist = queries_.checkOutStatement(queries_.DOES_VALUE_SET_EXIST);
                doesCodeSystemExist = queries_.checkOutStatement(queries_.DOES_CODE_SYSTEM_EXIST);
                //throw proper exceptions if necessary
                doesValueSetExist.setString(1, valueSetId.getV());
                results = doesValueSetExist.executeQuery();
                results.next(); //query always has 1 and only 1 row

                if (results.getInt("found") == 0)
                    throw new UnknownValueSet(Constructors.valueSetDescriptor(valueSetId.getV(), valueSetName.getV()));

                doesCodeSystemExist.setString(1, codeToValidate.getCodeSystem_id().getV());
                doesCodeSystemExist.setString(2, "%");

                results = doesCodeSystemExist.executeQuery();
                results.next(); //query always has 1 and only 1 row
                if (results.getInt("found") == 0)
                    throw new UnknownCodeSystem(codeToValidate.getCodeSystem_id());
                // They were both supplied, found nothing. Try again with only
                // the valueSetId.
                isCodeInValueSet.setString(1, "%");
                isCodeInValueSet.setString(2, valueSetIdTemp);
                isCodeInValueSet.setString(3, codeToValidate.getCodeSystem_id().getV());
                isCodeInValueSet.setString(4, codeToValidate.getConcept_code().getV());
                if (includeHeadCode.isV())
                    isCodeInValueSet.setInt(5, 1);
                else
                    isCodeInValueSet.setInt(5, 2);
                results = isCodeInValueSet.executeQuery();
                results.next();
                if (results.getBoolean("found"))
                {
                    //Found results when just using valueSetId
                    throw new ValueSetNameIdMismatch(valueSetId, valueSetName);
                }
                return Constructors.blm(false);
            }
            return Constructors.blm(true);
        }
        catch (SQLException e)
        {
            logger.error(e);
            throw new UnexpectedError(Constructors.stm(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString())));
        }
        catch (UnknownConceptCode e)
        {
            throw e;
        }
        catch (UnknownCodeSystem e)
        {
            throw e;
        }
        catch (UnknownValueSet e)
        {
            throw e;
        }
        catch (ValueSetNameIdMismatch e)
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
            queries_.checkInPreparedStatement(isCodeInValueSet);
            queries_.checkInPreparedStatement(doesCodeSystemExist);
            queries_.checkInPreparedStatement(doesValueSetExist);
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
}