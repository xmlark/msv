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
import java.net.URL;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.relax.*;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.RefExpRemover;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.util.*;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.relaxns.grammar.RELAXGrammar;
import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.reader.dtd.DTDReader;
import org.apache.xml.serialize.*;

/**
 * command line driver.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Driver {
	
	private static void usage() {
		System.err.println(
			"Sun XMLGenerator\n"+
			"----------------\n"+
			"Usage: XMLGenerator <options> <schema> [<output name>]\n"+
			"Options:\n"+
			"  -dtd      : use a DTD as a schema\n"+
			"  -ascii    : use US-ASCII characters only\n"+
			"  -seed <n> : set random seed\n"+
			"  -depth <n>: set cut back depth\n"+
			"  -width <n>: maximum number of times '*'/'+' are repeated\n" +
			"  -n <n>    : # of files to be generated\n" +
			"  -warning  : show warnings.\n"+
			"  -quiet    : be quiet.\n"+
			"  -encoding <str>\n"+
			"      output encoding (Java name)\n"+
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
		try {
			Driver driver = new Driver();
			try {
				driver.parseArguments(args);
			} catch( CommandLineException e ) {
                System.err.println(e.getMessage());
				usage();
				System.exit(-1);
			}
			
			System.exit( driver.run(System.err) );
		} catch( DataTypeGenerator.GenerationException e ) {
			System.err.println(e.getMessage());
		}
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
	
	public Grammar grammar;
	public String outputName=null;
	private String encoding="UTF-8";
	private boolean createError = false;
	private boolean validate = true;
	private boolean debug = false;
	private boolean quiet = false;
	private boolean warning = false;
	private GeneratorOption opt = new GeneratorOption();
	{
		opt.random = new Random();
	}
	private int number = 1;
	
	
	private SAXParserFactory factory = SAXParserFactory.newInstance();
	{
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		try {
			factory.setFeature("http://xml.org/sax/features/validation",false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",false);
		} catch(Exception e) { ; }
	}

	private DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	{
		domFactory.setNamespaceAware(true);
		domFactory.setValidating(false);
	}
	
	// this set will receive tokens found in the given examples.
	public final Set exampleTokens = new java.util.HashSet();
	private DataTypeGeneratorImpl dtgi = new DataTypeGeneratorImpl();
	{
		opt.dtGenerator = dtgi;
		dtgi.tokens = exampleTokens;
	}
	
	/** Command line argument error. */
    private static class CommandLineException extends Exception {
        public CommandLineException( String msg ) { super(msg); }
    }
    
	/**
	 * Parses the arguments and fill the fields accordingly.
	 */
    public void parseArguments( String[] args ) throws CommandLineException, ParserConfigurationException {
		String grammarName=null;
		boolean dtdAsSchema = false;

		for( int i=0; i<args.length; i++ ) {
			if( args[i].equalsIgnoreCase("-debug") )	// secret option
				debug = true;
			else
			if( args[i].equalsIgnoreCase("-dtd") )
				dtdAsSchema = true;
			else
			if( args[i].equalsIgnoreCase("-quiet") )
				quiet = true;
			else
			if( args[i].equalsIgnoreCase("-warning") )
				warning = true;
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
                String fileName = args[++i];
                try {
				    XMLReader p = factory.newSAXParser().getXMLReader();
				    p.setContentHandler( new ExampleReader(exampleTokens) );
				    p.parse( getInputSource(fileName) );
                } catch( IOException e ) {
                    throw new CommandLineException("unable to parse "+fileName+" :"+e.getMessage());
                } catch( SAXException e ) {
                    throw new CommandLineException("unable to parse "+fileName+" :"+e.getMessage());
                }
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
			if( args[i].equalsIgnoreCase("-nonvalidate") )	// secret option
				validate = false;
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
				else
					throw new CommandLineException("unrecognized option :" + args[i]);
				
			}
			else {
				if( args[i].charAt(0)=='-' )
					throw new CommandLineException("unrecognized option :" + args[i]);
					
				if( grammarName==null )	grammarName = args[i];
				else
				if( outputName==null ) outputName = args[i];
				else
                    throw new CommandLineException("too many parameters");
			}
		}
		
		
		if(grammarName!=null) {
			// load a schema
			if(!quiet)
				System.err.println("parsing a grammar: "+grammarName);
		
			InputSource is = getInputSource(grammarName);
		
            try {
			    if(dtdAsSchema) {
			    	grammar = DTDReader.parse(
			    		is,
			    		new DebugController(warning,quiet),
			    		new ExpressionPool());
			    } else {
			    	grammar = GrammarLoader.loadSchema(
			    		is,
			    		new DebugController(warning,quiet),
			    		factory);
			    }
            } catch( IOException e ) {
                throw new CommandLineException("unable to parse "+grammarName+" :"+e.getMessage());
            } catch( SAXException e ) {
                throw new CommandLineException("unable to parse "+grammarName+" :"+e.getMessage());
            }
		}
	}
	
	/**
	 * Generate XML instances.
	 * 
	 * @return 0 if it run successfully. Non-zero if any error is encountered.
	 */
	public int run( PrintStream out ) throws Exception {
		
		if( grammar==null ) {
			usage();
			return -1;
		}
		
		
		Expression topLevel  = grammar.getTopLevel();
		
		// polish up this AGM for instance generation.
		if( grammar instanceof RELAXGrammar
		||  grammar instanceof RELAXModule )
			topLevel = topLevel.visit( new NoneTypeRemover(grammar.getPool()) );
		
		if( grammar instanceof XMLSchemaGrammar )
			topLevel = topLevel.visit( new SchemaLocationRemover(grammar.getPool()) );
		
	
		topLevel = topLevel.visit( new RefExpRemover(grammar.getPool(),true) );
		opt.pool = grammar.getPool();
		
		// generate instances
		//===========================================
		for( int i=0; i<number; i++ ) {
			if(!quiet) {
				if(number<=10 )	out.println("generating document #"+(i+1));
				else			out.print(">");
			}
			
			org.w3c.dom.Document dom;
			int retry=0;
			
			while(true) {
				dom = domFactory.newDocumentBuilder().newDocument();
				Generator.generate(topLevel,dom,opt);
				
				if( !validate)		break;
				
				// check the validity of generated document.
				DOM2toSAX2 d2s = new DOM2toSAX2();
				Verifier v = new Verifier(
					new REDocumentDeclaration(grammar),
					debug?
						(ErrorHandler)new ErrorHandlerImpl():
						(ErrorHandler)new IgnoreErrorHandler() );
				d2s.setContentHandler(v);
				d2s.traverse(dom);
				
				if( createError && !v.isValid() )	break;
				if( !createError && v.isValid() )	break;
				
				// do it again
				if( retry++ == 100 ) {
					out.println("unable to generate a proper instance.");
					return -1;
				}
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
			String path = new File(fileOrURL).getAbsolutePath();
			if (File.separatorChar != '/')
				path = path.replace(File.separatorChar, '/');
			if (!path.startsWith("/"))
				path = "/" + path;
//			if (!path.endsWith("/") && isDirectory())
//				path = path + "/";
			return new InputSource( new URL("file", "", path).toExternalForm() );
		} catch( Exception e ) {
			// try it as an URL
			return new InputSource(fileOrURL);
		}
	}
}
