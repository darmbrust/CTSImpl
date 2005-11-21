package org.hl7.CTSMAPI.refImpl.test;

import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.CodeSystemDescriptor;
import org.hl7.CTSMAPI.CodeSystemInfo;
import org.hl7.CTSMAPI.ConceptId;
import org.hl7.CTSMAPI.RIMCodedAttribute;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.ValueSetDescriptor;
import org.hl7.CTSMAPI.VocabularyDomainDescription;
import org.hl7.CTSMAPI.refImpl.BrowserOperationsImpl;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.Constructors;
import edu.mayo.informatics.cts.utility.MapiObjectToStrings;


/**
 * <pre>
 * Title:        TestBrowser.java
 * Description:  Class for doing ad-hoc testing of the LDAP Browser implementation.
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
