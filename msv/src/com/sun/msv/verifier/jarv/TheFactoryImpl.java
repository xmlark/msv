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
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * VerifierFactory implementation that automatically detects the schema language.
 * 
 * To use this class, see 
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TheFactoryImpl extends FactoryImpl {
	
	public TheFactoryImpl( SAXParserFactory factory ) {
		super(factory);
	}

	/**
	 * use default SAXParser.
	 */
	public TheFactoryImpl() {
		super(createNewSAXParserFactory());
	}
	
	private static SAXParserFactory createNewSAXParserFactory() {
		SAXParserFactory f = SAXParserFactory.newInstance();
		f.setNamespaceAware(true);
		return f;
	}

	public Verifier newVerifier( String uri )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = GrammarLoader.loadSchema(uri,new IgnoreController(),factory);
			if(g==null)		return null;	// load failure
			return getVerifier(g);
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}

	public Verifier newVerifier( InputSource source )
		throws VerifierConfigurationException, SAXException {
		try {
			Grammar g = GrammarLoader.loadSchema(source,new IgnoreController(),factory);
			if(g==null)		return null;	// load failure
			return getVerifier(g);
		} catch( Exception pce ) {
			throw new VerifierConfigurationException(pce);
		}
	}
}
