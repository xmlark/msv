/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.util;

import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.relaxns.grammar.RELAXGrammar;
import com.sun.msv.relaxns.reader.RELAXNSReader;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
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
	public static REDocumentDeclaration loadVGM( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		Grammar g = _loadSchema(url,controller,factory);
		if(g!=null)		return new REDocumentDeclaration(g);
		else			return null;
	}
	
	public static REDocumentDeclaration loadVGM( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		Grammar g = _loadSchema(source,controller,factory);
		if(g!=null)		return new REDocumentDeclaration(g);
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
		ExpressionPool pool = new ExpressionPool();
		
		RELAXNSReader relaxNs = new RELAXNSReader(controller,factory,pool);
		RELAXCoreReader relaxCore = new RELAXCoreReader(controller,factory,pool);
		RELAXNGReader relaxNg = new RELAXNGReader(controller,factory,new RELAXNGReader.StateFactory(),pool);
		TREXGrammarReader trex = new TREXGrammarReader(controller,factory,new TREXGrammarReader.StateFactory(),pool);
		XMLSchemaReader xmlSchema = new XMLSchemaReader(controller,factory,new XMLSchemaReader.StateFactory(),pool);
		
		XMLReader parser = factory.newSAXParser().getXMLReader();
		Sniffer sniffer = new Sniffer(relaxNs,relaxCore,relaxNg,trex,xmlSchema,parser);
		parser.setContentHandler(sniffer);
		parser.setErrorHandler(new GrammarReaderControllerAdaptor(controller));
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
		if(sniffer.winner==relaxNg)		return relaxNg.getResult();
		if(sniffer.winner==relaxNs)		return relaxNs.getResult();
		if(sniffer.winner==relaxCore)	return relaxCore.getResult();
		if(sniffer.winner==trex)		return trex.getResult();
		else							return xmlSchema.getResult();
	}
	
	
	
	private static class Sniffer extends ForkContentHandler
	{
		Sniffer(
			RELAXNSReader relaxNs, RELAXCoreReader relaxCore,
			RELAXNGReader relaxNg,
			TREXGrammarReader trex, XMLSchemaReader xmlSchema,
			XMLReader parser ) {
			
			super(trex,
				new ForkContentHandler(xmlSchema,
					new ForkContentHandler(relaxNg,
						new ForkContentHandler(relaxCore,relaxNs))));
			this.relaxCore = relaxCore;
			this.relaxNs = relaxNs;
			this.relaxNg = relaxNg;
			this.trex = trex;
			this.xmlSchema = xmlSchema;
			this.parser = parser;
		}
		
		private final ContentHandler relaxCore,relaxNs,relaxNg,trex,xmlSchema;
		private final XMLReader parser;

		protected ContentHandler winner;

		
		public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
								throws SAXException {
			if( localName.equals("module") )
				winner = relaxCore;	// assume RELAX Core.
			else
			if( localName.equals("schema") )
				winner = xmlSchema; // assume XML Schema
			else
			if( RELAXNSReader.RELAXNamespaceNamespace.equals(namespaceURI) )
				winner = relaxNs;
			else
			if( RELAXNGReader.RELAXNGNamespace.equals(namespaceURI) )
				winner = relaxNg;
			else
			if( TREXGrammarReader.TREXNamespace.equals(namespaceURI)
			||  namespaceURI.equals("") )
				winner = trex;
			else
				winner = relaxNg;	// otherwise assume RELAX NG
			
			winner.startElement(namespaceURI,localName,qName,atts);
			// redirect all successive events to the winner.
			parser.setContentHandler(winner);
		}
	}
}
