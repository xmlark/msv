/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.ll;

import com.sun.tahiti.runtime.TypeDetecter;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.IOException;

public class Unmarshaller extends TypeDetecter {
	
	private final Binder binder;
	
	public Unmarshaller( BindableGrammar grammar ) {
		super(new REDocumentDeclaration(grammar));
		binder = new Binder(grammar);
		setContentHandler(binder);
	}
	
	public Object getResult() {
		return binder.getResult();
	}
	
	/**
	 * reads an XML document from the specified location and returns
	 * the parsed object.
	 * 
	 * <p>
	 * This method will use the default XML parser to parse the document.
	 */
	public static Object unmarshall( BindableGrammar grammar, String xmlDocument )
		throws IOException, SAXException, ParserConfigurationException {
		
		return unmarshall( grammar, new InputSource(xmlDocument) );
	}
	
	/**
	 * reads an XML document from the specified InputSource and returns
	 * the parsed object.
	 * 
	 * <p>
	 * This method will use the default XML parser to parse the document.
	 */
	public static Object unmarshall( BindableGrammar grammar, InputSource xmlDocument )
		throws IOException, SAXException, ParserConfigurationException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XMLReader reader = factory.newSAXParser().getXMLReader();

		Unmarshaller unmarshaller = new Unmarshaller(grammar);
		reader.setContentHandler(unmarshaller);
		
		reader.parse(xmlDocument);
		
		return unmarshaller.getResult();
	}
}
