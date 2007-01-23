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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.CompleteCodedConceptDescription;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.ConceptRelationship;
import org.hl7.CTSVAPI.RelatedCode;

import edu.mayo.informatics.cts.baseTests.VAPI.TestUtility;
import edu.mayo.informatics.cts.utility.CTSConstants;

/**
 * JUnit test cases for BrowserOperationsImpl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestBrowserOperationsMethods extends TestCase
{
    private BrowserOperations boi_;
    
    public TestBrowserOperationsMethods()
    {
        super();
    }
    public TestBrowserOperationsMethods(String name, BrowserOperations boi)
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

    public void testGetSupportedCodeSystems() throws Exception
    {
        ArrayList temp = new ArrayList();
        temp.add("IdenticalIgnoreCase");
        temp.add("StartsWithIgnoreCase");
        temp.add("EndsWithIgnoreCase");
        temp.add("ContainsPhraseIgnoreCase");
        
        //currently, only the sql and ldap implementations support the Lucene search stuff.
        if ((boi_ instanceof edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl || boi_ instanceof edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl) && CTSConstants.LUCENE_SEARCH_ENABLED.getValue())
        {
            temp.add("LuceneQuery");
        }
        if ((boi_ instanceof edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl || boi_ instanceof edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl) && CTSConstants.LUCENE_STEMMED_SEARCH_ENABLED.getValue())
        {
            temp.add("StemmedLuceneQuery");
        }
        if ((boi_ instanceof edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl || boi_ instanceof edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl) && CTSConstants.LUCENE_DOUBLE_METAPHONE_SEARCH_ENABLED.getValue())
        {
            temp.add("DoubleMetaphoneLuceneQuery");
        }
      
        
        String[] supportedMatchAlgorithms = (String[])temp.toArray(new String[temp.size()]);
        String[] foundMatchAlgorithms = boi_.getSupportedMatchAlgorithms();
        
        assertTrue(TestUtility.stringArraysEqual(supportedMatchAlgorithms, foundMatchAlgorithms));    
    }
    public void testLookupCodeExpansion() throws Exception
    {
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("11.11.0.1");
        conceptId3.setConcept_code("A0001");
        
        RelatedCode[] result = boi_.lookupCodeExpansion(conceptId3, "hasSubtype", true, false, "en", 0, 1000);
        
        
        
        assertEquals(result.length, 2);
        
        boolean found = true;
        for (int i = 0; i < result.length; i++)
        {
            RelatedCode code = result[i];
            if (code.getConcept_code().equals("C0001") && code.getDesignation().equals("Car"))
            {
                found = true;
                break;
            }
        }
        assertTrue(found);
        
        found = true;
        for (int i = 0; i < result.length; i++)
        {
            RelatedCode code = result[i];
            if (code.getConcept_code().equals("T0001") && code.getDesignation().equals("Truck"))
            {
                found = true;
                break;
            }
        }
        
        assertTrue(found);
    }
    
    public void testLookupCompleteCodedConcept() throws Exception
    {
           
        ConceptId conceptId2 = new ConceptId();
        conceptId2.setCodeSystem_id("11.11.0.1");
        conceptId2.setConcept_code("A0001");
        CompleteCodedConceptDescription result = boi_.lookupCompleteCodedConcept(conceptId2);
        ConceptId id = result.getConcept_id();
        assertEquals("A0001", id.getConcept_code());
   
        
        ConceptRelationship[] acr = result.getSourceFor();

        //check the returned array - should be size 5, and contain each of the following items:
        assertEquals(5, acr.length);
        
        ConceptRelationship temp = new ConceptRelationship();
        temp.setRelationship_code("hasSubtype");
        temp.setSourceConcept_id(conceptId2);
        ConceptId expected = new ConceptId();
        expected.setCodeSystem_id("11.11.0.1");
        expected.setConcept_code("C0001");
        temp.setTargetConcept_id(expected);
        
        assertTrue("expected result not present", TestUtility.conceptIdInArray(acr, temp));
        
        expected.setConcept_code("T0001");
        temp.setTargetConcept_id(expected);
        assertTrue("expected result not present", TestUtility.conceptIdInArray(acr, temp));
        
        temp.setRelationship_code("uses");
        expected.setCodeSystem_id("11.11.0.50");
        expected.setConcept_code("Batteries");
        temp.setTargetConcept_id(expected);
        assertTrue("expected result not present", TestUtility.conceptIdInArray(acr, temp));
        
        expected.setConcept_code("Brakes");
        temp.setTargetConcept_id(expected);
        assertTrue("expected result not present", TestUtility.conceptIdInArray(acr, temp));
        
        expected.setConcept_code("Tires");
        temp.setTargetConcept_id(expected);
        assertTrue("expected result not present", TestUtility.conceptIdInArray(acr, temp));
        
    }
    
    public void testLookupConceptCodesByDesignation() throws Exception {
        ConceptId[] concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "ca", "StartsWithIgnoreCase", "en", true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "C0001");
        
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "car", "IdenticalIgnoreCase", "en", true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "C0001");
        
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "uar", "EndsWithIgnoreCase", "en", true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "Jaguar");
        
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "tomob", "ContainsPhraseIgnoreCase", "en", true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");
    }
    public void testLookupConceptCodesByProperty() throws Exception {
        ConceptId[] concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "an", "StartsWithIgnoreCase", "en", true, new String[] {"definition"}, null, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");  
        
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "an automobile", "IdenticalIgnoreCase", "en", true, new String[] {"definition"}, null, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");
        
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "obile", "EndsWithIgnoreCase", "en", true, new String[] {"definition"}, null, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");
        
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "n automob", "ContainsPhraseIgnoreCase", "en", true, new String[] {"definition"}, null, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");
    }
    public void testLookupDesignations() throws Exception {
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("11.11.0.1");
        conceptId3.setConcept_code("A0001");
        
        ConceptDesignation[] concept = boi_.lookupDesignations(conceptId3, "A", "StartsWithIgnoreCase", "en");
        assertEquals(concept.length, 1); 
        assertEquals(concept[0].getDesignation(), "Automobile");
        assertEquals(concept[0].getLanguage_code(), "en");
        assertTrue(concept[0].isPreferredForLanguage());  
        
        concept = boi_.lookupDesignations(conceptId3, "ile", "EndsWithIgnoreCase", "en");
        assertEquals(concept.length, 1);  
        assertEquals(concept[0].getDesignation(), "Automobile");
        assertEquals(concept[0].getLanguage_code(), "en");
        assertTrue(concept[0].isPreferredForLanguage());  
        
        concept = boi_.lookupDesignations(conceptId3, "mobil", "ContainsPhraseIgnoreCase", "en");
        assertEquals(concept.length, 1);  
        assertEquals(concept[0].getDesignation(), "Automobile");
        assertEquals(concept[0].getLanguage_code(), "en");
        assertTrue(concept[0].isPreferredForLanguage());  
        
        concept = boi_.lookupDesignations(conceptId3, "automobile", "IdenticalIgnoreCase", "en");
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getDesignation(), "Automobile");
        assertEquals(concept[0].getLanguage_code(), "en");
        assertTrue(concept[0].isPreferredForLanguage());  
           
    }
    public void testLookupProperties() throws Exception {
        ConceptId conceptId5 = new ConceptId();
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("C0001");

        ConceptProperty[] result = boi_.lookupProperties(conceptId5, new String[]{"textualPresentation"}, null, "",  "en", new String[]{});
        assertEquals(result.length, 1);  
        
        assertEquals(result[0].getProperty_code(), "textualPresentation");
        assertEquals(result[0].getPropertyValue(), "Car");
        assertEquals(result[0].getLanguage_code(), "en");
        assertTrue(result[0].getMimeType_code() == null || result[0].getMimeType_code().length() == 0);
    
    }
}