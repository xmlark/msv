/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.generator;

import batch.model.*;
import batch.driver.ISchemaImpl;
import junit.framework.*;
import com.sun.msv.generator.*;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

class TestBuilderImpl implements TestBuilder
{
    protected TestBuilderImpl( IValidator _validator, SAXParserFactory factory ) throws Exception {
        this.validator = _validator;
        this.reader = factory.newSAXParser().getXMLReader();
    }
    
    private final IValidator validator;
    private final XMLReader reader;
    
    public Test createIncorrectSchemaTest( File schema ) {
        return emptyTest();
    }
    
    public Test createCorrectSchemaTest( File schema ) {
        current = new SchemaTestCase(schema);
        return current;
    }
    
    private SchemaTestCase current;
    protected class SchemaTestCase extends TestCase {
        
        protected SchemaTestCase( File schema ) {
            super(schema.getName());
            this.schemaFile = schema;
        }
        
        private final File schemaFile;
        private final Vector examples =new Vector();
        
        public void addExample( File example ) {
            examples.add(example);
        }
        
	    /**
	     * A test consists of
	     * 
	     * 1. converts a grammar into the target format
	     * 2. tests the instance documents.
	     */
		public void runTest() throws Exception {

			Driver driver = new Driver();	// generator instance.
				
			// parse parameters
			driver.parseArguments(new String[]{"-seed","0", "-n","30", "-quiet"});
				
			// parse example documents
            Iterator itr = examples.iterator();
			while( itr.hasNext() ) {
				File example = (File)itr.next();
                
                reader.setContentHandler( new ExampleReader(driver.exampleTokens) );
                reader.parse( com.sun.msv.util.Util.getInputSource(example.getAbsolutePath()) );
			}
				
			// set the grammar
			ISchemaImpl schema = (ISchemaImpl)validator.parseSchema(schemaFile);
			assert( "failed to parse the schema", schema!=null );
			driver.grammar = schema.grammar;
			driver.outputName = "NUL";
				
			// run the test
			assert( "generator for "+schemaFile.getName(), driver.run(System.out)==0 );
				
				
			// parse additional parameter
			// generally, calling the parseArguments method more than once
			// is not supported. So this is a hack.
			driver.parseArguments(new String[]{"-error","10/100"});

			assert( "generator for "+schemaFile.getName(), driver.run(System.out)==0 );
		}
	}
    
    public Test createValidDocumentTest( File document ) {
        current.addExample(document);
        return emptyTest();
    }
    
    public Test createInvalidDocumentTest( File document ) {
        return emptyTest();
    }
    
    /** Returns a test that does nothing. */
    private Test emptyTest() {
        return new TestSuite();
    }
}
