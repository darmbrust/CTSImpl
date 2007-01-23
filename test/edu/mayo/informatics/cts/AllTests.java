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
package edu.mayo.informatics.cts;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.LexGrid.messaging.impl.CommandLineMessageDirector;
import org.LexGrid.messaging.impl.NullMessageDirector;
import org.LexGrid.util.config.PropertiesUtility;
import org.LexGrid.util.config.parameter.StringParameter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.mayo.informatics.cts.baseTests.MAPI.MAPISuite;
import edu.mayo.informatics.cts.baseTests.VAPI.VAPISuite;
import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.lexgrid.convert.formats.InputFormatInterface;
import edu.mayo.informatics.lexgrid.convert.formats.MinimalConversionLauncher;
import edu.mayo.informatics.lexgrid.convert.formats.Option;
import edu.mayo.informatics.lexgrid.convert.formats.OptionHolder;
import edu.mayo.informatics.lexgrid.convert.formats.OutputFormatInterface;
import edu.mayo.informatics.lexgrid.convert.formats.inputFormats.LexGridSQL;
import edu.mayo.informatics.lexgrid.convert.formats.inputFormats.LexGridXML;
import edu.mayo.informatics.lexgrid.convert.formats.outputFormats.LexGridLDAPOut;
import edu.mayo.informatics.lexgrid.convert.formats.outputFormats.LexGridSQLLiteOut;
import edu.mayo.informatics.lexgrid.convert.formats.outputFormats.LexGridSQLOut;

/**
 * Class to help set up the JUnit Test Suite.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class AllTests extends TestSuite
{
    public final static Logger      logger = Logger.getLogger("edu.mayo.informatics.cts.JUnitTests");
    public static StringParameter SERVER_CONFIG_FILE = new StringParameter("");

    // types
    private static final int      VAPI               = 0;
    private static final int      MAPI               = 1;

    // impls
    public static final int       SQL                = 2;
    public static final int       SQLLITE            = 3;
    public static final int       LDAP               = 4;

    // vars
    private static final int      NAME               = 5;
    private static final int      URL                = 6;
    private static final int      DRIVER             = 7;
    private static final int      USERNAME           = 8;
    private static final int      PASSWORD           = 9;
    private static final int      SERVICE            = 10;

    public static String mapIntConstantToString(int val) throws Exception
    {
        switch (val)
        {
            case VAPI :
                return "VAPI";

            case MAPI :
                return "MAPI";

            case SQL :
                return "SQL";

            case SQLLITE :
                return "SQLLITE";

            case LDAP :
                return "LDAP";

            case NAME :
                return "NAME";

            case URL :
                return "URL";

            case DRIVER :
                return "DRIVER";

            case SERVICE :
                return "SERVICE";

            case USERNAME :
                return "USERNAME";

            case PASSWORD :
                return "PASSWORD";
            default :
                throw new Exception("Unknown type identifer");

        }
    }

    public static Test suite() throws Exception
    {
        TestSuite suite = new TestSuite("CTS Tests");
        try
        {
            CTSConfigurator._instance().initialize("UnitTestingProperties.prps", true, "LOG4J_CONTROL_FILE");
        }
        catch (Exception e)
        {
            System.out.println("A problem occured initializing the JUnit Tests.");
            throw e;
        }

        //silence some extraneous warnings from a logger here:
        Logger.getLogger("org.LexGrid.emf.base.xml.LgXMLHandlerImpl").setLevel(Level.ERROR);
        
        String location = PropertiesUtility.locatePropFile(SERVER_CONFIG_FILE.toString());
        Properties props = new Properties();
        props.load(new FileInputStream(new File(location)));

        logger.debug("reading test config from " + location);
        ArrayList configs = new ArrayList();
        // Here is the code for checking for all valid configs in the properties file
        // format documented in the provide file.
        for (int type = 0; type <= 1; type++)
        {
            for (int impl = 2; impl <= 4; impl++)
            {
                for (int identifier = 1; identifier <= 20; identifier++)
                {
                    String temp = mapIntConstantToString(type) + "_" + mapIntConstantToString(impl) + "_" + identifier;

                    Config config = new Config();
                    config.type = type;
                    config.implementation = impl;
                    config.number = identifier;
                    config.name = props.getProperty(temp + "_" + mapIntConstantToString(NAME));
                    config.url = props.getProperty(temp + "_" + mapIntConstantToString(URL));
                    config.driver = props.getProperty(temp + "_" + mapIntConstantToString(DRIVER));
                    config.password = props.getProperty(temp + "_" + mapIntConstantToString(PASSWORD));
                    config.username = props.getProperty(temp + "_" + mapIntConstantToString(USERNAME));
                    config.service = props.getProperty(temp + "_" + mapIntConstantToString(SERVICE));

                    if (config.name != null && config.url != null && (config.driver != null || config.service != null))
                    {
                        configs.add(config);
                    }
                }
            }
        }

        TestSuite subSuiteA = new TestSuite("VAPI SQL Tests");
        TestSuite subSuiteB = new TestSuite("VAPI SQLLite Tests");
        TestSuite subSuiteC = new TestSuite("VAPI LDAP Tests");
        TestSuite subSuiteD = new TestSuite("MAPI Tests");

        suite.addTest(subSuiteA);
        suite.addTest(subSuiteB);
        suite.addTest(subSuiteC);
        suite.addTest(subSuiteD);

        String xmlFileLocation = "resources/data/testDBs/LexGrid.xml";

        logger.debug("loading databases, creating lucene indexes");
        
        for (int i = 0; i < configs.size(); i++)
        {
            Config current = (Config) configs.get(i);

            String name;
            try
            {
                name = AllTests.mapIntConstantToString(current.type)
                        + "_"
                        + AllTests.mapIntConstantToString(current.implementation)
                        + "_"
                        + current.name;
            }
            catch (Exception e1)
            {
                suite.addTest(new TestCreationError("Invalid int constant passed in to test suite constructor"));
                return suite;
            }

            System.out.println("Preparing " + name);
            logger.info("Preparing " + name);
            if (current.type == VAPI)
            {
                TestSuite tempTestSuite = null;
                try
                {

                    if (current.implementation == SQL)
                    {
                        tempTestSuite = subSuiteA;
                        
                        InputFormatInterface inputFormat = new LexGridXML(xmlFileLocation);
                        OutputFormatInterface outputFormat = new LexGridSQLOut(current.username, current.password, current.url, current.driver, "");
                        OptionHolder oh = new OptionHolder();
                        oh.add(new Option(Option.FAIL_ON_ERROR, new Boolean(true)));
                        
                        MinimalConversionLauncher.startConversion(inputFormat, outputFormat, null, oh, new NullMessageDirector());
                    }
                    else if (current.implementation == SQLLITE)
                    {
                        tempTestSuite = subSuiteB;

                        // first go to a hypersonic sql db, then to sqllite.

                        String hsLocation = System.getProperty("java.io.tmpdir")
                                + System.getProperty("file.separator")
                                + "CTSSQLLiteConversionTempFolder_"
                                + current.name;

                        // make sure it is empty.
                        File file = new File(hsLocation);
                        deleteRecursive(file);
                        
                        InputFormatInterface inputFormat = new LexGridXML(xmlFileLocation);
                        OutputFormatInterface outputFormat = new LexGridSQLOut("sa", "", "jdbc:hsqldb:file:" + hsLocation + "\\temp", "org.hsqldb.jdbcDriver", "");
                        OptionHolder oh = new OptionHolder();
                        oh.add(new Option(Option.FAIL_ON_ERROR, new Boolean(true)));
                        
                        MinimalConversionLauncher.startConversion(inputFormat, outputFormat, null, oh, new NullMessageDirector());
                        
                        oh = new OptionHolder();
                        oh.add(new Option(Option.FAIL_ON_ERROR, new Boolean(true)));
                        oh.add(new Option(Option.SQL_FETCH_SIZE, new String("1000")));
                        oh.add(new Option(Option.ENFORCE_INTEGRITY, new Boolean(false)));
                        
                        inputFormat = new LexGridSQL("sa", "", "jdbc:hsqldb:file:" + hsLocation + "\\temp", "org.hsqldb.jdbcDriver", "");
                        outputFormat = new LexGridSQLLiteOut(current.username, current.password, current.url, current.driver);
                        
                        MinimalConversionLauncher.startConversion(inputFormat, outputFormat, new String[] {"Automobiles", "German Made Parts", "ActClass", "ActCode", "CompressionAlgorithm", "Race"}, oh, new NullMessageDirector());

                        // clean up
                        deleteRecursive(file);

                    }
                    else if (current.implementation == LDAP)
                    {
                        tempTestSuite = subSuiteC;
                        String tempUrl = current.url;
                        String port = current.url.substring(current.url.lastIndexOf(':') + 1);
                        if (port.endsWith("/"))
                        {
                            port = port.substring(0, port.length() -1);
                        }
                        tempUrl = current.url.substring(0, current.url.lastIndexOf(':'));
                        
                        
                        InputFormatInterface inputFormat = new LexGridXML(xmlFileLocation);
                        OutputFormatInterface outputFormat = new LexGridLDAPOut(current.username, current.password, tempUrl, Integer.parseInt(port), current.service);
                        
                        OptionHolder oh = new OptionHolder();
                        oh.add(new Option(Option.FAIL_ON_ERROR, new Boolean(true)));
                        
                        MinimalConversionLauncher.startConversion(inputFormat, outputFormat, null, oh, new NullMessageDirector());
                    }
                    VAPISuite.browserSetup(name, current, tempTestSuite);
                    VAPISuite.runtimeSetup(name, current, tempTestSuite);
                    VAPISuite.luceneSetup(name, current, tempTestSuite);
                }
                catch (Exception e)
                {
                    logger.error("Error preparing tests", e);
                    tempTestSuite.addTest(new TestCreationError(name
                            + " Problem loading the test database - "
                            + e.toString()
                            + " "
                            + (e.getCause() == null ? ""
                                    : e.getCause().toString())));
                }
            }
            else if (current.type == MAPI)
            {
                MAPISuite.runtimeSetup(current, subSuiteD);
                MAPISuite.browserSetup(current, subSuiteD);
            }
        }
        return suite;
    }

    public static class Config
    {
        public int type;
        public int implementation;
        public int number;
        public String name, url, driver, username, password, service, tablePrefix;
    }

    private static void deleteRecursive(File file)
    {
        if (!file.exists())
        {
            return;
        }
        if (file.isDirectory())
        {
            File[] temp = file.listFiles();
            for (int i = 0; i < temp.length; i++)
            {
                deleteRecursive(temp[i]);
            }
        }
        file.delete();
    }
}