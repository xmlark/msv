/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.psvi;

import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.util.ErrorHandlerImpl;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

/**
 * An example that uses {@link TypedContentHandler} for parsing XML documents.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class PSVIDump implements TypedContentHandler {
	
	public static void main( String[] args ) throws Exception {
		if( args.length!=2 ) {
			System.out.println("Usage: PSVIDump <schema> <XML instance>\n");
			return;
		}
		
		// load a schema. GrammarLoader will detect the schema language automatically.
		GrammarLoader loader = new GrammarLoader();
		loader.setController( new DebugController(false,false) );
		DocumentDeclaration grammar = loader.loadVGM( args[0] );
		
		if( grammar==null ) {
			System.err.println("failed to load a grammar");
			return;
		}
		
		// create an XMLReader to be used to parse the instance document
		XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		
		// create an instance of verifier,
		TypeDetector verifier = new TypeDetector(grammar, new ErrorHandlerImpl() );
		
		// configure a pipeline so that the verifier will receive SAX events first.
		reader.setContentHandler(verifier);
		
		// then set your application handler to verifier.
		verifier.setContentHandler(new PSVIDump());
		
		// finally, parse the document and see what happens!
		reader.parse(args[1]);
	}
	
	
	
	private int indent = 0;
	
	/** print indentation. */
	private void printIndent() {
		for( int i=0; i<indent; i++ )
			System.out.print("  ");
	}

	
	
//	
// TypedContentHandler callbacks
//

	public void startElement( String namespaceUri, String localName, String qName ) {
		printIndent();
		indent++;
		System.out.println("<"+qName+">");
	}
	
	public void startAttribute( String namespaceUri, String localName, String qName ) {
		printIndent();
		indent++;
		System.out.println("<@"+qName+">");
	}
	public void endAttribute( String namespaceUri, String localName, String qName, AttributeExp type ) {
		indent--;
		printIndent();
		System.out.print("</@"+qName+"> :");
		
		// the type parameter is of interest here.
		// this parameter tells you the type assigned to this attribute.
		
		// for starter, use ExpressionPrinter to print a content model.
		System.out.println( ExpressionPrinter.printContentModel(type.exp) );
	}
	
	public void endAttributePart() {
		// this method is called after all the attributes are reported.
		printIndent();
		System.out.println("------");
	}
	
	public void characterChunk( String literal, Datatype type ) {
		printIndent();
		
		// type shows the assigned type.
		// if you are using W3C XML Schema, RELAX Core or DTD, then
		// this type parameter can be always casted into DatabindableDatatype.
		// if you are using RELAX NG, then it depends on the situation.
		if(!(type instanceof DatabindableDatatype)) {
			System.out.print("not databindable");
		} else {
			DatabindableDatatype dt = (DatabindableDatatype)type;
			// the createJavaObject method can be used to convert this literal
			// into Java friendly object. Here, we use the context object
			// which is passed through the startDocument method.
			Object javaObject = dt.createJavaObject( literal, context );
			// dump the class name for a proof that an object is actually created.
			System.out.print( javaObject.getClass().getName() );
		}
		System.out.print(" : ");
		System.out.println(literal.trim());
		// the trim method is called only to make the output cleaner.
	}
	
	public void endElement( String uri, String local, String qName, ElementExp type ) {
		indent--;
		printIndent();
		System.out.print("</"+qName+"> : ");
		
		// the type parameter is of interest here.
		// this parameter tells you the type assigned to this element.
		
		// for starter, use ExpressionPrinter to print a content model.
		System.out.println( ExpressionPrinter.printContentModel(type.contentModel) );
	}
	
	
	private ValidationContext context;
	public void startDocument( ValidationContext context ) {
		// Later, this context object will be used to convert characters into
		// Java-friendly object.
		// So just store it for now.
		this.context = context;
		
		System.out.println("startDocument");
	}
	
	public void endDocument() {
		System.out.println("endDocument");
	}
}
