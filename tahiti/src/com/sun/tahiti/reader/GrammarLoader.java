/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader;

import com.sun.tahiti.grammar.AnnotatedGrammar;
import com.sun.tahiti.reader.relaxng.TRELAXNGReader;
import com.sun.tahiti.reader.xmlschema.TXMLSchemaReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.ForkContentHandler;
import com.sun.msv.reader.util.GrammarReaderControllerAdaptor;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * loads Tahiti-annotated grammar
 * by automatically detecting the language used.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarLoader {
	
	/**
	 * returns the parsed grammar, or null otherwise.
	 */
	public static AnnotatedGrammar loadSchema( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return _loadSchema(url,controller,factory);
	}
	
	/**
	 * returns the parsed grammar, or null otherwise.
	 */
	public static AnnotatedGrammar loadSchema( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		return _loadSchema(source,controller,factory);
	}
	
	/**
	 * returns the parsed grammar, or null otherwise.
	 */
	public static AnnotatedGrammar loadSchema( String source,
		GrammarReaderController controller )
			throws SAXException, ParserConfigurationException, java.io.IOException
	{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		return _loadSchema(source,controller,factory);
	}
	
	private static AnnotatedGrammar _loadSchema( Object source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		TRELAXNGReader relaxNg = new TRELAXNGReader(controller,factory);
		TXMLSchemaReader xmlSchema = new TXMLSchemaReader(controller,factory);
		
		XMLReader parser = factory.newSAXParser().getXMLReader();
		Sniffer sniffer = new Sniffer(relaxNg,xmlSchema,parser);
		parser.setContentHandler(sniffer);
		parser.setErrorHandler(new GrammarReaderControllerAdaptor(controller));
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
		if( relaxNg.hadError || xmlSchema.hadError )	return null;
		if(sniffer.winner==relaxNg)		return relaxNg.getAnnotatedResult();
		else							return xmlSchema.getAnnotatedResult();
	}
	
	
	
	private static class Sniffer extends ForkContentHandler
	{
		Sniffer(
			TRELAXNGReader relaxNg, TXMLSchemaReader xmlSchema,
			XMLReader parser ) {
			
			super(xmlSchema,relaxNg);
			this.relaxNg = relaxNg;
			this.xmlSchema = xmlSchema;
			this.parser = parser;
		}
		
		private final ContentHandler relaxNg,xmlSchema;
		private final XMLReader parser;

		protected ContentHandler winner;

		
		public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
								throws SAXException {
			if( localName.equals("schema") )
				winner = xmlSchema; // assume XML Schema
			else
				winner = relaxNg;
			
			winner.startElement(namespaceURI,localName,qName,atts);
			// redirect all successive events to the winner.
			parser.setContentHandler(winner);
		}
	}
}
