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
package edu.mayo.informatics.cts.baseTests.VAPI;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.UnexpectedError;

import edu.mayo.informatics.cts.AllTests;
import edu.mayo.informatics.cts.TestCreationError;
import edu.mayo.informatics.cts.AllTests.Config;
import edu.mayo.informatics.cts.baseTests.VAPI.browser.VAPI_BrowserExceptionsSuite;
import edu.mayo.informatics.cts.baseTests.VAPI.browser.VAPI_BrowserMethodsSuite;
import edu.mayo.informatics.cts.baseTests.VAPI.lucene.VAPI_LuceneSearchExceptionsSuite;
import edu.mayo.informatics.cts.baseTests.VAPI.lucene.VAPI_LuceneSearchMethodsSuite;
import edu.mayo.informatics.cts.baseTests.VAPI.runtime.VAPI_RuntimeExceptionsSuite;
import edu.mayo.informatics.cts.baseTests.VAPI.runtime.VAPI_RuntimeMethodsSuite;
import edu.mayo.informatics.cts.utility.CTSConstants;
import edu.mayo.informatics.lexgrid.convert.indexer.LdapIndexer;
import edu.mayo.informatics.lexgrid.convert.indexer.SQLIndexer;
import edu.mayo.informatics.lexgrid.convert.utility.NullMessageDirector;
import edu.mayo.mir.utility.parameter.StringParameter;

/**
 * Class to assist in the JUnit Test Suite setup.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class VAPISuite extends TestSuite
{
    public static Test browserSetup(String name, Config config, TestSuite suite)
    {
        BrowserOperations bo = null;
        try
        {
            if (config.implementation == AllTests.SQL)
            {
                bo = edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                                  config.url, config.driver, false, false);
            }

            else if (config.implementation == AllTests.SQLLITE)
            {
                bo = edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                                      config.url, config.driver,false,  false);
            }
            else if (config.implementation == AllTests.LDAP)
            {
                bo = edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                              config.url, config.service, false, false);
            }
            else
            {
                suite.addTest(new TestCreationError(name + " - Unexpected implementation type"));
                return suite;
            }

            suite.addTest(new VAPI_BrowserExceptionsSuite(name + " - Browser Exceptions", bo).suite());
            suite.addTest(new VAPI_BrowserMethodsSuite(name + " - Browser Methods", bo).suite());
        }
        catch (UnexpectedError e)
        {
            suite.addTest(new TestCreationError(name + " - Error creating the Browser Operations Object - " + e));
        }
        catch (Exception e)
        {
            suite.addTest(new TestCreationError(name + " - Problem creating the exception suite - " + e));
        }

        return suite;
    }

    public static Test runtimeSetup(String name, Config config, TestSuite suite)
    {
        RuntimeOperations ro = null;
        try
        {
            if (config.implementation == AllTests.SQL)
            {
                ro = edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
                                                                                  config.url, config.driver, false, false);
            }

            else if (config.implementation == AllTests.SQLLITE)
            {
                ro = edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
                                                                                      config.url, config.driver, false, false);
            }
            else if (config.implementation == AllTests.LDAP)
            {
                ro = edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
                                                                              config.url, config.service, false, false);
            }
            else
            {
                suite.addTest(new TestCreationError(name + " - Unexpected implementation type"));
                return suite;
            }

            suite.addTest(new VAPI_RuntimeExceptionsSuite(name + " - Runtime Exceptions", ro).suite());
            suite.addTest(new VAPI_RuntimeMethodsSuite(name + " - Runtime Methods", ro).suite());

        }
        catch (UnexpectedError e)
        {
            suite.addTest(new TestCreationError(name + " - Error creating the Runtime Operations Object - " + e));
        }
        catch (Exception e)
        {
            suite.addTest(new TestCreationError(name + " - Problem creating the exception suite - " + e));
        }

        return suite;
    }

    public static Test luceneSetup(String name, Config config, TestSuite suite)
    {
        name += " - Lucene";

        BrowserOperations bo = null;
        try
        {
            if (config.implementation == AllTests.SQL)
            {
                String indexLocation = System.getProperty("java.io.tmpdir")
                        + System.getProperty("file.separator")
                        + "CTSLuceneIndexTest_SQL_"
                        + config.name;
                String indexName = "CTS_LEXGRID_TEST_SQL_" + config.name;

                // create the index in the temp folder - index our test coding schemes.

                new SQLIndexer(indexName, indexLocation, config.username, config.password, config.url, config.driver,
                        new String[]{"Automobiles", "German Made Parts"}, new NullMessageDirector(), false, true, true);

                CTSConstants.LUCENE_INDEX_LOCATION = new StringParameter(indexLocation);

                bo = edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                                  config.url, config.driver, false, false);

                // do a quick search to get the Lucene Index initialized with the proper variables
                // (it initializes on demand - and assumes the variables are constant - which they aren't in
                // the testing framework
                bo.lookupConceptCodesByDesignation("Automobiles", "foobarmatchtext", "LuceneQuery", null, false, 0, 1);

            }

            else if (config.implementation == AllTests.SQLLITE)
            {
                // TODO not yet implemented
                bo = null;
                // bo = edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl._interface(config.username,
                // config.password,
                // config.url, config.driver, false);
            }

            else if (config.implementation == AllTests.LDAP)
            {
                String indexLocation = System.getProperty("java.io.tmpdir")
                        + System.getProperty("file.separator")
                        + "CTSLuceneIndexTest_LDAP_"
                        + config.name;
                String indexName = "CTS_LEXGRID_TEST_LDAP_" + config.name;

                CTSConstants.LUCENE_INDEX_LOCATION = new StringParameter(indexLocation);

                new LdapIndexer(indexName, indexLocation, config.username, config.password, config.url, config.service,
                        new String[]{"Automobiles", "German Made Parts"}, new NullMessageDirector(), false, true, true);

                bo = edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                              config.url, config.service, false, false);

                // do a quick search to get the Lucene Index initialized with the proper variables
                // (it initializes on demand - and assumes the variables are constant - which they aren't in
                // the testing framework
                bo.lookupConceptCodesByDesignation("Automobiles", "foobarmatchtext", "LuceneQuery", null, false, 0, 1);

            }
            else
            {
                suite.addTest(new TestCreationError(name + " - Unexpected implementation type"));
                return suite;
            }

            if (bo != null)
            {
                suite.addTest(new VAPI_LuceneSearchExceptionsSuite(name + " Exceptions", bo).suite());
                suite.addTest(new VAPI_LuceneSearchMethodsSuite(name + " Methods", bo).suite());
            }
        }
        catch (UnexpectedError e)
        {
            suite.addTest(new TestCreationError(name + " - Error creating the Browser Operations Object - " + e));
        }
        catch (Exception e)
        {
            suite.addTest(new TestCreationError(name + " - Error creating the lexgrid index or creating the suite - " + e));
            return suite;
        }

        return suite;
    }
}