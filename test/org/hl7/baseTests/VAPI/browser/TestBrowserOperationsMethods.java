package org.hl7.baseTests.VAPI.browser;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.CompleteCodedConceptDescription;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.ConceptRelationship;
import org.hl7.CTSVAPI.RelatedCode;
import org.hl7.baseTests.VAPI.TestUtility;
import org.hl7.utility.CTSConstants;

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
 * @version 1.0 - cvs $Revision: 1.10 $ checked in on $Date: 2005/10/11 14:32:07 $
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
        if ((boi_ instanceof org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl || boi_ instanceof org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl) && CTSConstants.LUCENE_SEARCH_ENABLED.getValue())
        {
            temp.add("LuceneQuery");
        }
        if ((boi_ instanceof org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl || boi_ instanceof org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl) && CTSConstants.LUCENE_NORM_SEARCH_ENABLED.getValue())
        {
            temp.add("NormalizedLuceneQuery");
        }
        if ((boi_ instanceof org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl || boi_ instanceof org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl) && CTSConstants.LUCENE_DOUBLE_METAPHONE_SEARCH_ENABLED.getValue())
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