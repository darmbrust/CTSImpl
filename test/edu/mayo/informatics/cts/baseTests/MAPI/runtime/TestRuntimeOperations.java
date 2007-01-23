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
package edu.mayo.informatics.cts.baseTests.MAPI.runtime;

import junit.framework.TestCase;

import org.hl7.CTSMAPI.CTSVersionId;
import org.hl7.CTSMAPI.InvalidExpansionContext;
import org.hl7.CTSMAPI.NoApplicableValueSet;
import org.hl7.CTSMAPI.QualifiersNotSupported;
import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.SubsumptionNotSupported;
import org.hl7.CTSMAPI.TimeoutError;
import org.hl7.CTSMAPI.UnknownApplicationContextCode;
import org.hl7.CTSMAPI.UnknownCodeSystem;
import org.hl7.CTSMAPI.UnknownConceptCode;
import org.hl7.CTSMAPI.UnknownLanguage;
import org.hl7.CTSMAPI.UnknownMatchAlgorithm;
import org.hl7.CTSMAPI.UnknownVocabularyDomain;
import org.hl7.CTSMAPI.ValidateCodeReturn;
import org.hl7.CTSMAPI.ValueSetExpansion;
import org.hl7.cts.types.BL;
import org.hl7.cts.types.CD;
import org.hl7.cts.types.CR;
import org.hl7.cts.types.CS;
import org.hl7.cts.types.CV;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.utility.Constructors;

/**
 * JUnit Testcases for Mapi RuntimeOperations.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestRuntimeOperations extends TestCase
{
    private RuntimeOperations roi_;

    public TestRuntimeOperations()
    {
        super();      
    }
    public TestRuntimeOperations(String name, RuntimeOperations roi)
    {
        super(name);    
        roi_ = roi;
    }

    public void testGetServiceName() throws Exception
    {
        ST result = roi_.getServiceName();
        assertNotNull(result);
        assertNotNull(result.getV());
        assertFalse("Service name returned empty string", result.getV().length() == 0);
    }

    public void testGetServiceVersion() throws Exception
    {
        ST result = roi_.getServiceVersion();
        assertNotNull(result);
        assertNotNull(result.getV());
        assertFalse("Service version returned empty string", result.getV().length() == 0);
    }

    public void testGetServiceDescription() throws Exception
    {
        ST result = roi_.getServiceDescription();
        assertNotNull(result);
        assertNotNull(result.getV());
        assertFalse("Service description returned empty string", result.getV().length() == 0);
    }

    public void testGetCTSVersion() throws Exception
    {
        CTSVersionId result = roi_.getCTSVersion();
        assertNotNull(result);
        assertNotNull(result.getMajor());
        assertNotNull(result.getMinor());
    }

    public void testGetHL7ReleaseVersion() throws Exception
    {
        ST result = roi_.getHL7ReleaseVersion();
        assertNotNull(result);
        assertNotNull(result.getV());
        assertFalse("HL7 Release Version returned empty string", result.getV().length() == 0);
    }

    public void testSubsumes() throws Exception
    {
        CD cd1 = new CD();
        CD cd2 = new CD();
        cd1.setCode("DIET");
        cd1.setCodeSystem("2.16.840.1.113883.5.4");
        cd2.setCode("FAST");
        cd2.setCodeSystem("2.16.840.1.113883.5.4");
        BL result = roi_.subsumes(cd1, cd2);
        assertNotNull(result);
        assertTrue(result.isV());
        
        
        assertTrue(roi_.subsumes(cd1, cd1).isV());
        
        try
        {
            cd1 = new CD();
            cd1.setCode("DIET");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            cd2 = new CD();
            cd2.setCode("FAST");
            cd2.setCodeSystem("foofnar");
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw SubsumptionNotSupported");
        }
        catch (SubsumptionNotSupported e)
        {
            //supposed to happen
        }
        try
        {
            cd1 = new CD();
            cd1.setCode("DIET");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            cd1.setQualifiers(new CR[]{new CR(), new CR()});
            cd2 = new CD();
            cd2.setCode("FAST");
            cd2.setCodeSystem("2.16.840.1.113883.5.4");
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw QualifiersNotSupported ");
        }
        catch (QualifiersNotSupported e)
        {
            // supposed to happen
        }
        try
        {
            cd1 = new CD();
            cd1.setCode("DIET");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            cd2 = new CD();
            cd2.setCode("FAST");
            cd2.setCodeSystem("2.16.840.1.113883.5.4");
            cd2.setQualifiers(new CR[]{new CR(), new CR()});
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw QualifiersNotSupported ");
        }
        catch (QualifiersNotSupported e)
        {
            // supposed to happen
        }
        try
        {
            cd1 = new CD();
            cd1.setCode("foofnar");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            cd2 = new CD();
            cd2.setCode("FAST");
            cd2.setCodeSystem("2.16.840.1.113883.5.4");
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }
        try
        {
            cd1 = new CD();
            cd1.setCode("DIET");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            cd2 = new CD();
            cd2.setCode("foofnar");
            cd2.setCodeSystem("2.16.840.1.113883.5.4");
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }
        try
        {
            cd1 = new CD();
            cd1.setCode("DIET");
            cd1.setCodeSystem("foofnar");
            cd2 = new CD();
            cd2.setCode("FAST");
            cd2.setCodeSystem("foofnar");
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
        try
        {
            cd1 = new CD();
            cd1.setCode(null);
            cd1.setCodeSystem(null);
            cd2 = new CD();
            cd2.setCode("FAST");
            cd2.setCodeSystem(null);
            result = roi_.subsumes(cd1, cd2);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
        //qualifier support is not implemented
        //        try
        //        {
        //            cd1 = new CD();
        //             cd2 = new CD();
        //
        //
        //            result = roi_.subsumes(cd1, cd2);
        //            fail("Didn't throw UnrecognizedQualifier");
        //            
        //        }
        //         catch (UnrecognizedQualifier e)
        //        {
        //            // supposed to happen
        //        }
    }

    public void testGetSupportedVocabularyDomains() throws Exception
    {
       
        ST[] results = roi_.getSupportedVocabularyDomains(null, null, 0, 0);
       
        
       
        assertNotNull(results);
        assertFalse("Didn't return any supported vocabulary domains", results.length == 0);

        results = roi_.getSupportedVocabularyDomains(null, null, 0, 2);
        assertTrue("Didn't limit results", results.length == 2);
        //        
        //        try
        //        {
        //            results = roi_.getSupportedVocabularyDomains(null, null, 1, 0);
        //            fail("Didn't throw timeout error");
        //        }
        //  
        //        catch (TimeoutError e)
        //        {
        //            // supposed to happen
        //        }

        try
        {
            results = roi_.getSupportedVocabularyDomains(Constructors.stm("pay"), Constructors.stm("foonar"), 1,
                                                         0);
            fail("Didn't throw UnknownMatchAlgorithm");
        }

        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        //TODO come up with a way to test BadlyFormedMatchText
    }

    public void testValidateCode() throws Exception
    {

        CD code = new CD();

        code.setCode("INC");
        CS cs = new CS();
        cs.setCode("HL7");
        code.setCodingRationale(cs);

        ValidateCodeReturn result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""),
                                                      Constructors.blm(true), Constructors.blm(false));

        if (result.getNErrors().getV() != 0 || result.getNWarnings().getV() != 0)
        {
            fail("Failed on a case that should work.");
        }

        try
        {
            roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm("foofnar"), Constructors.blm(true),
                              Constructors.blm(false));
            fail("Didn't throw UnknownApplicationContextCode");
        }
        catch (UnknownApplicationContextCode e)
        {
            // supposed to happen
        }
        try
        {
            roi_.validateCode(Constructors.stm("foofnar"), code, null, Constructors.blm(true), Constructors.blm(false));
            fail("Didn't throw UnknownVocabularyDomain ");
        }
        catch (UnknownVocabularyDomain e)
        {
            // supposed to happen
        }
        try
        {
            roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm("Canada"), Constructors.blm(true),
                              Constructors.blm(false));
            fail("Didn't throw NoApplicableValueSet ");
        }
        catch (NoApplicableValueSet e)
        {
            // supposed to happen
        }

        //Check null code error
        result = roi_.validateCode(Constructors.stm("ActClass"), null, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E013", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //Check missing code system error
        code.setCodeSystem("");
        code.setCode(null);

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E013", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        // check missing concept code error - this one should not have an error because ActClass is
        // of type CS
        code.setCodeSystem(null);
        code.setCode("INC");

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() != 0))
        {
            fail("Threw an error code when it shouldn't have.");
        }

        //      check missing concept code error on a type other than CS (this one should throw an error)
        // because ActSite is of type CD
        code.setCodeSystem(null);
        code.setCode("INC");

        result = roi_.validateCode(Constructors.stm("ActSite"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E012", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //check for valid code system
        code.setCode("INC");
        code.setCodeSystem("foonar");

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E001", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //Check for proper errors on missing codes

        code.setCode(null);
        code.setCodeSystem("2.16.840.1.113883.5.6");

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E002", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        code.setCode("");
        code.setCodeSystem("2.16.840.1.113883.5.6");

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E002", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        // check for code system valid in voc domain
        code.setCode("246RH0600N");
        code.setCodeSystem("2.16.840.1.113883.5.53");

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E003", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        // check for concept code valid in voc domain
        code.setCode("INFO");
        code.setCodeSystem("2.16.840.1.113883.5.6");

        result = roi_.validateCode(Constructors.stm("ActClassProcedure"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E005", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //      check coding rationale error
        code.setCode("INC");
        code.setCodeSystem("2.16.840.1.113883.5.6");
        cs.setCode("foo");
        code.setCodingRationale(cs);

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E014", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //Check for translation error - ActCode allows translations, this should succeed.
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        code.setCodingRationale(null);
        code.setTranslation(new CD[1]);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() != 0))
        {
            fail("Threw an error when it shouldn't have.");
        }

        //Check for translation error - ActClass doesn't allows translations, this should fail.
        code.setCode("INC");
        code.setCodeSystem("2.16.840.1.113883.5.6");
        code.setCodingRationale(null);
        code.setTranslation(new CD[1]);

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E007", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //Check for qualifiers error - ActCode allows qualifiers, this should succeed.
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        code.setQualifiers(new CR[1]);
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() != 0))
        {
            fail("Threw an error when it shouldn't have.");
        }

        //Check for translation error - ActClass doesn't allows qualifiers, this should fail.
        code.setCode("Inc");
        code.setCodeSystem("2.16.840.1.113883.5.6");
        code.setQualifiers(new CR[1]);

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E006", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //      Does it validate qualifiers - valid qualifier
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        CR tempCR = new CR();
        CV tempCV = new CV();
        tempCV.setCode("EM");
        tempCV.setCodeSystem("2.16.840.1.113883.5.4");
        tempCR.setValue(tempCV);
        code.setQualifiers(new CR[]{tempCR});
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() != 0))
        {
            fail("Threw an error when it shouldn't have.");
        }

        //      Does it validate qualifiers - invalid qualifier rationale
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        tempCR = new CR();
        tempCV = new CV();
        tempCV.setCode("EM");
        tempCV.setCodeSystem("2.16.840.1.113883.5.4");
        cs.setCode("foo");
        tempCV.setCodingRationale(cs);
        tempCR.setValue(tempCV);

        code.setQualifiers(new CR[]{tempCR});
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNErrors().getV() == 0) || (!validateCodeReturnContains("E014", result))
                || (result.getDetail()[0].getIsError().isV() != true))
        {
            fail("Didn't return proper error code");
        }

        //check warning 1
        code.setCodeSystem(null);
        code.setCode("INC");
        code.setCodeSystemName("foobar");
        code.setQualifiers(null);
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActClass"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNWarnings().getV() == 0) || (!validateCodeReturnContains("W001", result))
                || (result.getDetail()[0].getIsError().isV() != false))
        {
            fail("Didn't return proper error code");
        }

        //check warning 2
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        code.setCodeSystemName("foobar");
        code.setQualifiers(null);
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNWarnings().getV() == 0) || (!validateCodeReturnContains("W002", result))
                || (result.getDetail()[0].getIsError().isV() != false))
        {
            fail("Didn't return proper error code");
        }

        //      check warning 3
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        code.setCodeSystemVersion("4");
        code.setCodeSystemName(null);
        code.setQualifiers(null);
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNWarnings().getV() == 0) || (!validateCodeReturnContains("W003", result))
                || (result.getDetail()[0].getIsError().isV() != false))
        {
            fail("Didn't return proper error code");
        }

        //      check warning 4
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        code.setCodeSystemVersion(null);
        code.setCodeSystemName(null);
        code.setDisplayName("Emergency ++Supply");
        code.setQualifiers(null);
        code.setTranslation(null);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNWarnings().getV() == 0) || (!validateCodeReturnContains("W004", result))
                || (result.getDetail()[0].getIsError().isV() != false))
        {
            fail("Didn't return proper error code");
        }

        //      check warning 5
        code.setCode("EM");
        code.setCodeSystem("2.16.840.1.113883.5.4");
        code.setCodeSystemVersion(null);
        code.setCodeSystemName(null);
        code.setDisplayName(null);
        code.setQualifiers(null);
        code.setTranslation(new CD[1]);

        result = roi_.validateCode(Constructors.stm("ActCode"), code, Constructors.stm(""), Constructors.blm(true),
                                   Constructors.blm(false));

        if ((result.getNWarnings().getV() == 0) || (!validateCodeReturnContains("W005", result))
                || (result.getDetail()[0].getIsError().isV() != false))
        {
            fail("Didn't return proper error code");
        }

    }

    private boolean validateCodeReturnContains(String errorCode, ValidateCodeReturn validateCodeReturn)
    {
        for (int i = 0; i < validateCodeReturn.getDetail().length; i++)
        {
            if (validateCodeReturn.getDetail()[i].getError_id().getV().equals(errorCode))
            {
                return true;
            }
        }
        return false;
    }

    //    public void testValidateTranslation()
    //    {
    //        roi_.validateTranslation(null, null, null, null, null);
    //    }
    //    public void testTranslateCode()
    //    {
    //       roi_.translateCode(null, null, null, null);
    //    }
    //    public void testAreEquivalent()
    //    {
    //        roi_.areEquivalent(null, null);
    //    }
    public void testFillInDetails() throws Exception
    {
        CD cd3 = new CD();
        cd3.setCode("ACID");
        cd3.setCodeSystem("2.16.840.1.113883.5.4");
        CD result = roi_.fillInDetails(cd3, Constructors.stm("en"));
        assertNotNull(result);
        try
        {

            result = roi_.fillInDetails(cd3, Constructors.stm("foofnar"));
            fail("Didn't throw UnknownLanguage");
        }
        catch (UnknownLanguage e)
        {
            // supposed to happen
        }
        try
        {
            result = roi_.fillInDetails(cd3, null);
            fail("Didn't throw UnknownLanguage");
        }
        catch (UnknownLanguage e)
        {
            // supposed to happen
        }
        try
        {
            CD cd1 = new CD();
            cd1.setCode("foofnar");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            result = roi_.fillInDetails(cd1, Constructors.stm("en"));
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            // supposed to happen
        }
        try
        {
            CD cd1 = new CD();
            cd1.setCode("ACID");
            cd1.setCodeSystem("foofnar");
            result = roi_.fillInDetails(cd1, Constructors.stm("en"));
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
        try
        {
            result = roi_.fillInDetails(null, null);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
    }

    public void testLookupValueSetExpansion() throws Exception
    {
        ValueSetExpansion[] result = roi_.lookupValueSetExpansion(Constructors.stm("ActClass"), null, Constructors
                .stm("en"), Constructors.blm(false), 0, 0);
        assertNotNull(result);
        assertFalse("Value set expansion is empty", result.length == 0);

//TODO find a test case for this
//        //test context code
//        result = roi_.lookupValueSetExpansion(Constructors.stm("DiagnosisValue"), Constructors.stm("Canada"),
//                                              Constructors.stm("en"), Constructors.blm(false), 0, 0);
//        assertNotNull(result);
//        assertFalse("Value set expansion is empty", result.length == 0);

        result = roi_.lookupValueSetExpansion(Constructors.stm("ActClass"), null, Constructors.stm("en"), Constructors
                .blm(false), 0, 2);
        assertTrue("Didn't limit results", result.length == 2);
        try
        {
            result = roi_.lookupValueSetExpansion(Constructors.stm("ActClass"), null, Constructors.stm("foofnar"),
                                                  Constructors.blm(false), 0, 0);
            fail("Didn't throw UnknownLanguage");
        }
        catch (UnknownLanguage e)
        {
            // supposed to happen
        }
        try
        {
            result = roi_.lookupValueSetExpansion(Constructors.stm("ActClass"), Constructors.stm("foonar"), Constructors
                    .stm("en"), Constructors.blm(false), 0, 0);
            fail("Didn't throw UnknownApplicationContextCode");
        }
        catch (UnknownApplicationContextCode e)
        {
            // supposed to happen
        }
        try
        {
            result = roi_.lookupValueSetExpansion(Constructors.stm("foofnar"), null, Constructors.stm("en"), Constructors
                    .blm(false), 0, 0);
            fail("Didn't throw UnknownVocabularyDomain");
        }
        catch (UnknownVocabularyDomain e)
        {
            // supposed to happen
        }

        try
        {
            result = roi_.lookupValueSetExpansion(Constructors.stm("ActClass"), null, Constructors.stm("en"),
                                                  Constructors.blm(false), 1, 0);
            fail("Didn't throw timeout error");
        }

        catch (TimeoutError e)
        {
            // supposed to happen
        }

        try
        {
            result = roi_.lookupValueSetExpansion(Constructors.stm("ActClass"), Constructors.stm("Canada"), Constructors
                    .stm("en"), Constructors.blm(false), 0, 0);
            fail("Didn't throw NoApplicableValueSet error");
        }

        catch (NoApplicableValueSet e)
        {
            // supposed to happen
        }
    }

    public void testExpandValueSetExpansionContext() throws Exception
    {
        ValueSetExpansion[] result = roi_
                .expandValueSetExpansionContext("<context><baseValueSetId>2.16.840.1.113883.1.11.11527</baseValueSetId><nestedValueSetId>2.16.840.1.113883.1.11.13856</nestedValueSetId><nestingDepth>1</nestingDepth><language>en</language><timeout>0</timeout><sizeLimit>0</sizeLimit></context>"
                        .getBytes());
        assertNotNull(result);
        assertFalse("Value set expansion is empty", result.length == 0);
        try
        {
            roi_.expandValueSetExpansionContext("foofnar".getBytes());
            fail("Didn't throw InvalidExpansionContext");
        }
        catch (InvalidExpansionContext e)
        {
            //supposed to happen
        }
        try
        {
            roi_.expandValueSetExpansionContext(null);
            fail("Didn't throw InvalidExpansionContext");
        }
        catch (InvalidExpansionContext e)
        {
            //supposed to happen
        }
    }
}