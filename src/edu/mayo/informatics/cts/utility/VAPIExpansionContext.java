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

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * A simple little xml'ized version of the ExpansionContext.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
//TODO - This method of parsing the XML is rather slow - experiment with a better way to parse/deparse the XML 
//blobs.  I am seeing upwards of 300 ms just to parse and deparse one of these...  

public class VAPIExpansionContext
{
    public boolean forward_;
    public boolean directDescendantsOnly_;
    public String  ldapName_;
    public String  codeSystemName_;
    public int     nestingDepth_;
    public String  language_;
    public String  defaultLanguage_;
    public String  conceptCode_;
    public String  sourceCodeSystemId_;
    public String  relationship_;
    public String  defaultTargetCodeSystemId_;
    public int timeout_;
    public int sizeLimit_;
    
    private static String formatVersion = "3";

    public VAPIExpansionContext(boolean forward, boolean directDescendantsOnly, String relationship, String ldapName, String codeSystemName, String sourceCodeSystemId, int nestingDepth, String language, String defaultLanguage, String conceptCode,
             String targetCodeSystem, int timeout, int sizeLimit)
    {
        this.forward_ = forward;
        this.directDescendantsOnly_ = directDescendantsOnly;
        this.ldapName_ = ldapName;
        this.codeSystemName_ = codeSystemName;
        this.nestingDepth_ = nestingDepth;
        this.language_ = language;
        this.conceptCode_ = conceptCode;
        this.relationship_ = relationship;
        this.defaultTargetCodeSystemId_ = targetCodeSystem;
        this.timeout_ = timeout;
        this.sizeLimit_ = sizeLimit;
        this.defaultLanguage_ = defaultLanguage;
        this.sourceCodeSystemId_ = sourceCodeSystemId;
    }

    public static VAPIExpansionContext getExpansionContext(byte[] expansionContext) throws Exception
    {
        return new VAPIExpansionContext(new String(expansionContext));
    }

    public VAPIExpansionContext(String expansionContext) throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(expansionContext));
        Element rootElement = doc.getRootElement();

        if (rootElement.getAttribute("version") == null || !rootElement.getAttribute("version").getValue().equals("3"))
        {
            throw new RuntimeException("Cannot parse this expansion context");
        }
        
        forward_ = new Boolean(rootElement.getChildText("forward")).booleanValue();
        directDescendantsOnly_ = new Boolean(rootElement.getChildText("directDescendantsOnly")).booleanValue();
        ldapName_ = rootElement.getChildText("ldapName");
        codeSystemName_ = rootElement.getChildText("codeSystemName"); 
        nestingDepth_ = new Integer(rootElement.getChildText("nestingDepth")).intValue();
        language_ = rootElement.getChildText("language"); 
        defaultLanguage_ = rootElement.getChildText("defaultLanguage"); 
        conceptCode_ = rootElement.getChildText("conceptCode");
        relationship_ = rootElement.getChildText("relationship");
        defaultTargetCodeSystemId_ = rootElement.getChildText("defaultTargetCodeSystemId");
        sourceCodeSystemId_ = rootElement.getChildText("sourceCodeSystemId");
        timeout_ = new Integer(rootElement.getChildText("timeout")).intValue();
        sizeLimit_ = new Integer(rootElement.getChildText("sizeLimit")).intValue(); 
    }

    public String toString()
    {
        Document document = new Document(new Element("context"));
        Element root = document.getRootElement();
        root.setAttribute("version", formatVersion);
        
        Element newElement = new Element("forward");
        newElement.setText(forward_ + "");
        root.addContent(newElement);
        
        newElement = new Element("ldapName");
        newElement.setText(ldapName_);
        root.addContent(newElement);
       
        newElement = new Element("codeSystemName");
        newElement.setText(codeSystemName_);
        root.addContent(newElement);
        
        newElement = new Element("nestingDepth");
        newElement.setText(nestingDepth_ + "");
        root.addContent(newElement);
        
        newElement = new Element("language");
        newElement.setText(language_);
        root.addContent(newElement);
        
        newElement = new Element("conceptCode");
        newElement.setText(conceptCode_);
        root.addContent(newElement);
        
        newElement = new Element("relationship");
        newElement.setText(relationship_);
        root.addContent(newElement);

        newElement = new Element("timeout");
        newElement.setText(timeout_ + "");
        root.addContent(newElement);
        
        newElement = new Element("sizeLimit");
        newElement.setText(sizeLimit_ + "");
        root.addContent(newElement);
        
        newElement = new Element("defaultLanguage");
        newElement.setText(defaultLanguage_);
        root.addContent(newElement);
        
        newElement = new Element("directDescendantsOnly");
        newElement.setText(directDescendantsOnly_ + "");
        root.addContent(newElement);
        
        newElement = new Element("defaultTargetCodeSystemId");
        newElement.setText(defaultTargetCodeSystemId_);
        root.addContent(newElement);
        
        newElement = new Element("sourceCodeSystemId");
        newElement.setText(sourceCodeSystemId_);
        root.addContent(newElement);

        XMLOutputter output = new XMLOutputter(Format.getRawFormat());

        return output.outputString(document);
    }
}