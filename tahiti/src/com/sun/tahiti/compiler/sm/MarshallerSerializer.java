/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.sm;

import com.sun.tahiti.grammar.AnnotatedGrammar;
import com.sun.tahiti.grammar.ClassItem;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.util.xml.DocumentFilter;
import com.sun.msv.reader.GrammarReaderController;
import org.xml.sax.Parser;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * serializes all marshallers in the grammar into one XML representation.
 */
public class MarshallerSerializer {
	
	public static void serialize(
		AnnotatedGrammar grammar, Symbolizer symbolizer, GrammarReaderController controller,
				DocumentHandler handler ) throws SAXException,ParserConfigurationException,IOException {
		
		final XMLWriter out = new XMLWriter(handler);
		
		try {
			out.handler.startDocument();
			out.start("unmarshaller");
		
			Parser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser().getParser();
		
			ClassItem[] classes = grammar.getClasses();
			for( int i=0; i<classes.length; i++ ) {
				out.start("class",new String[]{"name", classes[i].getTypeName()});
				
				byte[] marshaller = MarshallerGenerator.write(symbolizer,classes[i],controller);
				
				if(marshaller==null) {
					// we've failed to produce marshaller for this class.
					out.element("unavailable");
				} else {
					// copy the generated marshaller into the output stream.
					parser.setDocumentHandler(
						new DocumentFilter(out.handler){
							public void startDocument() {}
							public void endDocument() {}
							public void processingInstruction( String target, String data ) throws SAXException {
								if( !target.equals("xml") )
									super.processingInstruction(target,data);
							}
						});
					parser.parse(new InputSource(new ByteArrayInputStream(marshaller)));
				}
				
				out.end("class");
			}
			
			out.end("unmarshaller");
			out.handler.endDocument();
			
		} catch( XMLWriter.SAXWrapper w ) {
			throw w.e;
		}
	}
}
