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
import com.sun.msv.reader.GrammarReader;
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
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Vector;


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
	
	/**
	 * returns RELAXGrammar or TREXGrammar, depending on the language used.
	 */
	public static Grammar loadSchema( String source,
		GrammarReaderController controller )
			throws SAXException, ParserConfigurationException, java.io.IOException
	{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		return _loadSchema(source,controller,factory);
	}
	
	private static Grammar _loadSchema( Object source,
		final GrammarReaderController controller,
		final SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		final ExpressionPool pool = new ExpressionPool();
		
		final XMLReader parser = factory.newSAXParser().getXMLReader();
		/*
			Use a "sniffer" handler and decide which reader to use.
			Once the schema language is detected, the appropriate reader
			instance is created and events are passed to that handler.
		
			From the performance perspective, it is important not to
			create unnecessary reader objects. Because readers typically
			have a lot of references to other classes, instanciating a
			reader instance will cause a lot of class loadings in the first time,
			which makes non-trivial difference in the performance.
		*/
		parser.setContentHandler( new DefaultHandler(){
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
				ContentHandler winner;
				// sniff the XML and decide the reader to use.
				if( localName.equals("module") )
					// assume RELAX Core.
					winner = new RELAXCoreReader(controller,factory,pool);
				else
				if( localName.equals("schema") )
					// assume W3C XML Schema
					winner = new XMLSchemaReader(controller,factory,
						new XMLSchemaReader.StateFactory(),pool);
				else
				if( RELAXNSReader.RELAXNamespaceNamespace.equals(namespaceURI) )
					// assume RELAX Namespace
					winner = new RELAXNSReader(controller,factory,pool);
				else
				if( TREXGrammarReader.TREXNamespace.equals(namespaceURI)
				||  namespaceURI.equals("") )
					// assume TREX
					winner = new TREXGrammarReader(controller,factory,
						new TREXGrammarReader.StateFactory(),pool); 
				else
					// otherwise assume RELAX NG
					winner = new RELAXNGReader(controller,factory,
						new RELAXNGReader.StateFactory(),pool);
				
				// simulate the start of the document.
				winner.setDocumentLocator(locator);
				winner.startDocument();
				for( int i=0; i<prefixes.size(); i++ ) {
					String[] d = (String[])prefixes.get(i);
					winner.startPrefixMapping( d[0], d[1] );
				}
				winner.startElement(namespaceURI,localName,qName,atts);
				// redirect all successive events to the winner.
				parser.setContentHandler(winner);
			}
		});
		parser.setErrorHandler(new GrammarReaderControllerAdaptor(controller));
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
		return ((GrammarReader)parser.getContentHandler()).getResultAsGrammar();
	}
	
	
}
