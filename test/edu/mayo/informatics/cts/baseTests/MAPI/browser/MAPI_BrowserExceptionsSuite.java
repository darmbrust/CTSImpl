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
package edu.mayo.informatics.cts.baseTests.MAPI.browser;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hl7.CTSMAPI.BrowserOperations;

import edu.mayo.informatics.cts.TestCreationError;

/**
 * Class to help set up the JUnit test Suite.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class MAPI_BrowserExceptionsSuite extends TestSuite
{

    static String            name;
    static BrowserOperations boi;

    public MAPI_BrowserExceptionsSuite(String theName, BrowserOperations theBoi)
    {
        super(theName);
        name = theName;
        boi = theBoi;
    }

    public static Test suite() throws Exception
    {
        TestSuite suite;

        suite = new TestSuite(name);
        try
        {

            Method methods[] = new TestBrowserOperations().getClass().getDeclaredMethods();
            for (int i = 0; i < methods.length; i++)
            {
                String methodName = methods[i].getName();
                if (methodName.startsWith("test"))
                {
                    suite.addTest(new TestBrowserOperations(methods[i].getName(), boi));
                }
            }

        }
        catch (Exception e)
        {
            suite.addTest(new TestCreationError("Failed on " + name + " because " + e));
        }

        return suite;
    }
}