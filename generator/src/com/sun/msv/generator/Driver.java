/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.generator;

import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.*;
import java.util.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.grammar.relax.*;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.relax.RELAXReader;
import org.apache.xml.serialize.*;
import com.sun.tranquilo.driver.textui.SchemaDetector;

/**
 * command line driver.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver
{
	private static void usage()
	{
		System.out.println(
			"Sun XMLGenerator\n"+
			"----------------\n"+
			"Usage: XMLGenerator <options> <schema> [<output name>]\n"+
			"Options:\n"+
			"  -relax    : assume RELAX as a schema\n"+
			"  -trex     : assume TREX as a schema\n"+
			"  -ascii    : use ASCII character only\n"+
			"  -seed <n> : set random seed\n"+
			"  -depth <n>: set cut back depth\n"+
			"  -width <n>: maximum number of times '*'/'+' are repeated\n" +
			"  -n <n>    : # of files to be generated\n" +
			"  -encoding <str>: output encoding (Java name)\n"+
			"\n"+
			"  <output name> must include one '$'. '$' will be replaced by number.\n"+
			"  e.g., test.$.xml -> test.1.xml test.2.xml test.3.xml ...\n"+
			"  if omitted, stdout will be used.\n"
			);
	}

	public static void main( String[] args ) throws Exception
	{
		boolean relax=false;
		boolean trex=false;
		String grammarName=null;
		String outputName=null;
		String encoding="UTF-8";
		
		int number = 1;

		GeneratorOption opt = new GeneratorOption();
		opt.random = new Random();
		
		opt.dtGenerator = new DataTypeGeneratorImpl();

		for( int i=0; i<args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("-relax") )			relax = true;
			else
			if( args[i].equalsIgnoreCase("-trex") )				trex = true;
			else
			if( args[i].equalsIgnoreCase("-ascii") )
			{
				((DataTypeGeneratorImpl)opt.dtGenerator).asciiOnly = true;
			}
			else
			if( args[i].equalsIgnoreCase("-depth") )
			{
				try {
					opt.cutBackDepth = new Integer(args[++i]).intValue();
				} catch( Exception e ) {
					usage();
					return;
				}
			}
			else
			if( args[i].equalsIgnoreCase("-width") )
			{
				try {
					opt.width = new Rand.UniformRand( opt.random, new Integer(args[++i]).intValue() );
				}catch( Exception e ) {
					usage();
					return;
				}
			}
			else
			if( args[i].equalsIgnoreCase("-n") ) {
				try {
					number = new Integer(args[++i]).intValue();
					if( number<1 )	number=1;
				} catch( Exception e ) {
					usage();
					return;
				}
			}
			else
			if( args[i].equalsIgnoreCase("-encoding") ) {
				try {
					encoding = args[++i];
				} catch( Exception e ) {
					usage();
					return;
				}
			}
			else
			if( args[i].equalsIgnoreCase("-seed") )
			{
				try
				{
					opt.random.setSeed( new Long(args[++i]).longValue() );
				}catch(Exception e)
				{
					usage(); return;
				}
			}
//			else
//			if( args[i].equalsIgnoreCase("-debug") )			Debug.debug = true;
//			else
//			if( args[i].equalsIgnoreCase("-xerces") )
//				factory = (SAXParserFactory)Class.forName("org.apache.xerces.jaxp.SAXParserFactoryImpl").newInstance();
//			else
//			if( args[i].equalsIgnoreCase("-crimson") )
//				factory = (SAXParserFactory)Class.forName("org.apache.crimson.jaxp.SAXParserFactoryImpl").newInstance();
//			else
//			if( args[i].equalsIgnoreCase("-verbose") )
//				verbose = true;
			else
			{
				if( args[i].charAt(0)=='-' )
				{
					System.err.println("unrecognized option :" + args[i]);
					usage();
					return;
				}
				
				if( grammarName==null )	grammarName = args[i];
				else
				if( outputName==null ) outputName = args[i];
				else
				{
					System.err.println("too many parameters");
					usage();
					return;
				}
			}
		}
		
		if( grammarName==null )
		{
			usage();
			return;
		}
		
		
			
		
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		DocumentBuilderFactory domFactory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
	
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		Expression topLevel;
		
		System.err.println("parsing a grammar");
		
		
		if( !trex && !relax )
		{// schema type is not specified. sniff it.
//			if( verbose )
//				System.out.println( localize( MSG_SNIFF_SCHEMA ) );
			
			SAXException e = SchemaDetector.detect(
				factory.newSAXParser().getXMLReader(),
				getInputSource(grammarName) );
			
			if( e==SchemaDetector.relax )	relax=true;
			else
			if( e==SchemaDetector.trex )	trex=true;
			else
			{
				System.out.println( "unable to detect schema type" );
				return;
			}
		}
		
		if( relax )
		{
			RELAXGrammar g =
				RELAXReader.parse(
					getInputSource(grammarName),
					factory,
					new com.sun.tranquilo.driver.textui.DebugController(),
					new TREXPatternPool() );
			NoneTypeRemover.removeNoneType(g);
			opt.pool = (TREXPatternPool)g.pool;
			topLevel = g.topLevel.visit( new RefExpRemover(g.pool) );
		}
		else
		{
			TREXGrammar g = 
				TREXGrammarReader.parse(
					getInputSource(grammarName),
					factory,
					new com.sun.tranquilo.driver.textui.DebugController() );
			topLevel = g.start.visit( new RefExpRemover(g.pool) );
		}
		
		for( int i=0; i<number; i++ )
		{
			if( number<10 )		System.err.println("generating a document #"+(i+1));
			else				System.err.print('.');
			
			org.w3c.dom.Document dom = domFactory.newDocumentBuilder().newDocument();
			
			Generator.generate(topLevel,dom,opt);
		
			// serialize it
			OutputStream os;
			if( outputName==null )		os = System.out;	// in case no output file name is specified
			else
			{
				int idx = outputName.indexOf('$');
				if( idx!=-1 )
					os = new FileOutputStream( outputName.substring(0,idx)+i+outputName.substring(idx+1) );
				else
					os = new FileOutputStream( outputName );
			}
			
			XMLSerializer2 s = new XMLSerializer2( os, new OutputFormat("XML",encoding,true) );
			s.serialize(dom);

//			Writer osw = new OutputStreamWriter(System.out);
//			org.apache.soap.util.xml.DOM2Writer.serializeAsXML( dom, osw );
//			osw.flush();
//			osw.close();
			
			if( os!=System.out )	os.close();
			else
			{
				System.in.read();
			}
		}
	}
	
	private static InputSource getInputSource( String fileOrURL )
	{
		try
		{// try it as a file
			InputSource is = new InputSource(new java.io.FileInputStream(fileOrURL));
			is.setSystemId(new File(fileOrURL).getAbsolutePath());
			return is;
		}
		catch( Exception e )
		{// try it as an URL
			return new InputSource(fileOrURL);
		}
	}
}
