package org.hl7.baseTests.VAPI;

import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.ConceptRelationship;


/**
 * Class to hold static utilty methods used by the JUnit Tests.
 * 
 * <pre>
 * Copyright (c) 2005 Mayo Foundation. All rights reserved.
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.1 $ checked in on $Date: 2005/07/07 20:42:36 $
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
