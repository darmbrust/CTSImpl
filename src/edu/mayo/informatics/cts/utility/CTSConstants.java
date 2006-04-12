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

import edu.mayo.mir.utility.parameter.*;

/**
 * Has the default constants that are used in the service.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class CTSConstants
{
    //ldap connection parameters (for vapi ldap implementation)
    public static StringParameter VAPI_LDAP_ADDRESS = new StringParameter("ldap://localhost:31900/");
    public static StringParameter VAPI_LDAP_SERVICE = new StringParameter("service=CTS,dc=HL7,dc=org");
    public static StringParameter VAPI_LDAP_USERNAME = new StringParameter("");
	public static StringParameter VAPI_LDAP_PASSWORD = new StringParameter("");
	
	//sql connection parameters (for vapi sqlLite implementation)
	public static StringParameter VAPI_SQLLITE_URL = new StringParameter("jdbc:mysql://localhost/LexGridLite");
    public static StringParameter VAPI_SQLLITE_DRIVER = new StringParameter("org.gjt.mm.mysql.Driver");
    public static StringParameter VAPI_SQLLITE_USERNAME = new StringParameter("");
    public static StringParameter VAPI_SQLLITE_PASSWORD = new StringParameter("");
    
    //sql connection parameters (for vapi sql implementation)
    public static StringParameter VAPI_SQL_URL = new StringParameter("jdbc:mysql://localhost/LexGrid");
    public static StringParameter VAPI_SQL_DRIVER = new StringParameter("org.gjt.mm.mysql.Driver");
    public static StringParameter VAPI_SQL_USERNAME = new StringParameter("");
    public static StringParameter VAPI_SQL_PASSWORD = new StringParameter("");
	
	
	//sql connection parameters (for mapi)
	public static StringParameter MAPI_DB_URL = new StringParameter("jdbc:mysql://localhost/rim0202dv1");
    public static StringParameter MAPI_DB_DRIVER = new StringParameter("org.gjt.mm.mysql.Driver");
    public static StringParameter MAPI_DB_USERNAME = new StringParameter("");
    public static StringParameter MAPI_DB_PASSWORD = new StringParameter("");
    
    //some performance affecting parameters for vapi
    public static IntParameter MAX_SYSTEMIZATION_ASSOCIATION_RECURSION = new IntParameter(10);
    public static IntParameter SEARCH_RESULT_LIMIT = new IntParameter(500);
    public static IntParameter SHORT_SEARCH_TIMEOUT = new IntParameter(8000);
    public static IntParameter LONG_SEARCH_TIMEOUT = new IntParameter(20000);
    
    //general performance parameters
    public static IntParameter CACHE_SIZE = new IntParameter(500);
    
    //Lucene search algorithm flags
    public static BooleanParameter LUCENE_SEARCH_ENABLED = new BooleanParameter(false);
    public static BooleanParameter LUCENE_NORM_SEARCH_ENABLED = new BooleanParameter(false);
    public static BooleanParameter LUCENE_DOUBLE_METAPHONE_SEARCH_ENABLED = new BooleanParameter(false);
    public static StringParameter LUCENE_INDEX_LOCATION = new StringParameter("");
    public static BooleanParameter LUCENE_THROW_ERROR_ON_MISSING_INDEX = new BooleanParameter(true);

    
    //EVSMode
    public static BooleanParameter EVSModeEnabled = new BooleanParameter(false);
    
    public static String FULL_VERSION = "1.2.12";
    public static String MAJOR_VERSION = "1";
    public static String MINOR_VERSION = "2";

    
    //service manager stuff - not currently used
//    public static StringParameter SERVICE_MANAGER = new StringParameter("edu.mayo.informatics.cts.utility.serviceManager.StandAloneServiceManager");
//    public static edu.mayo.mir.utility.parameter.EncryptedParameter SECURITY_PASSWORD = new edu.mayo.mir.utility.parameter.EncryptedParameter("");
//    public static StringParameter PASSWORD_FILE = new StringParameter("");

    // Singleton class
    private CTSConstants() {
    }
}