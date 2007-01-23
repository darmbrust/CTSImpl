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
package edu.mayo.informatics.cts.CTSMAPI.refImpl.test;

import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.CodeSystemDescriptor;
import org.hl7.CTSMAPI.CodeSystemInfo;
import org.hl7.CTSMAPI.ConceptId;
import org.hl7.CTSMAPI.RIMCodedAttribute;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.ValueSetDescriptor;
import org.hl7.CTSMAPI.VocabularyDomainDescription;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.CTSMAPI.refImpl.BrowserOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Constructors;
import edu.mayo.informatics.cts.utility.MapiObjectToStrings;

/**
 * Class for doing ad-hoc testing of the LDAP Browser implementation.
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
            System.out.println("HL7 Test browser");
            System.out.println("****************************************");

            System.out.println("Service Name: " + boi.getServiceName().getV());
            System.out.println("-------");
            System.out.println("Service Version: " + boi.getServiceVersion().getV());
            System.out.println("-------");
            System.out.println("Service HL7Release Version: " + boi.getHL7ReleaseVersion().getV());
            System.out.println("-------");
            
            System.out.println("Service Description: " + boi.getServiceDescription().getV());
            System.out.println("-------");
            System.out.println("CTSVersionId: " + boi.getCTSVersion().getMajor().getV() + "."
                    + boi.getCTSVersion().getMinor().getV());
            System.out.println("-------");
            System.out.println("Supported Match Algorithms:");
            System.out.println(MapiObjectToStrings.toString(boi.getSupportedMatchAlgorithms(), "  "));

            System.out.println("-------");
            System.out.println("getSupportedAttributes:");
            RIMCodedAttribute[] foo = boi.getSupportedAttributes(Constructors.stm("pay"), Constructors
                    .stm("StartsWithIgnoreCase"), 0, 500);
            System.out.println(MapiObjectToStrings.toString(foo));

            System.out.println("-------");
            System.out.println("supportedVocabularyDomains:");
            ST[] vocabDomains = boi.getSupportedVocabularyDomains(Constructors.stm("act"), Constructors
                    .stm("StartsWithIgnoreCase"), 0, 5);
            System.out.println(MapiObjectToStrings.toString(vocabDomains));

            System.out.println("-------");
            System.out.println("Supported Code Systems");
            CodeSystemDescriptor[] codeSystems = boi.getSupportedCodeSystems(Constructors.stm("A"), Constructors
                    .stm("StartsWithIgnoreCase"), 0, 5);
            System.out.println(MapiObjectToStrings.toString(codeSystems, "  "));

            System.out.println("-------");
            System.out.println("lookupVocabularyDomain");
            VocabularyDomainDescription vocabDomain = boi.lookupVocabularyDomain(Constructors
                    .stm("AdministrativeGender"));
            System.out.println(MapiObjectToStrings.toString(vocabDomain, "  "));

            System.out.println("-------");
            System.out.println("lookupCodeSystem");
            CodeSystemInfo codeSystem = boi.lookupCodeSystem(Constructors.uidm("2.16.840.1.113883.5.1007"), Constructors
                    .stm("DataType"));
            System.out.println(MapiObjectToStrings.toString(codeSystem, "  "));

            System.out.println("-------");
            System.out.println("supportedValueSets");
            ValueSetDescriptor[] valueSetIdentifiers = boi.getSupportedValueSets(Constructors.stm("Person"),
                                                                                 Constructors
                                                                                         .stm("StartsWithIgnoreCase"),
                                                                                 0, 3);
            System.out.println(MapiObjectToStrings.toString(valueSetIdentifiers, "  "));

            System.out.println("-------");
            System.out.println("Lookup value Set - both supplied");
            System.out.println(MapiObjectToStrings.toString(boi.lookupValueSet(Constructors
                    .uidm("2.16.840.1.113883.1.11.14609"), Constructors.stm("IntravenousInfusion")), "  "));

            System.out.println("-------");
            System.out.println("Get runtime value set for domain - both valid");
            System.out.println(MapiObjectToStrings.toString(boi.lookupValueSetForDomain(Constructors
                    .stm("ActProcedureCode"), Constructors.stm("Canada"))));

            //            System.out.println("Get runtime value set for domain - invalid VocDomain");
            //            System.out.println(MapiObjectToStrings.toString(boi.lookupValueSetForDomain(Constructors.stm("Afoo"),
            // Constructors.stm("Canada"))));
            //            
            //            System.out.println("Get runtime value set for domain - invalid application context");
            //            System.out.println(MapiObjectToStrings.toString(boi.lookupValueSetForDomain(Constructors.stm("ActInjuryCode"),
            // Constructors.stm("foo"))));
            //            
            //            System.out.println("Get runtime value set for domain - nulls");
            //            System.out.println(MapiObjectToStrings.toString(boi.lookupValueSetForDomain(null, null)));
            //
            //            System.out.println("Get runtime value set for domain - one null");
            //            System.out.println(MapiObjectToStrings.toString(boi.lookupValueSetForDomain(Constructors.stm("ActProcedureCode"),
            // null)));

            System.out.println("-------");
            System.out.print("Is Code in Value Set - ");
            ConceptId conceptId = new ConceptId();
            conceptId.setCodeSystem_id(Constructors.uidm("2.16.840.1.113883.5.112"));
            conceptId.setConcept_code(Constructors.stm("PCA"));
            System.out.println(boi.isCodeInValueSet(Constructors.uidm("2.16.840.1.113883.1.11.14609"),
                                                    Constructors.stm("IntravenousInfusion"), Constructors.blm(true),
                                                    conceptId).isV());

        }

        catch (UnexpectedError e)
        {
            System.out.println(e.getPossible_cause().getV());
        }

        catch (Exception e)
        {
            System.out.println(e.getClass().toString());
            e.printStackTrace();
        }
    }
}