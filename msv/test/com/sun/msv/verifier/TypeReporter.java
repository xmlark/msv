package com.sun.tranquilo.verifier;

import com.sun.tranquilo.grammar.relax.ElementRule;
import com.sun.tranquilo.grammar.relax.RELAXGrammar;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.reader.relax.RELAXReader;
import com.sun.tranquilo.verifier.VerifierFilter;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * dumps RELAX label assigned to each element.
 */
public class TypeReporter extends DefaultHandler
{
	public static void main( String[] args ) throws Exception
	{
		new TypeReporter().run(args);
	}
	
	private VerifierFilter filter;
	
	private void run( String[] args ) throws Exception
	{
		if( args.length!=2 )
		{
			System.out.println("Usage: TypeReporter <RELAX schema> <XML instance>\n");
			return;
		}
		
		SAXParserFactory factory = new org.apache.crimson.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
				
		RELAXGrammar g =
			RELAXReader.parse(
				args[0],
				factory,
				new com.sun.tranquilo.driver.textui.DebugController(),
				new TREXPatternPool() );
		// use TREXPatternPool so that we can verify it like TREX.
		
		if( g==null )
		{
			System.err.println("failed to load a grammar");
			return;
		}
		
		filter = new VerifierFilter(
			new TREXDocumentDeclaration(
			g.topLevel, (TREXPatternPool)g.pool, true ),
			new com.sun.tranquilo.driver.textui.ReportErrorHandler() );
		
		filter.setParent(factory.newSAXParser().getXMLReader());
		filter.setContentHandler(this);
		filter.parse( args[1] );
	}
	
	
	private int indent = 0;
	
	private void printIndent()
	{
		for( int i=0; i<indent; i++ )
			System.out.print("  ");
	}
	
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
	{
		printIndent();
		indent++;
		System.out.print("<"+qName+"> :");
		
		Object o = filter.getVerifier().getCurrentElementType();
		if(o==null)
			System.out.println("???");
		else
		{
			ElementRule er = (ElementRule)o;
			if( er.getParent()==null )
				System.out.println("##inline");
			else
				System.out.println(er.getParent().name);
		}
	}
	
	public void endElement( String namespaceUri, String localName, String qName )
	{
		if( filter.getVerifier().getLastCharacterType()!=null )
		{
			printIndent();
			System.out.println("-- "+filter.getVerifier().getLastCharacterType().getName()+" --");
		}
		indent--;
		printIndent();
		System.out.println("</"+qName+">");
	}

}
