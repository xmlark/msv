package com.sun.tranquilo.driver.textui;

import javax.xml.parsers.*;
import java.io.File;
import com.sun.tranquilo.grammar.trex.util.TREXPatternPrinter;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.grammar.relax.*;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.relax.RELAXReader;
import org.apache.xerces.parsers.SAXParser;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.verifier.util.VerificationErrorHandlerImpl;
import org.xml.sax.*;
import java.util.*;

public class TestRunner
{
	static SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
	
	public static void main( String[] args ) throws Exception
	{
		final Vector fileNames = new Vector();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		String grammarName = null;
		boolean dump=false;
		boolean relax=true;
		
		if( args.length==0 )
		{
			System.out.println("usage: java -jar tranquilo.jar (-relax|-trex) [-dump] [-debug] <grammar file> <instance file>");
			return;
		}
		
		for( int i=0; i<args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("-relax") )			relax = true;
			else
			if( args[i].equalsIgnoreCase("-trex") )				relax = false;
			else
			if( args[i].equalsIgnoreCase("-dump") )				dump = true;
			else
			if( args[i].equalsIgnoreCase("-debug") )			Debug.debug = true;
			else
			{
				if( args[i].charAt(0)=='-' )
				{
					System.err.println("unrecognized option:"+args[i]);
					return;
				}
				
				if( grammarName==null )	grammarName = args[i];
				else
				{
					fileNames.add(args[i]);
				}
			}
		}
		
		InputSource is = new InputSource(new java.io.FileInputStream(grammarName));
		is.setSystemId(new File(grammarName).getAbsolutePath());

		if(dump)
		{
			if(relax)		dumpRELAX(loadRELAX(is));
			else			dumpTREX(loadTREX(is));
			return;
		}
		else
		{
			TREXDocumentDeclaration docDecl;
		
			final long stime = System.currentTimeMillis();
			System.out.println( "start parsing" );
		
			if(relax)
			{
				RELAXGrammar g = loadRELAX(is);
				docDecl = new TREXDocumentDeclaration(g.topLevel, (TREXPatternPool)g.pool, true );
			}
			else
				docDecl = new TREXDocumentDeclaration(loadTREX(is));

			long parsingTime = System.currentTimeMillis();
			System.out.println( "parsing took "+(parsingTime-stime)+"ms" );
			System.out.println( "start validation" );
			
			for( int i=0; i<fileNames.size(); i++ )
			{
				final String fileName = (String)fileNames.elementAt(i);
				System.out.println("validating " + fileName );
				InputSource xml = new InputSource(new java.io.FileInputStream(fileName));
				xml.setSystemId(fileName);
				verify( docDecl, xml );
			}
			
			System.out.println( "validation took "+(System.currentTimeMillis()-parsingTime)+"ms" );
		}
	}
	
	public static TREXGrammar loadTREX( InputSource is ) throws Exception
	{
		TREXGrammar g =
		TREXGrammarReader.parse(
			is,
			factory,
			new DebugController() );

		if( g==null )
		{
			System.out.println("failed to load a grammar");
			System.exit(-1);
		}
		return g;
	}
	
	public static RELAXGrammar loadRELAX( InputSource is ) throws Exception
	{
		
		RELAXGrammar g =
			RELAXReader.parse(
				is,
				factory,
				new DebugController(),
				new TREXPatternPool() );
		// use TREXPatternPool so that we can verify it like TREX.
		
		if( g==null )
		{
			System.err.println("failed to load a grammar");
			System.exit(-1);
		}
		return g;
	}
	
	public static void dumpTREX( TREXGrammar g ) throws Exception
	{

		System.out.println("*** start ***");
		System.out.println(TREXPatternPrinter.printFragment(g.start));
		System.out.println("*** others ***");
		System.out.print(
			TREXPatternPrinter.fragmentInstance.printRefContainer(
				g.namedPatterns ) );
	}
	
	public static void dumpRELAX( RELAXGrammar g ) throws Exception
	{
		
		System.out.println("*** top level ***");
		System.out.println(TREXPatternPrinter.printFragment(g.topLevel));
		
		for( Iterator itr=g.moduleMap.values().iterator(); itr.hasNext(); )
		{
			RELAXModule m = (RELAXModule)itr.next();
			
			System.out.println("\n $$$$$$[ " + m.targetNamespace + " ]$$$$$$");
		
			System.out.println("*** elementRule ***");
			System.out.print(
				TREXPatternPrinter.fragmentInstance.printRefContainer(
					m.elementRules ) );
			System.out.println("*** hedgeRule ***");
			System.out.print(
				TREXPatternPrinter.fragmentInstance.printRefContainer(
					m.hedgeRules ) );
			System.out.println("*** attPool ***");
			System.out.print(
				TREXPatternPrinter.fragmentInstance.printRefContainer(
					m.attPools ) );
			System.out.println("*** tag ***");
			System.out.print(
				TREXPatternPrinter.fragmentInstance.printRefContainer(
					m.tags ) );
		}
	}
	
	public static void verify( DocumentDeclaration schema, InputSource instance ) throws Exception
	{
		XMLReader p = factory.newSAXParser().getXMLReader();
		
		Verifier v = new Verifier( schema, new ReportErrorHandler() );
		
		p.setDTDHandler(v);
		p.setContentHandler(v);
		
		try
		{
			p.parse( instance );
		}
		catch( com.sun.tranquilo.verifier.ValidationUnrecoverableException vv )
		{
			System.out.println("bailing out");
		}
		catch( SAXException se )
		{
			if(se.getException()!=null)
			{
				se.getException().printStackTrace();
			}
			else
				se.printStackTrace();
		}
		
		if( v.isValid() )	System.out.println("document is valid");
		else				System.out.println("document is NOT valid");
	}
	
	/* manually construct TREX pattern
	public static void main( String[] args ) throws Exception
	{
		TREXPatternPool pool = new TREXPatternPool();
		
		TREXGrammar grammar = new TREXGrammar(pool);
		grammar.start = 
			pool.createElement( new SimpleNameClass("","root"),
				pool.createChoice(
					Pattern.anyString,
					pool.createOneOrMore(
						pool.createElement( new SimpleNameClass("","item"),
							Pattern.epsilon )
						)
					)
				);
		
		SAXParser p = new SAXParser();
		
		Verifier v = new Verifier( new REDocumentDeclaration(grammar) );
		
		p.setDTDHandler(v);
		p.setContentHandler(v);
		
		try
		{
			p.parse( new InputSource(new java.io.FileInputStream("c:\\test.xml")) );
		}
		catch( SAXException se )
		{
			if(se.getException()!=null)
			{
				se.getException().printStackTrace();
			}
			else
				se.printStackTrace();
		}
	}
	*/
}
