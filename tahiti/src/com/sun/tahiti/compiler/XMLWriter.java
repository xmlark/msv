/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler;

import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.SAXException;
import com.sun.msv.writer.ContentHandlerAdaptor;

/**
 * helper class for generating XML through {@link DocumentHandler}.
 */
public class XMLWriter {
	
	public final DocumentHandler	handler;
	
	public XMLWriter( DocumentHandler handler ) {
		this.handler = handler;
	}
	
	/** creates a new XMLWriter from a ContentHandler. */
	public static XMLWriter fromContentHandler( ContentHandler handler ) {
		return new XMLWriter( new ContentHandlerAdaptor(handler) );
	}
	
// primitive write methods
//-----------------------------------------
	public void element( String name ) {
		element( name, new String[0] );
	}
	public void element( String name, String[] attributes ) {
		start(name,attributes);
		end(name);
	}
	public void element( String name, String contents ) {
		start(name);
		characters(contents);
		end(name);
	}
	public void start( String name ) {
		start(name, new String[0] );
	}
	public void start( String name, String[] attributes ) {
		
		// create attributes.
		AttributeListImpl as = new AttributeListImpl();
		for( int i=0; i<attributes.length; i+=2 )
			as.addAttribute( attributes[i], "", attributes[i+1] );
		
		try {
			handler.startElement( name, as );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	public void end( String name ) {
		try {
			handler.endElement( name );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	public void pi( String target, String data ) {
		try {
			handler.processingInstruction( target, data );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	public void characters( String str ) {
		try {
			handler.characters( str.toCharArray(), 0, str.length() );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	
	public static class SAXWrapper extends RuntimeException {
		public SAXException e;
		SAXWrapper( SAXException e ) { this.e=e; }
	}

}
