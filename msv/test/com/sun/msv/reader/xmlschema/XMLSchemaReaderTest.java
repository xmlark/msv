package com.sun.msv.reader.xmlschema;

import junit.framework.*;
import util.*;
import com.sun.msv.reader.GrammarReader;

public class XMLSchemaReaderTest extends TestCase
{
	public XMLSchemaReaderTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(XMLSchemaReaderTest.class);
	}
	
	/** tests the existence of all messages */
	public void testMessages() throws Exception {
		javax.xml.parsers.SAXParserFactory factory =
			new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		
		final XMLSchemaReader reader = new XMLSchemaReader(null,factory);
		
		Checker checker = new Checker(){
			public void check( String propertyName ) {
				// if the specified property doesn't exist, this will throw an error
				System.out.println(
					reader.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
			}
		};
		
		String prefixes[] = new String[]{"ERR_","WRN_"};
		
		for( int i=0; i<prefixes.length; i++ ) {
			ResourceChecker.check( XMLSchemaReader.class, prefixes[i], checker );
			ResourceChecker.check( GrammarReader.class, prefixes[i], checker );
		}
	}
}
