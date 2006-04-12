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
package edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl;

import java.rmi.RemoteException;

import javax.naming.NamingException;

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
import edu.mayo.informatics.cts.utility.Utility.LDAPConnectionInfo;

/**
 * Implementation of CodeMapping operations against the ldap database.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class CodeMappingOperationsImpl implements CodeMappingOperations
{

    public final static Logger logger = Logger.getLogger("edu.mayo.informatics.cts.VAPI_ldap_CodeMapper");
    
    /**
     * Create a new codeMappingOperations using current values of CTSConstants.
     * @return
     * @throws UnexpectedError
     */
    static public synchronized CodeMappingOperations _interface() throws UnexpectedError

    {
        CodeMappingOperationsImpl svc = new CodeMappingOperationsImpl(null, null, null, null, false, false);
        return (CodeMappingOperations) svc;
    }

    
    /**
     * Return a reference to a new CodeMappingOperations.
     * 
     * @param userName - the name to use to connect to the service - if null, loaded from properties
     * @param userPassword - the password used to connect to the service - if null, loaded from properties
     * @param address - The address of the service to connect to - if null, loaded from properties
     * @param service - The Service to connect to - if null, loaded from properties
     * @return reference or null if unable to construct it
     * @throws NamingException - if unable to connect to service
     */
    static public synchronized CodeMappingOperations _interface(String userName, String userPassword, String address,
            String service, boolean loadProperties, boolean initLogger) throws UnexpectedError

    {
        CodeMappingOperationsImpl svc = new CodeMappingOperationsImpl(userName, userPassword, address, service, loadProperties, initLogger);
        return (CodeMappingOperations) svc;
    }

    /**
     * Return a reference to a new Code Mapping Operations. Parses the connection information from an XML string.
     * 
     * @param xmlLexGridLDAPString The XML String.
     * @param loadProperties whether or not to load the properties file
     * @param initLogger whether or not to init the logger.
     * @return reference or null if unable to construct it
     * @throws UnexpectedError if something goes wrong.
     */
    static public synchronized CodeMappingOperations _interface(String xmlLexGridLDAPString, String username,
            String password, boolean loadProperties, boolean initLogger) throws UnexpectedError

    {
        CodeMappingOperationsImpl svc;
        try
        {
            LDAPConnectionInfo temp = Utility.parseLDAPXML(xmlLexGridLDAPString);
            if (username == null || username.length() == 0)
            {
                username = temp.username;
            }
            if (password == null || password.length() == 0)
            {
                password = temp.password;
            }
            svc = new CodeMappingOperationsImpl(username, password, temp.address, temp.service, loadProperties, initLogger);
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new UnexpectedError("Problem parsing the the XML connection info. "
                    + e.toString()
                    + " "
                    + (e.getCause() == null ? ""
                            : e.getCause().toString()));
        }

        return (CodeMappingOperations) svc;
    }

    /**
     * This method is here for AXIS to work properly. End users should use the _interface method.
     */
    public CodeMappingOperationsImpl()
    {
        try
        {

            CTSConfigurator._instance().initialize();
            //this.rootContext_ = initContext(null, null, null, null);
            //this.codingSchemesContext_ = (AutoReconnectDirContext) rootContext_.lookup("dc=CodingSchemes");
           // searchControls = new SearchControls();
           // this.nameParser = codingSchemesContext_.getNameParser(codingSchemesContext_.getNameInNamespace());
            //roi_ = new RuntimeOperationsImpl(null, null, null, null, false);
        }
        catch (Exception e)
        {
            logger.error("Constructor error", e);
        }
    }

    protected CodeMappingOperationsImpl(String userName, String userPassword, String address, String service,
            boolean loadProperties, boolean initLogger) throws UnexpectedError
    {
        try
        {
            if (loadProperties)
            {
                CTSConfigurator._instance().initialize(initLogger);
            }
            //this.rootContext_ = initContext(userName, userPassword, address, service);
            //this.codingSchemesContext_ = (AutoReconnectDirContext) rootContext_.lookup("dc=CodingSchemes");
           // searchControls = new SearchControls();
           // this.nameParser = codingSchemesContext_.getNameParser(codingSchemesContext_.getNameInNamespace());
            //roi_ = new RuntimeOperationsImpl(userName, userPassword, address, service, false);
        }
        catch (Exception e)
        {
            logger.error("Constructor error", e);
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
            return "Mayo CTS Vapi SQL Code Mapping Operations";
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
                    + " Final Draft, against the SQL backend.";
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