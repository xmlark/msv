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
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParserFactory;
import com.sun.tranquilo.grammar.relax.RELAXGrammar;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.reader.relax.RELAXReader;
import com.sun.tranquilo.reader.util.IgnoreController;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;

public class FactoryImpl extends VerifierFactory
{
	protected final SAXParserFactory factory;
	
	public FactoryImpl( SAXParserFactory factory ) { this.factory = factory; }
	
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
	
	public boolean isFeature(String feature)
		throws SAXNotRecognizedException
	{
		throw new SAXNotRecognizedException(feature);
	}

	public void setFeature(String feature, boolean value)
		throws SAXNotRecognizedException
	{
		throw new SAXNotRecognizedException(feature);
	}

	public Object getProperty(String property)
		throws SAXNotRecognizedException
	{
		throw new SAXNotRecognizedException(property);
	}

	public void setProperty(String property, Object value)
		throws SAXNotRecognizedException
	{
		throw new SAXNotRecognizedException(property);
	}
}
