/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch;

import junit.framework.*;
import org.xml.sax.*;
import java.io.*;
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.verifier.util.VerificationErrorHandlerImpl;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.relax.*;
import com.sun.msv.grammar.*;

/**
 * loads a schema and creates test case for every test instances.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class SchemaSuite extends TestCase {
	
	protected SchemaSuite( BatchTester parent, String schemaFileName ) {
		super("testSchema("+schemaFileName+")");
		this.parent = parent;
		this.schemaFileName = schemaFileName;
	}
	
	public final BatchTester parent;
	public final String schemaFileName;
		
	public TestSuite suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(this);	// this object itself will load schema as a test.
			
		final String prefix = schemaFileName.substring(0, schemaFileName.lastIndexOf('.')+1);

		// gets test instances.			
		String[] lst = parent.testDir.list( new FilenameFilter (){ 
			public boolean accept( File dir, String name ) {
				return name.startsWith(prefix) && name.endsWith(".xml");
			}
		} );
			
		for( int i=0; i<lst.length; i++ )
			createInstanceTestCase( parent.dir+File.separatorChar+lst[i], lst[i], suite );
			
		return suite;
	}
	
	/** creates test case for individual test instance, then adds it to the suite. */
	protected abstract void createInstanceTestCase( String pathName, String fileName, TestSuite suite );
		
	protected abstract void runTest() throws Exception;
}
