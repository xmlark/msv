package com.sun.msv.verifier.jaxp;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests the typical use cases of JAXP masquerading with DocumentBuilderFactory
 */
public class DOMBuilderTest extends TestCase
{
    public DOMBuilderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(DOMBuilderTest.class);
    }
    

    
//    
//    
// use of DocumentBuilderFactory without schema
//====================================================
//
//
    public void testScenario1_1() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        doTest1(factory);
    }

    public void testScenario1_2() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        doTest1(factory);
    }
    
    private void doTest1( DocumentBuilderFactory factory ) throws Exception {
        // use it without any schema
        parse(factory.newDocumentBuilder(),true);

        // set the incorrect schema and expect the error
        try {
            factory.setAttribute( Const.SCHEMA_PROPNAME,
                new InputSource( new StringReader(TestConst.incorrectSchema) ) );
            fail("incorrect schema was accepted");
        } catch( IllegalArgumentException e ) {
            // it should throw this exception
        }

        // set the schema.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.rngSchema) ) );
        
        // then parse again.
        parse(factory.newDocumentBuilder(),false);
        
        // set another schema.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.xsdSchema) ) );
        
        // then parse again.
        parse(factory.newDocumentBuilder(),false);
    }

    
    public void testScenario2_1() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        doTest2(factory);
    }
        
    public void testScenario2_2() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        doTest2(factory);
    }
        
    private void doTest2( DocumentBuilderFactory factory ) throws Exception {
        DocumentBuilder builder1 = factory.newDocumentBuilder();
        
        // set the schema.
        // since builder1 is already created, it should not be affected
        // by this change.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.xsdSchema) ) );
        DocumentBuilder builder2 = factory.newDocumentBuilder();
        
        parse(builder2,false);
        parse(builder1,true);
    }
    
    
    
    public void testScenario3_1() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        doTest3(factory);
    }
    public void testScenario3_2() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        doTest3(factory);
    }
    
    private void doTest3( DocumentBuilderFactory factory ) throws Exception {
        // set the schema.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.rngSchema) ) );
        
        factory.newDocumentBuilder().parse( new InputSource(
                    new StringReader("<root foo='abc'>abc</root>")) );
    }
    
    
    private void parse( DocumentBuilder builder, boolean expectationForInvalid ) throws Exception {
        // parse test. test the invalid case first to make sure that this failure
        // won't affect the rest of the story.
        
        for( int i=0; i<2; i++ ) {
            try {
                builder.parse( new InputSource(
                    new StringReader(TestConst.invalidDocument)) );
                if(expectationForInvalid==false)
                    fail("failed to reject an invalid document");
            } catch( SAXException e ) {
                if(expectationForInvalid==true)
                    fail("failed to accept a valid document");
            }
        
            builder.parse( new InputSource(
                new StringReader(TestConst.validDocument)) );
        }
    }
}
