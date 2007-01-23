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
package edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.test;

import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.UnexpectedError;

import edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.RuntimeOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.VapiObjectToStrings;

/**
 * Class for doing ad-hoc testing of the runtime impl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestRuntime
{

    public static void main(String[] args) throws UnexpectedError
    {
        try
        {
            CTSConfigurator._instance().initialize();
        }
        catch (Exception e1)
        {
            System.out.println("Problem loading configuration information.  Failure likely.");
        }
        RuntimeOperations roi = RuntimeOperationsImpl._interface();
        System.out.println("****************************************");
        System.out.println("HL7 Vocab Runtime Test");
        System.out.println("****************************************");

        try
        {
            System.out.println("service name " + roi.getServiceName());
            System.out.println("----------");
            System.out.println("service version " + roi.getServiceVersion());
            System.out.println("----------");
            System.out.println("service description " + roi.getServiceDescription());
            System.out.println("----------");
            System.out.println("cts version:");
            System.out.println(VapiObjectToStrings.toString(roi.getCTSVersion(), ""));
            System.out.println("----------");

            System.out.println("Are codes related");
            System.out.println(VapiObjectToStrings.toString(new Boolean(roi
                    .areCodesRelated("2.16.840.1.113883.5.104", "1004-1",
                                     "1080-1", "hasSubtype", null, true)), ""));
            
//            System.out.println("Are codes related testing qualifier stuff");
//            System.out.println(VapiObjectToStrings.toString(new Boolean(roi_
//                    .areCodesRelated("2.16.840.1.113883.5.4", "_ActCoverageConfirmationCode",
//                                     "AUTH", "hasSubtype", new String[] {"testQualifier", "testQualifier2"}, false)), ""));

            System.out.println("----------");

            System.out.println(VapiObjectToStrings.toString(roi.getSupportedCodeSystems(1000, 1000), ""));

            System.out.println("----------");

            System.out.print("IsConceptIdValid - ");
            ConceptId conceptId = new ConceptId();
            conceptId.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId.setConcept_code("16");

            System.out.println(roi.isConceptIdValid(conceptId, true));

            System.out.println("----------");

            System.out.print("lookupDesignation - ");
            ConceptId conceptId1 = new ConceptId();
            conceptId1.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId1.setConcept_code("12");
            System.out.println(VapiObjectToStrings.toString(roi.lookupDesignation(conceptId1, "en"), ""));

            System.out.println("----------");

            System.out.println("lookupCodeSystemInfo");
            System.out.println(VapiObjectToStrings.toString(roi
                    .lookupCodeSystemInfo("2.16.840.1.113883.5.4", "ActCode"), ""));

            //            //ISO demo
            //            System.out.println("lookupCodeSystemInfo");
            //            System.out.println(VapiObjectToStrings.toString(roi_
            //                    .lookupCodeSystemInfo("localName=2.16.1,ne=2.16.840.1.113883.5,ra=ISO", "ISO639-1"),
            //                                                            ""));

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}