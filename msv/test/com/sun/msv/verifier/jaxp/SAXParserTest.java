package com.sun.msv.verifier.jaxp;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Tests the typical use cases of JAXP masquerading with SAXParserFactory.
 */
public class SAXParserTest extends TestCase
{
    public SAXParserTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(SAXParserTest.class);
    }
    
//    
//    
// use of DocumentBuilderFactory without schema
//====================================================
//
//
    public void testScenario1_1() throws Exception {
        SAXParserFactory factory = new SAXParserFactoryImpl();
        doTest1(factory);
    }

    public void testScenario1_2() throws Exception {
        SAXParserFactory factory = new SAXParserFactoryImpl();
        factory.setNamespaceAware(true);
        doTest1(factory);
    }
    
    private void doTest1( SAXParserFactory factory ) throws Exception {
        // use it without any schema
        SAXParser parser = factory.newSAXParser();
        
        parse(parser,true);

        // set the incorrect schema and expect the error
        try {
            parser.setProperty( Const.SCHEMA_PROPNAME,
                new InputSource( new StringReader(TestConst.incorrectSchema) ) );
            fail("incorrect schema was accepted");
        } catch( org.xml.sax.SAXNotRecognizedException e ) {
            // it should throw an exception
        }

        // set the schema.
        parser.setProperty( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.rngSchema) ) );
        
        // then parse again.
        parse(parser,false);
        
        // set another schema.
        parser.setProperty( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.xsdSchema) ) );
        
        // then parse again.
        parse(parser,false);
    }

    
    
    
    
    public void testScenario3_1() throws Exception {
        SAXParserFactory factory = new SAXParserFactoryImpl();
        doTest3(factory);
    }
    public void testScenario3_2() throws Exception {
        SAXParserFactory factory = new SAXParserFactoryImpl();
        factory.setNamespaceAware(true);
        doTest3(factory);
    }
    
    private void doTest3( SAXParserFactory factory ) throws Exception {
        SAXParser parser = factory.newSAXParser();
        
        // set the schema.
        parser.setProperty( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.rngSchema) ) );
        
        parser.parse( new InputSource(
                    new StringReader("<root foo='abc'>abc</root>")) ,
            new Handler() );
    }
    
    
    private void parse( SAXParser parser, boolean expectationForInvalid ) throws Exception {
        // parse test. test the invalid case first to make sure that this failure
        // won't affect the rest of the story.
        
        for( int i=0; i<2; i++ ) {
            try {
                parser.parse( new InputSource(
                    new StringReader(TestConst.invalidDocument)),
                    new Handler() );
                if(expectationForInvalid==false)
                    fail("failed to reject an invalid document");
            } catch( Exception e ) {
                if(expectationForInvalid==true)
                    fail("failed to accept a valid document");
            }
        
            parser.parse( new InputSource(
                new StringReader(TestConst.validDocument)),
                new Handler() );
        }
    }
    
    
    private class Handler extends DefaultHandler {
        public void error( SAXParseException e ) throws SAXParseException {
            throw e;
        }
    }
}
