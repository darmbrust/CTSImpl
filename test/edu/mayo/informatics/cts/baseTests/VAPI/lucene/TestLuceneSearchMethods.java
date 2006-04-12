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

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;

import edu.mayo.informatics.cts.baseTests.VAPI.TestUtility;
import edu.mayo.informatics.cts.utility.CTSConstants;
import edu.mayo.mir.utility.parameter.BooleanParameter;

/**
 * JUnit test cases for BrowserOperationsImpl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestLuceneSearchMethods extends TestCase
{
    private BrowserOperations boi_;

    public TestLuceneSearchMethods() throws Exception
    {
        super();

        // Use this code for starting up the test to run it individually...
        // in combination with a manual prop file location, and custom config in eclipse to run
        // just one test
//        Log4JInit._instance().initialize();
//        String indexLocation = System.getProperty("java.io.tmpdir")
//                + System.getProperty("file.separator")
//                + "CTSLuceneIndexTest";
//        String indexName = "CTS_LEXGRID_TEST_SQL_MYSQL";
//
//        CTSConstants.LUCENE_INDEX_LOCATION = new StringParameter(indexLocation);
//        CTSConstants.LUCENE_INDEX_NAME = new StringParameter(indexName);
//        CTSConstants.LUCENE_NORM_SEARCH_ENABLED = new BooleanParameter(false);
//        CTSConstants.LUCENE_SEARCH_ENABLED = new BooleanParameter(true);
//
//        // now create the BOI that will use the lucene options.
//        boi_ = edu.mayo.informatics.cts.CTSVAPI.refImpl.BrowserOperationsImpl._interface(OperationsHolder.VAPI_LDAP_USERNAME
//                .getValue(), OperationsHolder.VAPI_LDAP_PASSWORD.getValue(), OperationsHolder.VAPI_LDAP_ADDRESS
//                .getValue(), OperationsHolder.VAPI_LDAP_SERVICE.getValue(), false);

    }

    public TestLuceneSearchMethods(String name, BrowserOperations boi)
    {
        super(name);
        boi_ = boi;

    }

    public void testLookupConceptCodesByDesignation() throws Exception
    {
        // test to see if it finds the one with "Auto" in it.
        ConceptId[] concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "auto", "LuceneQuery", "en", true,
                                                                   1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "005");

        // no match text should match all non-anonymous active concepts
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", null, "LuceneQuery", "en", true, 1000, 1000);
        assertEquals(concept.length, 8);

        // result limiting
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", null, "LuceneQuery", "en", true, 1000, 3);
        assertEquals(concept.length, 3);

        // try with activeOnly set to "false" - expect to get a hit on a non-active concept
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "auto", "LuceneQuery", "en", false, 1000, 1000);

        ConceptId[] expectedResult = new ConceptId[2];
        expectedResult[0] = new ConceptId();
        expectedResult[1] = new ConceptId();
        expectedResult[0].setCodeSystem_id("11.11.0.1");
        expectedResult[0].setConcept_code("005");
        expectedResult[1].setCodeSystem_id("11.11.0.1");
        expectedResult[1].setConcept_code("73");

        assertTrue("Result does not match expected result", TestUtility.ConceptIdArraysEqual(concept, expectedResult));

        // try with matching across multiple code systems (CTS breaking behavior, required by evs)
        CTSConstants.EVSModeEnabled = new BooleanParameter(true);
        concept = boi_.lookupConceptCodesByDesignation("*", "t*", "LuceneQuery", null, true, 1000, 1000);

        expectedResult = new ConceptId[2];
        ConceptId id1 = new ConceptId();
        id1.setCodeSystem_id("11.11.0.1");
        id1.setConcept_code("T0001");
        ConceptId id2 = new ConceptId();
        id2.setCodeSystem_id("11.11.0.2");
        id2.setConcept_code("T0001");

        expectedResult[0] = id1;
        expectedResult[1] = id2;

        assertTrue("Result does not match expected result", TestUtility.ConceptIdArraysEqual(concept, expectedResult));

        // go back into proper CTS mode
        CTSConstants.EVSModeEnabled = new BooleanParameter(false);

        // test language setting - specific language should be found
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.2", "engine", "LuceneQuery", "ge", true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.2");
        assertEquals(concept[0].getConcept_code(), "E0001");

        // test language setting - no language specified, should be found
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.2", "engine", "LuceneQuery", null, true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.2");
        assertEquals(concept[0].getConcept_code(), "E0001");

        // test language setting - no language with no match specfied - should not be found
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.2", "engine", "LuceneQuery", "en", true, 1000, 1000);
        assertEquals(concept.length, 0);
        
        //Test the "sounds like" features
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "Domestic", "DoubleMetaphoneLuceneQuery", null, true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "005");
        
        //Test the "sounds like" features
        concept = boi_.lookupConceptCodesByDesignation("11.11.0.1", "Domestik", "DoubleMetaphoneLuceneQuery", null, true, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "005");
    }

    public void testLookupConceptCodesByProperty() throws Exception
    {
        // test to see if it finds the one with "Auto*" in it - no properties specified (so all properties)
        ConceptId[] concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "auto*", "LuceneQuery", "en", true, null,
                                                                null, 1000, 1000);
        ConceptId[] expectedResult = new ConceptId[2];
        ConceptId id1 = new ConceptId();
        id1.setCodeSystem_id("11.11.0.1");
        id1.setConcept_code("005");
        ConceptId id2 = new ConceptId();
        id2.setCodeSystem_id("11.11.0.1");
        id2.setConcept_code("A0001");

        expectedResult[0] = id1;
        expectedResult[1] = id2;

        assertTrue("Result does not match expected result", TestUtility.ConceptIdArraysEqual(concept, expectedResult));

        // test to see if it finds the one with "Auto*" in it - textualPresentation Property specified
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "auto*", "LuceneQuery", "en", true,
                                                    new String[]{"definition"}, null, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");

        // test to see if it finds the one with "Auto*" in it - multiple Properties specified
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "auto*", "LuceneQuery", "en", true, new String[]{
                "textualPresentation", "definition"}, null, 1000, 1000);
        expectedResult = new ConceptId[2];
        id1 = new ConceptId();
        id1.setCodeSystem_id("11.11.0.1");
        id1.setConcept_code("005");
        id2 = new ConceptId();
        id2.setCodeSystem_id("11.11.0.1");
        id2.setConcept_code("A0001");

        expectedResult[0] = id1;
        expectedResult[1] = id2;

        assertTrue("Result does not match expected result", TestUtility.ConceptIdArraysEqual(concept, expectedResult));

        // test to see if it finds the one with "Auto" in it - no properties definition property specified - should not
        // match
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "auto", "LuceneQuery", "en", true,
                                                    new String[]{"definition"}, null, 1000, 1000);
        assertEquals(concept.length, 0);

        // try with activeOnly set to "false" - expect a result that is not active
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "auto", "LuceneQuery", "en", false, null, null, 1000,
                                                    1000);
        expectedResult = new ConceptId[2];
        expectedResult[0] = new ConceptId();
        expectedResult[1] = new ConceptId();
        expectedResult[0].setCodeSystem_id("11.11.0.1");
        expectedResult[0].setConcept_code("005");
        expectedResult[1].setCodeSystem_id("11.11.0.1");
        expectedResult[1].setConcept_code("73");

        assertTrue("Result does not match expected result", TestUtility.ConceptIdArraysEqual(concept, expectedResult));

        // try with matching across multiple code systems (CTS breaking behavior, required by evs)
        CTSConstants.EVSModeEnabled = new BooleanParameter(true);
        concept = boi_.lookupConceptCodesByProperty("*", "t*", "LuceneQuery", null, true, null, null, 1000, 1000);

        expectedResult = new ConceptId[2];
        id1 = new ConceptId();
        id1.setCodeSystem_id("11.11.0.1");
        id1.setConcept_code("T0001");
        id2 = new ConceptId();
        id2.setCodeSystem_id("11.11.0.2");
        id2.setConcept_code("T0001");

        expectedResult[0] = id1;
        expectedResult[1] = id2;

        assertTrue("Result does not match expected result", TestUtility.ConceptIdArraysEqual(concept, expectedResult));

        // go back into proper CTS mode
        CTSConstants.EVSModeEnabled = new BooleanParameter(false);

        // test language setting - specific language should be found
        concept = boi_.lookupConceptCodesByProperty("11.11.0.2", "engine", "LuceneQuery", "ge", true, null, null, 1000,
                                                    1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.2");
        assertEquals(concept[0].getConcept_code(), "E0001");

        // test language setting - no language specified, should be found
        concept = boi_.lookupConceptCodesByProperty("11.11.0.2", "engine", "LuceneQuery", null, true, null, null, 1000,
                                                    1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.2");
        assertEquals(concept[0].getConcept_code(), "E0001");

        // test language setting - no language with no match specfied - should not be found
        concept = boi_.lookupConceptCodesByProperty("11.11.0.2", "engine", "LuceneQuery", "en", true, null, null, 1000,
                                                    1000);
        assertEquals(concept.length, 0);

        // test to see if it finds the one with "Auto*" in it - where a mime type is specified
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "auto*", "LuceneQuery", "en", true, new String[]{},
                                                    new String[]{"text/plain"}, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "A0001");

        // test to see if it finds the one with "American" in it - where a mime type is specified
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "American", "LuceneQuery", "en", true, new String[]{},
                                                    new String[]{"text/plain"}, 1000, 1000);
        assertEquals(concept.length, 0);

        // test to see if it finds the one with "American" in it - where a mime type is not specified
        concept = boi_.lookupConceptCodesByProperty("11.11.0.1", "American", "LuceneQuery", "en", true, new String[]{},
                                                    new String[]{}, 1000, 1000);
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getCodeSystem_id(), "11.11.0.1");
        assertEquals(concept[0].getConcept_code(), "005");

    }

    public void testLookupDesignations() throws Exception
    {
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("11.11.0.1");
        conceptId3.setConcept_code("005");

        // test for proper results - specifying filter text
        ConceptDesignation[] concept = boi_.lookupDesignations(conceptId3, "Domestic", "LuceneQuery", "en");
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getDesignation(), "Domestic Auto Makers");
        assertEquals(concept[0].getLanguage_code(), "en");
        assertTrue(concept[0].isPreferredForLanguage());

        // test for proper results - no filter text
        concept = boi_.lookupDesignations(conceptId3, null, "LuceneQuery", "en");

        ConceptDesignation[] expectedResult = new ConceptDesignation[2];
        ConceptDesignation cd1 = new ConceptDesignation();
        ConceptDesignation cd2 = new ConceptDesignation();
        cd1.setDesignation("American Car Companies");
        cd1.setLanguage_code("en");
        cd2.setDesignation("Domestic Auto Makers");
        cd2.setLanguage_code("en");

        expectedResult[0] = cd1;
        expectedResult[1] = cd2;

        assertTrue("Result does not match expected result", TestUtility.ConceptDesignationArraysEqual(concept,
                                                                                                      expectedResult));

        // test for proper results - filter text that should match both
        concept = boi_.lookupDesignations(conceptId3, "a*", "LuceneQuery", "en");
        assertTrue("Result does not match expected result", TestUtility.ConceptDesignationArraysEqual(concept,
                                                                                                      expectedResult));

        conceptId3.setCodeSystem_id("11.11.0.2");
        conceptId3.setConcept_code("E0001");

        // test language setting - specific language should be found
        concept = boi_.lookupDesignations(conceptId3, "engine", "LuceneQuery", "ge");
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getDesignation(), "Engine");
        assertEquals(concept[0].getLanguage_code(), "ge");

        // test language setting - no language specified, should be found
        concept = boi_.lookupDesignations(conceptId3, "engine", "LuceneQuery", "");
        assertEquals(concept.length, 1);
        assertEquals(concept[0].getDesignation(), "Engine");
        assertEquals(concept[0].getLanguage_code(), "ge");

        // test language setting - no language with no match specfied - should not be found
        concept = boi_.lookupDesignations(conceptId3, "engine", "LuceneQuery", "en");
        assertEquals(concept.length, 0);

    }

    public void testLookupProperties() throws Exception
    {
        // check for results with match text
        ConceptId conceptId5 = new ConceptId();
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("A0001");

        ConceptProperty[] result = boi_.lookupProperties(conceptId5, new String[]{}, "Auto*", "LuceneQuery", "en",
                                                         new String[]{});
        ConceptProperty[] expectedResult = new ConceptProperty[2];

        expectedResult[0] = new ConceptProperty();
        expectedResult[1] = new ConceptProperty();
        expectedResult[0].setLanguage_code("en");
        expectedResult[0].setMimeType_code("text/plain");
        expectedResult[0].setProperty_code("definition");
        expectedResult[0].setPropertyValue("An automobile");

        expectedResult[1].setLanguage_code("en");
        expectedResult[1].setMimeType_code("text/plain");
        expectedResult[1].setProperty_code("textualPresentation");
        expectedResult[1].setPropertyValue("Automobile");

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // check for same results - no match text
        result = boi_.lookupProperties(conceptId5, new String[]{}, null, "LuceneQuery", "en", new String[]{});

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // check for same results - match text that only matches one
        result = boi_.lookupProperties(conceptId5, new String[]{}, "an", "LuceneQuery", "en", new String[]{});

        expectedResult = new ConceptProperty[1];

        expectedResult[0] = new ConceptProperty();
        expectedResult[0].setLanguage_code("en");
        expectedResult[0].setMimeType_code("text/plain");
        expectedResult[0].setProperty_code("definition");
        expectedResult[0].setPropertyValue("An automobile");

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // test language setting - specific language should be found
        conceptId5.setCodeSystem_id("11.11.0.2");
        conceptId5.setConcept_code("E0001");
        result = boi_.lookupProperties(conceptId5, null, "engine", "LuceneQuery", "ge", null);

        expectedResult = new ConceptProperty[1];

        expectedResult[0] = new ConceptProperty();
        expectedResult[0].setLanguage_code("ge");
        expectedResult[0].setMimeType_code(null);
        expectedResult[0].setProperty_code("textualPresentation");
        expectedResult[0].setPropertyValue("Engine");

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));
        // test language setting - no language specified, should be found
        result = boi_.lookupProperties(conceptId5, null, "engine", "LuceneQuery", "", null);

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // test language setting - no language with no match specfied - should not be found
        result = boi_.lookupProperties(conceptId5, null, "engine", "LuceneQuery", "en", null);
        assertEquals(result.length, 0);

        // Specify a property type
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("A0001");

        result = boi_.lookupProperties(conceptId5, new String[]{"definition"}, "Auto*", "LuceneQuery", "en",
                                       new String[]{});
        expectedResult = new ConceptProperty[1];

        expectedResult[0] = new ConceptProperty();
        expectedResult[0].setLanguage_code("en");
        expectedResult[0].setMimeType_code("text/plain");
        expectedResult[0].setProperty_code("definition");
        expectedResult[0].setPropertyValue("An automobile");

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // Specify a multiple property types
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("A0001");

        result = boi_.lookupProperties(conceptId5, new String[]{"definition", "textualPresentation"}, "Auto*",
                                       "LuceneQuery", "en", new String[]{});
        expectedResult = new ConceptProperty[2];

        expectedResult[0] = new ConceptProperty();
        expectedResult[1] = new ConceptProperty();
        expectedResult[0].setLanguage_code("en");
        expectedResult[0].setMimeType_code("text/plain");
        expectedResult[0].setProperty_code("definition");
        expectedResult[0].setPropertyValue("An automobile");

        expectedResult[1].setLanguage_code("en");
        expectedResult[1].setMimeType_code("text/plain");
        expectedResult[1].setProperty_code("textualPresentation");
        expectedResult[1].setPropertyValue("Automobile");

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // Specify a a mimeType, where it will match
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("A0001");

        result = boi_.lookupProperties(conceptId5, new String[]{"definition", "textualPresentation"}, "Auto*",
                                       "LuceneQuery", "en", new String[]{"text/plain"});
        expectedResult = new ConceptProperty[2];

        expectedResult[0] = new ConceptProperty();
        expectedResult[1] = new ConceptProperty();
        expectedResult[0].setLanguage_code("en");
        expectedResult[0].setMimeType_code("text/plain");
        expectedResult[0].setProperty_code("definition");
        expectedResult[0].setPropertyValue("An automobile");

        expectedResult[1].setLanguage_code("en");
        expectedResult[1].setMimeType_code("text/plain");
        expectedResult[1].setProperty_code("textualPresentation");
        expectedResult[1].setPropertyValue("Automobile");

        assertTrue("results did not match expected results", TestUtility.ConceptPropertyArraysEqual(result,
                                                                                                    expectedResult));

        // Specify a a mimeType, where it won't match
        conceptId5.setCodeSystem_id("11.11.0.1");
        conceptId5.setConcept_code("005");

        result = boi_.lookupProperties(conceptId5, new String[]{"definition", "textualPresentation"}, "Domestic",
                                       "LuceneQuery", "en", new String[]{"text/plain"});

        assertTrue("wrong number of results", result.length == 0);

    }

}