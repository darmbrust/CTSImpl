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

import org.hl7.CTSVAPI.*;

/**
 * toString methods for formatting the vapi objects
 * into text.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class VapiObjectToStrings
{
    private static String sp2 = "|_";
    private static String sp4 = "|_|_"; //with marks

    public static String toString(Object object, boolean printLines)
    {
        if (printLines)
        {
            sp2 = "|_";
            sp4 = sp2 + sp2;
        }
        else
        {
            sp2 = "  ";
            sp4 = sp2 + sp2;
        }
        return (toString(object, null, null));
    }
    
    public static String toString(Object object)
    {
        return (toString(object, null, null));
    }
    
    public static String toString(Object object, String indent)
    {
        return (toString(object, indent, null));
    }

    public static String toString(Object object, String indent, String lineReturn)
    {
        StringBuffer result = new StringBuffer();
        if (indent == null)
        {
            indent = "";
        }
        if (lineReturn == null || lineReturn.length() == 0)
        {
            lineReturn = System.getProperty("line.separator");
        }

        if (object instanceof RelatedCode)
        {
            RelatedCode temp = (RelatedCode)object;
            result.append(indent + "RelatedCode" + lineReturn);
            result.append(indent + sp2 + "Concept Code - " + temp.getConcept_code() + lineReturn);
            result.append(indent + sp2 + "Designation - " + temp.getDesignation() + lineReturn);
            result.append(indent + sp2 + "can expand - " + temp.isCanExpand() + lineReturn);
            result.append(indent + sp2 + "ExpansionContext - " + new String((temp.getExpansionContext() == null ? new byte[] {} :temp.getExpansionContext())) + lineReturn);
            result.append(indent + sp2 + "path length - " + temp.getPathLength() + lineReturn);
            result.append(indent + sp2 + "Relation Qualifiers - " + lineReturn);
            result.append(toString(temp.getRelationQualifiers(), indent + sp4));
        }

        else if (object instanceof CTSVersionId)
        {
            CTSVersionId temp = (CTSVersionId)object;
            result.append(indent + "CTSVersionId" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp.getMajor() + "." + temp.getMinor() + lineReturn);
        }

        else if (object instanceof String)
        {
            String temp = (String)object;
            result.append(indent + "String" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp + lineReturn);
        }

        else if (object instanceof CodeSystemIdAndVersions)
        {
            CodeSystemIdAndVersions temp = (CodeSystemIdAndVersions)object;
            result.append(indent + "CodeSystemIdAndVersions" + lineReturn);
            result.append(indent + sp2 + "Code System Id - " + temp.getCodeSystem_id() + lineReturn);
            result.append(indent + sp2 + "Code System name - " + temp.getCodeSystem_name() + lineReturn);
            result.append(indent + sp2 + "Code System Copyright - " + temp.getCopyright() + lineReturn);
            result.append(indent + sp2 + "Code System Versions - " + lineReturn);
            result.append(toString(temp.getCodeSystem_versions(), indent + sp4));
        }

        else if (object instanceof CompleteCodedConceptDescription)
        {
            CompleteCodedConceptDescription temp = (CompleteCodedConceptDescription)object;
            result.append(indent + "CompleteCodedConceptDescription" + lineReturn);
            result.append(indent + sp2 + "Code System version - " + temp.getCodeSystem_version() + lineReturn);
            result.append(indent + sp2 + "concept status - " + temp.getConceptStatus_code() + lineReturn);
            result.append(indent + sp2 + "Concept Id - " + lineReturn);
            result.append(toString(temp.getConcept_id(), indent + sp4));
            result.append(indent + sp2 + "Designated By - " + lineReturn);
            result.append(toString(temp.getDesignatedBy(), indent + sp4));
            result.append(indent + sp2 + "Has Properties - " + lineReturn);
            result.append(toString(temp.getHasProperties(), indent + sp4));
            result.append(indent + sp2 + "Source for - " + lineReturn);
            result.append(toString(temp.getSourceFor(), indent + sp4));
            result.append(indent + sp2 + "Target of - " + lineReturn);
            result.append(toString(temp.getTargetOf(), indent + sp4));
        }

        else if (object instanceof ConceptId)
        {
            ConceptId temp = (ConceptId)object;
            result.append(indent + "ConceptId" + lineReturn);
            result.append(indent + sp2 + "Code System Id - " + temp.getCodeSystem_id() + lineReturn);
            result.append(indent + sp2 + "concept code - " + temp.getConcept_code() + lineReturn);
        }

        else if (object instanceof ConceptDesignation)
        {
            ConceptDesignation temp = (ConceptDesignation)object;
            result.append(indent + "ConceptDesignation" + lineReturn);
            result.append(indent + sp2 + "Designation  - " + temp.getDesignation() + lineReturn);
            result.append(indent + sp2 + "language code - " + temp.getLanguage_code() + lineReturn);
            result.append(indent + sp2 + "isPreferred for Language - " + temp.isPreferredForLanguage() + lineReturn);
        }

        else if (object instanceof ConceptProperty)
        {
            ConceptProperty temp = (ConceptProperty)object;
            result.append(indent + "ConceptProperty" + lineReturn);
            result.append(indent + sp2 + "mime type  - " + temp.getMimeType_code() + lineReturn);
            result.append(indent + sp2 + "language code - " + temp.getLanguage_code() + lineReturn);
            result.append(indent + sp2 + "property  code - " + temp.getProperty_code() + lineReturn);
            result.append(indent + sp2 + "property value - " + temp.getPropertyValue() + lineReturn);
        }
        else if (object instanceof ConceptRelationship)
        {
            ConceptRelationship temp = (ConceptRelationship)object;
            result.append(indent + "ConceptRelationship" + lineReturn);
            result.append(indent + sp2 + "Relation Qualifiers - " + lineReturn);
            result.append(toString(temp.getRelationQualifiers(), indent + sp4));
            result.append(indent + sp2 + "relationship code - " + temp.getRelationship_code() +  lineReturn);
            result.append(indent + sp2 + "source concept id - " + lineReturn);
            result.append(toString(temp.getSourceConcept_id(), indent + sp4));
            result.append(indent + sp2 + "target concept id - " + lineReturn);
            result.append(toString(temp.getTargetConcept_id(), indent + sp4));

        }

        else if (object instanceof CodeSystemInfo)
        {
            CodeSystemInfo temp = (CodeSystemInfo)object;
            result.append(indent + "CodeSystemInfo" + lineReturn);
            result.append(indent + sp2 + "code system description  - " + temp.getCodeSystemDescription() + lineReturn);
            result.append(indent + sp2 + "full name - " + temp.getFullName() + lineReturn);
            result.append(indent + sp2 + "code system - " + lineReturn);
            result.append(toString(temp.getCodeSystem(), indent + sp4));
            result.append(indent + sp2 + "supported languages - " + lineReturn);
            result.append(toString(temp.getSupportedLanguages(), indent + sp4));
            result.append(indent + sp2 + "supported mime types - " + lineReturn);
            result.append(toString(temp.getSupportedMimeTypes(), indent + sp4));
            result.append(indent + sp2 + "supported properties - " + lineReturn);
            result.append(toString(temp.getSupportedProperties(), indent + sp4));
            result.append(indent + sp2 + "supported relation qualifiers - " + lineReturn);
            result.append(toString(temp.getSupportedRelationQualifiers(), indent + sp4));
            result.append(indent + sp2 + "supported relations - " + lineReturn);
            result.append(toString(temp.getSupportedRelations(), indent + sp4));
        }

        else if (object instanceof StringAndLanguage)
        {
            StringAndLanguage temp = (StringAndLanguage)object;
            result.append(indent + "StringAndLanguage" + lineReturn);
            result.append(indent + sp2 + "text  - " + temp.getText() + lineReturn);
            result.append(indent + sp2 + "language - " + temp.getLanguage_code() + lineReturn);

        }
        
//        else if (object instanceof ArrayOf_xsd_string)
//        {
//            ArrayOf_xsd_string temp = (ArrayOf_xsd_string) object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfConceptDesignation)
//        {
//            ArrayOfConceptDesignation temp = (ArrayOfConceptDesignation) object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfConceptProperty)
//        {
//            ArrayOfConceptProperty temp = (ArrayOfConceptProperty) object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfConceptRelationship)
//        {
//            ArrayOfConceptRelationship temp = (ArrayOfConceptRelationship) object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
        

        else if (object instanceof Object[])
        {
            Object[] temp = (Object[])object;
            String name = object.getClass().getName();
            name = name.substring(name.lastIndexOf('.') + 1, name.length() - 1);
            result.append(indent + name + " Array" + lineReturn);
            for (int i = 0; i < temp.length; i++)
            {
                result.append(toString(temp[i], indent + sp2));
            }
        }

        else if (object instanceof Boolean)
        {
            Boolean temp = (Boolean)object;
            result.append(indent + "Boolean" + lineReturn);
            result.append(indent + sp2 + "value - " + temp.toString() + lineReturn);
        }

        else
        {
            if (object == null)
                result.append(indent + sp2 + "null" + lineReturn);
            else
            {
                result.append("Unable to properly format result" + lineReturn);
                result.append(indent + sp2 + object.toString() + lineReturn);
            }
        }
        return result.toString();
    }
}