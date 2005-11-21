package org.hl7.CTSVAPI.refImpl;

import junit.framework.*;

import org.hl7.CTSVAPI.*;

/**
 * <pre>
 * Title:        TestRuntimeOperations.java
 * Description:  JUnit test cases for RuntimeOperationsImpl.
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
 * @version 1.0 - cvs $Revision: 1.3 $ checked in on $Date: 2005/07/13 23:07:14 $
 */

public class TestRuntimeOperations extends TestCase
{
    private RuntimeOperations roi_;
    
    public TestRuntimeOperations()
    {
        super();
    }

    public TestRuntimeOperations(RuntimeOperations roi)
    {
        super();
        roi_ = roi;
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TestRuntimeOperations.class);
    }

    protected void setUp() throws Exception
    {

        roi_ = OldOperationsHolder._interface().getRuntimeLDAPOperations();
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
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
        CodeSystemInfo result = roi_.lookupCodeSystemInfo("2.16.840.1.113883.5.4", "ActCode");
        assertNotNull(result);

        result = roi_.lookupCodeSystemInfo(null, "ActCode");
        assertNotNull(result);

        result = roi_.lookupCodeSystemInfo("2.16.840.1.113883.5.4", null);
        assertNotNull(result);

        try
        {
            roi_.lookupCodeSystemInfo("2.16.840.1.113883.5.4", "ActClass");
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
        assertTrue("Did not limit to right count", results.length == 5);

        results = roi_.getSupportedCodeSystems(2000, 1000);

    }

    public void testIsConceptIdValid() throws Exception
    {
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId.setConcept_code("16");

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
        ConceptId conceptId1 = new ConceptId();
        conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId1.setConcept_code("12");
        StringAndLanguage result = roi_.lookupDesignation(conceptId1, "en-UK");
        assertNotNull(result);
        assertNotNull(result.getLanguage_code());
        assertNotNull(result.getText());

        try
        {
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("foofnar");
            conceptId2.setConcept_code("12");
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
            conceptId2.setCodeSystem_id("2.16.840.1.113883.5.4");
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
            conceptId2.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId2.setConcept_code("12");
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
            conceptId2.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId2.setConcept_code("12");
            roi_.lookupDesignation(conceptId2, "ge");
            fail("Didn't throw NoApplicableDesignationFound");
        }
        catch (NoApplicableDesignationFound e)
        {
            //supposed to happen
        }
    }

    public void testAreCodesRelated() throws Exception
    {
        assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.104", "1004-1", "1080-1", "hasSubtype", null, true));

        try
        {
            assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.104", "1004-1", "1080-1", "foofnar", null, true));
            fail("Didn't throw UnknownRelationshipCode");
        }
        catch (UnknownRelationshipCode e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.104", "foofnar", "1080-1", "hasSubtype", null, true));
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.104", "1004-1", "foofnar", "hasSubtype", null, true));
            fail("Didn't throw UnknownConceptCode");
        }
        catch (UnknownConceptCode e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("foofnar", "1004-1", "1080-1", "hasSubtype", null, true));
            fail("Didn't throw UnknownCodeSystem");
        }
        catch (UnknownCodeSystem e)
        {
            //supposed to happen
        }

        try
        {
            assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.4", "DIET", "PAR", "hasSubtype",
                                            new String[]{"foonar"}, true));
            fail("Didn't throw UnknownRelationQualifier");
        }
        catch (UnknownRelationQualifier e)
        {
            //supposed to happen
        }

        //Test qualifier handling

        assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.4", "_ActCoverageConfirmationCode", "AUTH", "hasSubtype",
                                        new String[]{}, false));
        //TODO find examples that don't require manual manipulation
        //        assertTrue(roi_.areCodesRelated("2.16.840.1.113883.5.4", "_ActCoverageConfirmationCode", "AUTH",
        // "hasSubtype", new String[] {"testQualifier", "testQualifier2"}, false));
        //        assertFalse(roi_.areCodesRelated("2.16.840.1.113883.5.4", "_ActCoverageConfirmationCode", "AUTH",
        // "hasSubtype", new String[] {"testQualifier", "testQualifier2" , "testQualifier3"}, false));

    }
}