/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.util;

import com.sun.tranquilo.reader.relax.RELAXReader;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.GrammarReaderController;
import com.sun.tranquilo.grammar.relax.RELAXGrammar;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.grammar.trex.TREXGrammar;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * loads RELAX or TREX grammar by automatically detecting the language used.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IntelligentLoader
{
	public static TREXDocumentDeclaration loadVGM( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return makeVGM( _loadSchema(url,controller,factory) );
	}
	
	public static TREXDocumentDeclaration loadVGM( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return makeVGM( _loadSchema(source,controller,factory) );
	}
	
	private static TREXDocumentDeclaration makeVGM( Object grammar )
	{
		if( grammar instanceof RELAXGrammar )
			return new TREXDocumentDeclaration(
				((RELAXGrammar)grammar).topLevel,
				(TREXPatternPool)((RELAXGrammar)grammar).pool, true );
		
		if( grammar instanceof TREXGrammar )
			return new TREXDocumentDeclaration( (TREXGrammar)grammar );
		
		return null;
	}
	

	
	/**
	 * returns RELAXGrammar or TREXGrammar, depending on the language used.
	 */
	public static Object loadSchema( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return _loadSchema(url,controller,factory);
	}
	
	/**
	 * returns RELAXGrammar or TREXGrammar, depending on the language used.
	 */
	public static Object loadSchema( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return _loadSchema(source,controller,factory);
	}
	
	private static Object _loadSchema( Object source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		TREXPatternPool pool = new TREXPatternPool();
		RELAXReader relax = new RELAXReader(controller,factory,pool);
		TREXGrammarReader trex = new TREXGrammarReader(controller,factory,pool);
		
		XMLReader parser = factory.newSAXParser().getXMLReader();
		Sniffer sniffer = new Sniffer(relax,trex,parser);
		parser.setContentHandler(sniffer);
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
		if(sniffer.winner==relax)		return relax.getResult();
		else							return trex.getResult();
	}
	
	
	
	private static class Sniffer extends ForkContentHandler
	{
		Sniffer( ContentHandler relax, ContentHandler trex, XMLReader parser )
		{
			super(relax,trex);
			this.relax = relax;
			this.trex = trex;
			this.parser = parser;
		}
		
		private final ContentHandler relax,trex;
		private final XMLReader parser;

		protected ContentHandler winner;

		
		public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
			throws SAXException
		{
			
			if( TREXGrammarReader.TREXNamespace.equals(namespaceURI)
			||  namespaceURI.equals("") )
				winner = trex;
			else
				winner = relax;	// assume RELAX by default.
			
			winner.startElement(namespaceURI,localName,qName,atts);
			// redirect all successive events to the winner.
			parser.setContentHandler(winner);
		}
	}
}
