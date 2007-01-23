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

import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.CTSMAPI.refImpl.BrowserOperationsImpl;
import edu.mayo.informatics.cts.CTSMAPI.refImpl.RuntimeOperationsImpl;

/**
 * Some utility methods for the JSP Implementation.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class JSPUtility
{
    private BrowserOperations browserOperations;
    private RuntimeOperations runtimeOperations;
    
    public JSPUtility() throws UnexpectedError
    {
        try
        {
            CTSConfigurator._instance().initialize();
        }
        catch (Exception e1)
        {
            System.out.println("Problem loading configuration information.  Failure likely.");
        }
        browserOperations = BrowserOperationsImpl._interface();
        runtimeOperations = RuntimeOperationsImpl._interface();
        
    }

	/**
	 * Method used by jsp implementation for formatting
	 * @param string
	 * @return The string with some characters escaped...
	 */

	public static String escapeChars(ST string)
	{
		return escapeChars(string.getV());
	}

	public static String convertQuotes(ST string)
	{
		return convertQuotes(string.getV());
	}

	public static String escapeChars(String string)
	{
		if (string == null)
	       string = "";
        StringBuffer result = new StringBuffer();
		for (int i = 0; i < string.length(); i++)
		{
			if (string.charAt(i) == '<')
			   result.append("&lt;");
            else if (string.charAt(i) == '>')
				 result.append("&gt;");
            else if (string.charAt(i) == '&')
				 result.append("&amp;");
            else
				result.append(string.charAt(i));
			}
		return result.toString();
	}

	/**
	 * Method used by jsp implementation for formatting
	 * @param string The string with the quotes converted
	 * @return
	 */
	public static String convertQuotes(String string)
	{
		if (string == null)
	       string = "";
        StringBuffer result = new StringBuffer();
		for (int i = 0; i < string.length(); i++)
		{
			if (string.charAt(i) == '"')
			   result.append("``");
            else if (string.charAt(i) == '\'')
				 result.append("`");
            else if ((string.charAt(i) == '\r') && (string.length() >= i + 1) && (string.charAt(i + 1) == '\n'))
			{
				i = i + 1;
				result.append("    ");
			}
			else if ((string.charAt(i) == '\r'))
			{
				result.append("    ");
			}
			else if ((string.charAt(i) == '\n'))
			{
				result.append("    ");
			}
			else
				result.append(string.charAt(i));
			}
		return result.toString();
	}
    /**
     * @return
     */
    public BrowserOperations getBrowserOperations() throws UnexpectedError
    {
        try
        {
            browserOperations.getHL7ReleaseVersion();
        }
        catch (Exception e)
        {
            // sql connection died... lets just get a new cts.
            browserOperations = BrowserOperationsImpl._interface();
            
        }
        
        return browserOperations;
    }

    /**
     * @return
     */
    public RuntimeOperations getRuntimeOperations() throws UnexpectedError
    {
        try
        {
            browserOperations.getHL7ReleaseVersion();
        }
        catch (Exception e)
        {
            // sql connection died... lets just get a new cts.
            runtimeOperations = RuntimeOperationsImpl._interface();
            
        }
        return runtimeOperations;
    }

}