package org.hl7.utility;

import org.hl7.CTSMAPI.BrowserOperations;
import org.hl7.CTSMAPI.RuntimeOperations;
import org.hl7.CTSMAPI.UnexpectedError;
import org.hl7.CTSMAPI.refImpl.BrowserOperationsImpl;
import org.hl7.CTSMAPI.refImpl.RuntimeOperationsImpl;
import org.hl7.cts.types.ST;

import edu.mayo.informatics.cts.utility.CTSConfigurator;

/**
 * <pre>
 * Title:        JSPUtility
 * Description:  Some utility methods for the JSP Implementation.
 * Copyright: (c) 2002, 2003, 2004 Mayo Foundation. All rights reserved.
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
 * <H5> Bugs, to be done, notes, etc.</H5>
 * <UL>
 *      <LI>There are still several todo items in this class.</li>
 * </UL>
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.9 $ checked in on $Date: 2005/10/14 15:44:10 $
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