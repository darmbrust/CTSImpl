package org.hl7.CTSMAPI.refImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.lexgrid.commons.managedobj.FindException;
import org.lexgrid.commons.managedobj.ManagedObjIF;
import org.lexgrid.commons.managedobj.service.jdbc.JDBCBaseService;
import org.lexgrid.commons.managedobj.service.jdbc.JDBCConnectionDescriptor;
import org.lexgrid.commons.managedobj.service.jdbc.JDBCConnectionPoolPolicy;

/**
 * <pre>
 * Title:        SQLStatements.java
 * Description:  All of the sql statements used in the mapi implementation are defined here.
 * This class also extends the JDBCBaseService, which handles lost connections, pooling, etc.
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
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust </A>
 * @version 1.0 - cvs $Revision: 1.10 $ checked in on $Date: 2005/09/22 21:18:38 $
 */

public class SQLStatements extends JDBCBaseService
{
    public final static Logger logger               = Logger.getLogger("org.hl7.MAPI_Browser");
    private boolean            access               = false;

    private static Hashtable   sqlStatementsHolder_ = new Hashtable();

    public static SQLStatements instance(String username, String password, String url, String driver) throws Exception
    {
        if (username == null || username.length() == 0)
        {
            username = org.hl7.utility.CTSConstants.MAPI_DB_USERNAME.getValue();
        }
        if (password == null || password.length() == 0)
        {
            password = org.hl7.utility.CTSConstants.MAPI_DB_PASSWORD.getValue();
        }
        if (url == null || url.length() == 0)
        {
            url = org.hl7.utility.CTSConstants.MAPI_DB_URL.getValue();
        }
        if (driver == null || driver.length() == 0)
        {
            driver = org.hl7.utility.CTSConstants.MAPI_DB_DRIVER.getValue();
        }
        
        SQLStatements ss = (SQLStatements) sqlStatementsHolder_.get(createKey(username, password, url, driver));
        if (ss == null)
        {
            ss = new SQLStatements(username, password, url, driver);
            sqlStatementsHolder_.put(createKey(username, password, url, driver), ss);
        }
        return ss;
    }

    private static String createKey(String username, String password, String url, String driver)
    {
        return (username + password + url + driver).hashCode() + "";
    }

    private SQLStatements(String username, String password, String url, String driver) throws ClassNotFoundException
    {
        logger.debug("Initializing sql and sql connections");

        JDBCConnectionDescriptor desc = getConnectionDescriptor();

        try
        {
            desc.setDbDriver(driver);
        }
        catch (ClassNotFoundException e)
        {
            logger.error("The driver for your sql connection was not found.  I tried to load " + driver);
            throw e;
        }
        desc.setDbUid(username);
        desc.setDbPwd(password);
        desc.setAutoCommit(true);
        desc.setDbUrl(url);
        desc.setUseUTF8(true);
        desc.setAutoRetryFailedConnections(true);

        if (url.indexOf("Microsoft Access") > 0)
        {
            access = true;
        }
        else
        {
            access = false;
        }

        //This sets it up to verify that the connection is up and working before a statement
        // is executed, among other things.
        JDBCConnectionPoolPolicy pol = getConnectionPoolPolicy();
        pol.maxActive = 4;
        pol.maxIdle = -1;
        pol.maxWait = -1;
        pol.minEvictableIdleTimeMillis = -1;
        pol.numTestsPerEvictionRun = 1;
        pol.testOnBorrow = false;
        pol.testOnReturn = false;
        pol.testWhileIdle = false;
        pol.timeBetweenEvictionRunsMillis = -1;
        pol.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_GROW;
        desc.setPingSQL("Select ModelID from Model");
        initStatements();
    }

    public PreparedStatement checkOutStatement(String key) throws SQLException
    {
        return checkOutRegisteredStatement(key);
    }

    public PreparedStatement getArbitraryStatement(String sql) throws SQLException
    {
        return super.checkOutPreparedStatement(sql);
    }

    /**
     * MSAccess has some odditites in its query language, sometimes I need to know when I am connected to access to
     * create proper queries.
     * 
     * @return
     */
    public boolean isAccess()
    {
        return access;
    }

    public final String GET_HL7_RELEASE_VERSION             = "GET_HL7_RELEASE_VERSION";
    public final String GET_ACTIVE_ATTRIBUTES               = "GET_ACTIVE_ATTRIBUTES";
    public final String GET_VOCABULARY_DOMAINS              = "GET_VOCABULARY_DOMAINS";
    public final String GET_BASIS_OF_DOMAINS                = "GET_BASIS_OF_DOMAINS";
    public final String GET_RIM_ATTRIBUTE                   = "GET_RIM_ATTRIBUTE";
    public final String IS_CONCEPT_VALID                    = "IS_CONCEPT_VALID";
    public final String GET_VALUE_SET                       = "GET_VALUE_SET";
    public final String GET_DEFINED_BY_VALUE_SET            = "GET_DEFINED_BY_VALUE_SET";
    public final String IS_VOCABULARY_DOMAIN_VALID          = "IS_VOCABULARY_DOMAIN_VALID";
    public final String GET_ACTIVE_CODE_SYSTEMS             = "GET_ACTIVE_CODE_SYSTEMS";
    public final String GET_REGISTERED_CODE_SYSTEMS         = "GET_REGISTERED_CODE_SYSTEMS";
    public final String GET_SUPPORTED_VALUE_SETS            = "GET_SUPPORTED_VALUE_SETS ";
    public final String GET_NAME_FOR_VALUE_SET_ID           = "GET_NAME_FOR_VALUE_SET_ID";
    public final String GET_SUPPORTED_VALUE_SETS_MINIMAL    = "GET_SUPPORTED_VALUE_SETS_MINIMAL";
    public final String GET_USED_TO_DEFINE_CODE_SETS        = "GET_USED_TO_DEFINE_CODE_SETS";
    public final String GET_CONSTRUCTED_USING_CODE_SETS     = "GET_CONSTRUCTED_USING_CODE_SETS";
    public final String GET_CODE_REFERENCES                 = "GET_CODE_REFERENCES";
    public final String IS_CODE_IN_VALUE_SET                = "IS_CODE_IN_VALUE_SET";
    public final String DOES_VALUE_SET_EXIST                = "DOES_VALUE_SET_EXIST";
    public final String DOES_CODE_SYSTEM_EXIST              = "DOES_CODE_SYSTEM_EXIST";
    public final String GET_VOCABULARY_DOMAIN_ID            = "GET_VOCABULARY_DOMAIN_ID";
    public final String DOES_CODE_EXIST_IN_CODE_SYSTEM      = "DOES_CODE_EXIST_IN_CODE_SYSTEM";
    public final String GET_TARGETS_OF_SOURCE_CODE          = "GET_TARGETS_OF_SOURCE_CODE";
    public final String DOES_CONCEPT_CODE_EXIST             = "DOES_CONCEPT_CODE_EXIST";
    public final String DOES_DESIGNATION_EXIST              = "DOES_DESIGNATION_EXIST";
    public final String GET_CODE_DETAILS                    = "GET_CODE_DETAILS";
    public final String DOES_LANGUAGE_EXIST_FOR_VALUESET    = "DOES_LANGUAGE_EXIST_FOR_VALUESET";
    public final String DOES_LANGUAGE_EXIST_FOR_CODE_SYSTEM = "DOES_LANGUAGE_EXIST_FOR_CODE_SYSTEM";
    public final String IS_APPLICATION_CONTEXT_VALID        = "IS_APPLICATION_CONTEXT_VALID";
    public final String IS_VOCABULARY_DOMAIN_NAME_VALID     = "IS_VOCABULARY_DOMAIN_NAME_VALID";
    public final String IS_DATA_TYPE_ALLOWED_FOR_VOC_DOMAIN = "IS_DATA_TYPE_ALLOWED_FOR_VOC_DOMAIN";
    public final String IS_CODING_RATIONALE_VALID           = "IS_CODING_RATIONALE_VALID";
    public final String IS_CODE_SYSTEM_VERSION_VALID        = "IS_CODE_SYSTEM_VERSION_VALID";

    public void initStatements()
    {
        logger.debug("Registering sql statments");
        registerSQL(GET_HL7_RELEASE_VERSION, "SELECT modelID, lastModifiedDate" + " FROM Model ");
        registerSQL(GET_ACTIVE_ATTRIBUTES, "SELECT modelId, classname, attName, vocDomain, vocStrength, attDataType"
                + " FROM RIM_attribute" + " WHERE ((outVer is null) or (outVer = ''))" + " AND attTypeCode='code'"
                + " AND attName Like ?" + " ORDER BY className");
        registerSQL(GET_VOCABULARY_DOMAINS, "SELECT * " + " FROM RIM_vocabulary_domain WHERE vocDomain LIKE ?"
                + " ORDER BY vocDomain");
        registerSQL(GET_BASIS_OF_DOMAINS, "SELECT vocDomain " + " FROM RIM_vocabulary_domain WHERE restrictsDomain =?");
        registerSQL(GET_RIM_ATTRIBUTE, "SELECT attName, classname, modelId, vocDomain, vocStrength, attDataType"
                + " FROM RIM_attribute WHERE vocDomain = ?");
        registerSQL(IS_CONCEPT_VALID, "SELECT count(*) as found" + " FROM VCS_concept_code_xref"
                + " WHERE codeSystemId2 = ?" + " AND conceptCode2 LIKE ?");
        registerSQL(GET_VALUE_SET, "SELECT  representsVocDomain, definedByValueSet, appliesInContext, valueSetName"
                + " FROM VOC_vocabulary_domain_value_set, VOC_value_set" + " WHERE representsVocDomain = ?"
                + " AND valueSetId = definedByValueSet");
        registerSQL(GET_DEFINED_BY_VALUE_SET, "SELECT definedByValueSet FROM VOC_vocabulary_domain_value_set"
                + " WHERE representsVocDomain = ? AND appliesInContext = ?");
        registerSQL(IS_VOCABULARY_DOMAIN_VALID,
                    "SELECT Count(*) as found FROM RIM_vocabulary_domain where vocDomain = ?");
        registerSQL(GET_ACTIVE_CODE_SYSTEMS, "SELECT codeSystemId, codeSystemName, releaseId, copyrightNotice"
                + " FROM  VCS_code_system WHERE" + " codeSystemName LIKE ? AND codeSystemId LIKE ?"
                + " ORDER BY VCS_code_system.codeSystemName");
        registerSQL(GET_REGISTERED_CODE_SYSTEMS, "SELECT *" + " FROM VOC_registered_code_system where codeSystemId = ?");
        registerSQL(GET_SUPPORTED_VALUE_SETS, "SELECT *"
                + " FROM VOC_value_set, VOC_value_set_constructor WHERE valueSetId = includesOrExcludesSet"
                + " AND valueSetName LIKE ? AND valueSetId LIKE ?");
        registerSQL(GET_NAME_FOR_VALUE_SET_ID, "SELECT *" + " FROM VOC_value_set WHERE valueSetId = ?");
        registerSQL(GET_SUPPORTED_VALUE_SETS_MINIMAL, "SELECT valueSetId, valueSetName"
                + " FROM VOC_value_set WHERE valueSetId LIKE ? AND valueSetName LIKE ?");
        registerSQL(GET_USED_TO_DEFINE_CODE_SETS, "SELECT VOC_value_set_constructor.*"
                + " FROM VOC_value_set_constructor WHERE (VOC_value_set_constructor.usedToBuildValueSet=?)");
        registerSQL(GET_CONSTRUCTED_USING_CODE_SETS, "SELECT VOC_value_set_constructor.*"
                + " FROM VOC_value_set_constructor WHERE (VOC_value_set_constructor.includesOrExcludesSet=?)");
        registerSQL(GET_CODE_REFERENCES, "SELECT VOC_code_reference.*"
                + " FROM VOC_code_reference WHERE (VOC_code_reference.usedToBuildValueSet=?)");
        registerSQL(IS_CODE_IN_VALUE_SET, "SELECT Count(*) as found" + " FROM VOC_nested_value_set"
                + " WHERE VOC_nested_value_set.baseValueSetName LIKE ? AND VOC_nested_value_set.baseValueSetId LIKE ?"
                + " AND VOC_nested_value_set.codeSystem=?" + " AND VOC_nested_value_set.conceptCode=?"
                + " AND VOC_nested_value_set.nestedValueSetId >=?");
        registerSQL(DOES_VALUE_SET_EXIST, "SELECT Count(*) AS found" + " FROM VOC_value_set"
                + " WHERE VOC_value_set.valueSetId=?");
        registerSQL(DOES_CODE_SYSTEM_EXIST, "SELECT Count(VCS_code_system.codesystemid) as found"
                + " FROM VCS_code_system" + " WHERE (VCS_code_system.codesystemid=?"
                + " AND VCS_code_system.codeSystemName Like ?)");
        registerSQL(GET_VOCABULARY_DOMAIN_ID, "SELECT VOC_vocabulary_domain_value_set.definedByValueSet"
                + " FROM VOC_vocabulary_domain_value_set"
                + " WHERE VOC_vocabulary_domain_value_set.representsVocDomain=?"
                + " AND VOC_vocabulary_domain_value_set.appliesInContext LIKE ?");
        registerSQL(DOES_CODE_EXIST_IN_CODE_SYSTEM, "SELECT Count(VOC_nested_value_set.basevaluesetid) AS found"
                + " FROM VOC_nested_value_set" + " WHERE (VOC_nested_value_set.basevaluesetid LIKE ?"
                + " AND VOC_nested_value_set.codesystem LIKE ?" + " AND VOC_nested_value_set.conceptcode LIKE ?)");
        registerSQL(
                    GET_TARGETS_OF_SOURCE_CODE,
                    "SELECT VCS_concept_code_xref_1.conceptCode2 as targetCode"
                            + " FROM (VCS_concept_code_xref INNER JOIN VCS_concept_relationship ON VCS_concept_code_xref.internalId = VCS_concept_relationship.sourceInternalId)"
                            + " INNER JOIN VCS_concept_code_xref AS VCS_concept_code_xref_1 ON VCS_concept_relationship.targetInternalId = VCS_concept_code_xref_1.internalId"
                            + " WHERE ((VCS_concept_code_xref.codeSystemId2=?) AND (VCS_concept_code_xref.conceptCode2=?)"
                            + " AND (VCS_concept_relationship.relationCode='hasSubtype'))");
        registerSQL(DOES_CONCEPT_CODE_EXIST, "SELECT Count(VCS_concept_code_xref.conceptCode2) as found"
                + " FROM VCS_concept_code_xref" + " WHERE (VCS_concept_code_xref.codeSystemId2 =?"
                + " AND VCS_concept_code_xref.conceptCode2=?)");
        registerSQL(DOES_DESIGNATION_EXIST, "SELECT Count(*) as found"
                + " FROM VCS_concept_code_xref INNER JOIN VCS_concept_designation"
                + " ON VCS_concept_code_xref.internalId = VCS_concept_designation.internalId"
                + " WHERE VCS_concept_code_xref.codeSystemId2 LIKE ?" + " AND VCS_concept_designation.designation=?"
                + " AND VCS_concept_code_xref.conceptCode2=?");
        registerSQL(
                    GET_CODE_DETAILS,
                    "SELECT VCS_code_system.codeSystemName, VCS_code_system.releaseId, VCS_concept_designation.designation"
                            + " FROM (VCS_concept_code_xref"
                            + " INNER JOIN VCS_code_system ON VCS_concept_code_xref.codeSystemId2 = VCS_code_system.codeSystemid)"
                            + " INNER JOIN VCS_concept_designation ON VCS_concept_code_xref.internalId = VCS_concept_designation.internalId"
                            + " WHERE VCS_concept_code_xref.codeSystemId2=?"
                            + " AND VCS_concept_designation.language=?" + " AND VCS_concept_code_xref.conceptCode2=?");
        registerSQL(DOES_LANGUAGE_EXIST_FOR_VALUESET, "SELECT Count(XML_code_system_language.language) AS found"
                + " FROM VOC_nested_value_set INNER JOIN XML_code_system_language"
                + " ON VOC_nested_value_set.codeSystem = XML_code_system_language.codeSystemId"
                + " WHERE VOC_nested_value_set.baseValueSetId=?" + " AND XML_code_system_language.language=?");
        registerSQL(DOES_LANGUAGE_EXIST_FOR_CODE_SYSTEM, "SELECT Count(XML_code_system_language.language) AS found"
                + " FROM XML_code_system_language" + " WHERE XML_code_system_language.codeSystemId=?"
                + " AND XML_code_system_language.language=?");
        registerSQL(IS_APPLICATION_CONTEXT_VALID, "SELECT Count(VOC_nested_value_set.conceptCode) AS found"
                + " FROM VOC_nested_value_set" + " WHERE VOC_nested_value_set.codeSystem='2.16.840.1.113883.5.147'"
                + " AND VOC_nested_value_set.baseValueSetName='RealmOfUse'"
                + " AND VOC_nested_value_set.nestingDepth > 0" + " AND VOC_nested_value_set.conceptCode=?");
        registerSQL(IS_VOCABULARY_DOMAIN_NAME_VALID, "SELECT Count(RIM_vocabulary_domain.vocDomain) AS found"
                + " FROM RIM_vocabulary_domain" + " WHERE RIM_vocabulary_domain.vocDomain=?");
        registerSQL(IS_DATA_TYPE_ALLOWED_FOR_VOC_DOMAIN, "SELECT Count(*) as found" + " FROM RIM_attribute"
                + " WHERE RIM_attribute.vocDomain = ?" + " AND RIM_attribute.attDatatype LIKE ?");
        registerSQL(IS_CODING_RATIONALE_VALID, "SELECT Count(*) as found" + " FROM VCS_concept_code_xref"
                + " WHERE VCS_concept_code_xref.codeSystemId2='2.16.840.1.113883.5.1074'"
                + " AND VCS_concept_code_xref.conceptCode2=?");
        registerSQL(IS_CODE_SYSTEM_VERSION_VALID, "SELECT Count(*) AS found" + " FROM VCS_code_system"
                + " WHERE VCS_code_system.codeSystemid=?" + " AND VCS_code_system.releaseId=?");
    }

    //The following methods are all abstract, so they have to be here, but I don't need them.
    protected String getDbTableName()
    {
        return null;
    }

    protected ManagedObjIF findByPrimaryKeyPrim(Object key) throws FindException
    {
        return null;
    }

    protected Class getInstanceClass()
    {
        return null;
    }

    public ManagedObjIF row2ManagedObj(ResultSet rs) throws SQLException
    {
        return null;
    }

}