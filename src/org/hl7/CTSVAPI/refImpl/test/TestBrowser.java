package org.hl7.CTSVAPI.refImpl.test;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.VapiObjectToStrings;

/**
 * <pre>
 * Title:        TestBrowser.java
 * Description:  Class for doing ad-hoc testing of the LDAP impl.
 * Copyright: (c) 2005 Mayo Foundation. All rights reserved.
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
 * @version 1.0 - cvs $Revision: 1.2 $ checked in on $Date: 2005/10/14 15:44:09 $
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
