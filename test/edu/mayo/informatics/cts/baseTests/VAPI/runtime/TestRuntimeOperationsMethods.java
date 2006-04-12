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

import org.hl7.CTSVAPI.CodeSystemInfo;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.StringAndLanguage;

/**
 * JUnit test cases for BrowserOperationsImpl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestRuntimeOperationsMethods extends TestCase
{
    private RuntimeOperations roi_;
    
    public TestRuntimeOperationsMethods()
    {
        super();
    }
    public TestRuntimeOperationsMethods(String name, RuntimeOperations roi)
    {
        super(name);    
        roi_ = roi;
    }

    public void testIsConceptIdValid() throws Exception {
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("11.11.0.1");
        conceptId.setConcept_code("A0001");
        
        assertTrue(roi_.isConceptIdValid(conceptId, true));      
    }
    public void testLookupCodeSystemInfo() throws Exception {
        CodeSystemInfo result = roi_.lookupCodeSystemInfo("11.11.0.1", "Automobiles");
        
        result.getCodeSystem();
        result.getCodeSystemDescription();
        assertEquals(result.getFullName(), "test2");
        
//        ArrayOf_xsd_string languages = result.getSupportedLanguages();
//        
//        languages.
//        assertEquals(languages[0], 1);
//        assertEquals(languages[0], "en");
        
//       
//        System.out.println(result.getSupportedMimeTypes());
//        System.out.println(result.getSupportedProperties());
//        System.out.println(result.getSupportedRelationQualifiers());
//        System.out.println(result.getSupportedRelations());
        
    }
    public void testLookupDesignation() throws Exception {
        
        //test where it needs to strip the end of the language, isPreferred is specified
        ConceptId conceptId2 = new ConceptId();
        conceptId2.setCodeSystem_id("11.11.0.1");
        conceptId2.setConcept_code("A0001");
        
        StringAndLanguage strlan = roi_.lookupDesignation(conceptId2, "en-uk" );
        assertEquals(strlan.getLanguage_code(), "en");
        assertEquals("Automobile", strlan.getText());
        
        //test where language is unspecified in the database and the query, and no presentation
        //has isPreferred set on it.
        conceptId2 = new ConceptId();
        conceptId2.setCodeSystem_id("11.11.0.1");
        conceptId2.setConcept_code("Jaguar");
        
        strlan = roi_.lookupDesignation(conceptId2, null);
        assertEquals(strlan.getLanguage_code(), "en");
        assertEquals("Jaguar", strlan.getText());
    }
    
}