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

import javax.xml.parsers.*;
import java.io.File;
import com.sun.tranquilo.grammar.trex.util.TREXPatternPrinter;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.grammar.relax.*;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.relax.RELAXReader;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.verifier.util.VerificationErrorHandlerImpl;
import org.apache.xerces.parsers.SAXParser;
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
		boolean dtdValidation=false;
		
		if( args.length==0 )
		{
			System.out.println( localize(MSG_USAGE) );
			return;
		}
		
		for( int i=0; i<args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("-relax") )			relax = true;
			else
			if( args[i].equalsIgnoreCase("-trex") )				relax = false;
			else
			if( args[i].equalsIgnoreCase("-dtd") )				dtdValidation = true;
			else
			if( args[i].equalsIgnoreCase("-dump") )				dump = true;
			else
			if( args[i].equalsIgnoreCase("-debug") )			Debug.debug = true;
			else
			if( args[i].equalsIgnoreCase("-xerces") )
				factory = (SAXParserFactory)Class.forName("org.apache.xerces.jaxp.SAXParserFactoryImpl").newInstance();
			else
			if( args[i].equalsIgnoreCase("-crimson") )
				factory = (SAXParserFactory)Class.forName("org.apache.crimson.jaxp.SAXParserFactoryImpl").newInstance();
			else
			if( args[i].equalsIgnoreCase("-verbose") )
				verbose = true;
			else
			{
				if( args[i].charAt(0)=='-' )
				{
					System.err.println(localize(MSG_UNRECOGNIZED_OPTION,args[i]));
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
			factory = (SAXParserFactory)Class.forName("org.apache.xerces.jaxp.SAXParserFactoryImpl").newInstance();
		
		if( verbose )
			System.out.println( localize( MSG_PARSER, factory.getClass().getName()) );
		
		factory.setNamespaceAware(true);
		factory.setValidating(dtdValidation);
		if( dtdValidation && verbose )
			System.out.println( localize( MSG_DTDVALIDATION ) );
		
		if( !dtdValidation )
			try
			{
				factory.setFeature("http://xml.org/sax/features/validation",false);
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				if( verbose )
					System.out.println( localize( MSG_FAILED_TO_IGNORE_EXTERNAL_DTD ) );
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
			System.out.println( localize(MSG_START_PARSING_GRAMMAR) );
		
			if(relax)
			{
				RELAXGrammar g = loadRELAX(is);
				docDecl = new TREXDocumentDeclaration(g.topLevel, (TREXPatternPool)g.pool, true );
			}
			else
				docDecl = new TREXDocumentDeclaration(loadTREX(is));

			long parsingTime = System.currentTimeMillis();
			if( verbose )
				System.out.println( localize( MSG_PARSING_TIME, new Long(parsingTime-stime) ) );
			
			
			for( int i=0; i<fileNames.size(); i++ )
			{
				final String fileName = (String)fileNames.elementAt(i);
				System.out.println( localize( MSG_VALIDATING, fileName) );
				InputSource xml = new InputSource(new java.io.FileInputStream(fileName));
				xml.setSystemId(new File(fileName).getAbsolutePath());
				verify( docDecl, xml );
			}
			
			if( verbose )
				System.out.println( localize( MSG_VALIDATION_TIME, new Long(System.currentTimeMillis()-parsingTime) ) );
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
			System.out.println(localize(ERR_LOAD_GRAMMAR));
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
			System.out.println(localize(ERR_LOAD_GRAMMAR));
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
		
		ReportErrorHandler reh = new ReportErrorHandler();
		Verifier v = new Verifier( schema, reh );
		
		p.setDTDHandler(v);
		p.setContentHandler(v);
		p.setErrorHandler(reh);
		
		try
		{
			p.parse( instance );
		}
		catch( com.sun.tranquilo.verifier.ValidationUnrecoverableException vv )
		{
			System.out.println(localize(MSG_BAILOUT));
		}
		catch( SAXException se )
		{
			; // error is already reported by ErrorHandler
		}
		
		if( v.isValid() )	System.out.println(localize(MSG_VALID));
		else				System.out.println(localize(MSG_INVALID));
	}

	public static String localize( String propertyName, Object[] args )
	{
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.tranquilo.driver.textui.Messages").getString(propertyName);
	    return java.text.MessageFormat.format(format, args );
	}
	public static String localize( String prop )
	{ return localize(prop,null); }
	public static String localize( String prop, Object arg1 )
	{ return localize(prop,new Object[]{arg1}); }
	public static String localize( String prop, Object arg1, Object arg2 )
	{ return localize(prop,new Object[]{arg1,arg2}); }
	
	public static final String MSG_DTDVALIDATION =		"Driver.DTDValidation";
	public static final String MSG_PARSER =				"Driver.Parser";
	public static final String MSG_USAGE =				"Driver.Usage";
	public static final String MSG_UNRECOGNIZED_OPTION ="Driver.UnrecognizedOption";
	public static final String MSG_START_PARSING_GRAMMAR="Driver.StartParsingGrammar";
	public static final String MSG_PARSING_TIME =		"Driver.ParsingTime";
	public static final String MSG_VALIDATING =			"Driver.Validating";
	public static final String MSG_VALIDATION_TIME =	"Driver.ValidationTime";
	public static final String MSG_VALID =				"Driver.Valid";
	public static final String MSG_INVALID =			"Driver.Invalid";
	public static final String ERR_LOAD_GRAMMAR =		"Driver.ErrLoadGrammar";
	public static final String MSG_BAILOUT =			"Driver.BailOut";
	public static final String MSG_FAILED_TO_IGNORE_EXTERNAL_DTD ="Driver.FailedToIgnoreExternalDTD";
}
