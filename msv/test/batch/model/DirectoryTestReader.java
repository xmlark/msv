package batch.model;

import org.relaxng.testharness.model.*;
import org.relaxng.testharness.reader.TestSuiteReader;
import java.io.File;
import java.io.FilenameFilter;
import javax.xml.parsers.*;

/**
 * Parses a directory into the test suite object model.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DirectoryTestReader
{
	static final DocumentBuilderFactory domFactory;
	static {
		domFactory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
		domFactory.setNamespaceAware(true);
		domFactory.setValidating(false);
	}
	
	static final SAXParserFactory saxFactory;
	static {
		saxFactory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		saxFactory.setNamespaceAware(true);
		saxFactory.setValidating(false);
	}
	
	
	
	/**
	 * Obtains an RNGTest object from a schema file (e.g., abc.rng) and
	 * its instance documents.
	 */
	public static RNGTest parseSchema( File schema ) throws Exception {

		String schemaName = schema.getName();
		File parent = new File(schema.getParent());
		
		if(schemaName.endsWith(".ssuite")) {
			// this is a schema suite file. 
			return TestSuiteReader.parse(schema);
		}
		
		final String prefix = schemaName.substring(0, schemaName.lastIndexOf('.')+1);
		final boolean isCorrect = schemaName.indexOf(".e.")==-1;
		
		if(isCorrect) {
			RNGValidTestCase tcase = new RNGValidTestCase();
			tcase.header = new RNGHeaderImpl(schema);
			tcase.pattern = new XMLDocumentImpl(schema);
				
			// collects test instances.			
			String[] instances = parent.list( new FilenameFilter(){ 
				public boolean accept( File dir, String name ) {
					return name.startsWith(prefix) && name.endsWith(".xml");
				}
			} );
			
			if( instances!=null ) {
				for( int i=0; i<instances.length; i++ ) {
					boolean isValid = instances[i].indexOf(".v")!=-1;
					XMLDocument doc = new XMLDocumentImpl(new File(parent,instances[i]));
					
					if(isValid)		tcase.addValidDocument(doc);
					else			tcase.addInvalidDocument(doc);
				}
			}
			
			return tcase;
			
		} else {
			// if this schema is invalid
			RNGInvalidTestCase tcase = new RNGInvalidTestCase();
			tcase.header = new RNGHeaderImpl(schema);
			tcase.addPattern( new XMLDocumentImpl(schema) );
			
			return tcase;
		}
	}


	/**
	 * Parses a directory into the test suite object model.
	 */
	public static RNGTestSuite parseDirectory(
			File dir, final String ext, boolean recurseSubDirectory ) throws Exception {
		
		RNGTestSuite suite = new RNGTestSuite();
		suite.header = new RNGHeaderImpl(dir);
		
		// enumerate all schema
		String[] schemas = dir.list( new FilenameFilter(){
			public boolean accept( File dir, String name ) {
				return name.endsWith(ext)
					|| name.endsWith(ext+".ssuite");
			}
		} );
		
		for( int i=0; i<schemas.length; i++ )
			suite.addTest( parseSchema(new File(dir,schemas[i])) );
		
		if( recurseSubDirectory ) {
			// recursively process sub directories.
			String[] subdirs = dir.list( new FilenameFilter(){
				public boolean accept( File dir, String name ) {
					return new File(dir,name).isDirectory();
				}
			});
			for( int i=0; i<subdirs.length; i++ )
				suite.addTest( parseDirectory(
					new File(dir,subdirs[i]), ext, true ) );
		}
		
		return suite;
	}
}
