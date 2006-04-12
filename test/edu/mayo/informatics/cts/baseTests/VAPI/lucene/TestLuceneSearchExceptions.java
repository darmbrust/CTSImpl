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
package edu.mayo.informatics.cts.baseTests.VAPI.lucene;

import junit.framework.TestCase;

import org.hl7.CTSVAPI.BadlyFormedMatchText;
import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownConceptCode;
import org.hl7.CTSVAPI.UnknownLanguageCode;
import org.hl7.CTSVAPI.UnknownMatchAlgorithm;
import org.hl7.CTSVAPI.UnknownMimeTypeCode;
import org.hl7.CTSVAPI.UnknownPropertyCode;

/**
 * JUnit test cases for BrowserOperationsImpl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestLuceneSearchExceptions extends TestCase
{

    private BrowserOperations boi_;

    public TestLuceneSearchExceptions() throws Exception
    {

        super();

        // Use this code for starting up the test to run it individually...

//         Log4JInit._instance().initialize();
//         String indexLocation = System.getProperty("java.io.tmpdir")
//         + System.getProperty("file.separator")
//         + "CTSLuceneIndexTest";
//         String indexName = "CTS_LEXGRID_TEST_LDAP";
//        
//         CTSConstants.LUCENE_INDEX_LOCATION = new StringParameter(indexLocation);
//         CTSConstants.LUCENE_INDEX_NAME = new StringParameter(indexName);
//         CTSConstants.LUCENE_NORM_SEARCH_ENABLED = new BooleanParameter(false);
//         CTSConstants.LUCENE_SEARCH_ENABLED = new BooleanParameter(true);
//        
//         //now create the BOI that will use the lucene options.
//         boi_ = edu.mayo.informatics.cts.CTSVAPI.refImpl.BrowserOperationsImpl._interface(OperationsHolder.VAPI_LDAP_USERNAME
//                 .getValue(), OperationsHolder.VAPI_LDAP_PASSWORD.getValue(), OperationsHolder.VAPI_LDAP_ADDRESS
//                 .getValue(), OperationsHolder.VAPI_LDAP_SERVICE.getValue(), false);

    }

    public TestLuceneSearchExceptions(String name, BrowserOperations boi)
    {
        super(name);
        boi_ = boi;

    }

    public void testLookupConceptCodesByDesignation() throws Exception
    {
        // test for results
        ConceptId[] results = boi_.lookupConceptCodesByDesignation("11.11.0.1", "c*", "LuceneQuery", "en", true, 0,
                                                                   1000);
        assertNotNull(results);
        assertFalse("Returned 0 codes", results.length == 0);

        // test result limiting ability
        results = boi_.lookupConceptCodesByDesignation("11.11.0.1", "c*", "LuceneQuery", "en", true, 1000, 1);

        assertTrue("Didn't limit code results", results.length <= 1);

        // test UnknownMatchAlgorithm
        try
        {
            boi_.lookupConceptCodesByDesignation("11.11.0.1", "Automobile", "Foofnar", "en", true, 0, 1000);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        // test badlyFormedMatchText
        try
        {
            boi_.lookupConceptCodesByDesignation("11.11.0.1", "*sdf(", "LuceneQuery", "en", true, 0, 1000);
            fail("Didn't throw BadlyFormedMatchText");
        }
        catch (BadlyFormedMatchText e)
        {
            // supposed to happen
        }

        // test unknown code system
        try
        {
            boi_.lookupConceptCodesByDesignation("foofnar", "Intermediate", null, "en", true, 0, 1000);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        // test unknownlanguage code
        try
        {
            boi_.lookupConceptCodesByDesignation("11.11.0.1", "Automobile", "LuceneQuery", "foofnar", true, 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

    }

    public void testLookupConceptCodesByProperty() throws Exception
    {

        // test for results
        ConceptId[] result = boi_.lookupConceptCodesByProperty("11.11.0.1", "c*", "LuceneQuery", "en", true,
                                                               new String[]{"textualPresentation"}, null, 0, 1000);
        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);

        // test for result limiting
        result = boi_.lookupConceptCodesByProperty("11.11.0.1", "c*", "LuceneQuery", "en", true,
                                                   new String[]{"textualPresentation"}, null, 0, 2);

        assertFalse("Didn't limit results properly", result.length > 2);

        // test for unknownMatchAlgorithm
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "Foofnar", "en", true,
                                              new String[]{"textualPresentation"}, new String[]{"text/plain"}, 0, 1000);
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        // test for unknownMimeTypeCode
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "LuceneQuery", "en", true,
                                              new String[]{"textualPresentation"},
                                              new String[]{"text/plain", "foofnar"}, 0, 1000);
            fail("Didn't throw UnknownMimeTypeCode");
        }
        catch (UnknownMimeTypeCode e)
        {
            // supposed to happen
        }

        // test for badlyFormedMatchText
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "substance*(()", "LuceneQuery", "en", true,
                                              new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw BadlyFormedMatchText");
        }
        catch (BadlyFormedMatchText e)
        {
            // supposed to happen
        }

        // test for unknownCodeSystem
        try
        {
            boi_.lookupConceptCodesByProperty("foofnar", "substance", "LuceneQuery", "en", true,
                                              new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        // test for unknownlanguagecode
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "LuceneQuery", "frc", true,
                                              new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

        // test for unknownPropertyCode
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", "Automobile", "LuceneQuery", null, true, new String[]{
                    "textualPresentation", "foofnar"}, null, 0, 1000);
            fail("Didn't throw UnknownPropertyCode");
        }
        catch (UnknownPropertyCode e)
        {
            // supposed to happen
        }

        // test for missing search text
        try
        {
            boi_.lookupConceptCodesByProperty("11.11.0.1", null, "LuceneQuery", null, true,
                                              new String[]{"textualPresentation"}, null, 0, 1000);
            fail("Didn't throw BadlyFormedMatchText");
        }
        catch (BadlyFormedMatchText e)
        {
            // supposed to happen
        }
    }

    public void testLookupDesignations() throws Exception
    {
        // test for results
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("11.11.0.1");
        conceptId.setConcept_code("A0001");

        ConceptDesignation[] results = boi_.lookupDesignations(conceptId, "a*", "LuceneQuery", "en");

        assertNotNull(results);
        assertFalse("Returned 0 codes", results.length == 0);

        // test unknownMatchAlorithm

        try
        {
            boi_.lookupDesignations(conceptId, "a*", "Foofnar", "en");
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        // test unknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("foofnar");
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            // supposed to happen
        }

        // test unknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code(null);
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            // supposed to happen
        }

        // test unknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("");
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            // supposed to happen
        }

        // test badly formed match text
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "?><,.\" ((~~`", "LuceneQuery", "en");
            fail("Didn't throw BadlyFormedMatchText");
        }
        catch (BadlyFormedMatchText e)
        {
            // supposed to happen
        }

        // test unknown code system
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // Supposed to happen
        }
        // test unknown code system
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("*");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // Supposed to happen
        }
        // test unknown code system
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id(null);
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // Supposed to happen
        }
        // test unknown code system
        try
        {
            boi_.lookupDesignations(null, "a*", "LuceneQuery", "en");
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // Supposed to happen
        }

        // test unknownlanguagecode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("A0001");
            boi_.lookupDesignations(conceptId1, "a*", "LuceneQuery", "foofnar");
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

    }

    public void testLookupProperties() throws Exception
    {
        // test for results
        ConceptId conceptId5 = new ConceptId();
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("A0001");

        ConceptProperty[] result = boi_.lookupProperties(conceptId5, new String[]{"textualPresentation", "definition"},
                                                         null, "LuceneQuery", "en", new String[]{});

        assertNotNull(result);
        assertFalse("Returned 0 codes", result.length == 0);

        // test for unknownMatchAlgorithm
        try
        {
            boi_.lookupProperties(conceptId5, null, "a*", "Foofnar", "en", new String[]{});
            fail("Didn't throw UnknownMatchAlgorithm");
        }
        catch (UnknownMatchAlgorithm e)
        {
            // supposed to happen
        }

        // test for UnknownMimeTypeCode
        try
        {
            boi_.lookupProperties(conceptId5, null, null, "LuceneQuery", "en", new String[]{"text/fun"});
            fail("Didn't throw UnknownMimeTypeCode");
        }
        catch (UnknownMimeTypeCode e)
        {
            // supposed to happen
        }

        // test for UnknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("blah");

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }
        
        // test for UnknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code("");

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }
        
        // test for UnknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code(null);

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }

        // test for UnknownConceptCode
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("11.11.0.1");
            conceptId1.setConcept_code(null);

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownConceptCode");
        }

        catch (UnknownConceptCode e)
        {
            // //supposed to happen
        }

        // test for BadlyFormedMatchText
        try
        {
            boi_.lookupProperties(conceptId5, null, "&34f((", "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw BadlyFormedMatchText");
        }

        catch (BadlyFormedMatchText e)
        {
            // supposed to happen
        }

        // test for UnknownCodeSystem
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("foofnar");
            conceptId1.setConcept_code("foofnar");

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        // test for UnknownCodeSystem
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id(null);
            conceptId1.setConcept_code("foofnar");

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        // test for UnknownCodeSystem
        try
        {
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("*");
            conceptId1.setConcept_code("foofnar");

            boi_.lookupProperties(conceptId1, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        // test for UnknownCodeSystem
        try
        {
            boi_.lookupProperties(null, null, null, "LuceneQuery", "en", new String[]{"text/plain"});
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            // supposed to happen
        }

        // test for UnknownLanguageCode
        try
        {

            boi_.lookupProperties(conceptId5, null, null, "LuceneQuery", "foofnar", new String[]{"text/plain"});
            fail("Didn't throw UnknownLanguageCode");
        }
        catch (UnknownLanguageCode e)
        {
            // supposed to happen
        }

        // test for UnknownPropertyCode
        try
        {

            boi_.lookupProperties(conceptId5, new String[]{"textualPresentation", "foonar"}, null, "LuceneQuery", "en",
                                  new String[]{"text/plain"});
            fail("Didn't throw UnknownPropertyCode");
        }
        catch (UnknownPropertyCode e)
        {
            // supposed to happen
        }

    }

}