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
import com.sun.tranquilo.reader.util.GrammarLoader;
import com.sun.tranquilo.reader.dtd.DTDReader;
import com.sun.tranquilo.relaxns.grammar.RELAXGrammar;
import com.sun.tranquilo.relaxns.verifier.SchemaProviderImpl;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.verifier.util.VerificationErrorHandlerImpl;
import org.iso_relax.dispatcher.Dispatcher;
import org.iso_relax.dispatcher.SchemaProvider;
import org.iso_relax.dispatcher.impl.DispatcherImpl;
import org.xml.sax.*;
import java.util.*;

/**
 * command line Verifier.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver
{
	static SAXParserFactory factory;
	
	public static void main( String[] args ) throws Exception
	{
		final Vector fileNames = new Vector();
		
		String grammarName = null;
		boolean dump=false;
		boolean verbose = false;
		boolean warning = false;
		boolean loose=false;
		
		boolean dtdAsSchema = false;
		
		if( args.length==0 )
		{
			System.out.println( localize(MSG_USAGE) );
			return;
		}
		
		for( int i=0; i<args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("-loose") )			loose = true;
			else
			if( args[i].equalsIgnoreCase("-dtd") )				dtdAsSchema = true;
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
			if( args[i].equalsIgnoreCase("-verbose") )			verbose = true;
			else
			if( args[i].equalsIgnoreCase("-warning") )			warning = true;
			else
			if( args[i].equalsIgnoreCase("-version") )
			{
				System.out.println("Tranquilo Ver."+
					java.util.ResourceBundle.getBundle("version").getString("version") );
				return;
			}
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
		
		if( loose )
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
		
		
		InputSource is = getInputSource(grammarName);
		

	// parse schema
	//--------------------
		final long stime = System.currentTimeMillis();
		System.out.println( localize(MSG_START_PARSING_GRAMMAR) );

//			Grammar grammar = GrammarLoader.loadSchema(is,new DebugController(warning),factory);
		Grammar grammar=null;
			// other XML-based grammars: GrammarLoader will detect the language.
		try {
			if(dtdAsSchema) {
				grammar = DTDReader.parse(is,new DebugController(warning),"",new TREXPatternPool());
			} else {
				grammar = GrammarLoader.loadSchema(is,new DebugController(warning),factory);
			}
		} catch(SAXParseException spe) {
			; // this error is already reported.
		} catch(SAXException se ) {
			if( se.getException()!=null ) throw se.getException();
			throw se;
		}
		if( grammar==null ) {
			System.out.println( localize(ERR_LOAD_GRAMMAR) );
			return;
		}
			
		long parsingTime = System.currentTimeMillis();
		if( verbose )
			System.out.println( localize( MSG_PARSING_TIME, new Long(parsingTime-stime) ) );


		if(dump)
		{
			if( grammar instanceof RELAXModule )
				dumpRELAXModule( (RELAXModule)grammar );
			else
			if( grammar instanceof RELAXGrammar )
				dumpRELAXGrammar( (RELAXGrammar)grammar );
			else
			if( grammar instanceof TREXGrammar )
				dumpTREX( (TREXGrammar)grammar );
			
			return;
		}
		
	// validate documents
	//--------------------
		DocumentVerifier verifier;
		if( grammar instanceof RELAXGrammar )
			// use divide&validate framework to validate document
			verifier = new RELAXNSVerifier( new SchemaProviderImpl((RELAXGrammar)grammar) );
		else
			// validate normally by using Verifier.
			verifier = new SimpleVerifier( new TREXDocumentDeclaration(grammar) );
		
		for( int i=0; i<fileNames.size(); i++ )	{
			
			final String instName = (String)fileNames.elementAt(i);
			System.out.println( localize( MSG_VALIDATING, instName) );
			
			if(verifier.verify(getInputSource(instName)))
				System.out.println(localize(MSG_VALID));
			else
				System.out.println(localize(MSG_INVALID));
			
			if( i!=fileNames.size()-1 )
				System.out.println("--------------------------------------");
		}
		
			
		if( verbose )
			System.out.println( localize( MSG_VALIDATION_TIME, new Long(System.currentTimeMillis()-parsingTime) ) );
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
	
	public static void dumpRELAXModule( RELAXModule m ) throws Exception
	{
		
		System.out.println("*** top level ***");
		System.out.println(TREXPatternPrinter.printFragment(m.topLevel));
		
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

	public static void dumpRELAXGrammar( RELAXGrammar m ) throws Exception {
		System.out.println("operation is not implemented yet.");
	}

	/** acts as a function closure to validate a document. */
	private interface DocumentVerifier {
		boolean verify( InputSource instance ) throws Exception;
	}
	
	/** validates a document by using divide &amp; validate framework. */
	private static class RELAXNSVerifier implements DocumentVerifier {
		private final SchemaProvider sp;
		
		RELAXNSVerifier( SchemaProvider sp ) { this.sp=sp; }
		
		public boolean verify( InputSource instance ) throws Exception {
			XMLReader p = factory.newSAXParser().getXMLReader();
			Dispatcher dispatcher = new DispatcherImpl(sp);
			dispatcher.attachXMLReader(p);
			ReportErrorHandler errorHandler = new ReportErrorHandler();
			dispatcher.setErrorHandler( errorHandler );
			
			try {
				p.parse(instance);
				return !errorHandler.hadError;
			} catch( com.sun.tranquilo.verifier.ValidationUnrecoverableException vv ) {
				System.out.println(localize(MSG_BAILOUT));
			} catch( SAXParseException se ) {
				; // error is already reported by ErrorHandler
			} catch( SAXException e ) {
				  e.getException().printStackTrace();
			}
			return false;
		}
	}
	
	private static class SimpleVerifier implements DocumentVerifier {
		private final DocumentDeclaration docDecl;
		
		SimpleVerifier( DocumentDeclaration docDecl ) { this.docDecl = docDecl; }

		public boolean verify( InputSource instance ) throws Exception {
			XMLReader p = factory.newSAXParser().getXMLReader();
		
			ReportErrorHandler reh = new ReportErrorHandler();
			Verifier v = new Verifier( docDecl, reh );
		
			p.setDTDHandler(v);
			p.setContentHandler(v);
			p.setErrorHandler(reh);
		
			try {
				p.parse( instance );
				return v.isValid();
			} catch( com.sun.tranquilo.verifier.ValidationUnrecoverableException vv ) {
				System.out.println(localize(MSG_BAILOUT));
			} catch( SAXParseException se ) {
				; // error is already reported by ErrorHandler
			} catch( SAXException e ) {
				  e.getException().printStackTrace();
			}
			
			return false;
		}
	}

	private static InputSource getInputSource( String fileOrURL )
	{
		try
		{// try it as a file
			InputSource is = new InputSource(new java.io.FileInputStream(fileOrURL));
//			is.setSystemId(new File(fileOrURL).getAbsolutePath());
			// this has to be getCanonicalPath, otherwise
			// java cannot resolve relative paths like "c:work.rlx" (in Windows).
			is.setSystemId( fullPathToURL(new File(fileOrURL).getCanonicalPath()) );
			return is;
		}
		catch( Exception e )
		{// try it as an URL
			return new InputSource(fileOrURL);
		}
	}
	
	private static String fullPathToURL( String fileName ) {
		// under JDK1.2, use File.toURL
		StringBuffer buf = new StringBuffer("file://");
		if( fileName.charAt(0)!=File.separatorChar )	buf.append('/');
		
		for( int i=0; i<fileName.length(); i++ ) {
			char ch = fileName.charAt(i);
			if( ch==File.separatorChar )	buf.append('/');
			else							buf.append(ch);
		}
		return buf.toString();
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
//	public static final String MSG_SNIFF_SCHEMA =		"Driver.SniffSchema";
//	public static final String MSG_UNKNOWN_SCHEMA =		"Driver.UnknownSchema";
}
