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
import java.net.URL;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParserFactory;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.dtd.DTDReader;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.trex.TREXPatternPool;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import org.xml.sax.InputSource;


/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DTDBatchTest {

	protected static String toURL( String path ) throws Exception {
		path = new File(path).getAbsolutePath();
		if (File.separatorChar != '/')
			path = path.replace(File.separatorChar, '/');
		if (!path.startsWith("/"))
			path = "/" + path;
//		if (!path.endsWith("/") && isDirectory())
//			path = path + "/";
		return new URL("file", "", path).toExternalForm();
	}
	
	public static class Loader implements BatchVerifyTester.Loader {
		public TREXDocumentDeclaration load( InputSource is, GrammarReaderController controller, SAXParserFactory factory ) throws Exception {
			is.setSystemId( toURL(is.getSystemId()) );
			Grammar g = DTDReader.parse(is,controller,"",new TREXPatternPool() );
			if(g==null)		return null;
			return new TREXDocumentDeclaration(g);
		}
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
