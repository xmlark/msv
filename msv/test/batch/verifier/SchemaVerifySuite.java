/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.verifier;

import batch.*;
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
class SchemaVerifySuite extends batch.SchemaSuite {
	
	SchemaVerifySuite( BatchVerifyTester parent, String schemaFileName ) {
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
		if( pathName.endsWith(".e"+parent.ext) ) {
			Grammar g = null;
			try {
				g = parent.loader.load( is, new ThrowErrorController(), parent.factory );
			} catch( Error e ) {
				System.out.println("schema:"+e.getMessage());
			}
			if( g!=null )
				fail("unexpected result");
		} else {
			Grammar g = parent.loader.load( is, new ThrowErrorController(), parent.factory );
			if( g==null )
				fail("unexpected result");	// unexpected result

			{// ensure that the serialization works
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(g);
				oos.flush();
				
				ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
				
				g = (Grammar)ois.readObject();
			}
			
			docDecl = new REDocumentDeclaration(g);
		}
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
					
					if( parent.target.equals("xsd") )
						v = new IDConstraintChecker( docDecl, new VerificationErrorHandlerImpl() );
					else
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
			}
			catch( SAXException se )
			{
				if( se.getException()!=null )
					throw se.getException();
				else
					throw se;
			}
		}
	}
}
