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
package edu.mayo.informatics.cts.utility;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import edu.mayo.mir.utility.Log4JUtility;
import edu.mayo.mir.utility.PropertiesUtility;
import edu.mayo.mir.utility.parameter.Parameters;

/**
 * Singleton class to make sure that the it only attempts to load properties and config log4j once.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class CTSConfigurator
{
    public final static Logger logger      = Logger.getLogger("edu.mayo.informatics.cts.utility.log4j.Initializer");
    private static CTSConfigurator   myself_;
    private boolean            initialized = false;
    private String             source_     = null;

    public static CTSConfigurator _instance()
    {
        if (myself_ == null)
        {
            myself_ = new CTSConfigurator();
        }
        return myself_;
    }

    protected CTSConfigurator()
    {
    }
    
    public boolean alreadyInitialized()
    {
        return initialized;
    }

    /**
     * Load properties and configure log4j using the values "CTSProperties.prps", "LOG4J_CONTROL_FILE".
     * 
     * @return The location of the file it used.
     */
    public String initialize() throws Exception
    {
        return initialize(true);
    }

    /**
     * Load properties using the values "CTSProperties.prps", "LOG4J_CONTROL_FILE"
     * 
     * @param configureLog4J should log4j be configured.
     * @return The location of the file it used.
     */
    public String initialize(boolean configureLog4J) throws Exception
    {
        return initialize("CTSProperties.prps", configureLog4J, "LOG4J_CONTROL_FILE");
    }
    
    /**
     * Load properties using specified values.
     * 
     * @param configureLog4J should log4j be configured.
     * @return The location of the file it used.
     */
    public String initialize(Properties props, boolean configureLog4J) throws Exception
    {
        return doInitialize(props, configureLog4J, "LOG4J_CONTROL_FILE");
    }

    /**
     * Load the properties, optionally initialize log4j. Find the properties file named 'fileName'. Log4J configured
     * from file pointed to by the log4jVarName variable inside of the properties file.
     * 
     * @param fileName properties file to load - can be a real file (Absolute or short path) or on the classpath.
     * @param configureLog4J should log4j be configured?
     * @param log4jVarName variable name inside of properties file that points to the log4j config file required if
     *            configureLog4J is true.
     * @throws Exception
     * @return The location of the props file it used.
     */
    public String initialize(String fileName, boolean configureLog4J, String log4jVarName) throws Exception
    {
        return doInitialize(fileName, log4jVarName, configureLog4J, this.getClass().getName());
    }

    private String doInitialize(String fileName, String log4jVarName, boolean configureLog4J, String classToSearchFor)
            throws Exception
    {
        if (initialized)
        {
            return source_;
        }

        Properties props;
        try
        {
            try
            {
                props = PropertiesUtility.locateAndLoadPropFile(fileName, classToSearchFor);
            }
            catch (IOException e)
            {
                // try to find it on the classpath
                props = PropertiesUtility.loadPropertiesFromClasspath(fileName);
            }
            source_ = props.getProperty(PropertiesUtility.propertiesLocationKey);
            Parameters.setValues(props);
        }
        catch (IOException e)
        {
            throw new Exception("Problem finding and loading properties file " + fileName);
        }

        if (configureLog4J)
        {
            try
            {
                Log4JUtility.configureLog4JFromPathSpecifiedInProperties(props, log4jVarName, true);
                logger.debug("Properties loaded from " + source_ + ", logger configured.");
            }
            catch (Exception e)
            {
                throw new Exception("Problem configuring log4j");
            }
        }
        initialized = true;
        return source_;
    }

    private String doInitialize(Properties props, boolean configureLog4J, String log4jVarName) throws Exception
    {
        if (initialized)
        {
            return source_;
        }

        source_ = "Passed in properties object";
        Parameters.setValues(props);

        if (configureLog4J)
        {
            try
            {
                Log4JUtility.configureLog4JFromPathSpecifiedInProperties(props, log4jVarName, true);
                logger.debug("Properties loaded from " + source_ + ", logger configured.");
            }
            catch (Exception e)
            {
                throw new Exception("Problem configuring log4j");
            }
        }
        initialized = true;
        return source_;
    }
}