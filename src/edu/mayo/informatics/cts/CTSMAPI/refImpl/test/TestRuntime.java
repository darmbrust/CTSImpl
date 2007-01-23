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

import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.ValidateCodeReturn;
import org.hl7.CTSMAPI.ValueSetExpansion;
import org.hl7.cts.types.CD;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.CTSMAPI.refImpl.RuntimeOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Constructors;
import edu.mayo.informatics.cts.utility.MapiObjectToStrings;

/**
 * Class for doing adhoc testing of the LDAP runtime implementation.
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
        try
        {

            System.out.println("****************************************");
            System.out.println("HL7 Test runtime");
            System.out.println("****************************************");

            System.out.println("ServiceName " + roi.getServiceName().getV());
            System.out.println("--------------");
            System.out.println("Service Version " + roi.getServiceVersion().getV());
            System.out.println("--------------");
            System.out.println("HL7ReleaseVersion " + roi.getHL7ReleaseVersion().getV());
            System.out.println("--------------");
            System.out.println("Service Description " + roi.getServiceDescription().getV());
            System.out.println("--------------");

            System.out.println("CTSVersionId " + roi.getCTSVersion().getMajor().getV() + "."
                    + roi.getCTSVersion().getMinor().getV());
            System.out.println("--------------");

            System.out.println("-------");
            System.out.println("Supported Match Algorithms:");
            System.out.println(MapiObjectToStrings.toString(roi.getSupportedMatchAlgorithms(), "  "));

            System.out.println("lookupValueSetExpansion - ");
            ValueSetExpansion[] valueSetExpansions = roi.lookupValueSetExpansion(Constructors.stm("ActClass"), null,
                                                                                 Constructors.stm("en"), Constructors
                                                                                         .blm(false), 0, 50);
            System.out.println(MapiObjectToStrings.toString(valueSetExpansions, ""));

            System.out.println("--------------");

            System.out.println("expandValueSetExpansionContext");
            ValueSetExpansion[] valueSetExpansions2 = roi
                    .expandValueSetExpansionContext("<context><baseValueSetId>2.16.840.1.113883.1.11.11527</baseValueSetId><nestedValueSetId>2.16.840.1.113883.1.11.19445</nestedValueSetId><nestingDepth>2</nestingDepth><language>en</language><timeout>0</timeout><sizeLimit>0</sizeLimit></context>"
                            .getBytes());
            System.out.println(MapiObjectToStrings.toString(valueSetExpansions2, ""));

            System.out.println("--------------");

            System.out.println("ValidateCode");
            //            CD code = new CD();
            //            code.setCode("INC");
            //            code.setCodeSystem("2.16.840.1.113883.5.6");
            //            code.setCodeSystemName("ActClass");
            //            code.setCodeSystemVersion(null);
            //            code.setDisplayName("registration");
            //            CR[] cr = new CR[]{new CR(), new CR()};
            //            // cr[0].setCode("REG");
            //            // cr[0].setCodeSystem("2.16.840.1.113883.5.3");
            //            // cr[1].setCode("REGsd");
            //            // cr[1].setCodeSystem("2.16.840.1.113883.5.3");
            //
            //            code.setQualifiers(cr);
            //            code.setOriginalText(null);
            //            code.setTranslation(new CD[]{code});
            //
            //            ValidateCodeReturn validateCodeReturn = roi_.validateCode(Constructors.stm("ActClass"), code, null,
            // Constructors.blm(true),
            //                    Constructors.blm(false));
            //            System.out.println(MapiObjectToStrings.toString(validateCodeReturn, ""));

            CD code = new CD();
            code.setCode("IDENT");
            ValidateCodeReturn validateCodeReturn = roi.validateCode(Constructors.stm("RoleClass"), code, null,
                                                                     Constructors.blm(true), Constructors.blm(false));
            System.out.println(MapiObjectToStrings.toString(validateCodeReturn, ""));

            System.out.println("--------------");
            System.out.println("supportedVocabularyDomains");
            ST[] valueSetDomains = roi.getSupportedVocabularyDomains(Constructors.stm("act"), Constructors
                    .stm("StartsWithIgnoreCase"), 0, 5);
            System.out.println(MapiObjectToStrings.toString(valueSetDomains));

            System.out.println("--------------");
            System.out.println("Subsumes");
            CD cd1 = new CD();
            CD cd2 = new CD();
            cd1.setCode("DIET");
            cd1.setCodeSystem("2.16.840.1.113883.5.4");
            cd2.setCode("FAST");
            cd2.setCodeSystem("2.16.840.1.113883.5.4");

            System.out.println(roi.subsumes(cd1, cd2).isV());

            System.out.println("--------------");
            System.out.println("fillInDetails");
            CD cd3 = new CD();
            cd3.setCode("ACID");
            cd3.setCodeSystem("2.16.840.1.113883.5.4");
            System.out.println(MapiObjectToStrings.toString(roi.fillInDetails(cd3, Constructors.stm("en"))));

        }
        catch (UnexpectedError e)
        {
            System.out.println(e.getPossible_cause().getV());

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
    }
}