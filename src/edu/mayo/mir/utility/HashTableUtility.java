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

import java.util.*;

/**
 * For printing out the keys and values of a hash table.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class HashTableUtility
{

    /**
     * This method is for printing out a hashtable (keys and values).  It works best if the keys and values are strings.
     * @param hashTable - The hashtable to print
     * @return - The String representing the hashtable.
     */
    public static String hashTableToString(Hashtable hashTable)
    {
        if (hashTable == null)
            return "Null";
        StringBuffer foo = new StringBuffer();
        Enumeration enumeration = hashTable.keys();
        while (enumeration.hasMoreElements())
        {
            Object current = enumeration.nextElement();
            foo.append(current.toString() + ":" + hashTable.get(current) + System.getProperty("line.separator"));
        }
        return foo.toString();
    }

    /**
     * This method is for printing out a hashtable (keys and values).  It assumes that the keys are strings.
     * @param hashTable - The hashtable to print
     * @param keysToObscure - If one of the keys is sensitive, like a password, you can have it not be printed.
     * @return - The String representing the hashtable.
     */
    public static String hashTableToString(Hashtable hashTable, String[] keysToObscure)
    {
        if (hashTable == null)
            return "Null";

        if (keysToObscure == null)
            keysToObscure = new String[]{};

        StringBuffer foo = new StringBuffer();
        Enumeration enumeration = hashTable.keys();
        boolean obscure = false;
        while (enumeration.hasMoreElements())
        {
            Object current = enumeration.nextElement();
            for (int i = 0; i < keysToObscure.length; i++)
            {
                if (current.toString().equals(keysToObscure[i]))
                    obscure = true;
            }
            if (obscure)
            {
                foo.append(current.toString() + ":" + "**********" + System.getProperty("line.separator"));
                obscure = false;  //set for next time
            }
            else
                foo.append(current.toString() + ":" + hashTable.get(current) + System.getProperty("line.separator"));
        }
        return foo.toString();
    }
}