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
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * VerifierFactory implementation for TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXFactoryImpl extends FactoryImpl
{
	public TREXFactoryImpl( SAXParserFactory factory ) { super(factory); }

	public Verifier newVerifier( String uri )
		throws VerifierConfigurationException,
			   SAXException
	{
		TREXGrammar g = TREXGrammarReader.parse(uri,factory,new IgnoreController());
		if(g==null)		return null;	// load failure
		return getVerifier(g);
	}

	public Verifier newVerifier( InputSource source )
		throws VerifierConfigurationException,
			   SAXException
	{
		TREXGrammar g = TREXGrammarReader.parse(source,factory,new IgnoreController());
		if(g==null)		return null;	// load failure
		return getVerifier(g);
	}
	
	public Verifier newVerifier( java.io.File source )
		throws VerifierConfigurationException,
			   SAXException
	{
		return newVerifier( source.getAbsolutePath() );
	}
	
	private final Verifier getVerifier( TREXGrammar g )
		throws VerifierConfigurationException,
			   SAXException
	{
		try
		{
			return new VerifierImpl(
				new REDocumentDeclaration(g),
				factory.newSAXParser().getXMLReader() );
		}
		catch( javax.xml.parsers.ParserConfigurationException pce )
		{
			throw new VerifierConfigurationException(pce);
		}
	}
}
