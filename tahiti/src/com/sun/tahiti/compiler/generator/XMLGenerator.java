package com.sun.tahiti.compiler.generator;

import com.sun.msv.grammar.*;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.ClassCollector;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import java.util.Iterator;
import java.io.IOException;

class XMLGenerator
{
	XMLGenerator( Expression topLevel, String grammarClassName, Symbolizer symbolizer, OutputResolver resolver ) {
		this.topLevel = topLevel;
		this.symbolizer = symbolizer;
		this.outResolver = resolver;
		this.grammarClassName = grammarClassName;
	}
	
	private final Expression topLevel;
	private final Symbolizer symbolizer;
	private final OutputResolver outResolver;
	private final String grammarClassName;
		
	void generate() throws SAXException, IOException {
		try {
			// collect all ClassItems.
			ClassCollector col = new ClassCollector();
			topLevel.visit(col);
		
			ClassItem[] types = (ClassItem[])col.classItems.toArray(new ClassItem[0]);
			for( int i=0; i<types.length; i++ )
				write( types[i] );
		
			InterfaceItem[] itfs = (InterfaceItem[])col.interfaceItems.toArray(new InterfaceItem[0]);
			for( int i=0; i<itfs.length; i++ )
				write( itfs[i] );
		
		} catch( XMLWriter.SAXWrapper w ) {
			throw w.e;
		}
	}

	/** writes TypeItem to an XML file. */
	private void write( TypeItem type ) throws SAXException, IOException {
				
		DocumentHandler outHandler = new XMLSerializer(
			outResolver.getOutput(type),
			new OutputFormat("xml",null,true) );
		XMLWriter out = new XMLWriter(outHandler);
				
		outHandler.setDocumentLocator( new LocatorImpl() );
		outHandler.startDocument();
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
					
		out.end("class");
	}
	
	/**
	 * writes body of TypeItem.
	 */
	private void writeType( TypeItem t, XMLWriter out ) {
		InterfaceItem[] is = (InterfaceItem[])t.interfaces.toArray(new InterfaceItem[0]);
		for( int i=0; i<is.length; i++ )
			out.element("implements",
				new String[]{"name",is[i].name});
		
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
