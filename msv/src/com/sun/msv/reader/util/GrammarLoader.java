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

import com.sun.tranquilo.reader.relax.core.RELAXCoreReader;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.GrammarReaderController;
import com.sun.tranquilo.relaxns.grammar.RELAXGrammar;
import com.sun.tranquilo.relaxns.reader.RELAXNSReader;
import com.sun.tranquilo.grammar.Grammar;
import com.sun.tranquilo.grammar.relax.RELAXModule;
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
 * loads RELAX module, RELAX grammar, or TREX grammar
 * by automatically detecting the language used.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarLoader
{
	public static TREXDocumentDeclaration loadVGM( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		Grammar g = _loadSchema(url,controller,factory);
		if(g!=null)		return new TREXDocumentDeclaration(g);
		else			return null;
	}
	
	public static TREXDocumentDeclaration loadVGM( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		Grammar g = _loadSchema(source,controller,factory);
		if(g!=null)		return new TREXDocumentDeclaration(g);
		else			return null;
	}
	

	
	/**
	 * returns RELAXGrammar or TREXGrammar, depending on the language used.
	 */
	public static Grammar loadSchema( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return _loadSchema(url,controller,factory);
	}
	
	/**
	 * returns RELAXGrammar or TREXGrammar, depending on the language used.
	 */
	public static Grammar loadSchema( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return _loadSchema(source,controller,factory);
	}
	
	private static Grammar _loadSchema( Object source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		TREXPatternPool pool = new TREXPatternPool();
		RELAXNSReader relaxNs = new RELAXNSReader(controller,factory,pool);
		RELAXCoreReader relaxCore = new RELAXCoreReader(controller,factory,pool);
		TREXGrammarReader trex = new TREXGrammarReader(controller,factory,pool);
		
		XMLReader parser = factory.newSAXParser().getXMLReader();
		Sniffer sniffer = new Sniffer(relaxNs,relaxCore,trex,parser);
		parser.setContentHandler(sniffer);
		parser.setErrorHandler(new GrammarReaderControllerAdaptor(controller));
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
		if(sniffer.winner==relaxNs)		return relaxNs.getResult();
		if(sniffer.winner==relaxCore)	return relaxCore.getResult();
		else							return trex.getResult();
	}
	
	
	
	private static class Sniffer extends ForkContentHandler
	{
		Sniffer( RELAXNSReader relaxNs, RELAXCoreReader relaxCore, TREXGrammarReader trex, XMLReader parser )
		{
			super(trex,new ForkContentHandler(relaxCore,relaxNs));
			this.relaxCore = relaxCore;
			this.relaxNs = relaxNs;
			this.trex = trex;
			this.parser = parser;
		}
		
		private final ContentHandler relaxCore,relaxNs,trex;
		private final XMLReader parser;

		protected ContentHandler winner;

		
		public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
			throws SAXException
		{
			if( localName.equals("module") )
				winner = relaxCore;	// assume RELAX Core.
			else
			if( TREXGrammarReader.TREXNamespace.equals(namespaceURI)
			||  namespaceURI.equals("") )
				winner = trex;
			else
				winner = relaxNs;	// otherwise assume RELAX namespace
			
			winner.startElement(namespaceURI,localName,qName,atts);
			// redirect all successive events to the winner.
			parser.setContentHandler(winner);
		}
	}
}
