package com.sun.tranquilo.driver.textui;
import javax.xml.parsers.*;
import java.util.Iterator;
import java.io.*;
import com.sun.tranquilo.grammar.trex.util.TREXPatternPrinter;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.grammar.relax.*;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.reader.trex.TREXGrammarReader;
import com.sun.tranquilo.reader.relax.RELAXReader;
import org.apache.xerces.parsers.SAXParser;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.verifier.util.VerificationErrorHandlerImpl;
import org.xml.sax.*;

public class RELAXBatchTest
{
	private static void report( ValidityViolation vv )
	{
		System.out.println(
			vv.locator.getLineNumber()+":"+vv.locator.getColumnNumber()+
			"  " + vv.getMessage());
	}

	public static void main( String[] av ) throws Exception
	{
		TestRunner.factory.setNamespaceAware(true);
		TestRunner.factory.setValidating(false);
		
		final String dir = "c:\\work\\relax\\";
		final File testDir = new File( dir );
		
		// enumerate all schema
		String[] schemas = testDir.list( new FilenameFilter(){
			public boolean accept( File dir, String name )
			{
				return name.startsWith("relax") && name.endsWith(".rlx")
					&& !name.endsWith("e.rlx");
			}
		} );
		
		
		for( int i=0; i<schemas.length; i++ )
		{
			final String prefix = schemas[i].substring(0,8);
			final String schemaFileName = dir+prefix+".rlx";
			
			System.out.println( "load schema: " + schemaFileName);

			RELAXGrammar g;
			
			InputSource is = new InputSource(schemaFileName);
			is.setSystemId(schemaFileName);
			
			g = RELAXReader.parse(
					is, TestRunner.factory,
					new DebugController(),
					new TREXPatternPool() );
			
			if( g==null )	continue;	// module syntax error
			
			String[] lst = testDir.list( new FilenameFilter (){ 
				public boolean accept( File dir, String name )
				{
					return name.startsWith(prefix) && name.endsWith(".xml");
				}
			} );
			
			for( int j=0; j<lst.length; j++ )
			{
				System.out.print( lst[j] +" : " );
				
				final boolean supposedToBeValid = (lst[j].indexOf("-v")!=-1);
				
				// verify XML instance
				try
				{
					ValidityViolation vv=null;
					try
					{
						XMLReader r =TestRunner.factory.newSAXParser().getXMLReader();
						r.setContentHandler(
							new Verifier(
								new TREXDocumentDeclaration(
									g.topLevel,
									(TREXPatternPool)g.pool,
									true),
								new VerificationErrorHandlerImpl() )
							);
						
						r.parse( new InputSource(dir+lst[j]) );
						System.out.println("o");
					}
					catch( ValidityViolation _vv )
					{
						System.out.println("x");
						vv = _vv;
					}
					
					if(vv!=null)		report(vv);
						
					if( ( supposedToBeValid && vv!=null )
					||  (!supposedToBeValid && vv==null ) )
						System.out.println("*** unexpected result *************************************");
				}
				catch( SAXException se )
				{
					System.out.println("SAX error("+ se.toString()+")" );
					if( se.getException()!=null )
						se.getException().printStackTrace(System.out);
					else
						se.printStackTrace(System.out);
				}
			}
		}

		System.out.println("Test completed. Press enter to continue");				
		System.in.read();	// wait for user confirmation of the result
	}
}
