package org.hl7.CTSVAPI.refImpl;

import org.hl7.CTSVAPI.*;

import edu.mayo.informatics.cts.utility.CTSConfigurator;

/**
 * <pre>
 * Title:        OperationsHolder.java
 * Description:  Class to hold the runtime and browser implementations, so that I only have to make 
 * one connection each while running tests, instead of one per test.
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
 * @version 1.0 - cvs $Revision: 1.3 $ checked in on $Date: 2005/10/14 15:44:11 $
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
            roiSQLLite_ = org.hl7.CTSVAPI.sqlLite.refImpl.RuntimeOperationsImpl._interface();
        }
        return roiSQLLite_;

    }
    
    public BrowserOperations getBrowserSQLLiteOperations() throws Exception
    {
        if (boiSQLLite_ == null)
        {
            boiSQLLite_ = org.hl7.CTSVAPI.sqlLite.refImpl.BrowserOperationsImpl._interface();
        }
        return boiSQLLite_;

    }
    
    public RuntimeOperations getRuntimeSQLOperations() throws Exception
    {
        if (roiSQL_ == null)
        {
            roiSQL_ = org.hl7.CTSVAPI.sql.refImpl.RuntimeOperationsImpl._interface();
        }
        return roiSQL_;

    }
    
    public BrowserOperations getBrowserSQLOperations() throws Exception
    {
        if (boiSQL_ == null)
        {
            boiSQL_ = org.hl7.CTSVAPI.sql.refImpl.BrowserOperationsImpl._interface();
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