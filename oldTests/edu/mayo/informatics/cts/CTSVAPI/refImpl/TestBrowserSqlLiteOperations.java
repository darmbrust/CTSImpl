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

import junit.framework.TestCase;

/**
 * JUnit test cases for BrowserOperationsImpl, this time run against the sql implementation.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
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