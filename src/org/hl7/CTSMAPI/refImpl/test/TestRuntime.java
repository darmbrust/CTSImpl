package org.hl7.CTSMAPI.refImpl.test;

import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.ValidateCodeReturn;
import org.hl7.CTSMAPI.ValueSetExpansion;
import org.hl7.CTSMAPI.refImpl.RuntimeOperationsImpl;
import org.hl7.cts.types.CD;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Constructors;
import edu.mayo.informatics.cts.utility.MapiObjectToStrings;


/**
 * <pre>
 * Title:        TestRuntime.java
 * Description:  Class for doing adhoc testing of the LDAP runtime implementation.
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.2 $ checked in on $Date: 2005/10/14 15:44:09 $
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
