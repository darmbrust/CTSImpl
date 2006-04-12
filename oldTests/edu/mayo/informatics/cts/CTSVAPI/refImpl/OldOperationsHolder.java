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
package edu.mayo.informatics.cts.CTSVAPI.refImpl;


import org.hl7.CTSVAPI.BrowserOperations;
import org.hl7.CTSVAPI.RuntimeOperations;

import edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.BrowserOperationsImpl;
import edu.mayo.informatics.cts.CTSVAPI.ldap.refImpl.RuntimeOperationsImpl;
import edu.mayo.informatics.cts.utility.CTSConfigurator;

/**
 * Class to hold the runtime and browser implementations, so that I only have to make 
 * one connection each while running tests, instead of one per test.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class OldOperationsHolder
{
    private static OldOperationsHolder operationsHolder_;
    private RuntimeOperations       roi_;
    private BrowserOperations       boi_;
    private RuntimeOperations       roiSQLLite_;
    private BrowserOperations       boiSQLLite_;
    private RuntimeOperations       roiSQL_;
    private BrowserOperations       boiSQL_;

    public static OldOperationsHolder _interface()
    {
        if (operationsHolder_ == null)
        {
            operationsHolder_ = new OldOperationsHolder();
        }
        return operationsHolder_;
    }

    private OldOperationsHolder()
    {
        try
        {
            CTSConfigurator._instance().initialize();
        }
        catch (Exception e1)
        {
            System.out.println("Problem loading configuration information.  Failure likely.");
        }
    }

    public RuntimeOperations getRuntimeSQLLiteOperations() throws Exception
    {
        if (roiSQLLite_ == null)
        {
            roiSQLLite_ = edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.RuntimeOperationsImpl._interface();
        }
        return roiSQLLite_;

    }
    
    public BrowserOperations getBrowserSQLLiteOperations() throws Exception
    {
        if (boiSQLLite_ == null)
        {
            boiSQLLite_ = edu.mayo.informatics.cts.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl._interface();
        }
        return boiSQLLite_;

    }
    
    public RuntimeOperations getRuntimeSQLOperations() throws Exception
    {
        if (roiSQL_ == null)
        {
            roiSQL_ = edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.RuntimeOperationsImpl._interface();
        }
        return roiSQL_;

    }
    
    public BrowserOperations getBrowserSQLOperations() throws Exception
    {
        if (boiSQL_ == null)
        {
            boiSQL_ = edu.mayo.informatics.cts.CTSVAPI.sql.refImpl.BrowserOperationsImpl._interface();
        }
        return boiSQL_;

    }

    public RuntimeOperations getRuntimeLDAPOperations() throws Exception
    {
        if (roi_ == null)
        {
            roi_ = RuntimeOperationsImpl._interface();
        }
        return roi_;

    }

    public BrowserOperations getBrowserLDAPOperations() throws Exception
    {
        if (boi_ == null)
        {
            boi_ = BrowserOperationsImpl._interface();
        }
        return boi_;

    }

}