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

/**
 * A couple of utility type methods.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class Utility
{
    public static ConnectionInfo parseXMLConnectionString(String XML) throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(XML));
        Element temp = doc.getRootElement();

        if (temp.getName().equals("LDAPConnectionInfo"))
        {
            return new LDAPConnectionInfo(temp.getChild("username").getValue(), temp.getChild("password").getValue(),
                    temp.getChild("address").getValue(), temp.getChild("service").getValue());

        }
        else if (temp.getName().equals("SQLConnectionInfo"))
        {
            return new SQLConnectionInfo(temp.getChild("username").getValue(), temp.getChild("password").getValue(),
                    temp.getChild("server").getValue(), temp.getChild("driver").getValue());
        }

        else
        {
            throw new Exception("Invalid XML String");
        }
    }

    public static SQLConnectionInfo parseSQLXML(String XML) throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(XML));
        Element temp = doc.getRootElement();

        return new SQLConnectionInfo(temp.getChild("username").getValue(), temp.getChild("password").getValue(), temp
                .getChild("server").getValue(), temp.getChild("driver").getValue());

    }

    public static LDAPConnectionInfo parseLDAPXML(String XML) throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(XML));
        Element temp = doc.getRootElement();

        return new LDAPConnectionInfo(temp.getChild("username").getValue(), temp.getChild("password").getValue(), temp
                .getChild("address").getValue(), temp.getChild("service").getValue());

    }

    public static class ConnectionInfo
    {
        public String username, password;
    }

    public static class LDAPConnectionInfo extends ConnectionInfo
    {
        public String address, service;

        public LDAPConnectionInfo(String username, String password, String address, String service)
        {
            this.username = username;
            this.password = password;
            this.address = address;
            this.service = service;
        }
    }

    public static class SQLConnectionInfo extends ConnectionInfo
    {
        public String server, driver;

        public SQLConnectionInfo(String username, String password, String server, String driver)
        {
            this.username = username;
            this.password = password;
            this.server = server;
            this.driver = driver;
        }
    }
     public static void main(String[] args) throws Exception
    {
        // System.out.println(escapeLdapDN("Big bear <foobar> bear"));
        //
        // LDAPConnectionInfo temp = parseLDAPXML("<?xml version=\"1.0\"
        // encoding=\"UTF-8\"?><LDAPConnectionInfo><address>ldap://mir04.mayo.edu:31900</address><service>service=CTS,dc=LexGrid,dc=org</service><username>cn=public,dc=Users,service=userValidation,dc=LexGrid,dc=org</username><password>public</password></LDAPConnectionInfo>");
        //
        // SQLConnectionInfo temp2 = parseSQLXML("<?xml version=\"1.0\"
        // encoding=\"UTF-8\"?><SQLConnectionInfo><server>jdbc:mysql://mir04/LexGrid</server><driver>org.gjt.mm.mysql.Driver</driver><username>mirpub</username><password>mirpub</password></SQLConnectionInfo>");
        //
        // System.out.println("foo");
    }
}