/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
import junit.framework.*;
import org.xml.sax.*;
import java.io.*;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.util.VerificationErrorHandlerImpl;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.reader.util.GrammarLoader;
import com.sun.tranquilo.reader.util.IgnoreController;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.grammar.relax.*;
import com.sun.tranquilo.grammar.*;

/**
 * loads a schema and creates test case for every test instances.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class SchemaSuite extends TestCase
{
	SchemaSuite( BatchVerifyTester parent, String schemaFileName )
	{
		super("testLoadSchema("+schemaFileName+")");
		this.parent = parent;
		this.schemaFileName = schemaFileName;
	}
	
	protected final BatchVerifyTester parent;
	protected final String schemaFileName;
		
	public TestSuite suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTest(this);	// this object itself will load schema as a test.
			
		final String prefix = schemaFileName.substring(0, schemaFileName.lastIndexOf('.')+1);

		// gets test instances.			
		String[] lst = parent.testDir.list( new FilenameFilter (){ 
			public boolean accept( File dir, String name )
			{
				return name.startsWith(prefix) && name.endsWith(".xml");
			}
		} );
			
		// adds them as a test
		for( int i=0; i<lst.length; i++ )
			suite.addTest( new VerifyCase( lst[i] ) );
			
		return suite;
	}
		
	/** set by testLoadSchema method */
	protected TREXDocumentDeclaration docDecl;
		
	protected void runTest() throws Exception
	{
		final String pathName = parent.dir + "\\" + schemaFileName;
		InputSource is = new InputSource(pathName);
		is.setSystemId(pathName);
			
		// load grammar
		if( pathName.endsWith(".e"+parent.ext) ) {
			docDecl = GrammarLoader.loadVGM( is,
				new IgnoreController(), parent.factory );
			if( docDecl!=null )
				fail("unexpected result");
		} else {
			docDecl = GrammarLoader.loadVGM( is,
				new ThrowErrorController(), parent.factory );
			if( docDecl==null )
				fail("unexpected result");	// unexpected result
		}
	}
	
	
	public static boolean xor( boolean a, boolean b ) {
		return (a && !b) || (!a && b);
	}
	
		
	/** verifies one file */
	class VerifyCase extends TestCase
	{
		public final String fileName;
			
		public VerifyCase( String fileName )
		{
			super("testVerify("+fileName+")");
			this.fileName = fileName;
		}
			
		protected void runTest() throws Exception
		{
			if( docDecl==null )
				fail("docDecl==null");
			
			try
			{
				ValidityViolation vv=null;
				try
				{
					XMLReader r =parent.factory.newSAXParser().getXMLReader();
					Verifier v =
						new Verifier( docDecl,
							new VerificationErrorHandlerImpl() );
					r.setContentHandler(v);
						
					r.parse( new InputSource(parent.dir+"\\"+fileName) );
					
					assert( v.isValid() );
				}
				catch( ValidityViolation _vv )
				{
					vv = _vv;
				}
					
//				if(vv!=null)		parent.report(vv);
						
				final boolean supposedToBeValid = (fileName.indexOf(".v")!=-1);
				if( xor( supposedToBeValid , vv==null ) )
				{
					if( vv!=null )		fail( vv.getMessage() );
					else				fail( "should be invalid" );
				}
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
