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
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import junit.framework.*;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.reader.GrammarReaderController;
import com.sun.tranquilo.reader.dtd.DTDReader;
import com.sun.tranquilo.reader.util.GrammarLoader;
import com.sun.tranquilo.grammar.Grammar;

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
public class BatchVerifyTester
{
	protected SAXParserFactory factory =
		new org.apache.xerces.jaxp.SAXParserFactoryImpl();
//		new org.apache.crimson.jaxp.SAXParserFactoryImpl();

	
	/** test target: "relax", "trex", or "dtd" */
	protected final String target;
	/** test directory  */
	protected final String dir;
	protected final File testDir;
	/** schema file extension ".rlx", ".trex", or ".dtd" */
	protected final String ext;
	
	public static interface Loader {
		TREXDocumentDeclaration load(
			InputSource source, GrammarReaderController controller, SAXParserFactory factory )
			throws Exception;
	}
	protected final Loader loader;
	
	public BatchVerifyTester( String target, String dir, String ext, Loader loader )
	{
		this.target = target;
		this.dir = dir;
		this.ext = ext;
		this.loader = loader;
		
		testDir = new File(dir);
		
		factory.setNamespaceAware(true);
		factory.setValidating(false);
	}
	
	
	public static void main( String[] av ) throws Exception {
		
		if( av.length<2 ) {
			System.out.println("usage BatchVerifyTester (relax|trex|xsd|dtd) <test case directory>");
			return;
		}
		
		if( av[0].equals("relax") ) {
			junit.textui.TestRunner.run(
				new BatchVerifyTester( av[0], av[1], ".rlx", new RELAXBatchTest.Loader()).suite());
			return;
		}
		
		if( av[0].equals("trex") ) {
			junit.textui.TestRunner.run(
				new BatchVerifyTester( av[0], av[1], ".trex", new TREXBatchTest.Loader()).suite());
			return;
		}
		
		if( av[0].equals("xsd") ) {
			junit.textui.TestRunner.run(
				new BatchVerifyTester( av[0], av[1], ".xsd", new XSDBatchTest.Loader()).suite() );
			return;
		}
		
		if( av[0].equals("dtd") ) {
			junit.textui.TestRunner.run(
				new BatchVerifyTester( av[0], av[1], ".dtd", new DTDBatchTest.Loader()).suite() );
			return;
		}
		
		System.out.println("unrecognized language type: "+av[0] );
		return;
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
		for( int i=0; i<schemas.length; i++ )
			suite.addTest( new SchemaSuite(this,schemas[i]).suite() );
		return suite;
	}


	public static void report( ValidityViolation vv ) {
		System.out.println(
			vv.locator.getLineNumber()+":"+vv.locator.getColumnNumber()+
			"  " + vv.getMessage());
	}
}
