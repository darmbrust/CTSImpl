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
package edu.mayo.informatics.cts.CTSVAPI.lucene.test;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.UnexpectedError;

import edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.VapiObjectToStrings;

/**
 * Class for doing simple tests on the Lucene Search backend
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestLuceneSearch
{
    BrowserOperations boi_;

    public TestLuceneSearch() throws Exception
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
        TestLuceneSearch runtime = new TestLuceneSearch();
        runtime.testBrowser();
    }

    public void testBrowser() throws Exception
    {
        try
        {
            System.out.println("service name " + boi_.getServiceName());
            System.out.println("----------");
            System.out.println("service version " + boi_.getServiceVersion());
            System.out.println("----------");

            System.out.println("lookupConceptCodesByDesignation");
            System.out.println(VapiObjectToStrings.toString(boi_.lookupConceptCodesByDesignation("*", "procedure",
                                                                                                 "LuceneQuery", null,
                                                                                                 true, 1000, 10), ""));
            System.out.println("------------");

            System.out.println("lookupConceptCodesByProperties");
            System.out.println(VapiObjectToStrings.toString(boi_.lookupConceptCodesByProperty("*", "procedure",
                                                                                              "LuceneQuery", null,
                                                                                              true, null, null, 1000,
                                                                                              15), ""));
            System.out.println("------------");

             System.out.println("lookupDesignations");
             ConceptId conceptId = new ConceptId();
             conceptId.setCodeSystem_id("1.3.6.1.4.1.2114.108.1.9.101");
             conceptId.setConcept_code("C1447");
             System.out.println(VapiObjectToStrings.toString(boi_.lookupDesignations(conceptId, "lower",
             "LuceneQuery", ""), ""));
             System.out.println("------------");
             
             System.out.println("lookupProperties");
             ConceptId conceptId5 = new ConceptId();
             conceptId5.setCodeSystem_id("1.3.6.1.4.1.2114.108.1.9.101");
             conceptId5.setConcept_code("C15181");
             System.out.println(VapiObjectToStrings.toString(boi_.lookupProperties(conceptId5, new String[] {}, "", "LuceneQuery", "",
                                                                                  new String[] {}), ""));

        }
        catch (UnexpectedError e)
        {
            System.out.println(e.getPossible_cause());
            e.printStackTrace();
        }
    }

}