package batch.model;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Parses a directory into the test suite.
 * 
 * This object will enumerate test files, and TestBuilder will create
 * actual test cases.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestReader
{
    public TestReader( TestBuilder _builder ) {
        this.builder = _builder;
    }

    private final TestBuilder builder;
    

    
    /**
     * Obtains a test object from a schema file (e.g., abc.rng) and
     * its instance documents.
     */
    public Test parseSchema( File schema ) throws Exception {

        String schemaName = schema.getName();
        File parent = new File(schema.getParent());
        
        final String prefix = schemaName.substring(0, schemaName.lastIndexOf('.')+1);
        final boolean isCorrect = schemaName.indexOf(".e.")==-1;
        
        if(isCorrect) {
            TestSuite suite = new TestSuite();
            
            suite.addTest( builder.createCorrectSchemaTest(schema) );
                
            // collects test instances.            
            String[] instances = parent.list( new FilenameFilter(){ 
                public boolean accept( File dir, String name ) {
                    return name.startsWith(prefix) && name.endsWith(".xml");
                }
            } );
            
            if( instances!=null ) {
                for( int i=0; i<instances.length; i++ ) {
                    boolean isValid = instances[i].indexOf(".v")!=-1;
                    File document = new File(parent,instances[i]);
                    if(isValid)
                        suite.addTest( builder.createValidDocumentTest(document) );
                    else
                        suite.addTest( builder.createInvalidDocumentTest(document) );
                }
            }
            
            return suite;
            
        } else {
            // if this schema is invalid
            return builder.createIncorrectSchemaTest(schema);
        }
    }


    /**
     * Parses a directory into a test suite .
     */
    public Test parseDirectory(
            File dir, final String ext, boolean recurseSubDirectory ) throws Exception {
        
        TestSuite suite = new TestSuite();
        
        // enumerate all schema
        String[] schemas = dir.list( new FilenameFilter(){
            public boolean accept( File dir, String name ) {
                return name.endsWith(ext);
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


    static final DocumentBuilderFactory domFactory;
    static {
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        domFactory.setValidating(false);
    }
    
    static final SAXParserFactory saxFactory;
    static {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        saxFactory.setValidating(false);
    }
    
    
}
