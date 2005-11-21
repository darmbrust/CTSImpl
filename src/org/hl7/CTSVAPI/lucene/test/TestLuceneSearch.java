package org.hl7.CTSVAPI.lucene.test;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.VapiObjectToStrings;

/**
 * <pre>
 * Title:        TestLuceneSearch.java
 * Description:  Class for doing simple tests on the Lucene Search backend
 * Copyright: (c) 2002, 2003, 2004 Mayo Foundation. All rights reserved.
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
 * @version 1.0 - cvs $Revision: 1.2 $ checked in on $Date: 2005/10/14 15:44:08 $
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