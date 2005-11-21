package org.hl7.baseTests.MAPI.browser;

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
 * <pre>
 * Title:        TestBrowserOperations.java
 * Description:  JUnit test cases for MapiBrowserOperations.
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.3 $ checked in on $Date: 2005/05/25 22:03:17 $
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