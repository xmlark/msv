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

import com.sun.tahiti.runtime.sm.MarshallableObject;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.psvi.TypeDetector;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
//import com.sun.msv.verifier.ValidityViolation;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.ErrorHandler;
import java.io.IOException;

/**
 * controls the overall unmarshalling process.
 * 
 * This class receives type-augmented SAX events and pass it to
 * Binder.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Unmarshaller extends TypeDetector {
	
	private final Binder binder;
	
	public Unmarshaller( BindableGrammar grammar ) {
		super( new REDocumentDeclaration(grammar),
			new ErrorHandler(){
				// throw an exception if any error happens.
				public void warning( SAXParseException e ) {}
				public void fatalError( SAXParseException e ) throws UnmarshallingException {
					throw new UnmarshallingException(e);
				}
				public void error( SAXParseException e ) throws UnmarshallingException {
					throw new UnmarshallingException(e);
				}
			});
		binder = new Binder(grammar);
		setContentHandler(binder);
	}
	
	public MarshallableObject getResult() {
		return (MarshallableObject)binder.getResult();
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
