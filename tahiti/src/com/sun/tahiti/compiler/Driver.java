/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler;

import com.sun.tahiti.reader.relaxng.TRELAXNGReader;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Map;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import com.sun.tahiti.compiler.Controller;
import com.sun.tahiti.compiler.ControllerImpl;
import com.sun.tahiti.compiler.generator.ModelGenerator;
import com.sun.tahiti.compiler.ll.RuleSerializer;
import com.sun.tahiti.compiler.ll.RuleGenerator;
import com.sun.tahiti.compiler.sm.MarshallerSerializer;
import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.grammar.TypeItem;
import com.sun.tahiti.grammar.util.SuperClassBodyRemover;
import com.sun.tahiti.grammar.AnnotatedGrammar;
import com.sun.tahiti.util.xml.DOMBuilder;
import com.sun.tahiti.util.xml.XSLTUtil;
import com.sun.msv.reader.GrammarReaderController;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.XMLReader;
import org.xml.sax.DocumentHandler;

public class Driver
{
	private static class OutputMethod {}
	private static final OutputMethod java	= new OutputMethod();
	private static final OutputMethod xml	= new OutputMethod();
	
	private static void usage() {
		System.err.println(
			"Usage: tahitic <options> grammar.rng\n"+
			"[OUTPUT OPTIONS]\n"+
			"  -out xml    compiler will produce xml files.\n"+
			"  -out java   compiler will produce java source files.\n"+
			"  -d <dir>    specify output directory.\n"+
			"[GENERIC OPTIONS]\n"+
			"  -package <package name>\n"+
			"     place java files into the specified package.\n"+
			"" );
	}
	
	public static void main( String args[] ) throws Exception {
		System.exit(run(args));
	}
	
	public static int run( String args[] ) throws Exception {
		// TODO: where should we obtain the package name
		// and the file name of the generated grammar?
		
		OutputMethod out = java;
		String grammarFileName=null;
		File outDir = new File(".");
		
		for( int i=0; i<args.length; i++ ) {
			if( args[i].charAt(0)=='-' ) {
				if( args[i].equals("-out") ) {
					i++;
					if(args.length==i) { usage(); return -1; }
					out = null;
					if(args[i].equals("xml"))	out = xml;
					if(args[i].equals("java"))	out = java;
					if(out==null) { usage(); return -1; }
				} else
				if( args[i].equals("-d") ) {
					i++;
					if(args.length==i) { usage(); return -1; }
					outDir = new File(args[i]);
				}
				else {
					System.out.println("unknown option:"+args[i]);
					usage();
					return -1;
				}
			} else {
				if( grammarFileName!=null ) {
					System.err.println("more than one grammar specified");
					usage();
					return -1;
				}
				grammarFileName = args[i];
			}
		}
		if( grammarFileName==null ) {
			System.err.println("no grammar is specified");
			usage();
			return -1;
		}
		
		
		SAXParserFactory f = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		f.setNamespaceAware(true);		

	// parse a grammar file
	//-----------------------------------------
		System.err.println("parsing a schema...");
		GrammarReaderController grammarController =
			new com.sun.msv.driver.textui.DebugController(false,false);
		AnnotatedGrammar grammar;
		{// parse grammar
			TRELAXNGReader reader = new TRELAXNGReader( grammarController, f );
			reader.parse(grammarFileName);
			grammar = reader.getAnnotatedResult();
			if(grammar==null) {
				System.err.println("bailing out");
				return -1;
			}
		}
		
		
//		// grammar file will be generated as this name
//		String grammarName = g.grammarName;	// do not write the extension ".java".
//		System.out.println("grammar:"+grammarName);
		
		
	// generate a grammar file
	//-----------------------------------------
		System.err.println("generating a grammar file...");
		
		DocumentHandler grammarReceiver = null;
		if( out==xml ) {
			grammarReceiver = new XMLSerializer(
				new FileOutputStream( new File( outDir, "grammar.xml" ) ),
				new OutputFormat("xml",null,true) );
		} else {
			TransformerHandler xsltEngine = XSLTUtil.getTransformer(
				Driver.class.getResourceAsStream("grammar2java.xsl"));
			xsltEngine.setResult( new StreamResult(
				new FileOutputStream( getJavaFile(outDir,grammar.grammarName)) ));
			
			grammarReceiver = new com.sun.msv.writer.ContentHandlerAdaptor(xsltEngine);
		}

		SuperClassBodyRemover.remove(grammar);
		
		Map rules = RuleGenerator.create(grammar);
		
		Symbolizer symbolizer =
			RuleSerializer.serialize( grammar, rules, grammarReceiver );

	
	// generate marshallers
	//-----------------------------------------
		if( out==xml ) {
			System.err.println("generating marshallers...");
			DocumentHandler marshallerReceiver = null;
			marshallerReceiver = new XMLSerializer(
				new FileOutputStream( new File( outDir, "marshaller.xml" ) ),
				new OutputFormat("xml",null,true) );
			MarshallerSerializer.serialize( grammar, symbolizer, grammarController, marshallerReceiver );
		}
		// the marshaller file is unnecessary when we are generating Java files.
		

	// generate class definitions
	//-----------------------------------------
		System.err.println("generating class definitions...");
		
		final File od = outDir;
		ModelGenerator generator = null;
		Controller controller;
		if( out==xml ) {
			generator = ModelGenerator.xmlGenerator;
			controller = new ControllerImpl(grammarController) {
				public OutputStream getOutput( TypeItem item ) throws IOException {
					return new FileOutputStream(
						new File( od, item.getTypeName()+".xml") );
				}
			};
		} else {
			generator = ModelGenerator.javaGenerator;
			controller = new ControllerImpl(grammarController) {
				public OutputStream getOutput( TypeItem item ) throws IOException {
					return new FileOutputStream(
						getJavaFile( od, item.getTypeName() ) );
				}
			};
		}
		
		generator.generate( grammar, symbolizer, controller );

	
		System.err.println("done.");
		return 0;
	}
		

	
	/**
	 * gets a File object from the specified Java class name.
	 * 
	 * <p>
	 * This method creates directories if necessary.
	 * 
	 * @param javaPath
	 *		it will be something like "abc.def.ghi"
	 */
	private static File getJavaFile( File parent, String javaPath ) {
		File r = new File( parent, javaPath.replace('.','/')+".java" );
		new File(r.getParent()).mkdirs();
		return r;
	}
}
