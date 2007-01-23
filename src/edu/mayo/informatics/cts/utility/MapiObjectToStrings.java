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

import org.hl7.CTSMAPI.CTSVersionId;
import org.hl7.CTSMAPI.CodeSystemDescriptor;
import org.hl7.CTSMAPI.CodeSystemInfo;
import org.hl7.CTSMAPI.CodeSystemRegistration;
import org.hl7.CTSMAPI.ConceptId;
import org.hl7.CTSMAPI.FullValueSetDescription;
import org.hl7.CTSMAPI.RIMAttributeId;
import org.hl7.CTSMAPI.RIMCodedAttribute;
import org.hl7.CTSMAPI.ValidateCodeReturn;
import org.hl7.CTSMAPI.ValidationDetail;
import org.hl7.CTSMAPI.ValueSetCodeReference;
import org.hl7.CTSMAPI.ValueSetConstructor;
import org.hl7.CTSMAPI.ValueSetDescription;
import org.hl7.CTSMAPI.ValueSetDescriptor;
import org.hl7.CTSMAPI.ValueSetExpansion;
import org.hl7.CTSMAPI.VocabularyDomainDescription;
import org.hl7.CTSMAPI.VocabularyDomainValueSet;
import org.hl7.cts.types.BL;
import org.hl7.cts.types.CD;
import org.hl7.cts.types.CS;
import org.hl7.cts.types.ST;
import org.hl7.cts.types.UID;

/**
 * toString methods for the Mapi objects.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class MapiObjectToStrings
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

        if (object instanceof RIMAttributeId)
        {
            RIMAttributeId temp = (RIMAttributeId)object;
            result.append(indent + "RIMAttributeId" + lineReturn);
            result.append(indent + sp2 + "attribute name - " + temp.getAttribute_name().getV() + lineReturn);
            result.append(indent + sp2 + "model id - " + temp.getModel_id().getV() + lineReturn);
            result.append(indent + sp2 + "class name - " + temp.getClass_name().getV() + lineReturn);
        }

        else if (object instanceof CS)
        {
            CS temp = (CS)object;
            result.append(indent + "CS (Strength)" + lineReturn);
            result.append(indent + sp2 + "Code - " + temp.getCode() + lineReturn);
            result.append(indent + sp2 + "codingRationale - " + temp.getCodingRationale() + lineReturn);
            result.append(indent + sp2 + "OrigionalText:" + lineReturn);
            result.append(toString(temp.getOriginalText(), indent + sp4));
        }

        else if (object instanceof ConceptId)
        {
            ConceptId temp = (ConceptId)object;
            result.append(indent + "Concept ID" + lineReturn);
            result.append(indent + sp2 + "CodeSystemid:" + lineReturn);
            result.append(toString(temp.getCodeSystem_id(), indent + sp4));
            result.append(indent + sp2 + "Concept Code - " + temp.getConcept_code().getV() + lineReturn);
        }

        else if (object instanceof VocabularyDomainDescription)
        {
            VocabularyDomainDescription temp = (VocabularyDomainDescription)object;
            result.append(indent + "Vocabulary Domain Description" + lineReturn);
            result.append(indent + sp2 + "name - " + temp.getVocabularyDomain_name().getV() + lineReturn);
            result.append(indent + sp2 + "description - " + temp.getDescription().getV() + lineReturn);
            result.append(indent + sp2 + "restricts domain - " + temp.getRestrictsDomain_name().getV() + lineReturn);
            result.append(indent + sp2 + "Basis of Domains:" + lineReturn);
            result.append(toString(temp.getBasisOfDomains(), indent + sp4));
            result.append(indent + sp2 + "Constrains Attributes:" + lineReturn);
            result.append(toString(temp.getConstrainsAttributes(), indent + sp4));
            result.append(indent + sp2 + "Represented By Value Sets:" + lineReturn);
            result.append(toString(temp.getRepresentedByValueSets(), indent + sp4));
        }

        else if (object instanceof ValueSetDescription)
        {
            ValueSetDescription temp = (ValueSetDescription)object;
            result.append(indent + "ValueSetDescription" + lineReturn);
            result.append(indent + sp2 + "Based on code system: " + lineReturn);
            result.append(toString(temp.getBasedOnCodeSystem(), indent + sp4));
            result.append(indent + sp2 + "Defining Expression - " + temp.getDefiningExpression().getV() + lineReturn);
            result.append(indent + sp2 + "Description - " + temp.getDescription().getV() + lineReturn);
            result.append(indent + sp2 + "ID and Name:" + lineReturn);
            result.append(toString(temp.getIdAndName(), indent + sp4));
            result.append(indent + sp2 + "Head Code - " + temp.getHead_code().getV() + lineReturn);
            result.append(indent + sp2 + "Is all Codes - " + temp.getAllCodes().isV() + lineReturn);
        }

        else if (object instanceof FullValueSetDescription)
        {
            FullValueSetDescription temp = (FullValueSetDescription)object;
            result.append(indent + "FullValueSetDescription" + lineReturn);
            result.append(indent + sp2 + "Description:" + lineReturn);
            result.append(toString(temp.getDescription(), indent + sp4));
            result.append(indent + sp2 + "Constructed using code sets:" + lineReturn);
            result.append(toString(temp.getConstructedUsingValueSets(), indent + sp4));
            result.append(indent + sp2 + "Reference Codes:" + lineReturn);
            result.append(toString(temp.getReferencesCodes(), indent + sp4));
            result.append(indent + sp2 + "Used to define:" + lineReturn);
            result.append(toString(temp.getUsedToDefine(), indent + sp4));
        }

        else if (object instanceof ST)
        {
            ST temp = (ST)object;
            result.append(indent + "ST" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp.getV() + lineReturn);
        }
        
        else if (object instanceof String)
        {
            String temp = (String)object;
            result.append(indent + "String" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp + lineReturn);
        }

        else if (object instanceof CodeSystemDescriptor)
        {
            CodeSystemDescriptor temp = (CodeSystemDescriptor)object;
            result.append(indent + "CodeSystemDescriptor" + lineReturn);
            result.append(indent + sp2 + "AvailableReleases:" + lineReturn);
            result.append(toString(temp.getAvailableReleases(), indent + sp4));
            result.append(indent + sp2 + "CodeSystemId:" + lineReturn);
            result.append(toString(temp.getCodeSystem_id(), indent + sp4));
            result.append(indent + sp2 + "CodeSystemName - " + temp.getCodeSystem_name().getV() + lineReturn);
            result.append(indent + sp2 + "Copyright - " + (temp.getCopyright() == null ? "null" : temp.getCopyright().getV()) + lineReturn);

        }

        else if (object instanceof RIMCodedAttribute)
        {
            RIMCodedAttribute temp = (RIMCodedAttribute)object;
            result.append(indent + "RIMCodedAttribute" + lineReturn);
            result.append(indent + sp2 + "CodingStrengthCode - " + temp.getCodingStrength_code().getV() + lineReturn);
            result.append(indent + sp2 + "DataTypeCode - " + (temp.getDataType_code() == null ? null : temp.getDataType_code().getV())
                    + lineReturn);
            result.append(indent + sp2 + "VocabularyDomainName - " + temp.getVocabularyDomain_name().getV() + lineReturn);
            result.append(toString(temp.getRIMAttribute_id(), indent + sp4));
        }

        else if (object instanceof VocabularyDomainValueSet)
        {
            VocabularyDomainValueSet temp = (VocabularyDomainValueSet)object;
            result.append(indent + "VocabularyDomainValueSet" + lineReturn);
            result.append(indent + sp2 + "ApplicationContextCode - "
                    + (temp.getApplicationContext_code() == null ? "null" : temp.getApplicationContext_code().getV()) + lineReturn);
            result.append(indent + sp2 + "DefinedByValueSet - " + lineReturn);
            result.append(toString(temp.getDefinedByValueSet(), indent + sp4) + lineReturn);

        }

        else if (object instanceof ValueSetDescriptor)
        {
            ValueSetDescriptor temp = (ValueSetDescriptor)object;
            result.append(indent + "ValueSetDescriptor" + lineReturn);
            result.append(indent + sp2 + "ValueSetId - " + (temp.getValueSet_id() == null ? "null" : temp.getValueSet_id().getV())
                    + lineReturn);
            result.append(indent + sp2 + "ValueSetName - " + (temp.getValueSet_name() == null ? "null" : temp.getValueSet_name().getV())
                    + lineReturn);

        }

        else if (object instanceof ValueSetConstructor)
        {
            ValueSetConstructor temp = (ValueSetConstructor)object;
            result.append(indent + "ValueSetConstructor" + lineReturn);
            result.append(indent + sp2 + "IncludedHeadCode - "
                    + (temp.getIncludeHeadCode() == null ? "null" : temp.getIncludeHeadCode().isV() + "") + lineReturn);
            result.append(indent + "IncludedValueSet:" + lineReturn);
            result.append(toString(temp.getIncludedValueSet(), indent + sp4));

        }

        else if (object instanceof ValueSetCodeReference)
        {
            ValueSetCodeReference temp = (ValueSetCodeReference)object;
            result.append(indent + "ValueSetCodeReference" + lineReturn);
            result.append(indent + sp2 + "IncludedReferencedCode - "
                    + (temp.getIncludeReferencedCode() == null ? "null" : temp.getIncludeReferencedCode().isV() + "") + lineReturn);
            result.append(indent + sp2 + "Leaf Only - " + (temp.getLeafOnly() == null ? "null" : temp.getLeafOnly().isV() + "")
                    + lineReturn);
            result.append(indent + sp2 + "ReferencedCode - "
                    + (temp.getReferenced_code() == null ? "null" : temp.getReferenced_code().getV() + "") + lineReturn);
            result.append(indent + sp2 + "RelationshiopCode- "
                    + (temp.getRelationship_code() == null ? "null" : temp.getRelationship_code().getV() + "") + lineReturn);

        }

        else if (object instanceof ValueSetExpansion)
        {
            ValueSetExpansion temp = (ValueSetExpansion)object;
            result.append(indent + "ValueSetExpansion" + lineReturn);
            result.append(indent + sp2 + "ConceptId - " + lineReturn);
            result.append(toString(temp.getConcept_id(), indent + sp4));
            result.append(indent + sp2 + "DisplayName - " + (temp.getDisplayName() == null ? "null" : temp.getDisplayName().getV() + "")
                    + lineReturn);
            result.append(indent + sp2 + "Expansion Context - "
                    + (temp.getExpansionContext() == null ? "null" : new String(temp.getExpansionContext())) + lineReturn);
            result.append(indent + sp2 + "Is Expandable - "
                    + (temp.getIsExpandable() == null ? "null" : new String(temp.getIsExpandable().isV() + "")) + lineReturn);

            result.append(indent + sp2 + "Node Type Code - "
                    + (temp.getNodeType_code() == null ? "null" : new String(temp.getNodeType_code().getV() + "")) + lineReturn);

            result.append(indent + sp2 + "Path Length - "
                    + (temp.getPathLength() == null ? "null" : new String(temp.getPathLength().getV() + "")) + lineReturn);

            result.append(indent + sp2 + "Value Set - " + lineReturn);
            result.append(toString(temp.getValueSet(), indent + sp4));

        }

        else if (object instanceof ValidateCodeReturn)
        {
            ValidateCodeReturn temp = (ValidateCodeReturn)object;
            result.append(indent + "ValidateCodeReturn" + lineReturn);
            result.append(indent + sp2 + "Detail - " + lineReturn);
            result.append(toString(temp.getDetail(), indent + sp4));
            result.append(indent + sp2 + "NErrors - " + (temp.getNErrors() == null ? "null" : temp.getNErrors().getV() + "") + lineReturn);
            result.append(indent + sp2 + "NWarnings - "
                    + (temp.getNWarnings() == null ? "null" : new String(temp.getNWarnings().getV() + "")) + lineReturn);

        }

        else if (object instanceof CodeSystemInfo)
        {
            CodeSystemInfo temp = (CodeSystemInfo)object;
            result.append(indent + "CodeSystemInfo" + lineReturn);
            result.append(indent + sp2 + "Description - " + lineReturn);
            result.append(toString(temp.getDescription(), indent + sp4));
            result.append(indent + sp2 + "Registration Info - " + lineReturn);
            result.append(toString(temp.getRegistrationInfo(), indent + sp4));

        }

        else if (object instanceof CodeSystemRegistration)
        {
            CodeSystemRegistration temp = (CodeSystemRegistration)object;
            result.append(indent + "CodeSystemRegistration" + lineReturn);
            result.append(indent + sp2 + "CodeSystemTypeCode - "
                    + (temp.getCodeSystemType_code() == null ? "null" : new String(temp.getCodeSystemType_code().getV() + "")) + lineReturn);

            result.append(indent + sp2 + "inUMLS - " + (temp.getInUMLS() == null ? "null" : new String(temp.getInUMLS().isV() + ""))
                    + lineReturn);

            result.append(indent + sp2 + "Licensing Information - "
                    + (temp.getLicensingInformation() == null ? "null" : new String(temp.getLicensingInformation().getV() + ""))
                    + lineReturn);

            result.append(indent + sp2 + "Sponsor - " + (temp.getSponsor() == null ? "null" : new String(temp.getSponsor().getV() + ""))
                    + lineReturn);

            result.append(indent + sp2 + "SystemSpecificLocatorInfo - "
                    + (temp.getSystemSpecificLocatorInfo() == null ? "null" : new String(temp.getSystemSpecificLocatorInfo().getV() + ""))
                    + lineReturn);

            result.append(indent + sp2 + "VersionReportingMethod - "
                    + (temp.getVersionReportingMethod() == null ? "null" : new String(temp.getVersionReportingMethod().getV() + ""))
                    + lineReturn);

            result.append(indent + sp2 + "Publisher - "
                    + (temp.getPublisher() == null ? "null" : new String(temp.getPublisher().getV() + "")) + lineReturn);

        }

        else if (object instanceof ValidationDetail)
        {
            ValidationDetail temp = (ValidationDetail)object;
            result.append(indent + "ValidationDetail" + lineReturn);
            result.append(indent + sp2 + "Code In Error - " + lineReturn);
            result.append(toString(temp.getCodeInError(), indent + sp4));
            result.append(indent + sp2 + "Error Id - " + (temp.getError_id() == null ? "null" : temp.getError_id().getV() + "")
                    + lineReturn);
            result.append(indent + sp2 + "Error Text - "
                    + (temp.getErrorText() == null ? "null" : new String(temp.getErrorText().getV() + "")) + lineReturn);
            result.append(indent + sp2 + "Is Error - " + (temp.getIsError() == null ? "null" : new String(temp.getIsError().isV() + ""))
                    + lineReturn);

        }

        else if (object instanceof UID)
        {
            UID temp = (UID)object;
            result.append(indent + "UID" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp.getV() + lineReturn);
        }

        else if (object instanceof CTSVersionId)
        {
            CTSVersionId temp = (CTSVersionId)object;
            result.append(indent + "CTSVersionId" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp.getMajor().getV() + "." + temp.getMinor().getV() + lineReturn);
        }

        else if (object instanceof BL)
        {
            BL temp = (BL)object;
            result.append(indent + "BL" + lineReturn);
            result.append(indent + sp2 + "Value - " + temp.isV() + lineReturn);
        }

        else if (object instanceof CD)
        {
            CD temp = (CD)object;
            result.append(indent + "CD" + lineReturn);
            result.append(indent + sp2 + "CodeSystem - " + temp.getCodeSystem() + lineReturn);
            result.append(indent + sp2 + "Code - " + temp.getCode() + lineReturn);
            result.append(indent + sp2 + "CodeSystemName - " + temp.getCodeSystemName() + lineReturn);
            result.append(indent + sp2 + "CodeSystemVersion - " + temp.getCodeSystemVersion() + lineReturn);
            result.append(indent + sp2 + "DisplayName - " + temp.getDisplayName() + lineReturn);
            result.append(indent + sp2 + "CodingRationale - " + lineReturn);
            result.append(toString(temp.getCodingRationale(), indent + sp4));
            result.append(indent + sp2 + "OrigionalText - " + lineReturn);
            result.append(toString(temp.getOriginalText(), indent + sp4));
            result.append(indent + sp2 + "Qualifiers - " + lineReturn);
            result.append(toString(temp.getQualifiers(), indent + sp4));
            result.append(indent + sp2 + "Translation - " + lineReturn);
            result.append(toString(temp.getTranslation(), indent + sp4));
        }
        
//        else if (object instanceof ArrayOf_tns2_ST)
//        {
//            ArrayOf_tns2_ST temp = (ArrayOf_tns2_ST)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfValueSetConstructor)
//        {
//            ArrayOfValueSetConstructor temp = (ArrayOfValueSetConstructor)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfValueSetCodeReference)
//        {
//            ArrayOfValueSetCodeReference temp = (ArrayOfValueSetCodeReference)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfValueSetDescriptor)
//        {
//            ArrayOfValueSetDescriptor temp = (ArrayOfValueSetDescriptor)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfRIMCodedAttribute)
//        {
//            ArrayOfRIMCodedAttribute temp = (ArrayOfRIMCodedAttribute)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOfVocabularyDomainValueSet)
//        {
//            ArrayOfVocabularyDomainValueSet temp = (ArrayOfVocabularyDomainValueSet)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        else if (object instanceof ArrayOfValidationDetail)
//        {
//            ArrayOfValidationDetail temp = (ArrayOfValidationDetail)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOf_tns2_CR)
//        {
//            ArrayOf_tns2_CR temp = (ArrayOf_tns2_CR)object;
//            result.append(toString(temp.getItem(), indent + sp2));
//        }
//        
//        else if (object instanceof ArrayOf_tns2_CD)
//        {
//            ArrayOf_tns2_CD temp = (ArrayOf_tns2_CD)object;
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

        else if (object == null)
        {
            result.append(indent + "The object is null" + lineReturn);
        }

        else
        {
            result.append(indent + "Cannot format object type " + object.getClass() + lineReturn);
        }

        return result.toString();
    }

}