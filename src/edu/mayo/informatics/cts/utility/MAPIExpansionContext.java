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
public class MAPIExpansionContext
{
    public String         baseValueSetId_;
    public String         nestedValueSetId_;
    public int            nestingDepth_;
    public String         language_;
    public int            timeout_;
    public int            sizeLimit_;

    private static String contextVersion = "1";

    public MAPIExpansionContext(String baseValueSetId, String nestedValueSetId, int nestingDepth, String language,
            int timeout, int sizeLimit)
    {
        this.baseValueSetId_ = baseValueSetId;
        this.nestedValueSetId_ = nestedValueSetId;
        this.nestingDepth_ = nestingDepth;
        this.language_ = language;
        this.timeout_ = timeout;
        this.sizeLimit_ = sizeLimit;
    }

    public static MAPIExpansionContext getExpansionContext(byte[] expansionContext) throws Exception
    {
        return new MAPIExpansionContext(new String(expansionContext));
    }

    public MAPIExpansionContext(String expansionContext) throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(expansionContext));
        Element rootElement = doc.getRootElement();

        //didn't use to have a version, and now the version is 1, so 1 or no version is valid.
        if (rootElement.getAttribute("version") != null && !rootElement.getAttribute("version").getValue().equals("1"))
        {
            throw new RuntimeException("Cannot parse this expansion context");
        }

        baseValueSetId_ = rootElement.getChildText("baseValueSetId");
        nestedValueSetId_ = rootElement.getChildText("nestedValueSetId");
        nestingDepth_ = new Integer(rootElement.getChildText("nestingDepth")).intValue();
        language_ = rootElement.getChildText("language");
        timeout_ = Integer.parseInt(rootElement.getChildText("timeout"));
        sizeLimit_ = Integer.parseInt(rootElement.getChildText("sizeLimit"));
    }

    public String toString()
    {
        Document document = new Document(new Element("context"));
        Element root = document.getRootElement();
        root.setAttribute("version", contextVersion);

        Element newElement = new Element("baseValueSetId");
        newElement.setText(baseValueSetId_);
        root.addContent(newElement);

        newElement = new Element("nestedValueSetId");
        newElement.setText(nestedValueSetId_);
        root.addContent(newElement);

        newElement = new Element("nestingDepth");
        newElement.setText(nestingDepth_ + "");
        root.addContent(newElement);

        newElement = new Element("language");
        newElement.setText(language_);
        root.addContent(newElement);

        newElement = new Element("timeout");
        newElement.setText(timeout_ + "");
        root.addContent(newElement);

        newElement = new Element("sizeLimit");
        newElement.setText(sizeLimit_ + "");
        root.addContent(newElement);

        XMLOutputter output = new XMLOutputter(Format.getRawFormat());

        return output.outputString(document);
    }
}