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
package edu.mayo.informatics.cts.baseTests.MAPI;

import junit.framework.TestSuite;

import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.UnexpectedError;

import edu.mayo.informatics.cts.AllTests;
import edu.mayo.informatics.cts.TestCreationError;
import edu.mayo.informatics.cts.AllTests.Config;
import edu.mayo.informatics.cts.baseTests.MAPI.browser.MAPI_BrowserExceptionsSuite;
import edu.mayo.informatics.cts.baseTests.MAPI.runtime.MAPI_RuntimeExceptionsSuite;

/**
 * Class to help set up the JUnit test Suite.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @author Kevin Peterson
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
                ro = edu.mayo.informatics.cts.CTSMAPI.refImpl.RuntimeOperationsImpl._interface(config.username, config.password,
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
                bo = edu.mayo.informatics.cts.CTSMAPI.refImpl.BrowserOperationsImpl._interface(config.username, config.password,
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