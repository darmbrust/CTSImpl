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

import org.hl7.cts.types.ST;

/**
 * A helper class for constructing annoying but often used data types..
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class Constructors
{
	public static org.hl7.cts.types.ST stm(String stV)
	{
		org.hl7.cts.types.ST st = new org.hl7.cts.types.ST();
		st.setV(stV); 
		return st;
	}
    
    public static ST[] stArray(String[] stV)
    {
        org.hl7.cts.types.ST[] temp = new org.hl7.cts.types.ST[stV.length];
        for (int i = 0; i < stV.length; i++)
        {
            temp[i] = Constructors.stm(stV[i]);
        }
        return temp;
    }

	public static org.hl7.cts.types.INT intfm(int  intV)
	{
        org.hl7.cts.types.INT intf = new org.hl7.cts.types.INT();
		intf.setV(intV);
		return intf;
	}


	public static org.hl7.cts.types.BL blm(boolean blV)
	{
        org.hl7.cts.types.BL bl = new org.hl7.cts.types.BL();
		bl.setV(blV);
		return bl;
	}

	public static org.hl7.cts.types.UID uidm(String uidV)
	{
        org.hl7.cts.types.UID uid = new org.hl7.cts.types.UID();
		uid.setV(uidV);
		return uid;
	}
    
    public static org.hl7.CTSMAPI.CodeSystemDescriptor codeSystemDescriptor(String[] availableReleases, org.hl7.cts.types.UID codeSystemId, String codeSystemName, String copyright)
    {
        org.hl7.CTSMAPI.CodeSystemDescriptor temp = new org.hl7.CTSMAPI.CodeSystemDescriptor();
        temp.setAvailableReleases(Constructors.stArray(availableReleases));
        temp.setCodeSystem_id(codeSystemId);
        temp.setCodeSystem_name(Constructors.stm(codeSystemName));
        temp.setCopyright(Constructors.stm(copyright));
        return temp;
    }
    
    public static org.hl7.CTSMAPI.CTSVersionId ctsVersion(int major, int minor)
    {
        org.hl7.CTSMAPI.CTSVersionId ctsVersion = new org.hl7.CTSMAPI.CTSVersionId();
        ctsVersion.setMajor(intfm(major));
        ctsVersion.setMinor(intfm(minor));
        return ctsVersion;
        
    }

	public static org.hl7.CTSMAPI.ValueSetDescriptor valueSetDescriptor(String valueSetId, String valueSetName)
	{
        org.hl7.CTSMAPI.ValueSetDescriptor valueSetIdentifier = new org.hl7.CTSMAPI.ValueSetDescriptor();
		valueSetIdentifier.setValueSet_name(Constructors.stm(valueSetName));
		valueSetIdentifier.setValueSet_id(Constructors.uidm(valueSetId));
		return valueSetIdentifier;
	}

	public static org.hl7.CTSMAPI.ConceptId conceptIdm(String codeSystem, String conceptCode)
	{
		org.hl7.CTSMAPI.ConceptId conceptId = new org.hl7.CTSMAPI.ConceptId();
		conceptId.setCodeSystem_id(uidm(codeSystem));
		conceptId.setConcept_code(stm(conceptCode));
		return conceptId;
	}
}