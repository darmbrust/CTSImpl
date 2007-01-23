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
package edu.mayo.informatics.cts.baseTests.MAPI.browser;

import junit.framework.TestCase;

import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.CTSVersionId;
import org.hl7.CTSMAPI.CodeSystemDescriptor;
import org.hl7.CTSMAPI.CodeSystemInfo;
import org.hl7.CTSMAPI.CodeSystemNameIdMismatch;
import org.hl7.CTSMAPI.ConceptId;
import org.hl7.CTSMAPI.FullValueSetDescription;
import org.hl7.CTSMAPI.NoApplicableValueSet;
import org.hl7.CTSMAPI.RIMCodedAttribute;
import org.hl7.CTSMAPI.UnknownApplicationContextCode;
import org.hl7.CTSMAPI.UnknownCodeSystem;
import org.hl7.CTSMAPI.UnknownConceptCode;
import org.hl7.CTSMAPI.UnknownMatchAlgorithm;
import org.hl7.CTSMAPI.UnknownValueSet;
import org.hl7.CTSMAPI.UnknownVocabularyDomain;
import org.hl7.CTSMAPI.ValueSetDescriptor;
import org.hl7.CTSMAPI.ValueSetNameIdMismatch;
import org.hl7.CTSMAPI.VocabularyDomainDescription;
import org.hl7.cts.types.BL;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.utility.Constructors;

/**
 * JUnit test cases for MapiBrowserOperations.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestBrowserOperations extends TestCase
{
    private BrowserOperations boi_;

    public TestBrowserOperations()
    {
        super();    
    }
    
    public TestBrowserOperations(String name, BrowserOperations boi)
    {
        super(name);
        boi_ = boi;
      
    }
    /*
    public void setUp() throws Exception
    {
        boi_ = OperationsHolder._interface().getBrowserOperations();
    }
*/
    public void testGetServiceName() throws Exception
    {
        ST result = boi_.getServiceName();
        assertNotNull(result);
        assertNotNull(result.getV());
    }

    public void testGetServiceVersion() throws Exception
    {
        ST result = boi_.getServiceVersion();
        assertNotNull(result);
        assertNotNull(result.getV());
    }

    public void testGetCTSVersion() throws Exception
    {
        CTSVersionId result = boi_.getCTSVersion();
        assertNotNull(result);
        assertNotNull(result.getMajor());
        assertNotNull(result.getMinor());
    }

    public void testGetHL7ReleaseVersion() throws Exception
    {
        ST result = boi_.getHL7ReleaseVersion();
        assertNotNull(result);
        assertNotNull(result.getV());
    }

    public void testGetServiceDescription() throws Exception
    {
        ST result = boi_.getServiceDescription();
        assertNotNull(result);
        assertNotNull(result.getV());
    }

    public void testGetSupportedAttributes() throws Exception
    {
        RIMCodedAttribute[] result = boi_.getSupportedAttributes(null, null, 0, 500);
        assertNotNull(result);
        assertFalse("Returned 0 attributes", result.length == 0);
        
        result = boi_.getSupportedAttributes(null, null, 0, 2);
        assertTrue("Didn't limit results", result.length == 2);
        
//        try
//        {
//            result = boi_.getSupportedAttributes(null, null, 1, 0);
//            fail("Didn't throw timeout error");
//        }
//  
//        catch (TimeoutError e)
//        {
//            // supposed to happen
//        }
        
        try
        {
            result = boi_.getSupportedAttributes(Constructors.stm("pay"), Constructors.stm("foonar"), 1, 0);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
  
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }
        
        //TODO come up with a way to test BadlyFormedMatchText
    }

    public void testGetSupportedVocabularyDomains() throws Exception
    {
        ST[] result = boi_.getSupportedVocabularyDomains(null, null, 0, 500);
        assertNotNull(result);
        assertFalse("Returned 0 vocabulary domains", result.length == 0);
        
        result = boi_.getSupportedVocabularyDomains(null, null, 0, 2);
        assertTrue("Didn't limit results", result.length == 2);
        
//        try
//        {
//            result = boi_.getSupportedVocabularyDomains(null, null, 1, 0);
//            fail("Didn't throw timeout error");
//        }
//  
//        catch (TimeoutError e)
//        {
//            // supposed to happen
//        }
        
        try
        {
            result = boi_.getSupportedVocabularyDomains(Constructors.stm("pay"), Constructors.stm("foonar"), 1, 0);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
  
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }
        
        //TODO come up with a way to test BadlyFormedMatchText

    }

    public void testGetSupportedValueSets() throws Exception
    {
        ValueSetDescriptor[] result = boi_.getSupportedValueSets(null, null, 0, 500);
        assertNotNull(result);
        assertFalse("Returned 0 value sets", result.length == 0);
        
        result = boi_.getSupportedValueSets(null, null, 0, 2);
        assertTrue("Didn't limit results", result.length == 2);
        
//        try
//        {
//            result = boi_.getSupportedValueSets(null, null, 1, 0);
//            fail("Didn't throw timeout error");
//        }
//  
//        catch (TimeoutError e)
//        {
//            // supposed to happen
//        }
        
        try
        {
            result = boi_.getSupportedValueSets(Constructors.stm("pay"), Constructors.stm("foonar"), 1, 0);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
  
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }
        
        //TODO come up with a way to test BadlyFormedMatchText
    }

    public void testGetSupportedCodeSystems() throws Exception
    {
        CodeSystemDescriptor[] result = boi_.getSupportedCodeSystems(null, null, 0, 500);
        assertNotNull(result);
        assertFalse("Returned 0 code sets", result.length == 0);
        
        result = boi_.getSupportedCodeSystems(null, null, 0, 2);
        assertTrue("Didn't limit results", result.length == 2);
        
//        try
//        {
//            result = boi_.getSupportedCodeSystems(null, null, 1, 0);
//            fail("Didn't throw timeout error");
//        }
//  
//        catch (TimeoutError e)
//        {
//            // supposed to happen
//        }
        
        try
        {
            result = boi_.getSupportedCodeSystems(Constructors.stm("pay"), Constructors.stm("foonar"), 1, 0);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
  
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }
        
        //TODO come up with a way to test BadlyFormedMatchText
    }

    public void testLookupVocabularyDomain() throws Exception
    {
        VocabularyDomainDescription result = boi_.lookupVocabularyDomain(Constructors.stm("AdministrativeGender"));
        assertNotNull(result);

        try
        {
            boi_.lookupVocabularyDomain(Constructors.stm("foofnar"));
            fail("Didn't throw UnknownVocabularyDomain");
        }
        catch (UnknownVocabularyDomain e)
        {
            // supposed to happen
        }
    }

    public void testLookupValueSet() throws Exception
    {
        FullValueSetDescription result = boi_.lookupValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), Constructors
                .stm("IntravenousInfusion"));
        assertNotNull(result);

        result = boi_.lookupValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), null);
        assertNotNull(result);

        result = boi_.lookupValueSet(Constructors.uidm(""), Constructors.stm("IntravenousInfusion"));
        assertNotNull(result);

        try
        {
            boi_.lookupValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), Constructors.stm("IntravenousfooInfusion"));
            fail("Didn't throw ValueSetNameIdMismatch");
        }
        catch (ValueSetNameIdMismatch e)
        {
            // suppose to happen
        }

        try
        {
            boi_.lookupValueSet(Constructors.uidm("foofnar"), Constructors.stm("IntravenousInfusion"));
            fail("Didn't throw UnknownValueSet");
        }
        catch (UnknownValueSet e)
        {
            //suppose to happen
        }
    }

    public void testLookupCodeSystem() throws Exception
    {
        CodeSystemInfo result = boi_.lookupCodeSystem(Constructors.uidm("2.16.840.1.113883.5.1007"), Constructors.stm("DataType"));
        assertNotNull(result);

        try
        {
            boi_.lookupCodeSystem(Constructors.uidm("2.16.840.1.113883.5.1007"), Constructors.stm("foofnar"));
            fail("Didn't throw CodeSystemNameIdMismatch");
        }
        catch (CodeSystemNameIdMismatch e)
        {
            //supposed to happen
        }

        try
        {
            boi_.lookupCodeSystem(Constructors.uidm("foofnar"), Constructors.stm("DataType"));
            fail("Didn't throw CodeSystemNameIdMismatch");
        }

        catch (CodeSystemNameIdMismatch e)
        {
            // supposed to happen
        }
        
        try
        {
            boi_.lookupCodeSystem(Constructors.uidm("foofnar"), Constructors.stm("fred"));
            fail("Didn't throw UnknownCodeSystem");
        }

        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
    }

    public void testLookupValueSetForDomain() throws Exception
    {
        ValueSetDescriptor result = boi_.lookupValueSetForDomain(Constructors.stm("ActProcedureCode"), Constructors.stm("Canada"));
        assertNotNull(result);

        try
        {
            boi_.lookupValueSetForDomain(Constructors.stm("ActProcedureCode"), Constructors.stm("USA"));
            fail("Didn't throw NoApplicableValueSet");
        }
        catch (NoApplicableValueSet e)
        {
            // supposed to happen
        }

        try
        {
            boi_.lookupValueSetForDomain(Constructors.stm("ActInjuryCode"), Constructors.stm("foo"));
            fail("Didn't throw UnknownApplicationContextCode");
        }
        catch (UnknownApplicationContextCode e)
        {
            //supposed to happen
        }

        try
        {
            boi_.lookupValueSetForDomain(Constructors.stm("Afoo"), Constructors.stm("Canada"));
            fail("Didn't throw UnknownVocabularyDomain");
        }
        catch (UnknownVocabularyDomain e)
        {
            //supposed to happen
        }

        try
        {
            boi_.lookupValueSetForDomain(null, null);
            fail("Didn't throw UnknownVocabularyDomain");
        }
        catch (UnknownVocabularyDomain e)
        {
            //supposed to happen
        }
    }

    public void testIsCodeInValueSet() throws Exception
    {
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id(Constructors.uidm("2.16.840.1.113883.5.112"));
        conceptId.setConcept_code(Constructors.stm("PCA"));
        BL result = boi_.isCodeInValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), Constructors.stm("IntravenousInfusion"),
                Constructors.blm(true), conceptId);
        assertNotNull(result);
        assertTrue(result.isV());

        try
        {
            boi_.isCodeInValueSet(Constructors.uidm("foofnar"), Constructors.stm("IntravenousInfusion"), Constructors.blm(true), conceptId);
            fail("Didn't throw UnknownValueSet");
        }
        catch (UnknownValueSet e)
        {
            //supposed to happen
        }

        try
        {
            boi_.isCodeInValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), Constructors.stm("foofnar"), Constructors.blm(true),
                    conceptId);
            fail("Didn't throw ValueSetNameIdMismatch");
        }
        catch (ValueSetNameIdMismatch e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id(Constructors.uidm("2.16.840.1.113883.5.112"));
            conceptId1.setConcept_code(Constructors.stm("foofnar"));

            boi_.isCodeInValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), Constructors.stm("IntravenousInfusion"), Constructors
                    .blm(true),
                    conceptId1);
            fail("Didn't throw UnknownConceptCode ");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id(Constructors.uidm("foofnar"));
            conceptId1.setConcept_code(Constructors.stm("PCA"));

            boi_.isCodeInValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"), Constructors.stm("IntravenousInfusion"), Constructors
                    .blm(true),
                    conceptId1);
            fail("Didn't throw UnknownCodeSystem ");
        }

        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }
    }
}