/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.reader;

import java.util.Vector;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.relaxng.testharness.model.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * parses a test suite XML document.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestSuiteReader {
	
	/**
	 * parses a test suite.
	 */
	public static RNGTestSuite parse( InputSource source )
			throws ParserConfigurationException,SAXException, IOException {
		
		return new TestSuiteReader()._parse(source);
	}

	public static RNGTestSuite parse( InputSource source, DocumentBuilderFactory factory )
			throws ParserConfigurationException,SAXException, IOException {
		
		return new TestSuiteReader(factory)._parse(source);
	}
	
	
	/** DOM factory. */
	private DocumentBuilderFactory factory;
	
	private TestSuiteReader() throws ParserConfigurationException {
		factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
	}
	
	private TestSuiteReader( DocumentBuilderFactory factory ) {
		this.factory = factory;
	}
	
	
	private RNGTestSuite _parse( InputSource source )
			throws ParserConfigurationException,SAXException, IOException {
		
		// parse into a DOM tree.
		Document dom = factory.newDocumentBuilder().parse(source);
		return parseTestSuite(dom.getDocumentElement());
	}
	
	/**
	 * parses &lt;testSuite> element into a RNGTestSuite object.
	 */
	private RNGTestSuite parseTestSuite( Element suiteElement ) {
		RNGTestSuite suite = new RNGTestSuite();
		
		{// load test cases.
			
			NodeList lst = suiteElement.getChildNodes();
			int len = lst.getLength();
			for( int i=0; i<len; i++ ) {
				if( lst.item(i).getNodeType()!=Element.ELEMENT_NODE )
					continue;
				
				Element e = (Element)lst.item(i);
				
				if(e.getTagName().equals("testCase")) {
					if( e.getElementsByTagName("validPattern").getLength()!=0 )
						suite.addTest( parseValidTestCase(e) );
					else
						suite.addTest( parseInvalidTestCase(e) );
				}
				if(e.getTagName().equals("testSuite"))
					suite.addTest( parseTestSuite(e) );
				if(e.getTagName().equals("resource"))
					parseResource(suite, (Element)lst.item(i) );
			}
		}
		
		suite.header = parseHeader(suiteElement);
		
		return suite;
	}
	
	/**
	 * parses &lt;testCase> element with &lt;validPattern> into a RNGTestCase object.
	 */
	private RNGValidTestCase parseValidTestCase( Element testCaseNode ) {
		RNGValidTestCase testCase = new RNGValidTestCase();
		
		testCase.header = parseHeader(testCaseNode);
		
		testCase.pattern = parseXMLDocument(
			(Element)testCaseNode.getElementsByTagName("validPattern").item(0) );
		
		{// load valid test documents.
			NodeList lst = testCaseNode.getElementsByTagName("valid");
			int len = lst.getLength();
			testCase.validDocuments = new XMLDocument[len];
			for( int i=0; i<len; i++ )
				testCase.validDocuments[i] = parseXMLDocument( (Element)lst.item(i) );
		}
		
		{// load invalid test documents.
			NodeList lst = testCaseNode.getElementsByTagName("invalid");
			int len = lst.getLength();
			testCase.invalidDocuments = new XMLDocument[len];
			for( int i=0; i<len; i++ )
				testCase.invalidDocuments[i] = parseXMLDocument( (Element)lst.item(i) );
		}
		
		return testCase;
	}

	/**
	 * parses &lt;testCase> element with &lt;invalidPattern> into a RNGTestCase object.
	 */
	private RNGInvalidTestCase parseInvalidTestCase( Element testCaseNode ) {
		RNGInvalidTestCase testCase = new RNGInvalidTestCase();
		
		testCase.header = parseHeader(testCaseNode);
		
		{// load patterns
			NodeList lst = testCaseNode.getElementsByTagName("invalidPattern");
			int len = lst.getLength();
			testCase.patterns = new XMLDocument[len];
			for( int i=0; i<len; i++ )
				testCase.patterns[i] = parseXMLDocument( (Element)lst.item(i) );
		}
		
		return testCase;
	}
	
	/**
	 * parses a header.
	 * 
	 * @param item
	 *		an element that possibly contains a &lt;header> element.
	 */
	private RNGHeader parseHeader( Element item ) {
		// currently, no header field is defined.
		NodeList headers = item.getElementsByTagName("header");
		if( headers.getLength()>0 )
			return new RNGHeader( (Element)headers.item(0) );
		else
			return null;
	}
	
	/**
	 * parses children of an element into one XMLDocument object.
	 */
	private XMLDocument parseXMLDocument( Element owner ) {
		NodeList lst = owner.getChildNodes();
		int len = lst.getLength();
		
		try {
			Document dom = factory.newDocumentBuilder().newDocument();
			for( int i=0; i<len; i++ )
				if( lst.item(i).getNodeType() == owner.ELEMENT_NODE )
					dom.appendChild( dom.importNode( lst.item(i), true ) );
			return new XMLDocumentImpl(dom);
		} catch( ParserConfigurationException e ) {
			// since we've already created one instance,
			// it cannot fail.
			throw new Error();
		}
	}
	
	/**
	 * parses a &lt;resoure> element.
	 */
	private void parseResource( RNGTestSuite suite, Element resourceElement ) {
		String name = resourceElement.getAttribute("href");
		suite.addResource( name, parseXMLDocument(resourceElement) );
	}
}
