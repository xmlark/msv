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
import java.io.File;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParserFactory;
import com.sun.tranquilo.reader.GrammarReaderController;
import com.sun.tranquilo.reader.dtd.DTDReader;
import com.sun.tranquilo.grammar.Grammar;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import org.xml.sax.InputSource;


/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DTDBatchTest {

	public static class Loader implements BatchVerifyTester.Loader {
		public TREXDocumentDeclaration load( InputSource is, GrammarReaderController controller, SAXParserFactory factory ) throws Exception {
			is.setSystemId( fullPathToURL(is.getSystemId() ) );
			Grammar g = DTDReader.parse(is,controller,"",new TREXPatternPool() );
			if(g==null)		return null;
			return new TREXDocumentDeclaration(g);
		}
	}
	
	private static String fullPathToURL( String fileName ) {
		// under JDK1.2, use File.toURL
		StringBuffer buf = new StringBuffer("file://");
		if( fileName.charAt(0)!=File.separatorChar )	buf.append('/');
		
		for( int i=0; i<fileName.length(); i++ ) {
			char ch = fileName.charAt(i);
			if( ch==File.separatorChar )	buf.append('/');
			else							buf.append(ch);
		}
		return buf.toString();
	}

	public static TestSuite suite() {
		StringTokenizer tokens = new StringTokenizer( System.getProperty("DTDBatchTestDir"), ";" );
		
		TestSuite s = new TestSuite();
		while( tokens.hasMoreTokens() )
			s.addTest(
				new BatchVerifyTester("dtd", tokens.nextToken(), ".dtd", new Loader() ).suite() );
		
		return s;
	}
}
