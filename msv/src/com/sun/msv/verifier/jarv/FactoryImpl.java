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

import org.iso_relax.verifier.*;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.msv.grammar.Grammar;

/**
 * base implementation of RELAXFactoryImpl and TREXFactoryImpl
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class FactoryImpl extends VerifierFactory {
	protected final SAXParserFactory factory;
	
	protected FactoryImpl( SAXParserFactory factory ) { this.factory = factory; }
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
	
	
	
	public Verifier newVerifier( java.io.File source )
		throws VerifierConfigurationException, SAXException {
		return newVerifier( source.getAbsolutePath() );
	}
	
	protected final Verifier getVerifier( Grammar g )
			throws VerifierConfigurationException, SAXException {
		try	{
			return new VerifierImpl(
				new REDocumentDeclaration(g),
				factory.newSAXParser().getXMLReader() );
		} catch( ParserConfigurationException pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
}
