/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.driver.textui;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.relax.RELAXReader;

/**
 * detects whether this XML is TREX  or RELAX.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaDetector extends DefaultHandler
{
	private SchemaDetector(){}
	public static final SAXException relax = new SAXException("relax");
	public static final SAXException trex = new SAXException("trex");
	public static final SAXException unknown = new SAXException("unknown");
															   
	public void startElement( String namespaceURI, String localName, String qName, Attributes atts )
		throws SAXException
	{
		// detection by namespace URI
		if( RELAXReader.RELAXCoreNamespace.equals(namespaceURI) )
			throw relax;
		if( RELAXReader.RELAXNamespaceNamespace.equals(namespaceURI) )
			throw relax;
		if( TREXGrammarReader.TREXNamespace.equals(namespaceURI) )
			throw trex;
		
		if( namespaceURI.equals("") )
			throw trex;	// TREX allows no-namespace
		
		// guessing by namespace URI
		if( namespaceURI!=null )
		{
			if( namespaceURI.indexOf("relax")>=0 )	throw relax;
			if( namespaceURI.indexOf("trex")>=0 )	throw trex;
		}
		
		// no telling.
		throw unknown;
	}
	
	public static SAXException detect( XMLReader reader, InputSource source )
		throws java.io.IOException
	{
		reader.setContentHandler(new SchemaDetector());
		try
		{
			reader.parse(source);
			throw new Error();	// assertion failed
		}
		catch(SAXException e)
		{
			return e;
		}
	}
}
