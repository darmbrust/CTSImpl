/**
 * 
 */
package com.apelon.headwater.HWAPI.refImpl;

import java.rmi.RemoteException;

import org.hl7.CTSMAPI.BadlyFormedMatchText;
import org.hl7.CTSMAPI.CTSVersionId;
import org.hl7.CTSMAPI.InvalidExpansionContext;
import org.hl7.CTSMAPI.NoApplicableDesignationFound;
import org.hl7.CTSMAPI.NoApplicableValueSet;
import org.hl7.CTSMAPI.QualifiersNotSupported;
import org.hl7.CTSMAPI.SubsumptionNotSupported;
import org.hl7.CTSMAPI.TimeoutError;
import org.hl7.CTSMAPI.UnableToTranslate;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.UnknownApplicationContextCode;
import org.hl7.CTSMAPI.UnknownCodeSystem;
import org.hl7.CTSMAPI.UnknownConceptCode;
import org.hl7.CTSMAPI.UnknownLanguage;
import org.hl7.CTSMAPI.UnknownMatchAlgorithm;
import org.hl7.CTSMAPI.UnknownVocabularyDomain;
import org.hl7.CTSMAPI.UnrecognizedQualifier;
import org.hl7.CTSMAPI.ValidateCodeReturn;
import org.hl7.CTSMAPI.ValueSetExpansion;
import org.hl7.cts.types.BL;
import org.hl7.cts.types.CD;
import org.hl7.cts.types.ST;
import org.hl7.cts.types.UID;

import com.apelon.headwater.HWAPI.CodeToTranslate;
import com.apelon.headwater.HWAPI.HWRuntimeOperations;
import com.apelon.headwater.HWAPI.TranslatedCode;

/**
 * @author hsolbrig
 *
 */
public class HWRuntimeOperationsImpl implements HWRuntimeOperations
{

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#areEquivalent(org.hl7.cts.types.CD, org.hl7.cts.types.CD)
     */
    public BL areEquivalent(CD parm1, CD parm2) throws RemoteException, SubsumptionNotSupported, QualifiersNotSupported,
            UnknownConceptCode, UnknownCodeSystem, UnrecognizedQualifier, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#expandValueSetExpansionContext(byte[])
     */
    public ValueSetExpansion[] expandValueSetExpansionContext(byte[] expansionContext) throws RemoteException,
            InvalidExpansionContext, TimeoutError, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#fillInDetails(org.hl7.cts.types.CD, org.hl7.cts.types.ST)
     */
    public CD fillInDetails(CD codeToFillIn, ST displayLanguage_code) throws RemoteException, UnknownLanguage, UnknownConceptCode,
            UnknownCodeSystem, UnexpectedError, NoApplicableDesignationFound
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getCTSVersion()
     */
    public CTSVersionId getCTSVersion() throws RemoteException, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getHL7ReleaseVersion()
     */
    public ST getHL7ReleaseVersion() throws RemoteException, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getServiceDescription()
     */
    public ST getServiceDescription() throws RemoteException, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getServiceName()
     */
    public ST getServiceName() throws RemoteException, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getServiceVersion()
     */
    public ST getServiceVersion() throws RemoteException, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getSupportedMatchAlgorithms()
     */
    public ST[] getSupportedMatchAlgorithms() throws RemoteException, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#getSupportedVocabularyDomains(org.hl7.cts.types.ST, org.hl7.cts.types.ST, int, int)
     */
    public ST[] getSupportedVocabularyDomains(ST matchText, ST matchAlgorithm_code, int timeout, int sizeLimit) throws RemoteException,
            BadlyFormedMatchText, UnknownMatchAlgorithm, TimeoutError, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#lookupValueSetExpansion(org.hl7.cts.types.ST, org.hl7.cts.types.ST, org.hl7.cts.types.ST, org.hl7.cts.types.BL, int, int)
     */
    public ValueSetExpansion[] lookupValueSetExpansion(ST vocabularyDomain_name, ST applicationContext_code,
                                                       ST language_code, BL expandAll, int timeout, int sizeLimit)
            throws RemoteException, UnknownApplicationContextCode, UnknownLanguage, UnknownVocabularyDomain,
            NoApplicableValueSet, TimeoutError, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#subsumes(org.hl7.cts.types.CD, org.hl7.cts.types.CD)
     */
    public BL subsumes(CD parentCode, CD childCode) throws RemoteException, SubsumptionNotSupported, QualifiersNotSupported,
            UnknownConceptCode, UnknownCodeSystem, UnrecognizedQualifier, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#translateCode(org.hl7.cts.types.ST, org.hl7.cts.types.CD, org.hl7.cts.types.UID, org.hl7.cts.types.ST)
     */
    public CD translateCode(ST vocabularyDomain_name, CD fromCode, UID toCodeSystemId, ST toApplicationContext_code) throws RemoteException, UnknownApplicationContextCode,
            UnknownVocabularyDomain, UnableToTranslate, UnknownCodeSystem, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#translateCodesInBatch(org.hl7.cts.types.ST[], com.apelon.headwater.HWAPI.CodeToTranslate[])
     */
    public TranslatedCode[] translateCodesInBatch(ST[] toApplicationContext_codes, CodeToTranslate[] sourceCodes) throws RemoteException,
            UnknownApplicationContextCode, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#validateCode(org.hl7.cts.types.ST, org.hl7.cts.types.CD, org.hl7.cts.types.ST, org.hl7.cts.types.BL, org.hl7.cts.types.BL)
     */
    public ValidateCodeReturn validateCode(ST vocabluaryDomain, CD codeToValidate, ST applicationContext_code,
                                           BL activeConceptsOnly, BL errorCheckOnly) throws RemoteException,
            UnknownApplicationContextCode, UnknownVocabularyDomain, NoApplicableValueSet, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.apelon.headwater.HWAPI.HWRuntimeOperations#validateTranslation(org.hl7.cts.types.ST, org.hl7.cts.types.CD, org.hl7.cts.types.ST, org.hl7.cts.types.BL, org.hl7.cts.types.BL)
     */
    public ValidateCodeReturn validateTranslation(ST vocabularyDomain_name, CD codeToValidate, ST applicationContext_code, BL activeConceptsOnly, BL errorCheckOnly) throws RemoteException,
            UnknownApplicationContextCode, UnknownVocabularyDomain, UnexpectedError
    {
        // TODO Auto-generated method stub
        return null;
    }

}
