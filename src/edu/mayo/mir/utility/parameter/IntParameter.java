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

import java.io.*;

/**
 * An application parameter of type integer.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class IntParameter implements Serializable
{
	private static final long serialVersionUID = -9019153843810762475L;
	private int value;// = null;
    String usage;

    public IntParameter(int value)
    {
        this.value = value;
        usage = "";
    }

    public IntParameter(String value)
    {
        this.value = Integer.parseInt(value);
        usage = "";
    }


    public int getValue()
    {
        return value;
    }

    public int intValue()
    {
        return getValue();
    }


    public void setValue(int value)
    {
        this.value = value;
    }

    public void increment()
    {
        this.value++;
    }

    public void decrement()
    {
        this.value--;
    }

    public String toString()
    {
        return String.valueOf(value);
    }

    public String getUsage()
    {
        return usage;
    }
    public void setUsage(String usage)
    {
        this.usage=usage;
    }


}