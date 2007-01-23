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
package edu.mayo.informatics.cts.baseTests.VAPI;

import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.ConceptRelationship;

/**
 * Class to hold static utilty methods used by the JUnit Tests.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class TestUtility
{
    /*
     * Determine if two arrays are equal (all items the same, length the same, but the items can be in a different
     * order)
     */
    public static boolean ConceptIdArraysEqual(ConceptId[] array1, ConceptId[] array2)
    {
        if (array1.length != array2.length)
        {
            return false;
        }
        for (int i = 0; i < array1.length; i++)
        {
            boolean found = false;
            for (int j = 0; j < array2.length; j++)
            {
                if (array1[i].getCodeSystem_id().equals(array2[j].getCodeSystem_id()) && array1[i].getConcept_code().equals(array2[j].getConcept_code()))
                {
                    found = true;
                }
            }
            //if we didn't find one of the elements, return false.
            if (!found)
            {
                return false;
            }
        }
        
        //if we got here without returning false, it means we found every element.  return true.
        return true;
    }
    
    /*
     * Determine if two arrays are equal (all items the same, length the same, but the items can be in a different
     * order)
     */
    public static boolean ConceptDesignationArraysEqual(ConceptDesignation[] array1, ConceptDesignation[] array2)
    {
        if (array1.length != array2.length)
        {
            return false;
        }
        for (int i = 0; i < array1.length; i++)
        {
            boolean found = false;
            for (int j = 0; j < array2.length; j++)
            {
                if (array1[i].getDesignation().equals(array2[j].getDesignation()) && array1[i].getLanguage_code().equals(array2[j].getLanguage_code()))
                {
                    found = true;
                }
            }
            //if we didn't find one of the elements, return false.
            if (!found)
            {
                return false;
            }
        }
        
        //if we got here without returning false, it means we found every element.  return true.
        return true;
    }
    
    /*
     * Determine if two arrays are equal (all items the same, length the same, but the items can be in a different
     * order)
     */
    public static boolean ConceptPropertyArraysEqual(ConceptProperty[] array1, ConceptProperty[] array2)
    {
        if (array1.length != array2.length)
        {
            return false;
        }
        for (int i = 0; i < array1.length; i++)
        {
            boolean found = false;
            for (int j = 0; j < array2.length; j++)
            {
                if ((array1[i].getLanguage_code() == null && array2[j].getLanguage_code() == null) || (array1[i].getLanguage_code()!= null && array1[i].getLanguage_code().equals(array2[j].getLanguage_code()))
                        && (array1[i].getMimeType_code() == null && array2[j].getMimeType_code() == null) || (array1[i].getMimeType_code()!= null && array1[i].getMimeType_code().equals(array2[j].getMimeType_code()))
                        && (array1[i].getProperty_code() == null && array2[j].getProperty_code() == null) || (array1[i].getProperty_code()!= null && array1[i].getProperty_code().equals(array2[j].getProperty_code()))
                        && (array1[i].getPropertyValue() == null && array2[j].getPropertyValue() == null) || (array1[i].getPropertyValue()!= null && array1[i].getPropertyValue().equals(array2[j].getPropertyValue())))
                {
                    found = true;
                }
            }
            //if we didn't find one of the elements, return false.
            if (!found)
            {
                return false;
            }
        }
        
        //if we got here without returning false, it means we found every element.  return true.
        return true;
    }
    
    public static boolean conceptIdInArray(ConceptRelationship[] acr, ConceptRelationship conceptRelationship)
    {
        for (int i = 0; i < acr.length; i++)
        {
            //TODO verify qualifiers
            if (acr[i].getSourceConcept_id().getCodeSystem_id().equals(conceptRelationship.getSourceConcept_id().getCodeSystem_id()) && 
                acr[i].getSourceConcept_id().getConcept_code().equals(conceptRelationship.getSourceConcept_id().getConcept_code()) &&
                acr[i].getRelationship_code().equals(conceptRelationship.getRelationship_code()) &&
                acr[i].getTargetConcept_id().getCodeSystem_id().equals(conceptRelationship.getTargetConcept_id().getCodeSystem_id()) && 
                acr[i].getTargetConcept_id().getConcept_code().equals(conceptRelationship.getTargetConcept_id().getConcept_code()))
            {
                return true;
            }
        }
        return false;
    }
    
    /*
     * Determine if two arrays are equal (all items the same, length the same, but the items can be in a different
     * order)
     */
    public static boolean stringArraysEqual(String[] array1, String[] array2)
    {
        if (array1.length != array2.length)
        {
            return false;
        }
        for (int i = 0; i < array1.length; i++)
        {
            boolean found = false;
            for (int j = 0; j < array2.length; j++)
            {
                if (array1[i].equals(array2[j]))
                {
                    found = true;
                }
            }
            //if we didn't find one of the elements, return false.
            if (!found)
            {
                return false;
            }
        }
        
        //if we got here without returning false, it means we found every element.  return true.
        return true;
    }
}