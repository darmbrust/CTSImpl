package org.hl7.CTSVAPI.sql.refImpl.test;

import org.hl7.CTSVAPI.*;
import org.hl7.CTSVAPI.sql.refImpl.*;

import edu.mayo.informatics.cts.utility.*;

/**
 * <pre>
 * Title:        TestRuntime.java
 * Description:  Class for doing simple tests on the sql vapi runtime backend.
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
 * @version 1.0 - cvs $Revision: 1.9 $ checked in on $Date: 2005/10/14 15:44:06 $
 */
public class TestRuntime
{
    RuntimeOperations roi_;

    public TestRuntime() throws Exception
    {
        try
        {
            CTSConfigurator._instance().initialize();
        }
        catch (Exception e1)
        {
            System.out.println("Problem loading configuration information.  Failure likely.");
        }
        roi_ = RuntimeOperationsImpl._interface();
    }

    public static void main(String[] args) throws Exception
    {
        TestRuntime runtime = new TestRuntime();
        runtime.testRuntime();
    }

    public void testRuntime() throws Exception
    {
        System.out.println("service name " + roi_.getServiceName());
        System.out.println("----------");
        System.out.println("service version " + roi_.getServiceVersion());
        System.out.println("----------");
        System.out.println("service description " + roi_.getServiceDescription());
        System.out.println("----------");
        System.out.println("cts version:");
        System.out.println(VapiObjectToStrings.toString(roi_.getCTSVersion(), ""));
        System.out.println("----------");

        System.out.println("Are codes related");
        System.out.println(VapiObjectToStrings
                .toString(new Boolean(roi_.areCodesRelated("2.16.840.1.113883.5.104", "1004-1", "1080-1", "hasSubtype",
                                                           null, true)), ""));

        System.out.println("----------");

        System.out.println(VapiObjectToStrings.toString(roi_.getSupportedCodeSystems(0, 0), ""));

        System.out.println("----------");

        System.out.print("IsConceptIdValid - ");
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId.setConcept_code("16");

        System.out.println(roi_.isConceptIdValid(conceptId, true));

        System.out.println("----------");

        System.out.print("lookupDesignation - ");
        ConceptId conceptId1 = new ConceptId();
        conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId1.setConcept_code("12");
        System.out.println(VapiObjectToStrings.toString(roi_.lookupDesignation(conceptId1, "en-fr"), ""));

        System.out.println("----------");

        System.out.println("lookupCodeSystemInfo");
        System.out.println(VapiObjectToStrings.toString(roi_.lookupCodeSystemInfo("2.16.840.1.113883.5.4", "ActCode"),
                                                        ""));
    }

}