/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.generator;

import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.compiler.Controller;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.sm.MarshallerGenerator;
import com.sun.tahiti.grammar.*;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import java.util.Iterator;
import java.io.IOException;

/**
 * produces XML representation of the object model.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class XMLGenerator
{
	XMLGenerator( AnnotatedGrammar grammar, Symbolizer symbolizer, Controller controller ) {
		this.grammar = grammar;
		this.symbolizer = symbolizer;
		this.controller = controller;
	}
	
	private final AnnotatedGrammar grammar;
	private final Symbolizer symbolizer;
	private final Controller controller;
		
	void generate() throws SAXException, IOException {
		try {
			ClassItem[] types = grammar.getClasses();
			for( int i=0; i<types.length; i++ )
				write( types[i] );
		
			InterfaceItem[] itfs = grammar.getInterfaces();
			for( int i=0; i<itfs.length; i++ )
				write( itfs[i] );
		
		} catch( XMLWriter.SAXWrapper w ) {
			throw w.e;
		}
	}

	/** writes TypeItem to an XML file. */
	private void write( TypeItem type ) throws SAXException, IOException {
				
		DocumentHandler outHandler = new XMLSerializer(
			controller.getOutput(type),
			new OutputFormat("xml",null,true) );
		XMLWriter out = new XMLWriter(outHandler);
				
		outHandler.setDocumentLocator( new LocatorImpl() );
		outHandler.startDocument();
		outHandler.processingInstruction("xml-stylesheet",
			"type='text/xsl' href='classFileDebug.xsl'");
		writeClass( type, out );
		outHandler.endDocument();
	}
	
	/**
	 * writes body of TypeItem.
	 */
	private void writeClass( TypeItem type, XMLWriter out ) {
		
		out.start(
			(type instanceof ClassItem)?"class":"interface",
			new String[]{"name",type.name});
		
		if( type.getSuperType()!=null )
			out.element("extends",
				new String[]{"name",type.getSuperType().getTypeName()});
		
		writeType( type, out );

		
		// output miscellaneous information which may be useful.
		out.start("info");
		out.start("derivedTypes");
		writeDerivedTypes("class",type,grammar.iterateClasses(),out);
		writeDerivedTypes("interface",type,grammar.iterateInterfaces(),out);
		out.end("derivedTypes");
		out.end("info");
		
		
		out.end("class");
	}

	/** writes derived types of the specified type. */
	private void writeDerivedTypes( String tagName, TypeItem current, Iterator itr, XMLWriter out ) {
		while( itr.hasNext() ) {
			TypeItem type = (TypeItem)itr.next();
			
			boolean isDerived = false;
			
			if(type.getSuperType()==current)
				isDerived = true;
			
			Type[] itfs = type.getInterfaces();
			for( int i=0; i<itfs.length; i++ )
				if( itfs[i]==current )
					isDerived = true;
			
			if(isDerived)
				out.element(tagName, new String[]{"name",type.getTypeName()});
		}
	}
	
	/**
	 * writes body of TypeItem.
	 */
	private void writeType( TypeItem t, XMLWriter out ) {
		Type[] is = (Type[])t.interfaces.toArray(new Type[0]);
		for( int i=0; i<is.length; i++ )
			out.element("implements",
				new String[]{"name",is[i].getTypeName()});
		
		String[] fnames = (String[])t.fields.keySet().toArray(new String[0]);
		for( int i=0; i<fnames.length; i++ ) {
			final FieldUse fu = (FieldUse)t.fields.get(fnames[i]);
			out.start("field",
				new String[]{
					"name",fu.name,
					"itemType",fu.type.getTypeName(),
					"minOccurs",Integer.toString(fu.multiplicity.min),
					"maxOccurs",
						(fu.multiplicity.max==null)?"unbounded":fu.multiplicity.max.toString()} );
			
			Iterator itr = fu.items.iterator();
			while( itr.hasNext() ) {
				final FieldItem fi = (FieldItem)itr.next();
				out.element("symbol",
					new String[]{"name",symbolizer.getId(fi)});
			}
			
			out.end("field");
		}
	}
}
