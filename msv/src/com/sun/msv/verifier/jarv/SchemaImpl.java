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

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.iso_relax.verifier.*;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.DocumentDeclaration;

/**
 * base implementation of RELAXFactoryImpl and TREXFactoryImpl
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaImpl implements Schema
{
	protected final Grammar grammar;
	protected final SAXParserFactory factory;
	
	protected SchemaImpl( Grammar grammar, SAXParserFactory factory ) {
		this.grammar = grammar;
		this.factory = factory;
	}
	
	public Verifier newVerifier() throws VerifierConfigurationException {
		return new VerifierImpl( FactoryImpl.createVerifier(grammar), createXMLReader() );
	}
	
	private synchronized XMLReader createXMLReader() throws VerifierConfigurationException {
		// SAXParserFactory is not thread-safe. Thus we need to
		// synchronize this method.
		try {
			return factory.newSAXParser().getXMLReader();
		} catch( SAXException e ) {
			throw new VerifierConfigurationException(e);
		} catch( ParserConfigurationException e ) {
			throw new VerifierConfigurationException(e);
		}
	}
}
