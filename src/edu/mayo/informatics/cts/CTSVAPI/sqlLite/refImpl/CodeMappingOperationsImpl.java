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
package edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.hl7.CTSVAPI.AmbiguousMapRequest;
import org.hl7.CTSVAPI.CTSVersionId;
import org.hl7.CTSVAPI.CodeMap;
import org.hl7.CTSVAPI.CodeMappingOperations;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.MapNameSourceMismatch;
import org.hl7.CTSVAPI.MapNameTargetMismatch;
import org.hl7.CTSVAPI.MappedConceptCode;
import org.hl7.CTSVAPI.MappingNotAvailable;
import org.hl7.CTSVAPI.UnableToMap;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownConceptCode;
import org.hl7.CTSVAPI.UnknownMapName;

import edu.mayo.informatics.cts.utility.CTSConfigurator;
import edu.mayo.informatics.cts.utility.CTSConstants;
import edu.mayo.informatics.cts.utility.Utility;
import edu.mayo.informatics.cts.utility.Utility.SQLConnectionInfo;

/**
 * Implementation of CodeMapping operations against the sql database.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class CodeMappingOperationsImpl implements CodeMappingOperations
{

    public final static Logger logger = Logger.getLogger("edu.mayo.informatics.cts.VAPI_sqlLite_CodeMapper");
    
    /**
     * Create a new CodeMappingOperations instance using the current CTSConstants values.
     * @return
     * @throws UnexpectedError
     */
    static public synchronized CodeMappingOperations _interface() throws UnexpectedError
    {
        CodeMappingOperationsImpl svc = new CodeMappingOperationsImpl(null, null, null, null, false, false);
        return (CodeMappingOperations) svc;
    }
    
    /**
     * This is the method you want to use to instantiate a local RuntimeOperations object.
     * 
     * @param userName loaded from props if null
     * @param userPassword loaded from props if null
     * @param server loaded from props if null
     * @param driver loaded from props if null
     * @param initLogger whether or not to init the logger and load props.
     * @return
     */
    static public synchronized CodeMappingOperations _interface(String userName, String userPassword, String server,
            String driver, boolean loadProps, boolean initLogger) throws UnexpectedError
    {
        CodeMappingOperationsImpl svc = new CodeMappingOperationsImpl(userName, userPassword, server, driver, loadProps, initLogger);
        return (CodeMappingOperations) svc;
    }

    /**
     * Return a reference to a new Browser Operations. Parses the connection information from an XML string.
     * 
     * @param xmlLexGridLDAPString The XML String.
     * @param initLogger whether or not to init the logger.
     * @return reference or null if unable to construct it
     * @throws UnexpectedError if something goes wrong.
     */
    static public synchronized CodeMappingOperations _interface(String xmlLexGridSQLString, String username,
            String password, boolean loadProps, boolean initLogger) throws UnexpectedError

    {
        CodeMappingOperationsImpl svc;
        try
        {
            SQLConnectionInfo temp = Utility.parseSQLXML(xmlLexGridSQLString);
            if (username == null || username.length() == 0)
            {
                username = temp.username;
            }
            if (password == null || password.length() == 0)
            {
                password = temp.password;
            }
            svc = new CodeMappingOperationsImpl(username, password, temp.server, temp.driver, loadProps, initLogger);
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Unexpected Error", e);;
            throw new UnexpectedError("Problem parsing the the XML connection info. "
                    + e.toString()
                    + " "
                    + (e.getCause() == null ? ""
                            : e.getCause().toString()));
        }
        return (CodeMappingOperations) svc;
    }

    /**
     * This method is here for AXIS. Use the _interface method to instantiate this object locally.
     */
    public CodeMappingOperationsImpl()
    {
        try
        {
            CTSConfigurator._instance().initialize();
            logger.debug("CTSVAPI.CodeMappingOperationImpl SQL Constructor called");
            //queries_ = SQLStatements.instance(null, null, null, null);
            //roi_ = new RuntimeOperationsImpl(null, null, null, null, false);
        }
        catch (Exception e)
        {
            logger.error("BrowserOperationsImpl Constructor error", e);
        }
    }

    protected CodeMappingOperationsImpl(String username, String password, String server, String driver, 
            boolean loadProps, boolean initLogger)
            throws UnexpectedError
    {
        try
        {
            if (loadProps)
            {
                CTSConfigurator._instance().initialize(initLogger);
            }
            logger.debug("CTSVAPI.CodeMappingOperationImpl Constructor called");
            //queries_ = SQLStatements.instance(username, password, server, driver);
            //roi_ = new RuntimeOperationsImpl(username, password, server, driver, false);
        }
        catch (Exception e)
        {
            logger.error("CodeMappingOperationsImpl Constructor error", e);
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }
    

    public CodeMap[] getSupportedMaps() throws RemoteException, UnexpectedError
    {
        // TODO: Implement getSupportedMaps
        throw new java.lang.UnsupportedOperationException("Method getSupportedMaps not yet implemented.");
    }

    public MappedConceptCode mapConceptCode(ConceptId in0, String in1, String in2) throws RemoteException,
            AmbiguousMapRequest, MapNameSourceMismatch, UnexpectedError, MapNameTargetMismatch, MappingNotAvailable,
            UnableToMap, UnknownConceptCode, UnknownMapName, UnknownCodeSystem
    {
        // TODO: Implement mapConceptCode
        throw new java.lang.UnsupportedOperationException("Method mapConceptCode not yet implemented.");
    }

    public String getServiceName() throws UnexpectedError
    {
        logger.debug("getServicename called.");
        try
        {
            return "Mayo CTS Vapi SQLLite Code Mapping Operations";
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceVersion() throws UnexpectedError
    {
        logger.debug("getServiceVersion called.");
        try
        {
            return CTSConstants.FULL_VERSION;
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public String getServiceDescription() throws UnexpectedError
    {
        logger.debug("getServiceDescription called.");
        try
        {
            return "This is the Mayo Implementation of the CTS "
                    + CTSConstants.MAJOR_VERSION
                    + "."
                    + CTSConstants.MINOR_VERSION
                    + " Final Draft, against the SQLLite backend.";
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    public CTSVersionId getCTSVersion() throws UnexpectedError
    {
        logger.debug("getCTSVersion Called");
        try
        {
            CTSVersionId verID = new CTSVersionId();
            verID.setMajor(Short.parseShort(CTSConstants.MAJOR_VERSION));
            verID.setMinor(Short.parseShort(CTSConstants.MINOR_VERSION));
            return verID;
        }
        catch (Exception e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

}