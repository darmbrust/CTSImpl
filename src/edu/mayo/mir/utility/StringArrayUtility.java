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

import java.io.*;
import java.util.*;

/**
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class StringArrayUtility {
	public static boolean DEBUG = false;

	public static void print(String[] array)
	{	print(array, System.out);
	}

	public static void print(String[][] array)
	{	print(array, System.out);
	}

	public static void print(String[] array, PrintStream out)
	{
		if(array == null)
		{
			out.println("null");
			return;
		}
		for(int i=0; i< array.length; i++)
			out.print(array[i]+"|");
		out.println("");
	}

	public static void print(String[][] array, PrintStream out)
	{
		for(int i=0; i<array.length; i++)
			print(array[i], out);
	}

	public static String[] removeEmpties(String[] array)
	{
		if(array == null) return new String[0];
		ArrayList returnValues = new ArrayList(array.length);
		for (int i = 0; i < array.length; i++)
		{
			if(array[i] != null && !array[i].equals(""))
			    returnValues.add(array[i]);
		}
		return (String[])(returnValues.toArray(new String[returnValues.size()]));
	}

/**
Remove Strings that are in list2 from list1.  Strings in list2 that do not exist in list1 have
no effect in this method.

@param list1 List that is being subtracted from
@param list2 List of unwanted strings

@return method returns all strings in list1 except those strings that are in both list2 and list1.
*/
	public static String[] subtract(String[] list1,
									 String[] list2)
	{	ArrayList diff = new ArrayList(list1.length);
		boolean add = true;
		for(int i=0; i<list1.length; i++)
		{	for(int j=0; j<list2.length; j++)
			{	if(list1[i].equals(list2[j]))
				{	add = false;
					break;
				}
			}
			if(add)
				diff.add(list1[i]);
			add = true;
		}

		return arrayListToStringArray(diff);
	}

	public static String[] getIntersection(String[] list1,
										   String[] list2)
	{	ArrayList intersection = new ArrayList();
		for(int i=0; i< list1.length; i++)
		{	if(contains(list1[i], list2))
				intersection.add(list1[i]);
		}

		return arrayListToStringArray(intersection);
	}


	/**
	This method finds all of the Strings in list2 that do not currently exist in list1
	and adds them to an array of Strings whose first list1.length Strings are the
	Strings in list1.  WARNING: This method does not check that list1 has a list
	of unique Strings, however repeats in list2 will be discarded.
	*/
	public static String[] getUnion(String[] list1,
									String[] list2)
	{	ArrayList addThese = new ArrayList(list1.length+list2.length);
		for(int i=0; i< list2.length; i++)
		{	if(!contains(list2[i], list1))
				addThese.add(list2[i]);
		}
		String[] adds = arrayListToStringArray(addThese);

		return concatenate(list1,adds);
	}

	/** this method takes out all of the duplicate strings.  This method
	is case sensitive.
	*/
	public static String[] getDistinctStrings(String[] list)
	{	ArrayList distincts = new ArrayList(list.length);
		for(int i=0; i<list.length; i++)
		{	if(!distincts.contains(list[i]))
			{	distincts.add(list[i]);
			}
		}
		return toArray(distincts);
	}

	public static String[] getEmptyArray(int size)
	{
		String[] strings = new String[size];
		for(int i=0; i< strings.length; i++)
		{	strings[i] = "";
		}
		return strings;
	}

	public static String[] concatenate(String[] list1, String[] list2)
	{
		if(list1 == null && list2 == null) return new String[0];
		if(list1 == null) return list2;
		if(list2 == null) return list1;
		String[] sumList = new String[list1.length + list2.length];
		System.arraycopy(list1, 0, sumList, 0, list1.length);
		System.arraycopy(list2, 0, sumList, list1.length, list2.length);
		return sumList;
	}

	public static String[] concatenate(String[][] lists)
	{	String[] concatenation = new String[0];
		for(int i=0; i< lists.length; i++)
			concatenation = StringArrayUtility.concatenate(concatenation,lists[i]);
		return concatenation;
	}



/** Returns the input Strings in random order.
*/

	public static String[] randomize(String[] orderedList)
	{
		ArrayList list = stringArrayToArrayList(orderedList);
		Random random = new Random(System.currentTimeMillis());
		ArrayList randomList = new ArrayList(orderedList.length);
		System.out.println("list.size():"+list.size());
		int index;
		while(list.size()>0)
		{	index = random.nextInt(list.size());
			randomList.add(list.get(index));
			list.remove(index);
		}
		System.out.println("randomList.size():"+randomList.size());
		return (String[])(randomList.toArray(new String[randomList.size()]));
	}

/**Returns a subset of the list supplied.  The items in the
returned are non-repeated.
@param count the number of Strings you want returned in the subset
*/

	public static String[] randomSubset(String[] list, int count)
	{   if(list.length < count)
			return null;

		Vector vList = new Vector();
		for(int i=0; i<list.length; i++)
		{	vList.addElement(list[i]);
		}

		Random random = new Random(System.currentTimeMillis());

		String RandomItems[] = new String[count];
		int index;
		for(int i=0; i < count; i++)
		{	index = random.nextInt(vList.size());
			RandomItems[i] = (String)(vList.elementAt(index));
			vList.removeElementAt(index);
		}

		return RandomItems;

	}

/** determines whether a String is in the String array
	The code is very simplistic.
	<pre>
		public static boolean contains(String string, String[] array)
		{	for(int i=0; i<array.length; i++)
				if(string.equals(array[i]))
					return true;
			return false;
		}
	</pre>
*/

	public static boolean contains(String string, String[] array)
	{	if(array == null) return false;
		for(int i=0; i<array.length; i++)
			if(string.equals(array[i]))
				return true;
		return false;
	}

/** this function returns a string that contains all the strings in the array
delimited by the token.
<br>Usage:<br>
<pre>
 	public static String[] myArray=new String[]
		{"Never","turn","your","back","on","your","terminology"};

	String string = StringArrayUtility.tokenizeStringArray(myArray,", ");
	System.out.println("string="+string);

	output: string=Never, turn, your, back, on, your, terminology
</pre>
*/

	public static String tokenizeStringArray(String[] array, String token)
	{	if(array.equals(null) || token.equals(null))
		{		return "";
		}
		if(array.length > 0 && !array[0].equals(null))
		{	//System.out.println("array.length:"+array.length);
			//System.out.println("array[0]="+array[0]);
			StringBuffer string_buffer = new StringBuffer(array[0]);
			for(int i=1; i<array.length; i++)
			{//	System.out.println("array["+i+"]= "+array[i]);
				if(!array[i].equals(null) && !array[i].equals(""))
				{	string_buffer.append(token);
					string_buffer.append(array[i]);
				}
			}
			//System.out.println("returning: "+string_buffer.toString());
			return string_buffer.toString().trim();
		}
		//System.out.println("returning nothing (at end)");
		return "";
	}

    public static String stringArrayToString(String[] string)
    {
        if (string == null)
            return "Null";

        StringBuffer foo = new StringBuffer();
        for (int i = 0; i < string.length; i++)
        {
            foo.append(string[i]);
            if (i != string.length - 1)
                foo.append("|");
        }
        return foo.toString();
    }

	public static String[] vectorToStringArray(Vector vector)
	{	if(vector == null)
			return new String[0];

		String[] stringArray = new String[vector.size()];

		for(int i=0; i<vector.size(); i++)
			stringArray[i] = vector.elementAt(i).toString();

		return stringArray;
	}

/** Put in a string array and get back a Vector!*/

	public static Vector stringArrayToVector(String[] array)
	{	if(array == null)
			return new Vector(0);

		Vector vector = new Vector(array.length);
		for(int i=0; i<array.length; i++)
		{	vector.addElement(array[i]);
		}
		return vector;
	}


	/**
	 * This method is here because my first implementation used the brute force approach.
	 * Then I realized that you could accomplish this in one line of code - and it is
	 * more efficient because it uses System.arraycopy().
	 */
	public static String[] arrayListToStringArray(ArrayList arrayList)
	{	if(arrayList == null)
			return new String[0];
		return (String[])(arrayList.toArray(new String[arrayList.size()]));
	}

	public static String[] toArray(ArrayList arrayList)
	{	return arrayListToStringArray(arrayList);
	}


/** Put in a string array and get back a ArrayList*/

//public static List asList(Object[] a)

	public static ArrayList stringArrayToArrayList(String[] array)
	{	if(array == null)
			return new ArrayList(0);

		ArrayList arrayList = new ArrayList(array.length);
		for(int i=0; i<array.length; i++)
		{	arrayList.add(array[i]);
		}
		return arrayList;
	}


	public static String[] alphabetizeIgnoreCase(String[] strings){
	   	if(strings.length > 0){
	   		Arrays.sort(strings, new IgnoreCaseStringComparator());
	        return strings;
	   	}
	   	return new String[0];
	}

	public static String[] sortIntegers(String[] strings)
	{	if(strings.length > 0)
		{	ArrayList integers = new ArrayList(strings.length);
			ArrayList nonIntegers = new ArrayList(strings.length);
			for(int i=0; i<strings.length; i++)
	      	{	try{
	      			Integer integer = new Integer(strings[i]);
	      			integers.add(integer);
	      		}
	      		catch(NumberFormatException nfe)
	      		{	nonIntegers.add(strings[i]);
	      		}
	      	}

	      	Collections.sort((List)integers);

	      	for(int i=0; i<integers.size(); i++)
	      	{	String string = (integers.get(i)).toString();
	      	 	integers.set(i,string);
	      	}

	      	String[] stringIntegers = arrayListToStringArray(integers);
			String[] stringNonIntegers = arrayListToStringArray(nonIntegers);
			return concatenate(stringIntegers, stringNonIntegers);
	   }
	   return new String[0];
	}


	public static BitSet findChars(String[] strings,char[] chars)
	{	BitSet delimiters = new BitSet(strings.length);
		for(int i=0; i< strings.length; i++)
		{	//System.out.println("strings["+i+"] "+ strings[i]);
			for(int j=0; j< chars.length; j++)
			{	//System.out.println("chars["+j+"] "+ chars[j]);
				if(strings[i].indexOf(chars[j]) != -1)
				{	//System.out.println("setting delimiters: "+i);
					delimiters.set(i);
					j=chars.length;
				}
			}
		}
		return delimiters;
	}

	/**
	 *
	 */
//	static public void main(String[] args)
//	{
//	    try
//		{
//			if(args[0].equals("-r"))
//			{
//				String[] randomStrings = randomize(FileUtility.toArray(new File(args[1])));
//				File randomFile = new File(args[1]+".random");
//				randomFile.createNewFile();
//				FileUtility.saveToFile(randomStrings, randomFile);
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}