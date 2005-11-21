package org.hl7;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.hl7.baseTests.MAPI.MAPISuite;
import org.hl7.baseTests.VAPI.VAPISuite;
import org.lexgrid.convert.LgConvertException;
import org.lexgrid.convert.ldap.LdapConnectionDesc;
import org.lexgrid.convert.sql.SqlConnectionDesc;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.lexgrid.convert.apiWrapper.LgConvert;
import edu.mayo.mir.utility.PropertiesUtility;
import edu.mayo.mir.utility.parameter.StringParameter;

/**
 * Class to help set up the JUnit Test Suite.
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu ">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.6 $ checked in on $Date: 2005/10/14 15:44:11 $
 */

public class AllTests extends TestSuite
{
    public final static Logger      logger = Logger.getLogger("org.hl7.JUnitTests");
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

        LgConvert convert = new LgConvert();
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

            logger.debug("Preparing " + name);
            if (current.type == VAPI)
            {
                TestSuite tempTestSuite = null;
                try
                {

                    if (current.implementation == SQL)
                    {
                        tempTestSuite = subSuiteA;

                        SqlConnectionDesc scd = new SqlConnectionDesc();
                        scd.setDriver(current.driver);
                        scd.setUrl(current.url);
                        scd.setUsername(current.username);
                        scd.setPassword(current.password);

                        convert.lgxml2sql(xmlFileLocation, scd, null, true);
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

                        SqlConnectionDesc scd = new SqlConnectionDesc();
                        scd.setDriver("org.hsqldb.jdbcDriver");
                        scd.setUrl("jdbc:hsqldb:file:" + hsLocation + "\\temp");
                        scd.setUsername("sa");
                        scd.setPassword("");

                        convert.lgxml2sql(xmlFileLocation, scd, null, true);

                        SqlConnectionDesc scd2 = new SqlConnectionDesc();
                        scd2.setDriver(current.driver);
                        scd2.setUrl(current.url);
                        scd2.setUsername(current.username);
                        scd2.setPassword(current.password);

                        convert.sql2sqllite(scd, scd2, null, true, null);

                        // clean up
                        deleteRecursive(file);

                    }
                    else if (current.implementation == LDAP)
                    {
                        tempTestSuite = subSuiteC;
                        LdapConnectionDesc lcd = new LdapConnectionDesc();
                        lcd.setPassword(current.password);
                        lcd.setUrl(current.url);
                        lcd.setService(current.service);
                        lcd.setUsername(current.username);

                        convert.lgxml2ldap(xmlFileLocation, lcd, null);
                    }
                    VAPISuite.browserSetup(name, current, tempTestSuite);
                    VAPISuite.runtimeSetup(name, current, tempTestSuite);
                    VAPISuite.luceneSetup(name, current, tempTestSuite);
                }
                catch (LgConvertException e)
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
        public String name, url, driver, username, password, service;
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
