import javax.xml.parsers.*;
import java.util.Iterator;
import java.io.*;
import org.apache.xerces.parsers.SAXParser;
import junit.framework.*;
import com.sun.tranquilo.verifier.*;

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
 */
public class BatchVerifyTester
{
	protected SAXParserFactory factory =
		new org.apache.xerces.jaxp.SAXParserFactoryImpl();
//		new org.apache.crimson.jaxp.SAXParserFactoryImpl();

	
	/** test target: "relax" or "trex" */
	protected final String target;
	/** test directory  */
	protected final String dir;
	protected final File testDir;
	/** schema file extension ".rlx" or ".trex" */
	protected final String ext;
	
	public BatchVerifyTester( String target, String dir, String ext )
	{
		this.target = target;
		this.dir = dir;
		this.ext = ext;
		
		testDir = new File(dir);
		
		factory.setNamespaceAware(true);
		factory.setValidating(false);
	}
	
	
	public static void main( String[] av ) throws Exception
	{
		if( av.length<2 )
		{
			System.out.println("usage RELAXBatchTester (relax|trex) <test case directory>");
			return;
		}
		
		// run the test
		junit.textui.TestRunner.run( 
			new BatchVerifyTester( av[0], av[1]+"\\", av[0].equals("relax")?".rlx":".trex" ).suite() );
	}
	
	/** gets a TestSuite that loads and verifies all test instances in the test directory. */
	public TestSuite suite()
	{		
		// enumerate all schema
		String[] schemas = testDir.list( new FilenameFilter(){
			public boolean accept( File dir, String name )
			{
				return name.endsWith(ext);
			}
		} );
		
		// each schema will have its own suite.
		TestSuite suite = new TestSuite();
		for( int i=0; i<schemas.length; i++ )
			suite.addTest( new SchemaSuite(this,schemas[i]).suite() );
		return suite;
	}


	public static void report( ValidityViolation vv )
	{
		System.out.println(
			vv.locator.getLineNumber()+":"+vv.locator.getColumnNumber()+
			"  " + vv.getMessage());
	}
}
