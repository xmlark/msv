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

public class Driver
{
	static SAXParserFactory factory;
	
	public static void main( String[] args ) throws Exception
	{
		final Vector fileNames = new Vector();
		
		String grammarName = null;
		boolean dump=false;
		boolean relax=true;
		boolean verbose = false;
		
		if( args.length==0 )
		{
			System.out.println("usage: java -jar tranquilo.jar (-relax|-trex) [-xerces|-crimson] [-dump] [-debug] [-verbose] <grammar file> <instance file> ...");
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
			if( args[i].equalsIgnoreCase("-xerces") )
				factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
			else
			if( args[i].equalsIgnoreCase("-crimson") )
				factory = new org.apache.crimson.jaxp.SAXParserFactoryImpl();
			else
			if( args[i].equalsIgnoreCase("-verbose") )
				verbose = true;
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
		
		if( factory==null )
			factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		
		if( verbose )
			System.out.println("Using "+factory.getClass().getName());
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
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
			System.out.println( "start parsing a grammar" );
		
			if(relax)
			{
				RELAXGrammar g = loadRELAX(is);
				docDecl = new TREXDocumentDeclaration(g.topLevel, (TREXPatternPool)g.pool, true );
			}
			else
				docDecl = new TREXDocumentDeclaration(loadTREX(is));

			long parsingTime = System.currentTimeMillis();
			if( verbose )
				System.out.println( "parsing took "+(parsingTime-stime)+"ms" );
			
			
			for( int i=0; i<fileNames.size(); i++ )
			{
				final String fileName = (String)fileNames.elementAt(i);
				System.out.println("validating " + fileName );
				InputSource xml = new InputSource(new java.io.FileInputStream(fileName));
				xml.setSystemId(fileName);
				verify( docDecl, xml );
			}
			
			if( verbose )
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

	public static String localizeMessage( String propertyName, Object[] args )
	{
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.tranquilo.driver.textui.Messages").getString(propertyName);
	    return java.text.MessageFormat.format(format, args );
	}
	public static String localizeMessage( String prop )
	{ return localizeMessage(prop,null); }
	public static String localizeMessage( String prop, Object arg1 )
	{ return localizeMessage(prop,new Object[]{arg1}); }
	public static String localizeMessage( String prop, Object arg1, Object arg2 )
	{ return localizeMessage(prop,new Object[]{arg1,arg2}); }
}
