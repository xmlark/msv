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
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.util.ErrorHandlerImpl;
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
			Grammar g = parse(uri,new ThrowController());
			if(g==null)
				// theoretically this isn't possible because we throw an exception
				// if an error happens.
				throw new VerifierConfigurationException("unable to parse the schema");
			return new SchemaImpl(g,factory);
		} catch( WrapperException we ) {
			throw we.e;
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
	
	public Schema compileSchema( InputSource source )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(source,new ThrowController());
			if(g==null)
				// theoretically this isn't possible because we throw an exception
				// if an error happens.
				throw new VerifierConfigurationException("unable to parse the schema");
			return new SchemaImpl(g,factory);
		} catch( WrapperException we ) {
			throw we.e;
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
	
	
	
	public Verifier newVerifier( String uri )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(uri,new ThrowController());
			if(g==null)
				// theoretically this isn't possible because we throw an exception
				// if an error happens.
				throw new VerifierConfigurationException("unable to parse the schema");
			return getVerifier(g);
		} catch( WrapperException we ) {
			throw we.e;
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}

	public Verifier newVerifier( InputSource source )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = parse(source,new ThrowController());
			if(g==null)
				// theoretically this isn't possible because we throw an exception
				// if an error happens.
				throw new VerifierConfigurationException("unable to parse the schema");
			return getVerifier(g);
		} catch( WrapperException we ) {
			throw we.e;
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
				new ErrorHandlerImpl() );
		else
			return new com.sun.msv.verifier.Verifier(
				new REDocumentDeclaration(g),
				new ErrorHandlerImpl() );
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
	
	
	
	/** wrapper exception so that we can throw it from the GrammarReaderController. */
	private static class WrapperException extends RuntimeException {
		WrapperException( SAXException e ) {
			super(e.getMessage());
			this.e = e;
		}
		public final SAXException e;
	}
	private static class ThrowController implements GrammarReaderController {
		public void warning( Locator[] locs, String errorMessage ) {}
		public void error( Locator[] locs, String errorMessage, Exception nestedException ) {
			for( int i=0; i<locs.length; i++ )
				if(locs[i]!=null)
					throw new WrapperException(
						new SAXParseException(errorMessage,locs[i],nestedException));
			
			throw new WrapperException(
				new SAXException(errorMessage,nestedException));
		}
		public InputSource resolveEntity( String p, String s ) { return null; }
		
	}
	
}
