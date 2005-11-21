package org.hl7.utility;

import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * <pre>
 * Title:        VAPIExpansionContext
 * Description:  A simple little xml'ized version of the ExpansionContext.
 * Copyright: (c) 2002, 2003, 2004 Mayo Foundation. All rights reserved.
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
 * 
 * <H5>Bugs, to be done, notes, etc.</H5>
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.10 $ checked in on $Date: 2005/08/04 20:04:29 $
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