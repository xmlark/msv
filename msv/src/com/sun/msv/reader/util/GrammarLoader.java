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

import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.ng.comp.RELAXNGCompReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.relaxns.grammar.RELAXGrammar;
import com.sun.msv.relaxns.reader.RELAXNSReader;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.Util;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Vector;


/**
 * loads any supported grammar (except XML DTD)
 * by automatically detecting the schema language.
 * 
 * <p>
 * The static version of loadVGM/loadSchema methods provides simple ways to
 * load a grammar.
 * 
 * <p>
 * Another way to use GrammarLoader is
 * 
 * <ol>
 *  <li>To instanciate an object of GrammarLoader
 *  <li>call setXXX methods to configure the parameters
 *  <li>call loadSchema/loadVGM methods (possibly multiple times) to
 *      load grammars.
 * </ol>
 * 
 * This approach will give you finer control.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarLoader
{
//
// static utility methods
//==============================
//
	/**
	 * parses the specified schema and returns the result as a VGM.
	 * 
	 * This method is an utility method for those applications which
	 * don't need AGM (e.g., a single thread application).
	 * 
	 * @return
	 *		null if there was an error in the grammar.
	 */
	public static REDocumentDeclaration loadVGM( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		Grammar g = loadSchema(url,controller,factory);
		if(g!=null)		return wrapByVGM(g);
		else			return null;
	}
	
	public static REDocumentDeclaration loadVGM( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		Grammar g = loadSchema(source,controller,factory);
		if(g!=null)		return wrapByVGM(g);
		else			return null;
	}
	
	private static REDocumentDeclaration wrapByVGM( Grammar g ) {
		if( g instanceof XMLSchemaGrammar )
			return new XSREDocDecl((XMLSchemaGrammar)g);
		else
			return new REDocumentDeclaration(g);
	}
	
	
	/**
	 * parses the specified schema and returns the result as a VGM.
	 * 
	 * This method uses the default SAX parser and throws an exception
	 * if there is an error in the schema.
	 * 
	 * @return
	 *		non-null valid VGM object.
	 */
	public static REDocumentDeclaration loadVGM( String url )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		try {
			return loadVGM(url, new ThrowController(), null );
		} catch( GrammarLoaderException e ) {
			throw e.e;
		}
	}
	public static REDocumentDeclaration loadVGM( InputSource source )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		try {
			return loadVGM(source, new ThrowController(), null );
		} catch( GrammarLoaderException e ) {
			throw e.e;
		}
	}
	
	/** wrapper exception so that we can throw it from the GrammarReaderController. */
	private static class GrammarLoaderException extends RuntimeException {
		GrammarLoaderException( SAXException e ) {
			super(e.getMessage());
			this.e = e;
		}
		public final SAXException e;
	}
	private static class ThrowController implements GrammarReaderController {
		public void warning( Locator[] locs, String errorMessage ) {}
		public void error( Locator[] locs, String errorMessage, Exception nestedException ) {
			for( int i=0; i<locs.length; i++ )
				if(locs[i]!=null)
					throw new GrammarLoaderException(
						new SAXParseException(errorMessage,locs[i],nestedException));
			
			throw new GrammarLoaderException(
				new SAXException(errorMessage,nestedException));
		}
		public InputSource resolveEntity( String p, String s ) { return null; }
		
	}

	
	
	/**
	 * parses the specified schema and returns the result as a Grammar object.
	 * 
	 * @return
	 *		null if there was an error in the grammar.
	 */
	public static Grammar loadSchema( String url,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		GrammarLoader loader = new GrammarLoader();
		loader.setController(controller);
		loader.setSAXParserFactory(factory);
		return loader.parse(url);
	}
	
	public static Grammar loadSchema( InputSource source,
		GrammarReaderController controller,
		SAXParserFactory factory )
		throws SAXException, ParserConfigurationException, java.io.IOException
	{
		GrammarLoader loader = new GrammarLoader();
		loader.setController(controller);
		loader.setSAXParserFactory(factory);
		return loader.parse(source);
	}
	
	/**
	 * returns a thread-safe AGM object, depending on the language used.
	 */
	public static Grammar loadSchema( String source,
		GrammarReaderController controller )
			throws SAXException, ParserConfigurationException, java.io.IOException
	{
		GrammarLoader loader = new GrammarLoader();
		loader.setController(controller);
		return loader.parse(source);
	}

	/**
	 * returns a thread-safe AGM object, depending on the language used.
	 */
	public static Grammar loadSchema( InputSource source,
		GrammarReaderController controller )
			throws SAXException, ParserConfigurationException, java.io.IOException
	{
		GrammarLoader loader = new GrammarLoader();
		loader.setController(controller);
		return loader.parse(source);
	}
	
	/**
	 * parses the specified schema and returns the result as a Grammar object.
	 * 
	 * This method uses the default SAX parser and throws an exception
	 * if there is an error in the schema.
	 * 
	 * @return
	 *		a non-null valid Grammar.
	 */
	public static Grammar loadSchema( String url )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		try {
			return loadSchema(url, new ThrowController(), null );
		} catch( GrammarLoaderException e ) {
			throw e.e;
		}
	}
	public static Grammar loadSchema( InputSource source )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		try {
			return loadSchema(source, new ThrowController(), null );
		} catch( GrammarLoaderException e ) {
			throw e.e;
		}
	}
	


//
// finer control can be achieved by using the following methods.
//=================================================================
	public GrammarLoader() {}
	
	private SAXParserFactory factory;
	/**
	 * sets the factory object which is used to create XML parsers
	 * to parse schema files.
	 * The factory must be configured to namespace aware.
	 * 
	 * <p>
	 * If no SAXParserFactory is set, then the default parser is used.
	 * (The parser that can be obtained by SAXParserFactory.newInstance()).
	 */
	public void setSAXParserFactory( SAXParserFactory factory ) {
		this.factory = factory;
	}
	public SAXParserFactory getSAXParserFactory() {
		if(factory==null) {
			factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
		}
		return factory;
	}
	
	private GrammarReaderController controller;
	/**
	 * sets the GrammarReaderController object that will control
	 * various aspects of the parsing. If not set, no error report will be
	 * done.
	 */
	public void setController( GrammarReaderController controller ) {
		this.controller = controller;
	}
	public GrammarReaderController getController() {
		if(controller==null)
			controller = new GrammarReaderController() {
				public void warning( Locator[] locs, String errorMessage ) {}
				public void error( Locator[] locs, String errorMessage, Exception nestedException ) {}
				public InputSource resolveEntity( String s, String p ) { return null; }
			};
		return controller;
	}
	
	private ExpressionPool pool;
	/**
	 * sets the ExpressionPool object that will be used during the loading process.
	 * If not set, a fresh one is used for each time the loadXXX method is called.
	 */
	public void setPool( ExpressionPool pool ) {
		this.pool = pool;
	}
	public ExpressionPool getPool() {
		if( pool==null)		return new ExpressionPool();
		else				return pool;
	}

	
	
	
	public Grammar parse( InputSource source )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		
		return _loadSchema(source);
	}
	
	public Grammar parse( String url )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		
		return _loadSchema(url);
	}
	
	public REDocumentDeclaration parseVGM( String url )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		
		Grammar g = _loadSchema(url);
		if(g==null)		return null;
		else			return new REDocumentDeclaration(g);
	}
	
	public REDocumentDeclaration parseVGM( InputSource source )
		throws SAXException, ParserConfigurationException, java.io.IOException {
		
		Grammar g = _loadSchema(source);
		if(g==null)		return null;
		else			return new REDocumentDeclaration(g);
	}
	
	
	
	/**
	 * Checks if the specified name has ".dtd" extension.
	 */
	private boolean hasDTDextension( String name ) {
		if(name==null)		return false;
		
		int idx = name.length()-4;
		if(idx<0)			return false;
		
		return name.substring(idx).equalsIgnoreCase(".dtd");
	}
	
	/**
	 * Actual "meat" of parsing schema.
	 */
	private Grammar _loadSchema( Object source )
			throws SAXException, ParserConfigurationException, java.io.IOException {
		
		// perform the auto detection to decide whether
		// it is XML syntax based schema or DTD.
		// TODO: implement more serious detection algorithm.
				
		// use the file extension to decide language type.
		// sure this is a sloppy job, but works in practice.
		// and easy to implement.
		boolean isDTD = false;
		if( source instanceof String ) {
			if( hasDTDextension( (String)source) )
				isDTD = true;
		}
		if( source instanceof InputSource ) {
			if( hasDTDextension( ((InputSource)source).getSystemId() ) )
				isDTD = true;
		}
		
		if(isDTD) {
			// load as DTD
			if( source instanceof String )
				source = Util.getInputSource((String)source);
			return DTDReader.parse((InputSource)source,getController());
		}

		// otherwise this schema is an XML syntax based schema.
		
		
		
		final XMLReader parser = getSAXParserFactory().newSAXParser().getXMLReader();
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
					winner = new RELAXCoreReader(
						getController(),getSAXParserFactory(),getPool());
				else
				if( localName.equals("schema") )
					// assume W3C XML Schema
					winner = new XMLSchemaReader(
						getController(), getSAXParserFactory(),
						new XMLSchemaReader.StateFactory(), getPool() );
				else
				if( RELAXNSReader.RELAXNamespaceNamespace.equals(namespaceURI) )
					// assume RELAX Namespace
					winner = new RELAXNSReader(
						getController(),getSAXParserFactory(),getPool());
				else
				if( TREXGrammarReader.TREXNamespace.equals(namespaceURI)
				||  namespaceURI.equals("") )
					// assume TREX
					winner = new TREXGrammarReader(getController(),getSAXParserFactory(),
						new TREXGrammarReader.StateFactory(),getPool()); 
				else
					// otherwise assume RELAX NG
					winner = new RELAXNGCompReader(getController(),getSAXParserFactory(),
						new RELAXNGCompReader.StateFactory(),getPool() );
				
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
		parser.setErrorHandler(new GrammarReaderControllerAdaptor(getController()));
		if( source instanceof String )	parser.parse( (String)source );
		else							parser.parse( (InputSource)source );
		
		return ((GrammarReader)parser.getContentHandler()).getResultAsGrammar();
	}
	
	
}
