package org.hl7.baseTests.VAPI.runtime;

import junit.framework.TestCase;

import org.hl7.CTSVAPI.CodeSystemInfo;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.StringAndLanguage;

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
 * @version 1.0 - cvs $Revision: 1.4 $ checked in on $Date: 2005/09/28 19:25:27 $
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