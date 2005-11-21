package org.hl7.CTSVAPI.refImpl;

import junit.framework.*;

/**
 * <pre>
 * Title:        TestBrowserSqlOperations.java
 * Description:  JUnit test cases for BrowserOperationsImpl, this time run against the sql implementation.
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
 * @version 1.0 - cvs $Revision: 1.4 $ checked in on $Date: 2005/04/12 18:58:28 $
 */
public class TestBrowserSqlLiteOperations extends TestCase
{
    private TestBrowserOperations tests;

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TestBrowserOperations.class);
    }

    protected void setUp() throws Exception
    {
        tests = new TestBrowserOperations(OldOperationsHolder._interface().getBrowserSQLLiteOperations());
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testGetServiceName() throws Exception
    {
        tests.testGetServiceName();
    }
    public void testGetSupportedMatchAlgorithms() throws Exception
    {
        tests.testGetSupportedMatchAlgorithms();
    }

    public void testGetServiceVersion() throws Exception
    {
        tests.testGetServiceVersion();
    }

    public void testGetCTSVersion() throws Exception
    {
        tests.testGetCTSVersion();
    }

    public void testGetServiceDescription() throws Exception
    {
        tests.testGetServiceDescription();
    }
     
    public void testGetSupportedCodeSystems() throws Exception
    {
        tests.testGetSupportedCodeSystems();
    }
    
    public void testLookupConceptCodesByDesignation() throws Exception
    {
        tests.testLookupConceptCodesByDesignation();
    }
    
    public void testLookupConceptCodesByProperty() throws Exception
    {
        tests.testLookupConceptCodesByProperty();
    }
    
    public void testLookupDesignations() throws Exception
    {
        tests.testLookupDesignations();
    }
    
    public void testLookupProperties() throws Exception
    {
        tests.testLookupProperties();
    }
    
    public void testLookupCompleteCodedConcept() throws Exception
    {
        tests.testLookupCompleteCodedConcept();
    }
    
    public void testExpandCodeExpansionContextForward() throws Exception
    {
        tests.testExpandCodeExpansionContextForward();
    }
    
    public void testLookupCodeExpansionForward() throws Exception
    {
        tests.testLookupCodeExpansionForward();
    }
    public void testExpandCodeExpansionContextReverse() throws Exception
    {
        tests.testExpandCodeExpansionContextForward();
    }
    
    public void testLookupCodeExpansionReverse() throws Exception
    {
        tests.testLookupCodeExpansionForward();
    }
}