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
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.ForkContentHandler;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

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
		final GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		final TRELAXNGReader relaxNg = new TRELAXNGReader(controller,factory);
		final TXMLSchemaReader xmlSchema = new TXMLSchemaReader(controller,factory);
		
		final XMLReader parser = factory.newSAXParser().getXMLReader();
		final GrammarReader[] winner = new GrammarReader[1];

		
		parser.setContentHandler( new DefaultHandler() {

			private Locator locator;
			private Vector prefixes = new Vector();
			public void setDocumentLocator( Locator loc ) {
				this.locator = loc;
			}
			public void startPrefixMapping( String prefix, String uri ) {
				prefixes.add( new String[]{prefix,uri} );
			}
		
			public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
									throws SAXException {
				if( localName.equals("schema") )
					winner[0] = xmlSchema; // assume XML Schema
				else
					winner[0] = relaxNg;
				
				// simulate the start of the document.
				winner[0].setDocumentLocator(locator);
				winner[0].startDocument();
				for( int i=0; i<prefixes.size(); i++ ) {
					String[] d = (String[])prefixes.get(i);
					winner[0].startPrefixMapping( d[0], d[1] );
				}
				winner[0].startElement(namespaceURI,localName,qName,atts);
				// redirect all successive events to the winner.
				parser.setContentHandler(winner[0]);
				parser.setErrorHandler(
					new com.sun.msv.reader.Controller(controller));
			}
		});
		
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
//		if( relaxNg.hadError || xmlSchema.hadError )	return null;
		if(winner[0]==relaxNg)			return relaxNg.getAnnotatedResult();
		else							return xmlSchema.getAnnotatedResult();
	}
	
	
	
}
