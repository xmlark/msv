/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.jarv;

import java.io.IOException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.iso_relax.verifier.*;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.util.VerificationErrorHandlerImpl;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;

/**
 * base implementation of RELAXFactoryImpl and TREXFactoryImpl
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class FactoryImpl extends VerifierFactory {
	protected final SAXParserFactory factory;
	
	protected FactoryImpl( SAXParserFactory factory ) {
		this.factory = factory;
	}
	protected FactoryImpl() {
		factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
	}
	
	public boolean isFeature(String feature)
		throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(feature);
	}

	public void setFeature(String feature, boolean value)
		throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(feature);
	}

	public Object getProperty(String property)
		throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(property);
	}

	public void setProperty(String property, Object value)
		throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(property);
	}
	
	

	/**
	 * parses a Grammar from the specified source.
	 * return null if an error happens.
	 */
	protected abstract Grammar parse(
		InputSource source, GrammarReaderController controller )
			throws SAXException,VerifierConfigurationException;
	protected abstract Grammar parse(
		String source, GrammarReaderController controller )
			throws SAXException,VerifierConfigurationException;
	
	
	public Schema compileSchema( String uri )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(uri,new IgnoreController());
			if(g==null)		return null;	// load failure
			return new SchemaImpl(g,factory);
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
	
	public Schema compileSchema( InputSource source )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(source,new IgnoreController());
			if(g==null)		return null;	// load failure
			return new SchemaImpl(g,factory);
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
	
	
	
	public Verifier newVerifier( String uri )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(uri,new IgnoreController());
			if(g==null)		return null;	// load failure
			return getVerifier(g);
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}

	public Verifier newVerifier( InputSource source )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(source,new IgnoreController());
			if(g==null)		return null;	// load failure
			return getVerifier(g);
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
	
	
	/**
	 * gets the VGM by sniffing its type.
	 * 
	 * <p>
	 * To validate XML Schema correctly, we need to use the specialized VGM.
	 */
	static IVerifier createVerifier( Grammar g ) {
		if( g instanceof XMLSchemaGrammar )
			return new IDConstraintChecker(
				(XMLSchemaGrammar)g,
				new VerificationErrorHandlerImpl() );
		else
			return new com.sun.msv.verifier.Verifier(
				new REDocumentDeclaration(g),
				new VerificationErrorHandlerImpl() );
	}
	
	protected final Verifier getVerifier( Grammar g )
			throws VerifierConfigurationException, SAXException {
		try	{
			return new VerifierImpl(
				createVerifier(g), factory.newSAXParser().getXMLReader() );
		} catch( ParserConfigurationException pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
}
