/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer.relaxng;

import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.writer.trex.TREXWriter;
import java.io.PrintWriter;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;

/**
 * converts any supported languages into the equivalent RELAX NG grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver {
	public static void main( String[] args ) throws Exception {
		
		if( args.length!=1 ) {
			System.out.println(
				"Usage: RELAXNGConverter <schema filename/URL>\n");
			return;
		}
		
		// use Xerces as a parser.
		SAXParserFactoryImpl pf = new SAXParserFactoryImpl();
		pf.setNamespaceAware(true);
		
		// load a grammar.
		Grammar g = GrammarLoader.loadSchema( args[0],
			new com.sun.msv.driver.textui.DebugController( false,false, System.err ),
			pf );
		if( g==null ) {
			System.err.println("failed to load the grammar");
			return;
		}
		
		RELAXNGWriter writer = new RELAXNGWriter();
		// use XMLSerializer of Apache to serialize SAX event into plain text.
		// OutputFormat specifies "pretty printing".
		writer.setDocumentHandler(
			new XMLSerializer( new PrintWriter(System.out),
			new OutputFormat("xml",null,true) ) );
		// visit TREXGrammar and generate its XML representation.
		writer.write( g );
	}
}
