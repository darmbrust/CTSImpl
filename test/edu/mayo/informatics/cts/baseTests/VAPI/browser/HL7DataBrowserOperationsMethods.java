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
package edu.mayo.informatics.cts.baseTests.VAPI.browser;

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
 * JUnit test cases for BrowserOperationsImpl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class HL7DataBrowserOperationsMethods extends TestCase
{
    private BrowserOperations boi_;
    
    public HL7DataBrowserOperationsMethods()
    {
        super();
    }
    public HL7DataBrowserOperationsMethods(String name, BrowserOperations boi)
    {
        super(name);
        boi_ = boi;
     
    }
    
    protected void setUp() throws Exception
    {
        
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
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
    
    public void testExpandCodeExpansionContextForward() throws Exception
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
                .expandCodeExpansionContext("<context version=\"3\"><forward>true</forward><directDescendantsOnly>true</directDescendantsOnly><codeSystemName>ActCode</codeSystemName><relationship>hasSubtype</relationship><ldapName>association=hasSubtype,dc=Relations,codingScheme=ActCode</ldapName><nestingDepth>2</nestingDepth><language>en</language><defaultLanguage>en</defaultLanguage><conceptCode>DIET</conceptCode><sourceCodeSystemId>2.16.840.1.113883.5.4</sourceCodeSystemId><defaultTargetCodeSystemId>2.16.840.1.113883.5.4</defaultTargetCodeSystemId><timeout>0</timeout><sizeLimit>1000</sizeLimit></context>"
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
                .expandCodeExpansionContext("<context version=\"3\"><forward>false</forward><directDescendantsOnly>true</directDescendantsOnly><expansionContextVersion>2</expansionContextVersion><codeSystemName>ActCode</codeSystemName><relationship>hasSubtype</relationship><ldapName>association=hasSubtype,dc=Relations,codingScheme=ActCode</ldapName><nestingDepth>2</nestingDepth><language>en</language><defaultLanguage>en</defaultLanguage><conceptCode>DIET</conceptCode><sourceCodeSystemId>2.16.840.1.113883.5.4</sourceCodeSystemId><defaultTargetCodeSystemId>2.16.840.1.113883.5.4</defaultTargetCodeSystemId><timeout>0</timeout><sizeLimit>1000</sizeLimit></context>"
                        .getBytes());

        assertNotNull(result);
    }

    public void testGetSupportedCodeSystems() throws Exception
    {
        CodeSystemIdAndVersions[] result = boi_.getSupportedCodeSystems(0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 supported code systems", result.length == 0);

        result = boi_.getSupportedCodeSystems(1000, 5);
        assertTrue("Didn't limit code system return list", result.length == 5);
    }

    public void testLookupCodeExpansionForward() throws Exception
    {
        //conceptId with code");
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId3.setConcept_code("AGE");

        //conceptid with no code");
        ConceptId conceptId4 = new ConceptId();
        conceptId4.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId4.setConcept_code(null);

        //test with code
        RelatedCode[] result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", true, false, "en", 0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);

        //test without code
        result = boi_.lookupCodeExpansion(conceptId4, "hasSubtype", true, true, "en", 0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);
        
        //test with no code on a coding scheme that doesn't support hasSubtype
        ConceptId conceptId9 = new ConceptId();
        conceptId9.setCodeSystem_id("2.16.840.1.113883.5.1009");
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
            conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
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
            conceptId2.setConcept_code("AGE");
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
        conceptId3.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId3.setConcept_code("PAR");

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
            conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
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
            conceptId2.setConcept_code("AGE");
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
    }

    public void testLookupCompleteCodedConcept() throws Exception
    {
        ConceptId conceptId2 = new ConceptId();
        conceptId2.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId2.setConcept_code("DIET");
        CompleteCodedConceptDescription result = boi_.lookupCompleteCodedConcept(conceptId2);

        assertNotNull(result);

        try
        {
            ConceptId conceptId3 = new ConceptId();
            conceptId3.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId3.setConcept_code("foofnar");

            boi_.lookupCompleteCodedConcept(conceptId3);
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("DIET");

            boi_.lookupCompleteCodedConcept(conceptId1);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

    }

    public void testLookupConceptCodesByDesignation() throws Exception
    {
        ConceptId[] results = boi_.lookupConceptCodesByDesignation("2.16.840.1.113883.5.4", "Intermediate",
                                                                   "StartsWithIgnoreCase", "en", true, 0, 1000);
        assertNotNull(results);
        assertFalse("Returned 0 codes", results.length == 0);

        results = boi_.lookupConceptCodesByDesignation("2.16.840.1.113883.5.4", "I",
                                                       "StartsWithIgnoreCase", "en", true, 3000, 5);
        
        assertTrue("Didn't limit code results", results.length == 5);


        try
        {
            boi_.lookupConceptCodesByDesignation("2.16.840.1.113883.5.4", "Intermediate", "Foofnar", "en", true, 0, 1000);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            //supposed to happen
        }

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
            boi_.lookupConceptCodesByDesignation("2.16.840.1.113883.5.4", "Intermediate*", "StartsWithIgnoreCase",
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

        ConceptId[] result = boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.4", "substance",
                                                               "StartsWithIgnoreCase", "en", true,
                                                               new String[]{"textualPresentation"}, null, 0, 1000);
        assertNotNull(result);
        assertTrue(1 == result.length);
        
        result = boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.4", "s",
                                                   "StartsWithIgnoreCase", "en", true,
                                                   new String[]{"textualPresentation"}, null, 0, 5);
        
        assertTrue("Didn't limit results properly", result.length == 5);

        

        try
        {
            boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.4", "substance", "Foofnar", "en", true,
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
            boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.4", "substance", "StartsWithIgnoreCase", "en", true,
                                              new String[]{"textualPresentation"},
                                              new String[]{"text/plain", "foofnar"}, 0, 1000);
            fail("Didn't throw UnknownMimeTypeCode");
        }
        catch (UnknownMimeTypeCode e)
        {
            // supposed to happen
        }

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
            boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.4", "substance", "StartsWithIgnoreCase", "frc",
                                              true, new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

        try
        {
            boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.4", "substance", "StartsWithIgnoreCase", null, true,
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
        conceptId.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId.setConcept_code("AE");

        ConceptDesignation[] results = boi_.lookupDesignations(conceptId, "am", "StartsWithIgnoreCase", "en");

        assertNotNull(results);
        assertFalse("Returned 0 codes", results.length == 0);
        
        
        try
        {
            boi_.lookupDesignations(conceptId, "am", "Foofnar", "en");
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId1.setConcept_code("foofnar");
            boi_.lookupDesignations(conceptId1, "am", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("AE");
            boi_.lookupDesignations(conceptId1, "am", "StartsWithIgnoreCase", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //Supposed to happen
        }

        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId1.setConcept_code("AE");
            boi_.lookupDesignations(conceptId1, "am", "StartsWithIgnoreCase", "foofnar");
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
        conceptId5.setCodeSystem_id("2.16.840.1.113883.5.6");
        conceptId5.setConcept_code("SPLY");

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
            conceptId1.setCodeSystem_id("2.16.840.1.113883.5.6");
            conceptId1.setConcept_code("foofnar");

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