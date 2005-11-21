package org.hl7.utility;

import edu.mayo.mir.utility.parameter.*;

/**
 * <pre>
 * Title:        A Constants file
 * Description:  Has the default constants that are used in the service.
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
 * <H5> Bugs, to be done, notes, etc.</H5>
 * @author  <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 * @version 1.0 - cvs $Revision: 1.17 $ checked in on $Date: 2005/10/14 15:44:10 $
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
    
    public static String FULL_VERSION = "1.2.11";
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