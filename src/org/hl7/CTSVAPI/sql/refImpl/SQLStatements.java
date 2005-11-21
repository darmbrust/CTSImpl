package org.hl7.CTSVAPI.sql.refImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.hl7.utility.CTSConstants;
import org.lexgrid.commons.managedobj.FindException;
import org.lexgrid.commons.managedobj.ManagedObjIF;
import org.lexgrid.commons.managedobj.service.jdbc.JDBCBaseService;
import org.lexgrid.commons.managedobj.service.jdbc.JDBCConnectionDescriptor;
import org.lexgrid.commons.managedobj.service.jdbc.JDBCConnectionPoolPolicy;

import edu.mayo.informatics.lexgrid.convert.utility.DBUtility;
import edu.mayo.informatics.lexgrid.convert.utility.GenericSQLModifier;

/**
 * <pre>
 * Title:        SQLStatements.java
 * Description:  All of the sql statements used in the vapi implementation are defined here.
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
 * @version 1.0 - cvs $Revision: 1.35 $ checked in on $Date: 2005/10/17 14:52:03 $
 */

public class SQLStatements extends JDBCBaseService
{
    public final static Logger logger               = Logger.getLogger("org.hl7.VAPI_sql_queries");

    private static Hashtable   sqlStatementsHolder_ = new Hashtable();
    private GenericSQLModifier gSQLMod_;

    public static SQLStatements instance(String username, String password, String url, String driver) throws Exception
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

    private SQLStatements(String username, String password, String url, String driver) throws Exception
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
        getConnectionPool().returnObject(conn);

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
    
    public boolean isDB2()
    {
        return gSQLMod_.getDatabaseType().startsWith("DB2");
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
        logger.debug("Registering sql statments");
        registerSQL(
                    GET_CODE_SYSTEM_DETAILS,
                    "SELECT codingScheme.codingSchemeName, codingScheme.registeredName, codingScheme.copyright, codingScheme.formalName, codingScheme.entityDescription, codingScheme.representsVersion"
                            + " FROM codingScheme"
                            + " WHERE codingScheme.codingSchemeName Like ?");

        registerSQL(GET_CODE_SYSTEM_SUPPORTED_PROPERTYS,
                    "SELECT codingSchemeSupportedAttributes.supportedAttributeValue"
                            + " FROM codingSchemeSupportedAttributes"
                            + " WHERE codingSchemeSupportedAttributes.codingSchemeName=?"
                            + " AND codingSchemeSupportedAttributes.supportedAttributeTag=?");
        
        registerSQL(GET_ID_FOR_RELATIONSHIP_CODE_SYSTEM_NAME,
                    "SELECT codingSchemeSupportedAttributes.urn"
                            + " FROM codingSchemeSupportedAttributes"
                            + " WHERE codingSchemeSupportedAttributes.codingSchemeName=?"
                            + " AND codingSchemeSupportedAttributes.supportedAttributeTag='CodingScheme'"
                            + " AND codingSchemeSupportedAttributes.supportedAttributeValue=?");

        registerSQL(IS_CONCEPT_VALID, modifySQL("SELECT count(concept.codingSchemeName) AS found"
                + " FROM concept"
                + " WHERE concept.conceptCode = ?"
                + " AND concept.codingSchemeName= ? "
                + " AND (concept.isActive = ? OR concept.isActive = ?)"));

        registerSQL(GET_CODE_SYSTEM_NAME, "SELECT codingSchemeMultiAttributes.codingSchemeName"
                + " FROM codingSchemeMultiAttributes"
                + " WHERE attributeName = 'localName'"
                + " AND attributeValue=? ");
        
        registerSQL(GET_CODE_SYSTEM_NAME2, modifySQL("SELECT codingScheme.codingSchemeName"
                    + " FROM codingScheme"
                    + " WHERE registeredName {LIKE} ?"));

        registerSQL(GET_CODE_SYSTEM_ID, "SELECT codingSchemeMultiAttributes.attributeValue"
                + " FROM codingSchemeMultiAttributes"
                + " WHERE codingSchemeName=? "
                + " AND attributeName = 'localName'");
        
        registerSQL(GET_CODE_SYSTEM_ID2, "SELECT codingScheme.registeredName"
                    + " FROM codingScheme"
                    + " WHERE codingSchemeName=? ");

        registerSQL(GET_DEFAULT_LANGUAGE, "SELECT codingScheme.defaultLanguage"
                + " FROM codingScheme"
                + " WHERE codingScheme.codingSchemeName=? ");

        registerSQL(GET_DESIGNATION, "SELECT conceptProperty.language, conceptProperty.propertyValue, conceptProperty.isPreferred"
                + " FROM conceptProperty"
                + " WHERE conceptProperty.conceptCode=?"
                + " AND conceptProperty.codingSchemeName=?"
                + " AND conceptProperty.property='textualPresentation'"
                + " AND conceptProperty.language=?");

        registerSQL(GET_DESIGNATION_NULL_STRING, "SELECT conceptProperty.language, conceptProperty.propertyValue, conceptProperty.isPreferred"
                + " FROM conceptProperty"
                + " WHERE conceptProperty.conceptCode=?"
                + " AND conceptProperty.codingSchemeName=?"
                + " AND conceptProperty.property='textualPresentation'"
                + " AND (conceptProperty.language is Null OR conceptProperty.language='')");

        registerSQL(IS_PROPERTY_VALID,
                    "SELECT count(codingSchemeSupportedAttributes.supportedAttributeValue) as found"
                            + " FROM codingSchemeSupportedAttributes"
                            + " WHERE codingSchemeSupportedAttributes.codingSchemeName=?"
                            + " AND codingSchemeSupportedAttributes.supportedAttributeTag=?"
                            + " AND codingSchemeSupportedAttributes.supportedAttributeValue=?");

        registerSQL(GET_ASSOCIATION_PROPERTIES,
                    "SELECT association.isTransitive, association.isSymmetric, association.isReflexive"
                            + " FROM association"
                            + " WHERE association.codingSchemeName=?"
                            + " AND association.relationName=?"
                            + " AND association.association=?");

        registerSQL(
                    DOES_DIRECT_RELATION_EXIST,
                    "SELECT targetConceptCode, multiAttributesKey"
                            + " FROM conceptAssociationsToConcept"
                            + " WHERE conceptAssociationsToConcept.codingSchemeName=? "
                            + " AND conceptAssociationsToConcept.relationName=?"
                            + " AND conceptAssociationsToConcept.association=?"
                            + " AND conceptAssociationsToConcept.sourceCodingSchemeName=conceptAssociationsToConcept.codingSchemeName"
                            + " AND conceptAssociationsToConcept.sourceConceptCode=?"
                            + " AND conceptAssociationsToConcept.targetCodingSchemeName=conceptAssociationsToConcept.codingSchemeName"
                            + " AND conceptAssociationsToConcept.targetConceptCode=?");

        registerSQL(
                    GET_TARGETS_OF_SOURCE,
                    "SELECT conceptAssociationsToConcept.targetConceptCode"
                            + " FROM conceptAssociationsToConcept"
                            + " WHERE conceptAssociationsToConcept.sourceConceptCode=?"
                            + " AND conceptAssociationsToConcept.codingSchemeName=? "
                            + " AND conceptAssociationsToConcept.relationName=?"
                            + " AND conceptAssociationsToConcept.association=?"
                            + " AND conceptAssociationsToConcept.sourceCodingSchemeName=conceptAssociationsToConcept.codingSchemeName"
                            
                            + " AND conceptAssociationsToConcept.targetCodingSchemeName=conceptAssociationsToConcept.sourceCodingSchemeName ");

        registerSQL(GET_CODE_DETAILS,
                    "SELECT concept.conceptStatus, codingScheme.representsVersion, concept.conceptCode"
                            + " FROM codingScheme INNER JOIN concept "
                            + " ON codingScheme.codingSchemeName = concept.codingSchemeName"
                            + " WHERE concept.conceptCode=?"
                            + " AND concept.codingSchemeName=?");

        registerSQL(
                    GET_SOURCE_FOR,
                    modifySQL("SELECT conceptAssociationsToConcept.association, conceptAssociationsToConcept.sourceCodingSchemeName, conceptAssociationsToConcept.targetCodingSchemeName, conceptAssociationsToConcept.targetConceptCode, conceptAssociationsToConcept.multiAttributesKey"
                            + " FROM conceptAssociationsToConcept"
                            + " WHERE conceptAssociationsToConcept.sourceConceptCode=?"
                            + " AND conceptAssociationsToConcept.codingSchemeName=?"
                            + " AND conceptAssociationsToConcept.relationName = ?"
                            + " AND conceptAssociationsToConcept.association = ?"));
        
        registerSQL(
                    GET_SOURCE_FOR_ALL,
                    modifySQL("SELECT conceptAssociationsToConcept.association, conceptAssociationsToConcept.sourceCodingSchemeName, conceptAssociationsToConcept.targetCodingSchemeName, conceptAssociationsToConcept.targetConceptCode, conceptAssociationsToConcept.multiAttributesKey"
                            + " FROM conceptAssociationsToConcept"
                            + " WHERE conceptAssociationsToConcept.sourceConceptCode=?"
                            + " AND conceptAssociationsToConcept.codingSchemeName=?"
                            + " AND conceptAssociationsToConcept.relationName = ?"));

        registerSQL(
                    GET_TARGET_OF,
                    modifySQL("SELECT conceptAssociationsToConcept.association, conceptAssociationsToConcept.sourceCodingSchemeName, conceptAssociationsToConcept.sourceConceptCode, conceptAssociationsToConcept.targetCodingSchemeName, conceptAssociationsToConcept.multiAttributesKey"
                            + " FROM conceptAssociationsToConcept"
                            + " WHERE conceptAssociationsToConcept.targetConceptCode=?"
                            + " AND conceptAssociationsToConcept.codingSchemeName = ?"
                            + " AND conceptAssociationsToConcept.relationName = ?"
                            + " AND conceptAssociationsToConcept.association = ?"));

        registerSQL(
                    GET_TARGET_OF_ALL,
                    modifySQL("SELECT conceptAssociationsToConcept.association, conceptAssociationsToConcept.sourceCodingSchemeName, conceptAssociationsToConcept.sourceConceptCode, conceptAssociationsToConcept.targetCodingSchemeName, conceptAssociationsToConcept.multiAttributesKey"
                            + " FROM conceptAssociationsToConcept"
                            + " WHERE conceptAssociationsToConcept.targetConceptCode=?"
                            + " AND conceptAssociationsToConcept.codingSchemeName = ?"
                            + " AND conceptAssociationsToConcept.relationName = ?"));

        registerSQL(GET_ALL_CONCEPTS, "SELECT concept.conceptCode, concept.entityDescription"
                + " FROM concept"
                + " WHERE concept.codingSchemeName=?");

        registerSQL(GET_NATIVE_RELATION, modifySQL("SELECT relationName"
                + " FROM relation"
                + " WHERE relation.codingSchemeName=?"
                + " AND isNative = {true}"));

        registerSQL(GET_SUPPORTED_ASSOCIATIONS, modifySQL("SELECT DISTINCT association"
                + " FROM association"
                + " WHERE association.codingSchemeName = ?"
                + " AND association.relationName = ?"));

        registerSQL(GET_CONCEPT_ASSOCIATIONS_TOC_QUALS,
                    modifySQL("SELECT attributeValue, dataType, associationQualifierValue"
                            + " FROM conceptAssociationsToCMultiAttrib"
                            + " WHERE codingSchemeName = ?"
                            + " AND multiAttributesKey= ?"
                            + " AND attributeName='qualifier'"));
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