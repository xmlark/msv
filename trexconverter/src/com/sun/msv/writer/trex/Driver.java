/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer.trex;

import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.grammar.Grammar;
import java.io.PrintWriter;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;

/**
 * converts any supported languages into the equivalent TREX grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver {
	public static void main( String[] args ) throws Exception {
		if( args.length!=1 ) {
			System.out.println( localize(MSG_USAGE) );
			return;
		}
		
		boolean dtd;
		String schema;
		
		if( args[0].equalsIgnoreCase("-dtd") ) {
			dtd = true;
			schema = args[1];
		} else {
			dtd = false;
			schema = args[0];
		}
		
		// use Xerces as a parser.
		SAXParserFactoryImpl pf = new SAXParserFactoryImpl();
		pf.setNamespaceAware(true);
		
		// load a grammar.
		Grammar g;
		
		if( dtd ) {
			g = DTDReader.parse(
				com.sun.msv.driver.textui.Driver.getInputSource(schema),
				new com.sun.msv.driver.textui.DebugController( false,false, System.err ) );
		} else {
			g = GrammarLoader.loadSchema( schema,
				new com.sun.msv.driver.textui.DebugController( false,false, System.err ),
				pf );
		}
		if( g==null ) {
			System.err.println(localize(MSG_GRAMMAR_ERROR));
			return;
		}
		
		TREXWriter writer = new TREXWriter();
		// use XMLSerializer of Apache to serialize SAX event into plain text.
		// OutputFormat specifies "pretty printing".
		writer.setDocumentHandler(
			new XMLSerializer( new PrintWriter(System.out),
			new OutputFormat("xml",null,true) ) );
		// visit TREXGrammar and generate its XML representation.
		writer.write( g );
	}
	
	
	public static String localize( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.writer.relaxng.Messages").getString(propertyName);
	    return java.text.MessageFormat.format(format, args );
	}
	public static String localize( String prop ) {
		return localize(prop,null);
	}
	public static String localize( String prop, Object arg1 ) {
		return localize(prop,new Object[]{arg1});
	}
	public static String localize( String prop, Object arg1, Object arg2 ) {
		return localize(prop,new Object[]{arg1,arg2});
	}
	
	private static final String MSG_USAGE = // arg:0
		"Driver.Usage";
	private static final String MSG_GRAMMAR_ERROR = // arg:0
		"Driver.GrammarError";
}
