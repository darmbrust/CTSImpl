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
package edu.mayo.mir.utility.parameter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 *  A base class for all application parameters.
 *  
 * A base class for all application parameters. The primary purpose behind this class is to enable programmers to define
 * application parameters within the scope of the Java language. The benefits of doing this include:
 * <OL>
 * <LI> To allow parameter names to be validated at compile time. getProperties(String) approaches introduce the
 * possibility of misspelling and typographical errors that may not be detected until quite late in the development
 * cycle.
 * <LI> To utilize the existing documentation mechanisms provided by the Java JDK rather thatn trying to arrive at
 * another alternate mechanism for in-file documentation
 * <LI> To promote the re-use of parameters where appropriate, reducing the application configuration task.
 * </OL>
 * 
 * Parms is a singleton base class, maintaining one centralized repository for parameters - the Hashtable called props.
 * To create a set of properties:
 * 
 * 1) Derive a class from this class 2) Include the appropriate loader invocation in the static class constructor 3)
 * Enter each parameter as one of StringParameter, BooleanParameter, IntParameter or EncryptedParameter 4) Retrieve the
 * parameter value via the getValue() function
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class Parameters
{
    /**
     * parameters include all the parameters of the classes in the init method
     */
    protected static HashMap   parameters  = new HashMap();
    /**
     * contains a list of all the Classes that have been passed to the init method
     */
    protected static ArrayList initClasses = new ArrayList();

    protected static Logger    logger      = Logger
                                                   .getLogger("edu.mayo.informatics.cts.utility.parameters.configuration");

    protected static void init(Class newClass)
    {
        logger.debug("enter init(): " + newClass.getName());
        Field[] fields = newClass.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            logger.debug("fields["
                    + i
                    + "]=   name="
                    + fields[i].getName()
                    + " declaringClass="
                    + fields[i].getDeclaringClass().getName()
                    + "  type="
                    + fields[i].getType().getName());

            String type = fields[i].getType().getName();
            if (type.equals(StringParameter.class.getName())
                    || type.equals(BooleanParameter.class.getName())
                    || type.equals(IntParameter.class.getName()) // ||
            // type.equals(EncryptedParameter.class.getName())
            )
            {
                String declaringClass = fields[i].getDeclaringClass().getName();
                if (!initClasses.contains(declaringClass))
                    initClasses.add(declaringClass);
                String name = fields[i].getName();
                String parameterName = declaringClass + "." + name;
                logger.debug("adding parameter=" + parameterName);
                parameters.put(declaringClass + "." + name, fields[i]);
            }
        }
        logger.debug("exit init()");
    }

    public static Properties getProperties(Class newClass)
    {
        Properties properties = new Properties();
        try
        {

            Iterator parameterNames = parameters.keySet().iterator();
            while (parameterNames.hasNext())
            {
                String parameterName = (String) (parameterNames.next());
                String parameterClassName = parameterName.substring(0, parameterName.lastIndexOf('.'));
                if (parameterClassName.equals(newClass.getName()))
                {
                    Field field = (Field) (parameters.get(parameterName));
                    Object value = field.get(null);
                    properties.setProperty(parameterName, value.toString());
                }
            }
        }
        catch (Exception exception)
        {
            logger.error("Error", exception);
        }
        return properties;
    }

    public static Properties getProperties()
    {
        Properties properties = new Properties();
        try
        {
            Iterator parameterNames = parameters.keySet().iterator();
            while (parameterNames.hasNext())
            {
                String parameterName = (String) (parameterNames.next());
                Field field = (Field) (parameters.get(parameterName));
                Object value = field.get(null);
                properties.setProperty(parameterName, value.toString());
            }
        }
        catch (Exception exception)
        {
            logger.error("Error", exception);
        }
        return properties;
    }

    public static void setValues(Properties properties)
    {
        logger.debug("enter setValues()");

        // try
        // {
        Enumeration names = properties.propertyNames();
        while (names.hasMoreElements())
        {
            String parameterName = (String) (names.nextElement());
            logger.debug("parameterName=" + parameterName);

            if (parameterName.indexOf(".") == -1)
                continue;
            String parameterClass = parameterName.substring(0, parameterName.lastIndexOf('.'));
            logger.debug("parameterClass=" + parameterClass);

            if (!initClasses.contains(parameterClass))
            {
                try
                {
                    init(Class.forName(parameterClass));
                }
                catch (Exception exception)
                {
                    logger.error("Error", exception);
                    continue;
                }
                catch (Error error)
                {
                    logger.error("Error", error);
                    continue;
                }

            }
            if (parameters.containsKey(parameterName))
            {
                logger.debug("Parameters contains parameterName=" + parameterName);

                Field field = (Field) (parameters.get(parameterName));
                String type = field.getType().getName();
                logger.debug("parameter type=" + type);

                String value = properties.getProperty(parameterName);
                logger.debug("parameter value=" + value);
                try
                {
                    if (type.equals(StringParameter.class.getName()))
                    {
                        field.set(null, new StringParameter(value));
                        logger.debug("set StringParameter");
                    }
                    else if (type.equals(BooleanParameter.class.getName()))
                    {
                        field.set(null, new BooleanParameter(value));
                        logger.debug("set BooleanParameter");
                    }
                    else if (type.equals(IntParameter.class.getName()))
                    {
                        field.set(null, new IntParameter(value));
                        logger.debug("set IntParameter");
                    }
                    // else if(type.equals(EncryptedParameter.class.getName()))
                    // {
                    // field.set(null, new EncryptedParameter(value));
                    // logger.debug("set EncryptedParameter");
                    // }
                }
                catch (Exception exception)
                {
                    logger.error("Error", exception);
                    continue;
                }
            }
        }

        logger.debug("exit setValues()");

    }
}