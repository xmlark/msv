package batch.model;

import java.io.File;

import junit.framework.Test;

/**
 * Builds test cases from individual schema/document files.
 */
public interface TestBuilder
{
    /**
     * Builds a test for a correct schema file.
     * 
     * If this schema has associated test documents,
     * The createXXXXDocumentTest methods will be called
     * after this method.
     */
    Test createCorrectSchemaTest( File schemaFile );
    
    // these methods are called after the createCorrectSchemaTest method
    // is called
    Test createValidDocumentTest( File documentFile );
    Test createInvalidDocumentTest( File documentFile );
    
    /**
     * Builds a test for an incorrect schema file.
     * 
     * The createXXXXDocumentTest methods will never be called
     * for this type of the schema test.
     */
    Test createIncorrectSchemaTest( File schemaFile );
}
