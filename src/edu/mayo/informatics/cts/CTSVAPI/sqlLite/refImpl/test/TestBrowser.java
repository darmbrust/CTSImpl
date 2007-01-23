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
package edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.test;


import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptId;

import edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.VapiObjectToStrings;

/**
 * Class for doing simple tests on the sql vapi runtime backend.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestBrowser
{
    BrowserOperations boi_;

    public TestBrowser() throws Exception
    {
        try
        {
            CTSConfigurator._instance().initialize();
        }
        catch (Exception e1)
        {
            System.out.println("Problem loading configuration information.  Failure likely.");
        }
        boi_ = BrowserOperationsImpl._interface();
    }

    public static void main(String[] args) throws Exception
    {
        TestBrowser runtime = new TestBrowser();
        runtime.testRuntime();
    }

    public void testRuntime() throws Exception
    {
        System.out.println("service name " + boi_.getServiceName());
        System.out.println("----------");
        System.out.println("service version " + boi_.getServiceVersion());
        System.out.println("----------");
        System.out.println("service description " + boi_.getServiceDescription());
        System.out.println("----------");
        System.out.println("cts version:");
        System.out.println(VapiObjectToStrings.toString(boi_.getCTSVersion(), ""));
        System.out.println("----------");

        System.out.println(VapiObjectToStrings.toString(boi_.getSupportedCodeSystems(0, 0), ""));

        System.out.println("----------");
        
        System.out.println("lookupConceptCodesByDesignation");
        System.out.println(VapiObjectToStrings.toString(boi_
                .lookupConceptCodesByDesignation("2.16.840.1.113883.5.4", "Intermediate", "StartsWithIgnoreCase",
                                                 "en", true, 1000, 1000), ""));
        System.out.println("------------");
        
        System.out.println("lookupConceptCodesByProperties");
        System.out.println(VapiObjectToStrings.toString(boi_.lookupConceptCodesByProperty("2.16.840.1.113883.5.6",
                                                                                         null, null, null, true,
                                                                                         null, null, 1000, 1000),
                                                        ""));
        System.out.println("------------");
        
        System.out.println("lookupDesignations");
        ConceptId conceptId = new ConceptId();
        conceptId.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId.setConcept_code("AE");
        System.out.println(VapiObjectToStrings.toString(boi_.lookupDesignations(conceptId, "Am",
                                                                               "StartsWithIgnoreCase", "en"), ""));
        System.out.println("------------");
        
        System.out.println("lookupProperties");
        ConceptId conceptId5 = new ConceptId();
        conceptId5.setCodeSystem_id("2.16.840.1.113883.5.6");
        conceptId5.setConcept_code("SPLY");
        System.out.println(VapiObjectToStrings.toString(boi_.lookupProperties(conceptId5, null, null, null, null,
                                                                             null), ""));
        System.out.println("lookupCompleteCodedConcept");
        ConceptId conceptId2 = new ConceptId();
        conceptId2.setCodeSystem_id("2.16.840.1.113883.5.8");
        conceptId2.setConcept_code("_ActIneligibilityReason");
        System.out.println(VapiObjectToStrings.toString(boi_.lookupCompleteCodedConcept(conceptId2), ""));
        System.out.println("-----------");
        
        System.out.println("lookupCodeExpansion - with code");
        ConceptId conceptId3 = new ConceptId();
        conceptId3.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId3.setConcept_code("_HL7DefinedActCodes");
        System.out.println(VapiObjectToStrings.toString(boi_.lookupCodeExpansion(conceptId3, "hasSubtype", true,
                                                                                true, "en", 0, 1000), ""));
        System.out.println("-----------");

        System.out.println("lookupCodeExpansion - no code forward");
        ConceptId conceptId4 = new ConceptId();
        conceptId4.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId4.setConcept_code(null);
        System.out.println(VapiObjectToStrings.toString(boi_.lookupCodeExpansion(conceptId4, "hasSubtype", true,
                                                                                true, "en", 1000, 1000), ""));
        System.out.println("-----------");

        System.out.println("lookupCodeExpansion - no code forward - on unsupported hasSubtype");
        ConceptId conceptId9 = new ConceptId();
        conceptId9.setCodeSystem_id("2.16.840.1.113883.5.1009");
        conceptId9.setConcept_code(null);
        System.out.println(VapiObjectToStrings.toString(boi_.lookupCodeExpansion(conceptId9, "hasSubtype", true,
                                                                                true, "en", 1000, 1000), ""));
        System.out.println("-----------");

        System.out.println("expandCodeExpansionContextForward");
        System.out
                .println(VapiObjectToStrings
                        .toString(
                                  boi_
                                          .expandCodeExpansionContext("<context version=\"3\"><expansionContextVersion>2</expansionContextVersion><forward>true</forward><directDescendantsOnly>true</directDescendantsOnly><codeSystemName>ActCode</codeSystemName><ldapName>null</ldapName><nestingDepth>2</nestingDepth><language>en</language><conceptCode>DIET</conceptCode><defaultTargetCodeSystemId>2.16.840.1.113883.5.4</defaultTargetCodeSystemId><relationship>hasSubtype</relationship><defaultLanguage>en</defaultLanguage><timeout>1000</timeout><sizeLimit>1000</sizeLimit></context>"
                                                  .getBytes()), ""));
        
        System.out.println("lookupCodeReverse - with code");
        ConceptId conceptId7 = new ConceptId();
        conceptId7.setCodeSystem_id("2.16.840.1.113883.5.4");
        conceptId7.setConcept_code("RD");
        System.out.println(VapiObjectToStrings.toString(boi_.lookupCodeExpansion(conceptId7, "hasSubtype", false,
                                                                                false, "en", 1000, 1000), ""));
        System.out.println("-----------");

        System.out.println("expandCodeExpansionContextReverse");
        System.out
                .println(VapiObjectToStrings
                        .toString(
                                  boi_
                                          .expandCodeExpansionContext("<context version=\"3\"><expansionContextVersion>2</expansionContextVersion><codeSystemName>ActCode</codeSystemName><forward>false</forward><directDescendantsOnly>true</directDescendantsOnly><ldapName>association=hasSubtype,dc=Relations,codingScheme=ActCode</ldapName><nestingDepth>2</nestingDepth><language>en</language><conceptCode>DIET</conceptCode><relationship>hasSubtype</relationship><defaultTargetCodeSystemId>2.16.840.1.113883.5.4</defaultTargetCodeSystemId><defaultLanguage>en</defaultLanguage><timeout>1000</timeout><sizeLimit>1000</sizeLimit></context>"
                                                  .getBytes()), ""));


    }
    
    

}