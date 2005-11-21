package org.hl7.baseTests.VAPI.browser;

import junit.framework.TestCase;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.CTSVersionId;
import org.hl7.CTSVAPI.CodeSystemIdAndVersions;
import org.hl7.CTSVAPI.CompleteCodedConceptDescription;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.InvalidExpansionContext;
import org.hl7.CTSVAPI.RelatedCode;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownConceptCode;
import org.hl7.CTSVAPI.UnknownLanguageCode;
import org.hl7.CTSVAPI.UnknownMatchAlgorithm;
import org.hl7.CTSVAPI.UnknownMimeTypeCode;
import org.hl7.CTSVAPI.UnknownPropertyCode;
import org.hl7.CTSVAPI.UnknownRelationshipCode;

/**
 * <pre>
 * Title:        TestBrowserOperations.java
 * Description:  JUnit test cases for BrowserOperationsImpl.
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
 * @version 1.0 - cvs $Revision: 1.9 $ checked in on $Date: 2005/09/28 19:25:05 $
 */

public class TestBrowserOperationsExceptions extends TestCase
{
    
    private BrowserOperations boi_;    
    
    public TestBrowserOperationsExceptions() {
       
        super();
    }
    
    public TestBrowserOperationsExceptions(String name, BrowserOperations boi)
    {
        super(name);
        boi_ = boi;
      
    }
    
    public void testGetCTSVersion() throws Exception
    {
        
        CTSVersionId temp = new CTSVersionId();
        temp.setMajor((short) 1);
        temp.setMinor((short) 2);
        assertNotNull(boi_.getCTSVersion());
        assertEquals(boi_.getCTSVersion(), temp);
    
    }

    public void testGetServiceDescription() throws Exception
    {
        assertNotNull(boi_.getServiceDescription());
    }

    public void testGetServiceName() throws Exception
    {
        assertNotNull(boi_.getServiceName());
    }

    public void testGetServiceVersion() throws Exception
    {
        assertNotNull(boi_.getServiceVersion());
    }
    
    public void testGetSupportedMatchAlgorithms() throws Exception
    {
        String[] result = boi_.getSupportedMatchAlgorithms();
        assertNotNull(result);
        assertTrue("Did not return correct number of match algorithms", result.length >= 4);
    }


    public void testExpandCodeExpansionContextForward() throws Exception
    {
        try
        {
            assertNotNull(boi_.expandCodeExpansionContext("foofnar".getBytes()));
            fail("Shoud raise InvalidExpansionContext");
        }
        catch (InvalidExpansionContext e)
        {
            // supposed to happen
        }

        // This string is a combination of the SQL and XML strings (so it works on both)
        RelatedCode[] result = boi_
                .expandCodeExpansionContext("<?xml version='1.0' encoding='UTF-8'?><context version='3'><forward>true</forward><ldapName>association=hasSubtype,dc=Relations,codingScheme=Automobiles</ldapName><codeSystemName>Automobiles</codeSystemName><nestingDepth>1</nestingDepth><language>en</language><conceptCode>A0001</conceptCode><relationship>hasSubtype</relationship><timeout>0</timeout><sizeLimit>1000</sizeLimit><defaultLanguage>en</defaultLanguage><directDescendantsOnly>false</directDescendantsOnly><defaultTargetCodeSystemId>Automobiles</defaultTargetCodeSystemId><sourceCodeSystemId>11.11.0.1</sourceCodeSystemId></context>"
                        .getBytes());
        assertNotNull(result);

    }

    public void testExpandCodeExpansionContextReverse() throws Exception
    {
        try
        {
            assertNotNull(boi_.expandCodeExpansionContext("foofnar".getBytes()));
            fail("Shoud raise InvalidExpansionContext");
        }
        catch (InvalidExpansionContext e)
        {
            //supposed to happen
        }

        RelatedCode[] result = boi_
                .expandCodeExpansionContext("<context version=\"3\"><forward>true</forward><ldapName>association=hasSubtype,dc=Relations,codingScheme=Automobiles</ldapName><codeSystemName>Automobiles</codeSystemName><nestingDepth>2</nestingDepth><language>en</language><conceptCode>A0001</conceptCode><relationship>hasSubtype</relationship><timeout>0</timeout><sizeLimit>1000</sizeLimit><defaultLanguage>en</defaultLanguage><directDescendantsOnly>true</directDescendantsOnly><defaultTargetCodeSystemId>Automobiles</defaultTargetCodeSystemId><sourceCodeSystemId>11.11.0.1</sourceCodeSystemId></context>".getBytes());

        assertNotNull(result);
    }

    public void testGetSupportedCodeSystems() throws Exception
    {
        CodeSystemIdAndVersions[] result = boi_.getSupportedCodeSystems(0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 supported code systems", result.length == 0);

        result = boi_.getSupportedCodeSystems(1000, 5);
        assertTrue("Didn't limit code system return list", result.length <= 5);
    }

    public void testLookupCodeExpansionForward() throws Exception
    {
        //conceptId with code");
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("11.11.0.1");
        conceptId3.setConcept_code("A0001");
   
        //conceptid with no code");
        ConceptId conceptId4 = new ConceptId();
        conceptId4.setCodeSystem_id("11.11.0.1");
        conceptId4.setConcept_code(null);

        //test with code
        RelatedCode[] result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", true, false, "en", 0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);

        //test without code
        result = boi_.lookupCodeExpansion(conceptId4, "hasSubtype", true, false, "en", 0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);

        //test with no code on a coding scheme that doesn't support hasSubtype
        ConceptId conceptId9 = new ConceptId();
        conceptId9.setCodeSystem_id("11.11.0.1");
        conceptId9.setConcept_code(null);
        result = boi_.lookupCodeExpansion(conceptId9, "hasSubtype", true, true, "en", 0, 1000);
        
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);

        //test result limiting
        result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", true, false, "en", 0, 1);
        assertTrue("Didn't properly limit results", result.length == 1);

        try
        {
            result = boi_.lookupCodeExpansion(conceptId3, "foofnar", true, false, "en", 0, 1000);
            fail("Didn't throw UnknownRelationshipCode");
        }
        catch (UnknownRelationshipCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("foofnar");

            result = boi_.lookupCodeExpansion(conceptId1, "hasSubtype", true, false, "en", 0, 1000);
            fail("Didn't throw UnknownConceptCode");
        }
	    catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("foofnar");
            conceptId2.setConcept_code("P0001");
            result = boi_.lookupCodeExpansion(conceptId2, "hasSubtype", true, false, "en", 0, 1000);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            result = boi_.lookupCodeExpansion(conceptId4, "hasSubtype", true, false, "foofnar", 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            //supposed to happen
        }

    }

    public void testLookupCodeExpansionReverse() throws Exception
    {

        //conceptId with code");
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("11.11.0.1");
        conceptId3.setConcept_code("C0001");

        //test with code
        RelatedCode[] result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", false, false, "en", 0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);
        
        result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", false, false, "en", 0, 1);
        assertTrue("Didn't properly limit results", result.length == 1);


        try
        {
            result = boi_.lookupCodeExpansion(conceptId3, "foofnar", false, false, "en", 0, 1000);
            fail("Didn't throw UnknownRelationshipCode");
        }
        catch (UnknownRelationshipCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("foofnar");

            result = boi_.lookupCodeExpansion(conceptId1, "hasSubtype", false, false, "en", 0, 1000);
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("foofnar");
            conceptId2.setConcept_code("A0001");
            result = boi_.lookupCodeExpansion(conceptId2, "hasSubtype", false, false, "en", 0, 1000);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", false, false, "foofnar", 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            //supposed to happen
        }

        //        try
        //        {
        //            ConceptId conceptId5 = new ConceptId();
        //            conceptId5.setCodeSystem_id("2.16.840.1.113883.5.4");
        //            conceptId5.setConcept_code("AGE");
        //            result = boi_.lookupCodeExpansion(conceptId5, "hasSubtype", false, false, "ge");
        //            fail("Didn't throw NoApplicableDesignationFound");
        //        }
        //        catch (NoApplicableDesignationFound e)
        //        {
        //            //supposed to happen
        //  
        // }
   
    }

    public void testLookupCompleteCodedConcept() throws Exception
    {
        ConceptId conceptId2 = new ConceptId();
        conceptId2.setCodeSystem_id("11.11.0.1");
        conceptId2.setConcept_code("A0001");
        CompleteCodedConceptDescription result = boi_.lookupCompleteCodedConcept(conceptId2);

        assertNotNull(result);

        try
        {
            ConceptId conceptId3 = new ConceptId();
            conceptId3.setCodeSystem_id("11.11.0.1");
            conceptId3.setConcept_code("sdfkjds");

            boi_.lookupCompleteCodedConcept(conceptId3);
            fail("Didn't throw UnknownConceptCode 1");
        }
        catch (UnknownConceptCode e)
    
        {
            //supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("C0001");

            boi_.lookupCompleteCodedConcept(conceptId1);
            fail("Didn't throw UnknownCodeSystem 2");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

    }

    public void testLookupConceptCodesByDesignation() throws Exception
    {
        ConceptId[] results = boi_.lookupConceptCodesByDesignation("11.11.0.1", "a",
                                                                   "StartsWithIgnoreCase", "en", true, 0, 1000);
        assertNotNull(results);
        assertFalse("Returned 0 codes", results.length == 0);

        results = boi_.lookupConceptCodesByDesignation("11.11.0.1", "a",
                                                       "StartsWithIgnoreCase", "en", true, 1000, 1);
        
        assertTrue("Didn't limit code results", results.length <= 1);


        try
        {
            boi_.lookupConceptCodesByDesignation("11.11.0.1", "Automobile", "Foofnar", "en", true, 0, 1000);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            //supposed to happen
        }
     
        
        //TODO figure out how to make this happen with sql backend
//        try
//        {
//            boi_.lookupConceptCodesByDesignation("11.11.0.1", "*sdf(", "", "en", true, 0, 1000);
//            fail("Didn't throw BadlyFormedMatchText");
//        }
//        catch (BadlyFormedMatchText e)
//        {
//            //supposed to happen
//        }

        try
        {
            boi_.lookupConceptCodesByDesignation("foofnar", "Intermediate", null, "en", true, 0,
                                                 1000);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            boi_.lookupConceptCodesByDesignation("11.11.0.1", "Automobile", "StartsWithIgnoreCase",
                                                 "foofnar", true, 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            //supposed to happen
        }

    }

    public void testLookupConceptCodesByProperty() throws Exception
    {

        ConceptId[] result = boi_.lookupConceptCodesByProperty("11.11.0.1", "c",
                                                               "StartsWithIgnoreCase", "en", true,
                                                               new String[]{"textualPresentation"}, null, 0, 1000);
        assertNotNull(result);
        assertTrue(2 == result.length);
       
        result = boi_.lookupConceptCodesByProperty("11.11.0.1", "c",
                                                   "StartsWithIgnoreCase", "en", true,
                                                   new String[]{"textualPresentation"}, null, 0, 5);
        
        assertFalse("Returned zero results", result.length == 0);
        assertTrue("Didn't limit results properly", result.length <= 5);

        

        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "Foofnar", "en", true,
                                              new String[]{"textualPresentation"},
                                              new String[]{"text/plain"}, 0, 1000);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        
 
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "StartsWithIgnoreCase", "en", true,
                                              new String[]{"textualPresentation"},
                                              new String[]{"text/plain", "foofnar"}, 0, 1000);
            fail("Didn't throw UnknownMimeTypeCode");
        }
        catch (UnknownMimeTypeCode e)
        {
            // supposed to happen
        }

        //TODO figure out a way to make this happen on the sql implementation
//        try
//        {
//            boi_.lookupConceptCodesByProperty("11.11.0.1", "substance*(()", "", "en", true,
//                                              new String[]{"textualPresentation"}, null, 0, 1000);
//            fail("Didn't throw BadlyFormedMatchText");
//        }
//        catch (BadlyFormedMatchText e)
//        {
//            // supposed to happen
//        }

        try
        {
            boi_.lookupConceptCodesByProperty("foofnar", "substance", "StartsWithIgnoreCase", "en", true,
                                              new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "StartsWithIgnoreCase", "frc",
                                              true, new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "StartsWithIgnoreCase", null, true,
                                              new String[]{"textualPresentation", "foofnar"}, null, 0, 1000);
            fail("Didn't throw UnknownPropertyCode");
        }
        catch (UnknownPropertyCode e)
        {
            // supposed to happen
        }
    }

    public void testLookupDesignations() throws Exception
    {
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("11.11.0.1");
        conceptId.setConcept_code("A0001");

        ConceptDesignation[] results = boi_.lookupDesignations(conceptId, "A", "StartsWithIgnoreCase", "en");

        assertNotNull(results);
        assertFalse("Returned 0 codes", results.length == 0);
        
        
        try
        {
            boi_.lookupDesignations(conceptId, "a", "Foofnar", "en");
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("foofnar");
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code(null);
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("");
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        //TODO figure out how to throw this on both implementations
//        try
//        {
//            ConceptId conceptId1 = new ConceptId();
//            conceptId1.setCodeSystem_id("11.11.0.1");
//            conceptId1.setConcept_code("A0001");
//            boi_.lookupDesignations(conceptId1, "?><,.\" ((~~`", "", "en");
//            fail("Didn't throw BadlyFormedMatchText");
//        }
//        catch (BadlyFormedMatchText e)
//        {
//            //supposed to happen
//        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //Supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id(null);
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //Supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //Supposed to happen
        }
        
        try
        {
            boi_.lookupDesignations(null, "a", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //Supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a", "StartsWithIgnoreCase", "foofnar");
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            //supposed to happen
        }

    }

    public void testLookupProperties() throws Exception
    {
        ConceptId conceptId5 = new ConceptId();
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("A0001");

        ConceptProperty[] result = boi_.lookupProperties(conceptId5, new String[]{"textualPresentation", 
                "definition"}, null, "",  "en", new String[]{});
        assertNotNull(result);
        assertTrue(result.length == 2);
      
        try
        {
            boi_.lookupProperties(conceptId5, null, "a", "Foofnar", "en", new String[]{});
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        try
        {
            boi_.lookupProperties(conceptId5, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/fun"});
            fail("Didn't throw UnknownMimeTypeCode");
        }
        catch (UnknownMimeTypeCode e)
        {
            // supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("blah");

            boi_.lookupProperties(conceptId1, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("");

            boi_.lookupProperties(conceptId1, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code(null);

            boi_.lookupProperties(conceptId1, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }
        
        //TODO figure out how to make this happen in the sql backend
//        try
//        {
//            boi_.lookupProperties(conceptId5, null, "&34f((", "EndsWithIgnoreCase", "en", new String[]{"text/plain"});
//            fail("Didn't throw BadlyFormedMatchText");
//        }
//
//        catch (BadlyFormedMatchText e)
//        {
//            // supposed to happen
//        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("foofnar");

            boi_.lookupProperties(conceptId1, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
        
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id(null);
            conceptId1.setConcept_code("foofnar");

            boi_.lookupProperties(conceptId1, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }
        
        try
        {
            boi_.lookupProperties(null, null, null, "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        try
        {

            boi_
                    .lookupProperties(conceptId5, null, null, "StartsWithIgnoreCase", "foofnar",
                                      new String[]{"text/plain"});
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

        try
        {

            boi_.lookupProperties(conceptId5, new String[]{"textualPresentation", "foonar"}, null,
                                  "StartsWithIgnoreCase", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownPropertyCode");
        }
        catch (UnknownPropertyCode e)
        {
            // supposed to happen
        }
  
     }
    
}