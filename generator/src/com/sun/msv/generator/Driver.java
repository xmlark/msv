/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.*;
import java.util.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.relax.*;
import com.sun.msv.grammar.*;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.util.*;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.msv.relaxns.grammar.RELAXGrammar;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.reader.util.GrammarLoader;
import org.apache.xml.serialize.*;

/**
 * command line driver.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver {
	
	protected void usage() {
		System.err.println(
			"Sun XMLGenerator\n"+
			"----------------\n"+
			"Usage: XMLGenerator <options> <schema> [<output name>]\n"+
			"Options:\n"+
			"  -ascii    : use US-ASCII characters only\n"+
			"  -seed <n> : set random seed\n"+
			"  -depth <n>: set cut back depth\n"+
			"  -width <n>: maximum number of times '*'/'+' are repeated\n" +
			"  -n <n>    : # of files to be generated\n" +
			"  -quiet    : be quiet.\n"+
			"  -encoding <str>\n"+
			"      output encoding (Java name)\n"+
			"  -validate : validate documents before write to output.\n"+
			"      when generating errors, check that the document is actually invalid.\n"+
			"  -example <filename>\n"+
			"      use the given file as an example. tokens found in the example\n"+
			"      is used to generate documents\n"+
			"  -error <n>/<m>\n"+
			"      error ratio. generate n errors per m elemnts (average).\n"+
			"      to control error generation, see manual for details.\n"+
			"  -nocomment: suppress insertion of comments that indicate generated errors.\n"+
			"\n"+
			"  <output name> must include one '$'. '$' will be replaced by number.\n"+
			"  e.g., test.$.xml -> test.1.xml test.2.xml test.3.xml ...\n"+
			"  if omitted, generated file will be sent to stdout.\n"
			);
	}

	public static void main( String[] args ) throws Exception {
		System.exit( new Driver().run(args, System.err) );
	}
	
	protected double getRatio( String s ) {
		int idx = s.indexOf('/');
		double n = Double.parseDouble(s.substring(0,idx));
		double m = Double.parseDouble(s.substring(idx+1));
						
		double ratio = n/m;
						
		if( ratio<=0 || ratio>1 ) {
			System.err.println("error ratio out of range");
			usage();
			System.exit(-1);
		}
		return ratio;
	}
	
	/**
	 * runs command line tool.
	 * 
	 * @return 0 if it run successfully. Non-zero if any error is encountered.
	 */
	public int run( String[] args, PrintStream out ) throws Exception {
		String grammarName=null;
		String outputName=null;
		String encoding="UTF-8";
		boolean createError = false;
		boolean validate = false;
		boolean debug = false;
		boolean quiet = false;
		
		int number = 1;

		GeneratorOption opt = new GeneratorOption();
		opt.random = new Random();
		
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		// this set will receive tokens found in the given examples.
		Set exampleTokens = new java.util.HashSet();
		
		DataTypeGeneratorImpl dtgi = new DataTypeGeneratorImpl();
		opt.dtGenerator = dtgi;
		dtgi.tokens = exampleTokens;
		

		// parse options
		//===========================================
		try {
			for( int i=0; i<args.length; i++ ) {
				if( args[i].equalsIgnoreCase("-debug") )
					debug = true;
				else
				if( args[i].equalsIgnoreCase("-quiet") )
					quiet = true;
				else
				if( args[i].equalsIgnoreCase("-ascii") )
					((DataTypeGeneratorImpl)opt.dtGenerator).asciiOnly = true;
				else
				if( args[i].equalsIgnoreCase("-nocomment") )
					opt.insertComment = false;
				else
				if( args[i].equalsIgnoreCase("-depth") )
					opt.cutBackDepth = new Integer(args[++i]).intValue();
				else
				if( args[i].equalsIgnoreCase("-example") ) {
					XMLReader p = factory.newSAXParser().getXMLReader();
					p.setContentHandler( new ExampleReader(exampleTokens) );
					p.parse( getInputSource(args[++i]) );
				} else
				if( args[i].equalsIgnoreCase("-width") )
					opt.width = new Rand.UniformRand( opt.random, new Integer(args[++i]).intValue() );
				else
				if( args[i].equalsIgnoreCase("-n") ) {
					number = new Integer(args[++i]).intValue();
					if( number<1 )	number=1;
				}
				else
				if( args[i].equalsIgnoreCase("-encoding") )
					encoding = args[++i];
				else
				if( args[i].equalsIgnoreCase("-seed") )
					opt.random.setSeed( new Long(args[++i]).longValue() );
				else
				if( args[i].equalsIgnoreCase("-validate") )
					validate = true;
				else
				if( args[i].startsWith("-error") ) {
					createError = true;
					if( args[i].equalsIgnoreCase("-error") ) {
						opt.probGreedyChoiceError=
						opt.probMissingAttrError=
						opt.probMissingElemError=
						opt.probMutatedAttrError=
						opt.probMutatedElemError=
						opt.probSeqError=
						opt.probSlipInAttrError=
						opt.probSlipInElemError=
						opt.probMissingPlus=
						opt.probAttrNameTypo=
						opt.probElemNameTypo=
							getRatio(args[++i]);
					}
					else
					if( args[i].equalsIgnoreCase("-error-greedyChoice") )
						opt.probGreedyChoiceError	= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-missingAttribute") )
						opt.probMissingAttrError	= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-missingElement") )
						opt.probMissingElemError	= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-mutatedAttribute") )
						opt.probMutatedAttrError	= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-mutatedElement") )
						opt.probMutatedElemError	= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-sequenceError") )
						opt.probSeqError			= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-slipInAttribute") )
						opt.probSlipInAttrError		= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-slipInElement") )
						opt.probSlipInElemError		= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-missingPlus") )
						opt.probMissingPlus			= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-attributeNameTypo") )
						opt.probAttrNameTypo		= getRatio(args[++i]);
					else
					if( args[i].equalsIgnoreCase("-error-attributeNameTypo") )
							opt.probElemNameTypo	= getRatio(args[++i]);
					else {
						System.err.println("unrecognized option :" + args[i]);
						usage();
						return -1;
					}
				}
				else {
					if( args[i].charAt(0)=='-' ) {
						System.err.println("unrecognized option :" + args[i]);
						usage();
						return -1;
					}
					
					if( grammarName==null )	grammarName = args[i];
					else
					if( outputName==null ) outputName = args[i];
					else {
						System.err.println("too many parameters");
						usage();
						return -1;
					}
				}
			}
		} catch(Exception e) {
			usage();
			return -1;
		}
		
		if( grammarName==null ) {
			usage();
			return -1;
		}
		
		
			
		
		DocumentBuilderFactory domFactory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
	
		
		try {
			factory.setFeature("http://xml.org/sax/features/validation",false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
		} catch(Exception e) { ;	}
		
		Expression topLevel;
		
		if(!quiet)
			out.println("parsing a grammar: "+grammarName);
		
		
		// load a schema
		//===========================================
		Grammar grammar = GrammarLoader.loadSchema(
				grammarName, new DebugController(false), factory );
		
		topLevel = grammar.getTopLevel();
		
		if( grammar instanceof RELAXGrammar
		||  grammar instanceof RELAXModule )
			topLevel = topLevel.visit( new NoneTypeRemover(grammar.getPool()) );
	
		topLevel = topLevel.visit( new RefExpRemover(grammar.getPool()) );
		opt.pool = (TREXPatternPool)grammar.getPool();
		
		// generate instances
		//===========================================
		for( int i=0; i<number; i++ ) {
			if(quiet) {
				if(number<=10 )	out.println("generating a document #"+(i+1));
				else			out.print(">");
			}
			
			org.w3c.dom.Document dom;
			while(true) {
				dom = domFactory.newDocumentBuilder().newDocument();
				Generator.generate(topLevel,dom,opt);
				
				if( !validate)		break;
				
				// check the validity of generated document.
				DOM2toSAX2 d2s = new DOM2toSAX2();
				Verifier v = new Verifier(
					new TREXDocumentDeclaration(grammar),
					debug?
						(VerificationErrorHandler)new VerificationErrorHandlerImpl():
						(VerificationErrorHandler)new IgnoreVerificationErrorHandler() );
				d2s.setContentHandler(v);
				d2s.traverse(dom);
				
				if( createError && !v.isValid() )	break;
				if( !createError && v.isValid() )	break;
				// do it again
			}
		
			// serialize it
			OutputStream os;
			if( outputName==null )		os = System.out;	// in case no output file name is specified
			else {
				int idx = outputName.indexOf('$');
				if( idx!=-1 ) {
					String s = Integer.toString(i);
					for( int j=s.length(); j<Integer.toString(number-1).length(); j++ )
						s = "0"+s;
					
					os = new FileOutputStream( outputName.substring(0,idx)+s+outputName.substring(idx+1) );
				}
				else
					os = new FileOutputStream( outputName );
			}
			
			DOMDecorator.decorate(dom);
			
			// write generated instance.
			XMLSerializer s = new XMLSerializer( os, new OutputFormat("XML",encoding,true) );
			s.serialize(dom);
			
			if( os!=System.out )	os.close();
		}

		out.println();
		
		return 0;
	}
	
	private static InputSource getInputSource( String fileOrURL ) {
		try {
			// try it as a file
			InputSource is = new InputSource(new java.io.FileInputStream(fileOrURL));
			is.setSystemId(new File(fileOrURL).getCanonicalPath());
			return is;
		} catch( Exception e ) {
			// try it as an URL
			return new InputSource(fileOrURL);
		}
	}
}
