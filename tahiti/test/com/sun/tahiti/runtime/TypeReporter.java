package com.sun.tahiti.runtime;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.util.ExpressionPrinter;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;

public class TypeReporter implements TypedContentHandler
{
	public static void main( String[] args ) throws Exception {
		
		if( args.length!=2 ) {
			System.err.println("Usage: <schema file> <instance>");
			return;
		}
		
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		
		// load the grammar.
		REDocumentDeclaration g = GrammarLoader.loadVGM( args[0], new DebugController(false), factory );
		if(g==null)	return;
		
		XMLReader reader = factory.newSAXParser().getXMLReader();
		reader.setContentHandler( new TypeDetecter( g, new TypeReporter() ) );
		
		reader.parse( args[1] );
	}
	
	
	
	private int indent = 0;
	
	private void printIndent() {
		for( int i=0; i<indent; i++ )
			System.out.print("  ");
	}
	
	
	public void characterChunk( String literal, Datatype type ) {
		printIndent();
		System.out.println("("+literal+") as "+
			(type instanceof XSDatatype?((XSDatatype)type).displayName():type.getClass().getName()) );
	}
	
	public void startElement( String namespaceURI, String localName, String qName ) {
		printIndent();
		indent++;
		System.out.println("<"+qName+">");
	}
	
	public void endElement( String namespaceURI, String localName, String qName, ElementExp type ) {
		indent--;
		printIndent();
		System.out.println( "</"+qName+"> as "+
			ExpressionPrinter.printSmallest(type.contentModel));
	}

	public void startAttribute( String namespaceURI, String localName, String qName ) {
		printIndent();
		indent++;
		System.out.println("@"+qName);
	}
	
	public void endAttribute( String namespaceURI, String localName, String qName, AttributeExp type ) {
		indent--;
		printIndent();
		System.out.println( "/@"+qName+" as "+
			ExpressionPrinter.printSmallest(type.exp) );
	}

	public void endAttributePart() {
	}

	public void startDocument( ValidationContext context ) {
		System.out.println("------");
	}

	public void endDocument() {
		System.out.println("------");
	}

}
