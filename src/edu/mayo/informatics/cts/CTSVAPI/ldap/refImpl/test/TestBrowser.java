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

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.UnexpectedError;

import edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.VapiObjectToStrings;

/**
 * Class for doing ad-hoc testing of the LDAP impl.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestBrowser
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
        BrowserOperations boi = BrowserOperationsImpl._interface();
        try
        {
            System.out.println("****************************************");
            System.out.println("HL7 Test");
            System.out.println("****************************************");

            System.out.println("Service Name " + boi.getServiceName());
            System.out.println("------------");
            System.out.println("Service Version " + boi.getServiceVersion());
            System.out.println("------------");
            System.out.println("CTSVersion " + boi.getCTSVersion().getMajor() + "." + boi.getCTSVersion().getMinor());
            System.out.println("------------");
            System.out.println("Service Description " + boi.getServiceDescription());
            System.out.println("------------");

            System.out.println("Supported Match Algorithms "
                    + VapiObjectToStrings.toString(boi.getSupportedMatchAlgorithms(), ""));
            System.out.println("------------");

            System.out.println("Supported Code Systems");
            System.out.println(VapiObjectToStrings.toString(boi.getSupportedCodeSystems(1000, 1000), ""));
            System.out.println("------------");

            System.out.println("lookupConceptCodesByDesignation");
            System.out.println(VapiObjectToStrings.toString(boi
                    .lookupConceptCodesByDesignation("2.16.840.1.113883.5.4", "Intermediate", "StartsWithIgnoreCase",
                                                     "en", true, 1000, 1000), ""));
            System.out.println("------------");

            System.out.println("lookupDesignations");
            ConceptId conceptId = new ConceptId();
            conceptId.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId.setConcept_code("AE");
            System.out.println(VapiObjectToStrings.toString(boi.lookupDesignations(conceptId, "am",
                                                                                   "StartsWithIgnoreCase", "en"), ""));
            System.out.println("------------");

            System.out.println("lookupCompleteCodedConcept");
            ConceptId conceptId2 = new ConceptId();
            conceptId2.setCodeSystem_id("2.16.840.1.113883.5.8");
            conceptId2.setConcept_code("_ActIneligibilityReason");
            System.out.println(VapiObjectToStrings.toString(boi.lookupCompleteCodedConcept(conceptId2), ""));
            System.out.println("-----------");

            System.out.println("lookupCodeExpansion - with code");
            ConceptId conceptId3 = new ConceptId();
            conceptId3.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId3.setConcept_code("_HL7DefinedActCodes");
            System.out.println(VapiObjectToStrings.toString(boi.lookupCodeExpansion(conceptId3, "hasSubtype", true,
                                                                                    true, "en", 0, 1000), ""));
            System.out.println("-----------");

            System.out.println("lookupCodeExpansion - no code forward");
            ConceptId conceptId4 = new ConceptId();
            conceptId4.setCodeSystem_id("2.16.840.1.113883.5.54");
            conceptId4.setConcept_code(null);
            System.out.println(VapiObjectToStrings.toString(boi.lookupCodeExpansion(conceptId4, "hasSubtype", true,
                                                                                    true, "en", 1000, 1000), ""));
            System.out.println("-----------");

            System.out.println("lookupCodeExpansion - no code forward - on unsupported hasSubtype");
            ConceptId conceptId9 = new ConceptId();
            conceptId9.setCodeSystem_id("2.16.840.1.113883.5.1009");
            conceptId9.setConcept_code(null);
            System.out.println(VapiObjectToStrings.toString(boi.lookupCodeExpansion(conceptId9, "hasSubtype", true,
                                                                                    true, "en", 1000, 1000), ""));
            System.out.println("-----------");

            System.out.println("expandCodeExpansionContextForward");
            System.out
                    .println(VapiObjectToStrings
                            .toString(
                                      boi
                                              .expandCodeExpansionContext("<context version=\"3\"><codeSystemName>null</codeSystemName><relationship>null</relationship><forward>true</forward><directDescendantsOnly>true</directDescendantsOnly><ldapName>association=hasSubtype,dc=Relations,codingScheme=ActCode</ldapName><nestingDepth>2</nestingDepth><language>en</language><conceptCode>DIET</conceptCode><defaultTargetCodeSystemId>2.16.840.1.113883.5.4</defaultTargetCodeSystemId><sourceCodeSystemId>2.16.840.1.113883.5.4</sourceCodeSystemId><defaultLanguage>en</defaultLanguage><timeout>1000</timeout><sizeLimit>1000</sizeLimit></context>"
                                                      .getBytes()), ""));

            System.out.println("lookupProperties");
            ConceptId conceptId5 = new ConceptId();
            conceptId5.setCodeSystem_id("2.16.840.1.113883.5.6");
            conceptId5.setConcept_code("SPLY");
            System.out.println(VapiObjectToStrings.toString(boi.lookupProperties(conceptId5, null, null, null, null,
                                                                                 null), ""));

            System.out.println("lookupConceptCodesByProperties");
            System.out.println(VapiObjectToStrings.toString(boi.lookupConceptCodesByProperty("2.16.840.1.113883.5.6",
                                                                                             null, null, null, true,
                                                                                             null, null, 1000, 1000),
                                                            ""));

            System.out.println("lookupCodeReverse - with code");
            ConceptId conceptId7 = new ConceptId();
            conceptId7.setCodeSystem_id("2.16.840.1.113883.5.4");
            conceptId7.setConcept_code("RD");
            System.out.println(VapiObjectToStrings.toString(boi.lookupCodeExpansion(conceptId7, "hasSubtype", false,
                                                                                    false, "en", 1000, 1000), ""));
            System.out.println("-----------");

            System.out.println("expandCodeExpansionContextReverse");
            System.out
                    .println(VapiObjectToStrings
                            .toString(
                                      boi
                                              .expandCodeExpansionContext("<context version=\"3\"><codeSystemName>null</codeSystemName><relationship>null</relationship><forward>false</forward><directDescendantsOnly>true</directDescendantsOnly><ldapName>association=hasSubtype,dc=Relations,codingScheme=ActCode</ldapName><nestingDepth>2</nestingDepth><language>en</language><conceptCode>DIET</conceptCode><defaultTargetCodeSystemId>2.16.840.1.113883.5.4</defaultTargetCodeSystemId><sourceCodeSystemId>2.16.840.1.113883.5.4</sourceCodeSystemId><defaultLanguage>en</defaultLanguage><timeout>1000</timeout><sizeLimit>1000</sizeLimit></context>"
                                                      .getBytes()), ""));

        }
        catch (UnexpectedError e)
        {
            System.out.println(e.getPossible_cause());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println(e.getClass().toString());
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }
}