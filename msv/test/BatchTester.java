/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
import javax.xml.parsers.*;
import java.util.Iterator;
import java.io.*;
import java.net.URL;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import junit.framework.*;
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.trex.TREXPatternPool;

/**
 * integration test: reads and verifies a lot.
 * 
 * Test schemata/instances are expected to follow a naming convention.
 * 
 * <ol>
 *  <li>legal schema must have "*.rlx" or "*.trex"
 *  <li>invalid schema must have "*.e.rlx" or "*.e.trex"
 *  <li>valid test document must have "*.vNN.xml".
 *      these documents are validated against "*.rlx" or "*.trex".
 *  <li>invalid test document must have "*.nNN.xml".
 * </ol>
 * 
 * Files that follow this naming convention are all tested. If any unexpected
 * result is found, main method returns non-0 exit code.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BatchTester
{
	protected SAXParserFactory factory =
		new org.apache.xerces.jaxp.SAXParserFactoryImpl();
//		new org.apache.crimson.jaxp.SAXParserFactoryImpl();

	
	/** test target: "relax", "trex", or "dtd" */
	public String target;
	/** test directory  */
	public String dir;
	public File testDir;
	/** schema file extension ".rlx", ".trex", or ".dtd" */
	public String ext;
	
	public static interface Loader {
		TREXDocumentDeclaration load(
			InputSource source, GrammarReaderController controller, SAXParserFactory factory )
			throws Exception;
	}
	protected Loader loader;
	
	/** DTD loader. */
	public static final Loader dtdLoader = new Loader(){
		public TREXDocumentDeclaration load( InputSource is, GrammarReaderController controller, SAXParserFactory factory ) throws Exception {
			is.setSystemId( toURL(is.getSystemId()) );
			Grammar g = DTDReader.parse(is,controller,"",new TREXPatternPool() );
			if(g==null)		return null;
			return new TREXDocumentDeclaration(g);
		}
		protected String toURL( String path ) throws Exception {
			path = new File(path).getAbsolutePath();
			if (File.separatorChar != '/')
				path = path.replace(File.separatorChar, '/');
			if (!path.startsWith("/"))
				path = "/" + path;
//			if (!path.endsWith("/") && isDirectory())
//				path = path + "/";
			return new URL("file", "", path).toExternalForm();
		}
	};
	/** RELAX/TREX/XSD loader. */
	public static final Loader genericLoader = new Loader(){
		public TREXDocumentDeclaration load( InputSource is, GrammarReaderController controller, SAXParserFactory factory ) throws Exception {
			return GrammarLoader.loadVGM(is,controller,factory);
		}
	};
	
	
	
	
	
	public void init( String target, String dir, String ext, Loader loader ) {
		this.target = target;
		this.dir = dir;
		this.ext = ext;
		this.loader = loader;
		
		testDir = new File(dir);
		
		factory.setNamespaceAware(true);
		factory.setValidating(false);
	}
	
	
	public void run( String[] av ) throws Exception {
		
		if( av.length<2 ) {
			System.out.println("usage "+this.getClass().getName()+" (relax|trex|xsd|dtd) <test case directory>");
			return;
		}
		
		if( av[0].equals("relax") )
			init( av[0], av[1], ".rlx", genericLoader );
		else
		if( av[0].equals("trex") )
			init( av[0], av[1], ".trex", genericLoader );
		else
		if( av[0].equals("xsd") )
			init( av[0], av[1], ".xsd", genericLoader );
		else
		if( av[0].equals("dtd") )
			init( av[0], av[1], ".dtd", dtdLoader );
		else {
			System.out.println("unrecognized language type: "+av[0] );
			return;
		}
		
		junit.textui.TestRunner.run( suite() );
	}
	
	/** gets a TestSuite that loads and verifies all test instances in the test directory. */
	public TestSuite suite() {		
		// enumerate all schema
		String[] schemas = testDir.list( new FilenameFilter(){
			public boolean accept( File dir, String name ) {
				return name.endsWith(ext);
			}
		} );
		
		// each schema will have its own suite.
		TestSuite suite = new TestSuite();
		populateSuite( suite, schemas );
//		if( schemas!=null )
//			for( int i=0; i<schemas.length; i++ )
//				suite.addTest( new SchemaSuite(this,schemas[i]).suite() );
		return suite;
	}
	
	protected abstract void populateSuite( TestSuite suite, String[] schemata );
	
	public static void report( ValidityViolation vv ) {
		System.out.println(
			vv.locator.getLineNumber()+":"+vv.locator.getColumnNumber()+
			"  " + vv.getMessage());
	}
}
