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
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParserFactory;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import org.xml.sax.InputSource;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXBatchTest
{
	public static class Loader implements BatchVerifyTester.Loader {
		public TREXDocumentDeclaration load( InputSource is, GrammarReaderController controller, SAXParserFactory factory ) throws Exception {
			return GrammarLoader.loadVGM(is,controller,factory);
		}
	}
	
	public static TestSuite suite()
	{
		StringTokenizer tokens = new StringTokenizer( System.getProperty("RELAXBatchTestDir"), ";" );
		
		TestSuite s = new TestSuite();
		while( tokens.hasMoreTokens() )
			s.addTest(
				new BatchVerifyTester("relax", tokens.nextToken(), ".rlx", new Loader() ).suite() );
		
		return s;
	}
}
