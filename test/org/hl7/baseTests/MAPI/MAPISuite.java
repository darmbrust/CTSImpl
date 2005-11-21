package org.hl7.baseTests.MAPI;

import junit.framework.TestSuite;

import org.hl7.AllTests;
import org.hl7.TestCreationError;
import org.hl7.AllTests.Config;
import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.baseTests.MAPI.browser.MAPI_BrowserExceptionsSuite;
import org.hl7.baseTests.MAPI.runtime.MAPI_RuntimeExceptionsSuite;

/**
 * Class to help set up the JUnit test Suite.
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
 * @author Kevin Peterson</A>
 * @version 1.0 - cvs $Revision: 1.2 $ checked in on $Date: 2005/10/14 15:44:12 $
 */
public class MAPISuite extends TestSuite
{
    public static void runtimeSetup(Config config, TestSuite suite)
    {
        String name;
        try
        {
            name = AllTests.mapIntConstantToString(config.type)
                    + "_"
                    + AllTests.mapIntConstantToString(config.implementation)
                    + "_"
                    + config.name;
        }
        catch (Exception e1)
        {
            suite.addTest(new TestCreationError("Invalid int constant passed in to test suite constructor"));
            return;
        }

        RuntimeOperations ro = null;
        try
        {
            if (config.implementation == AllTests.SQL)
            {
                ro = org.hl7.CTSMAPI.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
                                                                              config.url, config.driver, false, false);
            }
            else
            {
                suite.addTest(new TestCreationError(name + " - Unexpected implementation type"));
                return;
            }

            suite.addTest(new MAPI_RuntimeExceptionsSuite(name + " - Runtime ", ro).suite());
        }
        catch (UnexpectedError e)
        {
            suite.addTest(new TestCreationError(name + " - Error creating the Runtime Operations Object - " + e));
        }
        catch (Exception e)
        {
            suite.addTest(new TestCreationError(name + " - Problem creating the exception suite - " + e));
        }
    }

    public static void browserSetup(Config config, TestSuite suite)
    {
        String name;
        try
        {
            name = AllTests.mapIntConstantToString(config.type)
                    + "_"
                    + AllTests.mapIntConstantToString(config.implementation)
                    + "_"
                    + config.name;
        }
        catch (Exception e1)
        {
            suite.addTest(new TestCreationError("Invalid int constant passed in to test suite constructor"));
            return;
        }

        BrowserOperations bo = null;
        try
        {
            if (config.implementation == AllTests.SQL)
            {
                bo = org.hl7.CTSMAPI.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
                                                                              config.url, config.driver, false, false);
            }
            else
            {
                suite.addTest(new TestCreationError(name + " - Unexpected implementation type"));
                return;
            }

            suite.addTest(new MAPI_BrowserExceptionsSuite(name + " - Browser ", bo).suite());
        }
        catch (UnexpectedError e)
        {
            suite.addTest(new TestCreationError(name + " - Error creating the Browser Operations Object - " + e));
        }
        catch (Exception e)
        {
            suite.addTest(new TestCreationError(name + " - Problem creating the exception suite - " + e));
        }
    }
}
