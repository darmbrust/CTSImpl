package org.hl7.baseTests.VAPI.lucene;

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
 * @version 1.0 - cvs $Revision: 1.3 $ checked in on $Date: 2005/07/08 22:05:50 $
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
//         boi_ = org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl._interface(OperationsHolder.VAPI_LDAP_USERNAME
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