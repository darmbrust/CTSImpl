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

/**
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
import java.io.*;
import java.util.*;

import edu.mayo.mir.utility.parameter.*;


public class WordUtility
{
	public static boolean DEBUG = false;
	public static boolean SHOW_TIMES = false;

	/** The value of WORD_PATTERN_LOOSE is "\\S+". This pattern delimits words based on white space*/
	public static final String WORD_PATTERN_LOOSE = "\\S+";

	/** The value of WORD_PATTERN_STRICT is "\\w+".  This pattern delimits words based on non-word characters.
	Word characters are letters and digits.*/
	public static final String WORD_PATTERN_STRICT = "\\w+";

	/** The value of WORD_PATTERN_STRICT_UNDERSCORES is "[\\w_]+".  Same as WORD_PATTERN_STRICT except that
	the underscore '_' is considered a word character. */
	public static final String WORD_PATTERN_STRICT_UNDERSCORES = "[\\w_]+";

    public static final String LINE_PATTERN = "[^\r\n]+";

//	/** uses gnu.regexp.RE.  This method is much faster for large InputStreams.
//     *
//     * gnu.regexp has a nasty bug that causs this to go into an infinite loop if you pass it a file input stream
//     * and there is no match at the end of the file.  It appears to work with other types of input streams.
//     *
//     * Maybe we can move up to jdk1.4 fairly soon, and move to their regularexpression package.  I reported this
//     * bug, but the gnu.regexp project appears to be dead.
//     */
//	public static Iterator getWords(InputStream inputStream, String wordPattern) throws gnu.regexp.REException
//	{
//		gnu.regexp.RE regularExpression = new gnu.regexp.RE(wordPattern);
//		gnu.regexp.REMatchEnumeration enumeration = regularExpression.getMatchEnumeration(inputStream);
//		return new edu.mayo.mir.utility.REIterator(enumeration);
//	}
//
//	public static Iterator getWordsRE(String input, String wordPattern) throws gnu.regexp.REException
//	{
//		gnu.regexp.RE regularExpression = new gnu.regexp.RE(wordPattern);
//		gnu.regexp.REMatchEnumeration enumeration = regularExpression.getMatchEnumeration(input);
//		return new edu.mayo.mir.utility.REIterator(enumeration);
//	}
    
/**
returns the number of words contained in string.  Words are seperated by a
single space with no accounting for any extra white space.
Uses StringTokenizer
*/
	public static int numberOfWords(String string)
	{   StringTokenizer st = new StringTokenizer(string);
    	return st.countTokens();
	}

/** returns the number of words contained in string.  All tokens found in string
are replaced with spaces.  Words are seperated by a single space with no accounting
for any extra white space.
Uses StringTokenizer
*/

	public static int numberOfWords(String string, String delimiter)
	{	StringTokenizer st = new StringTokenizer(string, delimiter);
		return st.countTokens();
	}

/**
returns the index (word count) of word in the string.  If word is not in the string then it
returns -1.  If word exists inside string that does not mean that it is considered a 'word'
in the string.
Uses StringTokenizer
*/

   	public static int indexOfWord(String word, String string){
    	word = word.toLowerCase().trim();
		String wordInString;

    	StringTokenizer st = new StringTokenizer(string);
    	for(int i=0; st.hasMoreElements(); i++)
     	{   wordInString = st.nextToken().toLowerCase().trim();
     		if(word.equals(wordInString))
     			return i;
     	}

     	return -1;
	}

/**
Uses StringTokenizer
*/
   	public static String[] getWords(String string){
		StringTokenizer st = new StringTokenizer(string);
     	String[] rvals = new String[st.countTokens()];
     	int i = 0;

     	while (st.hasMoreElements())
            rvals[i++] = st.nextToken();
     	return rvals;
   	}

/**
Uses StringTokenizer
*/

   	public static String[] getWords(String string, String delimiter)
   	{	StringTokenizer st = new StringTokenizer(string, delimiter);
     	String[] rvals = new String[st.countTokens()];
     	int i = 0;

     	while (st.hasMoreElements())
            rvals[i++] = st.nextToken();
     	return rvals;
	}

	public static String alphabetizeWords(String phrase)
	{
		String[] words = getWords(phrase);
		words = StringArrayUtility.alphabetizeIgnoreCase(words);
		return StringArrayUtility.tokenizeStringArray(words, " ");
	}

   public static void main(String[] args)
   {
   		try
		{	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			while(true)
			{	String string = bufferedReader.readLine();
				String[] words = WordUtility.getWords(string);
				for(int i=0; i< words.length; i++)
				{System.out.println(words[i]);
				}
				System.out.println("");
			}
		}
		catch(Exception e){}
	}

   	/** returns the word within a String at a given position.  For example if
   	string="Hello there" and position = new IntegerHolder(7) than this method
   	will return "there" and position will be set to 6.  This method is not sophisticated
   	when it comes to breaking the String up into words - it only looks for spaces.
   	@param string - The String that we are getting the word from
   	@param position - the position of a character within the String
   	@return the word that contains the position.  If the character at position
   	is a space then "" is returned.

	Uses StringTokenizer

   	*/

   	public static String getWordAt(String string, IntParameter position)
   	{	
		if(position.intValue() >= string.length())
		{	position.setValue(-1);
			return "";
		}
		if(position.intValue() < 0)
		{	position.setValue(-1);
			return "";
		}

		int pos1 = string.lastIndexOf(" ",position.intValue());
		//System.out.println("pos1 = "+pos1);
		int pos2 = string.indexOf(" ",position.intValue());
		//System.out.println("pos2 = "+pos2);
		if(pos1 == -1 && pos2 == -1 && string.length() > 0 && position.intValue() < string.length())
		{	position.setValue(0);
			return string;
		}
		else if(pos1 == pos2)
		{	position.setValue(-1);
			return "";
		}
		else if(pos1 == -1)
		{	position.setValue(0);
			return string.substring(0,pos2);
		}
		else if(pos2 == -1)
		{	position.setValue(++pos1);
			return string.substring(pos1, string.length());
		}
		else
		{	position.setValue(++pos1);
			return string.substring(pos1,pos2);
		}
	}

}