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
package edu.mayo.informatics.cts.baseTests.VAPI.runtime;

import junit.framework.TestCase;

import org.hl7.CTSVAPI.CodeSystemIdAndVersions;
import org.hl7.CTSVAPI.CodeSystemInfo;
import org.hl7.CTSVAPI.CodeSystemNameIdMismatch;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.NoApplicableDesignationFound;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.StringAndLanguage;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownConceptCode;
import org.hl7.CTSVAPI.UnknownLanguageCode;
import org.hl7.CTSVAPI.UnknownRelationQualifier;
import org.hl7.CTSVAPI.UnknownRelationshipCode;

/**
 * JUnit test cases for RuntimeOperationsImpl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestRuntimeOperationsExceptions extends TestCase
{
    private RuntimeOperations roi_;
 
    public TestRuntimeOperationsExceptions()
    {
        super();      
    }
    public TestRuntimeOperationsExceptions(String name, RuntimeOperations roi)
    {
        super(name);    
        roi_ = roi;
    }
    
    public void testGetServiceName() throws Exception
    {
        assertNotNull(roi_.getServiceName());
    }

    public void testGetServiceVersion() throws Exception
    {
        assertNotNull(roi_.getServiceVersion());
    }

    public void testGetCTSVersion() throws Exception
    {
        assertNotNull(roi_.getCTSVersion());
    }

    public void testGetServiceDescription() throws Exception
    {
        assertNotNull(roi_.getServiceDescription());
    }

    public void testLookupCodeSystemInfo() throws Exception
    {
       
        CodeSystemInfo result = roi_.lookupCodeSystemInfo("11.11.0.1", "Automobiles");
        assertNotNull(result);

        result = roi_.lookupCodeSystemInfo(null, "Automobiles");
        assertNotNull(result);

        result = roi_.lookupCodeSystemInfo("11.11.0.1", null);
        assertNotNull(result);

        try
        {
            roi_.lookupCodeSystemInfo("11.11.0.1", "German Made Parts");
            fail("Didn't throw CodeSystemNameIdMismatch");
        }
        catch (CodeSystemNameIdMismatch e)
        {
            //supposed to happen
        }

        try
        {
            roi_.lookupCodeSystemInfo(null, "foofnar");
            fail("Didn't throw UnknownCodeSystem");
        }

        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            roi_.lookupCodeSystemInfo("foofnar", null);
            fail("Didn't throw UnknownCodeSystem");
        }

        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            roi_.lookupCodeSystemInfo(null, null);
            fail("Didn't throw UnknownCodeSystem");
        }

        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            roi_.lookupCodeSystemInfo("", "");
            fail("Didn't throw UnknownCodeSystem");
        }

        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

    }

    public void testGetSupportedCodeSystems() throws Exception
    {
        CodeSystemIdAndVersions[] results = roi_.getSupportedCodeSystems(0, 1000);
        assertNotNull(results);
        assertFalse("No supported code systems found", results.length == 0);

        results = roi_.getSupportedCodeSystems(1000, 5);
        assertNotNull(results);
        assertTrue("Did not limit to right count", results.length <= 5);
        results = roi_.getSupportedCodeSystems(2000, 1000);

    }

    public void testIsConceptIdValid() throws Exception
    {
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("11.11.0.1");
        conceptId.setConcept_code("C0001");

        assertTrue(roi_.isConceptIdValid(conceptId, true));

        conceptId.setConcept_code("foofnar");

        assertFalse(roi_.isConceptIdValid(conceptId, true));

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("foofnar");
            conceptId2.setConcept_code("16");
            assertFalse(roi_.isConceptIdValid(conceptId2, true));
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

    }

    public void testLookupDesignation() throws Exception
    {
        try {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.2");
            conceptId1.setConcept_code("E0001");
        	roi_.lookupDesignation(conceptId1, "en");
        	fail("didn't throw NoApplicableDesignation");
        }
         
        catch (NoApplicableDesignationFound e) {
            //supposed to happen
        }
      
        ConceptId conceptId1 = new ConceptId();
        conceptId1.setCodeSystem_id("11.11.0.1");
        conceptId1.setConcept_code("C0001");
   
        StringAndLanguage result = roi_.lookupDesignation(conceptId1, "en");
        assertNotNull(result);
        assertNotNull(result.getLanguage_code());
        assertNotNull(result.getText());

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("foofnar");
            conceptId2.setConcept_code("C0002");
            roi_.lookupDesignation(conceptId2, "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("11.11.0.1");
            conceptId2.setConcept_code("foofnar");
            roi_.lookupDesignation(conceptId2, "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("11.11.0.1");
            conceptId2.setConcept_code("C0002");
            roi_.lookupDesignation(conceptId2, "foofnar");
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            //supposed to happen
        }

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("11.11.0.2");
            conceptId2.setConcept_code("T0001");
            roi_.lookupDesignation(conceptId2, "en");
            fail("Didn't throw NoApplicableDesignationFound");
        }
        catch (NoApplicableDesignationFound e)
        {
            //supposed to happen
        }

    }

    public void testAreCodesRelated() throws Exception
    {
        assertTrue(roi_.areCodesRelated("11.11.0.1", "A0001", "C0001", "hasSubtype", null, true));

        try
        {
            assertTrue(roi_.areCodesRelated("11.11.0.1", "A0001", "C0001", "foofnar", null, true));
            fail("Didn't throw UnknownRelationshipCode");
        }
        catch (UnknownRelationshipCode e)
        {
            //supposed to happen
        }
        
        try
        {
            assertTrue(roi_.areCodesRelated("11.11.0.2", "E0001", "P0001", "hasSubtype", null, true));
        }
        catch (UnknownRelationshipCode e)
        {
            //supposed to happen.
        }
         
        try
        {
            assertTrue(roi_.areCodesRelated("11.11.0.2", "foofnar", "P0001", "hasSubtype", null, true));
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("11.11.0.2", "E0001", "foofnar", "hasSubtype", null, true));
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("foofnar", "E0001", "P0001", "hasSubtype", null, true));
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("11.11.0.2", "E0001", "P0001", "hasSubtype",
                                            new String[]{"foonar"}, true));
            fail("Didn't throw UnknownRelationQualifier");
        }
        catch (UnknownRelationQualifier e)
        {
            //supposed to happen
        }

        //Test qualifier handling

        assertTrue(roi_.areCodesRelated("11.11.0.1", "A0001", "C0001", "hasSubtype",
                new String[]{}, false));
      
        //TODO find examples that don't require manual manipulation
//       
//            assertTrue(roi_.areCodesRelated("11.11.0.1", "A0001", "C0001",
//            "hasSubtype", new String[] {"hasEngine"}, false));
//        
//        	assertFalse(roi_.areCodesRelated("11.11.0.1", "A0001", "T0001",
//            "hasSubtype", new String[] {"hasEngine"}, false));
        
    }
}