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
package edu.mayo.informatics.cts.CTSVAPI.sql.refImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.LexGrid.managedobj.FindException;
import org.LexGrid.managedobj.ManagedObjIF;
import org.LexGrid.managedobj.jdbc.JDBCBaseService;
import org.LexGrid.managedobj.jdbc.JDBCConnectionDescriptor;
import org.LexGrid.managedobj.jdbc.JDBCConnectionPoolPolicy;
import org.LexGrid.util.sql.DBUtility;
import org.LexGrid.util.sql.GenericSQLModifier;
import org.LexGrid.util.sql.lgTables.SQLTableConstants;
import org.LexGrid.util.sql.lgTables.SQLTableUtilities;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import edu.mayo.informatics.cts.utility.CTSConstants;

/**
 * All of the sql statements used in the vapi implementation are defined here.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class SQLStatements extends JDBCBaseService
{
    public final static Logger logger_               = Logger.getLogger("edu.mayo.informatics.cts.VAPI_sql_queries");

    private static Hashtable   sqlStatementsHolder_ = new Hashtable();
    private GenericSQLModifier gSQLMod_;
    private SQLTableConstants  stc_;

    public static SQLStatements instance(String username, String password, String url, String driver, String tablePrefix) throws Exception
    {
        if (username == null || username.length() == 0)
        {
            username = CTSConstants.VAPI_SQL_USERNAME.getValue();
        }
        if (password == null || password.length() == 0)
        {
            password = CTSConstants.VAPI_SQL_PASSWORD.getValue();
        }
        if (url == null || url.length() == 0)
        {
            url = CTSConstants.VAPI_SQL_URL.getValue();
        }
        if (driver == null || driver.length() == 0)
        {
            driver = CTSConstants.VAPI_SQL_DRIVER.getValue();
        }
        if (tablePrefix == null || tablePrefix.length() == 0)
        {
            tablePrefix = CTSConstants.VAPI_SQL_TABLE_PREFIX.getValue();
        }
        SQLStatements ss = (SQLStatements) sqlStatementsHolder_.get(createKey(username, password, url, driver, tablePrefix));
        if (ss == null)
        {
            ss = new SQLStatements(username, password, url, driver, tablePrefix);
            sqlStatementsHolder_.put(createKey(username, password, url, driver, tablePrefix), ss);
        }
        return ss;
    }

    private static String createKey(String username, String password, String url, String driver, String tablePrefix)
    {
        return (username + password + url + driver + tablePrefix).hashCode() + "";
    }

    private SQLStatements(String username, String password, String url, String driver, String tablePrefix) throws Exception
    {
        logger_.debug("Initializing sql and sql connections");

        JDBCConnectionDescriptor desc = getConnectionDescriptor();

        try
        {
            desc.setDbDriver(driver);
        }
        catch (ClassNotFoundException e)
        {
            logger_.error("The driver for your sql connection was not found.  I tried to load " + driver);
            throw e;
        }
        desc.setDbUid(username);
        desc.setDbPwd(password);
        desc.setAutoCommit(true);
        desc.setDbUrl(url);
        desc.setUseUTF8(true);
        desc.setAutoRetryFailedConnections(true);

        // Connection pool parameters
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
        desc.setPingSQL("Select CodingSchemeName from codingScheme where CodingSchemeName='foobar'");

        // I need to know this to generate proper queries.

        Connection conn = (Connection) getConnectionPool().borrowObject();

        String databaseName = conn.getMetaData().getDatabaseProductName();
        stc_ = new SQLTableUtilities(conn, tablePrefix).getSQLTableConstants();
        
        getConnectionPool().returnObject(conn);

        //need to override the like since the converter now creates case sensitive tables 
        //this forces a case insensitive search
        GenericSQLModifier.mySqlLikeOverride = "COLLATE latin1_swedish_ci LIKE";
        gSQLMod_ = new GenericSQLModifier(databaseName, false);
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
    
    public boolean requiresUppercasing()
    {
        return gSQLMod_.requiresLikeQueryTextToBeUpperCased();
    }

    public String modifySQL(String query)
    {
        return gSQLMod_.modifySQL(query);
    }

    public final String GET_CODE_SYSTEM_DETAILS             = "GET_CODE_SYSTEM_DETAILS";
    public final String GET_CODE_SYSTEM_NAME                = "GET_CODE_SYSTEM_NAME";
    public final String GET_ID_FOR_RELATIONSHIP_CODE_SYSTEM_NAME  = "GET_ID_FOR_TARGET_CODE_SYSTEM_NAME";
    public final String GET_CODE_SYSTEM_NAME2               = "GET_CODE_SYSTEM_NAME2";
    public final String GET_CODE_SYSTEM_ID                  = "GET_CODE_SYSTEM_ID";
    public final String GET_CODE_SYSTEM_ID2                 = "GET_CODE_SYSTEM_ID2";
    public final String GET_CODE_SYSTEM_SUPPORTED_PROPERTYS = "GET_CODE_SYSTEM_SUPPORTED_PROPERTYS";
    public final String IS_CONCEPT_VALID                    = "IS_CONCEPT_VALID";
    public final String GET_DESIGNATION                     = "GET_DESIGNATION";
    public final String GET_DESIGNATION_NULL_STRING         = "GET_DESIGNATION_NULL_STRING  ";
    public final String GET_DEFAULT_LANGUAGE                = "GET_DEFAULT_LANGUAGE";
    public final String IS_PROPERTY_VALID                   = "IS_LANGUAGE_VALID";
    public final String GET_ASSOCIATION_PROPERTIES          = "GET_ASSOCIATION_PROPERTIES";
    public final String DOES_DIRECT_RELATION_EXIST          = "DOES_DIRECT_RELATION_EXIST";
    public final String GET_TARGETS_OF_SOURCE               = "GET_TARGETS_OF_SOURCE";
    public final String GET_CODE_DETAILS                    = "GET_CODE_DETAILS";
    public final String GET_SOURCE_FOR                      = "GET_SOURCE_FOR";
    public final String GET_SOURCE_FOR_ALL                  = "GET_SOURCE_FOR_ALL";
    public final String GET_TARGET_OF                       = "GET_TARGET_OF";
    public final String GET_TARGET_OF_ALL                   = "GET_TARGET_OF_LIKE";
    public final String GET_ALL_CONCEPTS                    = "GET_ALL_CONCEPTS";
    public final String GET_NATIVE_RELATION                 = "GET_NATIVE_RELATION";
    public final String GET_SUPPORTED_ASSOCIATIONS          = "GET_SUPPORTED_ASSOCIATIONS";
    public final String GET_CONCEPT_ASSOCIATIONS_TOC_QUALS  = "GET_CONCEPT_ASSOCIATIONS_TOC_QUALS";

    private void initStatements()
    {
        logger_.debug("Registering sql statments");
        registerSQL(
                    GET_CODE_SYSTEM_DETAILS,
                    "SELECT codingSchemeName, registeredName, copyright, formalName, entityDescription, representsVersion"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME)
                            + " WHERE codingSchemeName Like ?");

        registerSQL(GET_CODE_SYSTEM_SUPPORTED_PROPERTYS,
                    "SELECT " + (supports2006Model() ? "id" : "supportedAttributeValue")
                            + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME_SUPPORTED_ATTRIBUTES)
                            + " WHERE codingSchemeName=?"
                            + " AND supportedAttributeTag=?");
        
        registerSQL(GET_ID_FOR_RELATIONSHIP_CODE_SYSTEM_NAME,
                    "SELECT urn"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME_SUPPORTED_ATTRIBUTES)
                            + " WHERE codingSchemeName=?"
                            + " AND supportedAttributeTag='CodingScheme'"
                            + " AND " + (supports2006Model() ? "id" : "supportedAttributeValue") + "=?");

        registerSQL(IS_CONCEPT_VALID, modifySQL("SELECT count(codingSchemeName) AS found"
                + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT)
                + " WHERE conceptCode = ?"
                + " AND codingSchemeName= ? "
                + " AND (isActive = ? OR isActive = ?)"));

        registerSQL(GET_CODE_SYSTEM_NAME, "SELECT codingSchemeName"
                + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME_MULTI_ATTRIBUTES)
                + " WHERE " + (stc_.supports2006Model() ? "typeName" : "attributeName") + "= 'localName'"
                + " AND attributeValue=? ");
        
        registerSQL(GET_CODE_SYSTEM_NAME2, modifySQL("SELECT codingSchemeName"
                    + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME)
                    + " WHERE registeredName {LIKE} ?"));

        registerSQL(GET_CODE_SYSTEM_ID, "SELECT attributeValue"
                + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME_MULTI_ATTRIBUTES)
                + " WHERE codingSchemeName=? "
                + " AND " + (stc_.supports2006Model() ? "typeName" : "attributeName") + " = 'localName'");
        
        registerSQL(GET_CODE_SYSTEM_ID2, "SELECT registeredName"
                    + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME)
                    + " WHERE codingSchemeName=? ");

        registerSQL(GET_DEFAULT_LANGUAGE, "SELECT defaultLanguage"
                + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME)
                + " WHERE codingSchemeName=? ");

        registerSQL(GET_DESIGNATION, "SELECT language, propertyValue, isPreferred"
                + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_PROPERTY)
                + " WHERE conceptCode=?"
                + " AND codingSchemeName=?"
                + " AND " + (supports2006Model() ? "propertyType='presentation'" : "property='textualPresentation'")
                + " AND language=?");

        registerSQL(GET_DESIGNATION_NULL_STRING, "SELECT language, propertyValue, isPreferred"
                + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_PROPERTY)
                + " WHERE conceptCode=?"
                + " AND codingSchemeName=?"
                + " AND " + (supports2006Model() ? "propertyType='presentation'" : "property='textualPresentation'")
                + " AND (language is Null OR language='')");

        registerSQL(IS_PROPERTY_VALID,
                    "SELECT count(" + (supports2006Model() ? "id" : "supportedAttributeValue") + ") as found"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME_SUPPORTED_ATTRIBUTES)
                            + " WHERE codingSchemeName=?"
                            + " AND supportedAttributeTag=?"
                            + " AND " + (supports2006Model() ? "id" : "supportedAttributeValue")+ "=?");

        registerSQL(GET_ASSOCIATION_PROPERTIES,
                    "SELECT isTransitive, isSymmetric, isReflexive"
                            + " FROM " + stc_.getTableName(SQLTableConstants.ASSOCIATION)
                            + " WHERE codingSchemeName=?"
                            + " AND relationName=?"
                            + " AND association=?");

        registerSQL(
                    DOES_DIRECT_RELATION_EXIST,
                    "SELECT targetConceptCode, multiAttributesKey"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_CONCEPT)
                            + " WHERE codingSchemeName=? "
                            + " AND relationName=?"
                            + " AND association=?"
                            + " AND sourceCodingSchemeName=codingSchemeName"
                            + " AND sourceConceptCode=?"
                            + " AND targetCodingSchemeName=codingSchemeName"
                            + " AND targetConceptCode=?");

        registerSQL(
                    GET_TARGETS_OF_SOURCE,
                    "SELECT targetConceptCode"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_CONCEPT)
                            + " WHERE sourceConceptCode=?"
                            + " AND codingSchemeName=? "
                            + " AND relationName=?"
                            + " AND association=?"
                            + " AND sourceCodingSchemeName=codingSchemeName"
                            
                            + " AND targetCodingSchemeName=sourceCodingSchemeName ");

        registerSQL(GET_CODE_DETAILS,
                    "SELECT " + stc_.getTableName(SQLTableConstants.CONCEPT) + ".conceptStatus, " + stc_.getTableName(SQLTableConstants.CODING_SCHEME) + ".representsVersion, " + stc_.getTableName(SQLTableConstants.CONCEPT) + ".conceptCode"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CODING_SCHEME) + " INNER JOIN " + stc_.getTableName(SQLTableConstants.CONCEPT)
                            + " ON " + stc_.getTableName(SQLTableConstants.CODING_SCHEME) + ".codingSchemeName = " + stc_.getTableName(SQLTableConstants.CONCEPT) + ".codingSchemeName"
                            + " WHERE " + stc_.getTableName(SQLTableConstants.CONCEPT) + ".conceptCode=?"
                            + " AND " + stc_.getTableName(SQLTableConstants.CONCEPT) + ".codingSchemeName=?");

        registerSQL(
                    GET_SOURCE_FOR,
                    modifySQL("SELECT association, sourceCodingSchemeName, targetCodingSchemeName, targetConceptCode, multiAttributesKey"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_CONCEPT)
                            + " WHERE sourceConceptCode=?"
                            + " AND codingSchemeName=?"
                            + " AND relationName = ?"
                            + " AND association = ?"));
        
        registerSQL(
                    GET_SOURCE_FOR_ALL,
                    modifySQL("SELECT association, sourceCodingSchemeName, targetCodingSchemeName, targetConceptCode, multiAttributesKey"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_CONCEPT)
                            + " WHERE sourceConceptCode=?"
                            + " AND codingSchemeName=?"
                            + " AND relationName = ?"));

        registerSQL(
                    GET_TARGET_OF,
                    modifySQL("SELECT association, sourceCodingSchemeName, sourceConceptCode, targetCodingSchemeName, multiAttributesKey"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_CONCEPT)
                            + " WHERE targetConceptCode=?"
                            + " AND codingSchemeName = ?"
                            + " AND relationName = ?"
                            + " AND association = ?"));

        registerSQL(
                    GET_TARGET_OF_ALL,
                    modifySQL("SELECT association, sourceCodingSchemeName, sourceConceptCode, targetCodingSchemeName, multiAttributesKey"
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_CONCEPT)
                            + " WHERE targetConceptCode=?"
                            + " AND codingSchemeName = ?"
                            + " AND relationName = ?"));

        registerSQL(GET_ALL_CONCEPTS, "SELECT conceptCode, entityDescription"
                + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT)
                + " WHERE codingSchemeName=?");

        registerSQL(GET_NATIVE_RELATION, modifySQL("SELECT relationName"
                + " FROM " + stc_.getTableName(SQLTableConstants.RELATION)
                + " WHERE codingSchemeName=?"
                + " AND isNative = {true}"));

        registerSQL(GET_SUPPORTED_ASSOCIATIONS, modifySQL("SELECT DISTINCT association"
                + " FROM " + stc_.getTableName(SQLTableConstants.ASSOCIATION)
                + " WHERE codingSchemeName = ?"
                + " AND relationName = ?"));

        registerSQL(GET_CONCEPT_ASSOCIATIONS_TOC_QUALS,
                    modifySQL("SELECT " + (supports2006Model() ? "qualifierName" : "attributeValue")
                            + " FROM " + stc_.getTableName(SQLTableConstants.CONCEPT_ASSOCIATION_TO_C_QUALS)
                            + " WHERE codingSchemeName = ?"
                            + " AND multiAttributesKey= ?"
                            + (supports2006Model() ? "" : " AND attributeName='qualifier'")));
    }

    public static void setBooleanOnPreparedStatment(PreparedStatement statement, int colNumber, Boolean value)
            throws SQLException
    {
        DBUtility.setBooleanOnPreparedStatment(statement, colNumber, value, false, null);
    }

    public static boolean getBooleanResult(ResultSet results, String column) throws SQLException
    {
        return DBUtility.getbooleanFromResultSet(results, column);
    }
    
    public String getTableName(String tableKey)
    {
        return stc_.getTableName(tableKey);
    }
    
    public boolean supports2006Model()
    {
        return stc_.supports2006Model();
    }

    // The following methods are all abstract, so they have to be here, but I don't need them.
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