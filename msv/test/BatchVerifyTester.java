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
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.grammar.Grammar;

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
public class BatchVerifyTester extends BatchTester
{
	public static void main( String[] av ) throws Exception {
		new BatchVerifyTester().main(av);
	}
	
	/** gets a TestSuite that loads and verifies all test instances in the test directory. */
	protected void populateSuite( TestSuite suite, String[] schemas ) {
		// each schema will have its own suite.
		if( schemas!=null )
			for( int i=0; i<schemas.length; i++ )
				suite.addTest( new SchemaSuite(this,schemas[i]).suite() );
	}
}
