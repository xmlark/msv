package batch.verifier;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import batch.model.ISchema;
import batch.model.IValidator;
import batch.model.TestBuilder;

public class TestBuilderImpl implements TestBuilder
{
    public TestBuilderImpl( IValidator _validator ) {
        this.validator = _validator;
    }
    
    private final IValidator validator;
    
    
    private class SchemaTestCase extends TestCase {
        public SchemaTestCase( File _src ) {
            super(TestBuilderImpl.getName(_src));
            this.src=_src;
        }
        
        private final File src;
        public ISchema schema;
        public void runTest() throws Exception {
            schema = validator.parseSchema(src);
            
            assertNotNull(
                "validator failed to compile a correct schema",
                schema );
        }
    }
    
    private SchemaTestCase current;
    
    public Test createCorrectSchemaTest( final File schema ) {
        current = new SchemaTestCase(schema);
        return current;
    }
    
    public Test createValidDocumentTest( File document ) {
        return createDocumentTest(document,true);
    }
    public Test createInvalidDocumentTest( File document ) {
        return createDocumentTest(document,false);
    }
    
    private Test createDocumentTest( final File document, final boolean expectation ) {
        final SchemaTestCase schema = current;
        
        return new TestCase(getName(document)) {
            public void runTest() throws Exception {
                if(schema.schema==null)
                    // abort. there was an error in the schema
                    return;
                
                boolean r = validator.validate(schema.schema,document);
                if(r==expectation)  return; // OK
                
                if(expectation)
                    fail("validator failed to accept a valid document");
                else
                    fail("validator accepted an invalid document");
            }
        };
    }
    
    public Test createIncorrectSchemaTest( final File schema ) {
        return new TestCase(getName(schema)) {
            public void runTest() throws Exception {
                assertNull(
                    "validator compiled an incorrect schema",
                    validator.parseSchema(schema) );
            }
        };
    }


    /** Computes the test name from a file name. */
    private static String getName( File f ) {
        return f.getPath();
    }
}
