/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.jarv;

import org.iso_relax.verifier.*;
import com.sun.tranquilo.grammar.relax.RELAXGrammar;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.reader.relax.RELAXReader;
import com.sun.tranquilo.reader.util.IgnoreController;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;

/**
 * VerifierFactory implementation of RELAX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXFactoryImpl extends FactoryImpl
{
	public RELAXFactoryImpl( SAXParserFactory factory ) { super(factory); }

	public Verifier newVerifier( String uri )
		throws VerifierConfigurationException,
			   SAXException
	{
		TREXPatternPool pool = new TREXPatternPool();
		RELAXGrammar g = RELAXReader.parse(uri,factory,new IgnoreController(),pool);
		if(g==null)		return null;	// load failure
		return getVerifier(g);
	}

	public Verifier newVerifier( InputSource source )
		throws VerifierConfigurationException,
			   SAXException
	{
		TREXPatternPool pool = new TREXPatternPool();
		RELAXGrammar g = RELAXReader.parse(source,factory,new IgnoreController(),pool);
		if(g==null)		return null;	// load failure
		return getVerifier(g);
	}
	
	public Verifier newVerifier( java.io.File source )
		throws VerifierConfigurationException,
			   SAXException
	{
		return newVerifier( source.getAbsolutePath() );
	}
	
	private final Verifier getVerifier( RELAXGrammar g )
		throws VerifierConfigurationException,
			   SAXException
	{
		try
		{
			return new VerifierImpl(
				new TREXDocumentDeclaration(g.topLevel,(TREXPatternPool)g.pool,true),
				factory.newSAXParser().getXMLReader() );
		}
		catch( javax.xml.parsers.ParserConfigurationException pce )
		{
			throw new VerifierConfigurationException(pce);
		}
	}
}
