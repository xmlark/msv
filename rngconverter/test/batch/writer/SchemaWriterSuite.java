/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.writer;

import batch.*;
import junit.framework.*;
import org.xml.sax.*;
import java.io.*;
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.util.VerificationErrorHandlerImpl;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.reader.util.IgnoreController;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.relax.*;
import com.sun.msv.grammar.*;
import com.sun.msv.writer.*;

/**
 * loads a schema and creates test case for every test instances.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class SchemaWriterSuite extends batch.SchemaSuite {
	
	SchemaWriterSuite( BatchWriterTester parent, String schemaFileName ) {
		super(parent,schemaFileName);
	}
	
	protected void createInstanceTestCase( String pathName, String fileName, TestSuite suite ) {
		suite.addTest( new VerifyCase( fileName ) );
	}
		
	/** set by testLoadSchema method */
	protected REDocumentDeclaration docDecl;
		
	protected void runTest() throws Exception {
		
		final String pathName = parent.dir + File.separatorChar + schemaFileName;
		InputSource is = new InputSource(pathName);
		is.setSystemId(pathName);
			
		// load grammar
		Grammar g = parent.loader.load( is, new ThrowErrorController(), parent.factory );
		if( g==null )
			fail("failed to parse the original grammar");	// unexpected result
		
		// then convert it to the RELAX NG, and parse it by the RELAX NG parser.
		GrammarReader reader = ((BatchWriterTester)parent).createReader();
		GrammarWriter writer = ((BatchWriterTester)parent).getWriter();
		
		writer.setDocumentHandler(
			new ContentHandlerAdaptor(reader));
		writer.write(g);
		
		g = reader.getResultAsGrammar();
		if( g==null )
			fail("conversion failed");	// unexpected result
		
		docDecl = new REDocumentDeclaration(g);
	}
	
	public static boolean xor( boolean a, boolean b ) {
		return (a && !b) || (!a && b);
	}
	
		
	/** verifies one file */
	class VerifyCase extends TestCase {
		
		/** instance file name to be tested. */
		public final String fileName;
			
		public VerifyCase( String fileName ) {
			super("testVerify("+fileName+")");
			this.fileName = fileName;
		}
			
		protected void runTest() throws Exception {
			if( docDecl==null )
				fail("docDecl==null");
			
			try {
				ValidityViolation vv=null;
				try {
					XMLReader r =parent.factory.newSAXParser().getXMLReader();
					Verifier v;
					
					v = new Verifier( docDecl, new VerificationErrorHandlerImpl() );
					r.setContentHandler(v);
						
					r.parse( new InputSource(parent.dir+File.separatorChar+fileName) );
					
					assert( v.isValid() );
				} catch( ValidityViolation _vv ) {
					vv = _vv;
				}
					
//				if(vv!=null)		parent.report(vv);
						
				final boolean supposedToBeValid = (fileName.indexOf(".v")!=-1);
				if( xor( supposedToBeValid , vv==null ) ) {
					if( vv!=null )		fail( vv.getMessage() );
					else				fail( "should be invalid" );
				}
				
				if( vv!=null )
					System.out.println(vv.getMessage());
				
			} catch( SAXException se ) {
				
				if( se.getException()!=null )
					throw se.getException();
				else
					throw se;
			}
		}
	}
}
