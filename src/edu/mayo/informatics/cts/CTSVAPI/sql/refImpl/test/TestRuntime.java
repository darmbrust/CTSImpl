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
package edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.test;


import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.RuntimeOperations;

import edu.mayo.informatics.cts.CTSVAPI.*;
import edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.*;
import edu.mayo.informatics.cts.utility.*;

/**
 * Class for doing simple tests on the sql vapi runtime backend.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
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