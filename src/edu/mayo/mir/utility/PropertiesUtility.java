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
package edu.mayo.mir.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Class to aid in finding and loading properties files.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class PropertiesUtility
{
    public static final String propertiesLocationKey = "_propertiesFileLocation_";
    public static final String propertiesParentFolderKey = "_propertiesFileParentFolder_";
    /**
     * Create a java properties object from a location. Adds a "propertiesFileParentFolder" value to the properties
     * object. The value is set to the file path of the folder containing the properties file. This is useful for the
     * method Log4JUtility.configureLog4JFromPathSpecifiedInProperties
     * 
     * @param propertiesFile can be a file path or a url.
     * @return The loaded properties file.
     * @throws IOException
     */
    public static Properties loadPropertiesFromFileOrURL(String propertiesFile) throws IOException
    {
        if (propertiesFile == null || propertiesFile.length() == 0)
        {
            throw new IOException("No file provided");
        }
        InputStream inputStream = null;

        String location;
        String parentFolder;

        try
        {
            URL temp = new URL(propertiesFile);
            inputStream = temp.openStream();
            location = temp.toString();
            parentFolder = temp.toString();
            int i = parentFolder.lastIndexOf("/");
            if (i == -1)
            {
                i = parentFolder.lastIndexOf("\\");
            }
            parentFolder = parentFolder.substring(0, i);
        }
        catch (MalformedURLException e) // Its not a url, must be a file name
        {
            File inputFile = new File(propertiesFile);
            inputStream = new FileInputStream(inputFile);
            parentFolder = inputFile.getParentFile().getAbsolutePath();
            location = inputFile.getAbsolutePath();
        }
        Properties props = new Properties();
        props.load(inputStream);
        inputStream.close();
        props.put(propertiesLocationKey, location);
        props.put(propertiesParentFolderKey, parentFolder);

        return props;
    }

    /**
     * Load a properties file from the classpath.
     * 
     * @param absolutePath absolute path to the properties file in the classpath. Should start with '/'
     * @return the properties object.
     * @throws IOException
     */
    public static Properties loadPropertiesFromClasspath(String absolutePath) throws IOException
    {
        Properties props = new Properties();
        
        if (absolutePath.charAt(0) != '/')
        {
            absolutePath = '/' + absolutePath;
        }
        
        InputStream temp = PropertiesUtility.class.getResourceAsStream(absolutePath);
        if (temp == null)
        {
            throw new IOException("No file '" + absolutePath + "' found on the classpath");
        }
        props.load(temp);
        props.put(propertiesLocationKey, "CLASSPATH: " + absolutePath);
        return props;
    }

    /**
     * Located the named file on the file system. Does the best it can to find it. Returns null if it could not be
     * found. If you pass in the VM parameter 'PropFileLocation' - this will override everything - example:
     * -DPropFileLocation=c:/temp/HL7TestProperties.prps However, if the named file doesn't exist, then it will attempt
     * to locate the file. This is not a drive search, it will always return very quickly.
     * 
     * @param fileName Name of the file to locate
     * @return file location (or null if file could not be found) - the may be a URL.
     */
    public static String locatePropFile(String fileName)
    {
        return locatePropFile(fileName, PropertiesUtility.class.getName());
    }

    /**
     * Located the named file on the file system. Does the best it can to find it. Returns null if it could not be
     * found. If you pass in the VM parameter 'PropFileLocation' - this will override everything - example:
     * -DPropFileLocation=c:/temp/HL7TestProperties.prps However, if the named file doesn't exist, then it will attempt
     * to locate the file. This is not a drive search, it will always return very quickly.
     * 
     * @param fileName Name of the file to locate
     * @param classToSearchFor A class that exists in your classpath to use as a starting point for the search. The
     *            recommended value for most use cases is "this.getClass().getName()".
     * @return file location (or null if file could not be found) - this may be a URL.
     */
    public static String locatePropFile(String fileName, String classToSearchFor)
    {
        // If they passed in a location, and it exists, then use it.
        String location = System.getProperty("PropFileLocation");
        if (location != null)
        {
            try
            {
                java.io.FileReader fileReader = new java.io.FileReader(location);
                fileReader.close();
                return location;
            }
            catch (Exception e)
            {
            } // file didn't exist at specified location... try others.
            try
            // is it a url
            {
                new URL(location);
                return location;
            }
            catch (Exception e)
            {
            }
        }

        try
        {
            Class clazz = Class.forName(classToSearchFor);
            java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            location = url.toString();

            File file = null;

            if ((location.endsWith("jar")) || (location.startsWith("jar")))
            {
                try
                {
                    url = ((java.net.JarURLConnection) url.openConnection()).getJarFileURL();

                } // javas behavior seems to have changed from 1.3 to 1.4 (or from linux to windows, not sure
                // whiche...
                // the above is necessary in 1.3, and not in 1.4
                catch (Exception e)
                {
                }

                // if its a jar file - need to chop off the file name.
                file = new java.io.File(convertEncodedSpaceToSpace(url.getFile()));
            }
            else
            {
                file = new java.io.File(convertEncodedSpaceToSpace(url.getFile()));
            }

            int depthCnt = 0;
            
            while (true)
            {
                // chop off the containing folder
                file = file.getParentFile();

                if (file == null)
                {
                    System.out.println("ERROR LOCATING PROPS FILE '" + fileName + "'!  Returning Null.  Failure is imminent.");
                    return null;
                }

                // go down to the "resources" folder
                File tempFile = new File(file, "resources");

                // go down to the file name
                tempFile = new File(tempFile, fileName);

                // See if it exists...

                // location = convertEncodedSpaceToSpace(location);
                if (tempFile.exists())
                {
                    return tempFile.getAbsolutePath();
                }

                //break out if we look too deep...
                depthCnt++;
                if (depthCnt > 10)
                {
                    System.out.println("ERROR LOCATING PROPS FILE '" + fileName + "'!  Returning Null.  Failure is imminent.");
                    return null;
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();

            System.out.println("ERROR LOCATING PROPS FILE '" + fileName + "'!  Returning Null.  Failure is imminent.");
            return null;
        }
    }

    private static String convertEncodedSpaceToSpace(String in)
    {
        int loc = in.indexOf("%20");
        while (loc != -1)
        {
            in = in.substring(0, loc) + " " + in.substring(loc + 3);
            loc = in.indexOf("%20");
        }
        return in;
    }

    /**
     * Convenience method that combines locatePropFile(fileName) and loadPropertiesFromFileOrURL(String)
     * @param fileName
     * @return The properties object found at the given location.
     * @throws IOException
     */
    public static Properties locateAndLoadPropFile(String fileName) throws IOException
    {
        String temp = locatePropFile(fileName, PropertiesUtility.class.getName());
        if (temp == null)
        {
            throw new IOException("Couldn't find file '" + fileName + "'");
        }
        return loadPropertiesFromFileOrURL(temp);
    }
    
    /**
     * Convenience method that combines locatePropFile(fileName, classToSearchFor) and loadPropertiesFromFileOrURL(String)
     * @param fileName
     * @return the Properties object found at the given location.
     * @throws IOException
     */
    public static Properties locateAndLoadPropFile(String fileName, String classToSearchFor) throws IOException
    {
        String temp = locatePropFile(fileName, classToSearchFor);
        if (temp == null)
        {
            throw new IOException("Couldn't find file '" + fileName + "'");
        }
        return loadPropertiesFromFileOrURL(temp);
    }
    
    /**
     * Convenience method that combines locatePropFile(fileName, classToSearchFor) and 
     * loadPropertiesFromFileOrURL(String) and Log4JUtility.configureLog4JFromPathSpecifiedInProperties
     * @param fileName
     * @return The Properties that are found.
     * @throws Exception 
     */
    public static Properties locateAndLoadPropFileConfigureLog4J(String fileName, String log4JConfigFilePropertyName,
            String classToSearchFor) throws Exception
    {
        String temp = locatePropFile(fileName, classToSearchFor);
        if (temp == null)
        {
            throw new IOException("Couldn't find file '" + fileName + "'");
        }
        Properties props = loadPropertiesFromFileOrURL(temp);
        Log4JUtility.configureLog4JFromPathSpecifiedInProperties(props, log4JConfigFilePropertyName, true);
        return props;
    }
    
    /**
     * Convenience method that combines locatePropFile(fileName, classToSearchFor) and 
     * loadPropertiesFromFileOrURL(String) and Log4JUtility.configureLog4JFromPathSpecifiedInProperties
     * @param fileName
     * @return The Properties found at the location.
     * @throws Exception 
     */
    public static Properties locateAndLoadPropFileConfigureLog4J(String fileName, String log4JConfigFilePropertyName) throws Exception
    {
        String temp = locatePropFile(fileName, PropertiesUtility.class.getName());
        if (temp == null)
        {
            throw new IOException("Couldn't find file '" + fileName + "'");
        }
        Properties props = loadPropertiesFromFileOrURL(temp);
        Log4JUtility.configureLog4JFromPathSpecifiedInProperties(props, log4JConfigFilePropertyName, true);
        return props;
    }

    // public static void main(String[] args) throws IOException, ClassNotFoundException
    // {
    //        
    // File temp = new File("C:\\Eclipse Projects\\general-workspace\\CTS\\resources\\CTSProperties.prps");
    // loadPropertiesFromFileOrURL(temp.getAbsolutePath());
    //        
    // Class clazz = Class.forName("edu.mayo.mir.utility.PropertiesUtility");
    // java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
    //        
    //       
    // loadPropertiesFromFileOrURL(url.toString() + "../../CTS/resources/CTSProperties.prps");
    // }
}