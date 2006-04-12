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
package edu.mayo.informatics.cts.utility.lexGrid;

/**
 *  All of the constants used to talk with the LDAP server.
 *  
 * @author  <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class SchemaConstants {
    //Dans additions again
    public static String dc = "dc";
    public static String Relations = "Relations";
    public static String Versions = "Versions";
    public static String Concepts = "Concepts";
    public static String translationAssociation = "translationAssociation";
    public static String valueDomains = "valueDomains";

    //These were all generated from the LDAP schema files.
    // objectclass (2.5.4.41) - core.schema
    public static String name = "name";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.15)  - TerminologyServiceObjects.schema
    public static String associatedConcept = "associatedConcept";

    // objectclass (1.3.6.1.4.1.2114.108.1.6.46)  - TerminologyServiceAttributes.schema
    public static String approxNumConcepts = "approxNumConcepts";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.16)  - TerminologyServiceObjects.schema
    public static String associatedFacet = "associatedFacet";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.12)  - TerminologyServiceObjects.schema
    public static String association = "association";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.12)  - TerminologyServiceAttributes.schema
    public static String associationId = "associationId";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.13)  - TerminologyServiceObjects.schema
    public static String associationInstance = "associationInstance";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.10)  - TerminologyServiceObjects.schema
    public static String associationLink = "associationLink";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.41)  - TerminologyServiceAttributes.schema
    public static String associationQualifier = "associationQualifier";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.4)  - TerminologyServiceObjects.schema
    public static String associationQualifierLink = "associationQualifierLink";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.14)  - TerminologyServiceObjects.schema
    public static String associationTarget = "associationTarget";

    // objectclass (1.3.6.1.4.1.2114.108.1.5.5)  - NamingAuthority.schema
    public static String authorityId = "authorityId";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.8)  - TerminologyServiceObjects.schema
    public static String binaryPresentation = "binaryPresentation";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.28)  - TerminologyServiceAttributes.schema
    public static String blob = "blob";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.13)  - TerminologyServiceObjects.schema
    public static String codedEntry = "codedEntry";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.2)  - TerminologyServiceObjects.schema
    public static String codingScheme = "codingScheme";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.4)  - TerminologyServiceAttributes.schema
    public static String codingSchemeId = "codingSchemeId";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.9)  - TerminologyServiceObjects.schema
    public static String codingSchemeLink = "codingSchemeLink";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.3)  - TerminologyServiceObjects.schema
    public static String codingSchemeVersion = "codingSchemeVersion";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.12)  - TerminologyServiceObjects.schema
    public static String codingSchemeVersionLink = "codingSchemeVersionLink";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.5)  - TerminologyServiceObjects.schema
    public static String comment = "comment";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.6)  - TerminologyServiceAttributes.schema
    public static String conceptCode = "conceptCode";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.11)  - TerminologyServiceObjects.schema
    public static String conceptLink = "conceptLink";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.39)  - TerminologyServiceAttributes.schema
    public static String defaultCodingScheme = "defaultCodingScheme";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.11)  - TerminologyServiceAttributes.schema
    public static String defaultLanguage = "defaultLanguage";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.4)  - TerminologyServiceObjects.schema
    public static String definition = "definition";

    // objectclass (1.3.6.1.4.1.2114.108.1.6.44)  - TerminologyServiceAttributes.schema
    public static String defaultVersion = "defaultVersion";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.29)  - TerminologyServiceAttributes.schema
    public static String degreeOfFidelity = "degreeOfFidelity";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.1)  - TerminologyServiceObjects.schema
    public static String describable = "describable";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.21)  - TerminologyServiceAttributes.schema
    public static String entityDescription = "entityDescription";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.27)  - TerminologyServiceAttributes.schema
    public static String entryOrder = "entryOrder";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.3)  - TerminologyServiceAttributes.schema
    public static String facet = "facet";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.42)  - TerminologyServiceAttributes.schema
    public static String firstVersion = "firstVersion";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.22)  - TerminologyServiceAttributes.schema
    public static String formalRules = "formalRules";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.35)  - TerminologyServiceAttributes.schema
    public static String forward = "forwardName";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.6)  - TerminologyServiceObjects.schema
    public static String instruction = "instruction";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.36)  - TerminologyServiceAttributes.schema
    public static String inverse = "reverseName";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.18)  - TerminologyServiceAttributes.schema
    public static String isComplete = "isComplete";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.15)  - TerminologyServiceAttributes.schema
    public static String isDefault = "isDefault";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.17)  - TerminologyServiceAttributes.schema
    public static String isLatest = "isLatest";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.32)  - TerminologyServiceAttributes.schema
    public static String isNative = "isNative";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.19)  - TerminologyServiceAttributes.schema
    public static String isPreferred = "isPreferred";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.34)  - TerminologyServiceAttributes.schema
    public static String isSymmetric = "isSymmetric";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.33)  - TerminologyServiceAttributes.schema
    public static String isTransitive = "isTransitive";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.10)  - TerminologyServiceAttributes.schema
    public static String language = "language";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.5)  - TerminologyServiceObjects.schema
    public static String languageLink = "languageLink";

    // objectclass (1.3.5.1.4.1.2114.108.1.6.43)  - TerminologyServiceAttributes.schema
    public static String lastVersion = "lastVersion";

    // objectclass (1.3.5.1.4.1.2114.108.1.6.45)  - TerminologyServiceAttributes.schema
    public static String latestVersion = "latestVersion";

    // attributetype (1.3.6.1.4.1.2114.108.1.5.3)  - NamingAuthority.schema
    public static String localName = "localName";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.24)  - TerminologyServiceAttributes.schema
    public static String matchIfNoContext = "matchIfNoContext";

    // attributetype (1.3.6.1.4.1.2114.108.1.5.2)  - NamingAuthority.schema
    public static String ne = "ne";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.19)  - TerminologyServiceObjects.schema
    public static String pickListEntry = "pickListEntry";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.14)  - TerminologyServiceAttributes.schema
    public static String pickText = "pickText";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.7)  - TerminologyServiceObjects.schema
    public static String presentation = "presentation";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.9)  - TerminologyServiceAttributes.schema
    public static String presentationFormat = "presentationFormat";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.1)  - TerminologyServiceAttributes.schema
    public static String presentationId = "presentationId";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.7)  - TerminologyServiceAttributes.schema
    public static String qualifiedCode = "qualifiedCode";

    // attributetype (1.3.6.1.4.1.2114.108.1.5.4)  - NamingAuthority.schema
    public static String qualifiedName = "qualifiedName";

    // attributetype (1.3.6.1.4.1.2114.108.1.5.1)  - NamingAuthority.schema
    public static String ra = "ra";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.3)  - TerminologyServiceObjects.schema
    public static String referenceLink = "referenceLink";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.5)  - TerminologyServiceAttributes.schema
    public static String registeredName = "registeredName";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.30)  - TerminologyServiceAttributes.schema
    public static String representationalForm = "representationalForm";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.1)  - TerminologyServiceObjects.schema
    public static String service = "service";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.8)  - TerminologyServiceAttributes.schema
    public static String source = "source";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.25)  - TerminologyServiceAttributes.schema
    public static String sourceConcept = "sourceConcept";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.6)  - TerminologyServiceObjects.schema
    public static String sourceLink = "sourceLink";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.10)  - TerminologyServiceObjects.schema
    public static String systemization = "systemization";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.2)  - TerminologyServiceAttributes.schema
    public static String systemizationId = "systemizationId";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.11)  - TerminologyServiceObjects.schema
    public static String systemizationVersion = "systemizationVersion";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.31)  - TerminologyServiceAttributes.schema
    public static String targetCodingScheme = "targetCodingScheme";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.26)  - TerminologyServiceAttributes.schema
    public static String targetConcept = "targetConcept";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.7)  - TerminologyServiceObjects.schema
    public static String targetLink = "targetLink";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.20)  - TerminologyServiceAttributes.schema
    public static String text = "text";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.9)  - TerminologyServiceObjects.schema
    public static String textualPresentation = "textualPresentation";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.5)  - NamingAuthority.schema
    public static String tsBoolean = "tsBoolean";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.3)  - NamingAuthority.schema
    public static String tsCaseIgnoreDirectoryString = "tsCaseIgnoreDirectoryString";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.1)  - NamingAuthority.schema
    public static String tsCaseIgnoreIA5String = "tsCaseIgnoreIA5String";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.4)  - NamingAuthority.schema
    public static String tsCaseSensitiveDirectoryString = "tsCaseSensitiveDirectoryString";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.2)  - NamingAuthority.schema
    public static String tsCaseSensitiveIA5String = "tsCaseSensitiveIA5String";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.6)  - NamingAuthority.schema
    public static String tsInteger = "tsInteger";

    // attributetype (1.3.6.1.4.1.2114.108.1.4.7)  - NamingAuthority.schema
    public static String tsOctetString = "tsOctetString";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.13)  - TerminologyServiceAttributes.schema
    public static String usageContext = "usageContext";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.8)  - TerminologyServiceObjects.schema
    public static String usageContextLink = "usageContextLink";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.15)  - TerminologyServiceObjects.schema
    public static String valueDomain = "valueDomain";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.16)  - TerminologyServiceObjects.schema
    public static String valueDomainEntry = "valueDomainEntry";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.37)  - TerminologyServiceAttributes.schema
    public static String valueDomainId = "valueDomainId";

    // objectclass (1.3.6.1.4.1.2114.108.1.7.18)  - TerminologyServiceObjects.schema
    public static String valueDomainVersion = "valueDomainVersion";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.16)  - TerminologyServiceAttributes.schema
    public static String version = "version";

    // attributetype (1.3.6.1.4.1.2114.108.1.6.40)  - TerminologyServiceAttributes.schema
    public static String versionOrder = "versionOrder";

    // objectclass (1.3.5.1.4.1.2114.108.1.7.2)  - TerminologyServiceObjects.schema
    public static String versionable = "versionable";

    // Singleton class
    private SchemaConstants()
	{}
}