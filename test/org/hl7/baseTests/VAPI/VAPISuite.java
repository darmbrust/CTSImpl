package org.hl7.baseTests.VAPI;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hl7.AllTests;
import org.hl7.TestCreationError;
import org.hl7.AllTests.Config;
import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.RuntimeOperations;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.baseTests.VAPI.browser.VAPI_BrowserExceptionsSuite;
import org.hl7.baseTests.VAPI.browser.VAPI_BrowserMethodsSuite;
import org.hl7.baseTests.VAPI.lucene.VAPI_LuceneSearchExceptionsSuite;
import org.hl7.baseTests.VAPI.lucene.VAPI_LuceneSearchMethodsSuite;
import org.hl7.baseTests.VAPI.runtime.VAPI_RuntimeExceptionsSuite;
import org.hl7.baseTests.VAPI.runtime.VAPI_RuntimeMethodsSuite;
import org.hl7.utility.CTSConstants;

import edu.mayo.informatics.cts.lucene.LdapLoader;
import edu.mayo.informatics.cts.lucene.SQLLoader;
import edu.mayo.informatics.cts.lucene.gui.NullMessageDirector;
import edu.mayo.mir.utility.parameter.StringParameter;

/**
 * Class to assist in the JUnit Test Suite setup.
 * 
 * <pre>
 * Copyright (c) 2005 Mayo Foundation. All rights reserved.
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
 *        Informatics Research (http://informatics.mayo.edu/
 )."
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu ">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.3 $ checked in on $Date: 2005/10/14 15:44:11 $
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
                bo = org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                                  config.url, config.driver, false, false);
            }

            else if (config.implementation == AllTests.SQLLITE)
            {
                bo = org.hl7.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                                      config.url, config.driver,false,  false);
            }
            else if (config.implementation == AllTests.LDAP)
            {
                bo = org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
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
                ro = org.hl7.CTSVAPI.sql.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
                                                                                  config.url, config.driver, false, false);
            }

            else if (config.implementation == AllTests.SQLLITE)
            {
                ro = org.hl7.CTSVAPI.sqlLite.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
                                                                                      config.url, config.driver, false, false);
            }
            else if (config.implementation == AllTests.LDAP)
            {
                ro = org.hl7.CTSVAPI.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
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

                new SQLLoader(indexName, indexLocation, config.username, config.password, config.url, config.driver,
                        new String[]{"Automobiles", "German Made Parts"}, new NullMessageDirector(), false, true, true);

                CTSConstants.LUCENE_INDEX_LOCATION = new StringParameter(indexLocation);

                bo = org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
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
                // bo = org.hl7.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl._interface(config.username,
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

                new LdapLoader(indexName, indexLocation, config.username, config.password, config.url, config.service,
                        new String[]{"Automobiles", "German Made Parts"}, new NullMessageDirector(), false, true, true, 50);

                bo = org.hl7.CTSVAPI.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
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
