package batch.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import batch.model.ISchema;
import batch.model.IValidator;
import batch.model.TestBuilder;

import com.sun.msv.writer.GrammarWriter;

/**
 * Builds a test suite for schema converter.
 * 
 * A derived class needs to implement the getWriter method and
 * the createReader method. The writer is used to convert AGM into
 * any XML format, then the corresponding reader will be used to
 * parse it into AGM again.
 */
public abstract class TestBuilderImpl implements TestBuilder
{
    protected abstract GrammarWriter getWriter();

    /*
    	RELAX allows undeclared attributes, but no other schema language does.
    	Therefore, some RELAX test instances which are originally valid can
    	results in an invalid instance.
    
    	this set contains all such test cases.
    */
    protected static final Set invalidTestCases = new java.util.HashSet();

    static {
        Set s = invalidTestCases;
        s.add("relax001.v00.xml");
        s.add("relax031.v00.xml");
        s.add("relax039.v00.xml");
        s.add("relax040.v00.xml");
        s.add("relax041.v00.xml");
    }

    public TestBuilderImpl(IValidator _firstValidator, IValidator _secondValidator) {
        this.firstValidator = _firstValidator;
        this.secondValidator = _secondValidator;
    }

    /** Used to parse the source schema language. */
    private final IValidator firstValidator;

    /** Used to parse and validate the converted schema. */
    private final IValidator secondValidator;

    private class SchemaTestCase extends TestCase {
        public SchemaTestCase(File _src) {
            super(TestBuilderImpl.getName(_src));
            this.src = _src;
        }

        private final File src;
        public ISchema schema;

        public void runTest() throws Exception {
            // load grammar
            ISchema source = firstValidator.parseSchema(src);
            if (source == null)
                fail("failed to parse the original grammar"); // unexpected result

            // then convert it to the target grammar,
            // and parse it by the target grammar reader parser.
            GrammarWriter writer = getWriter();

            // serialize it into the XML representation
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writer.setDocumentHandler(new XMLSerializer(baos, new OutputFormat("xml", null, true)));
            writer.write(source.asGrammar());

            // then parse it again
            schema = secondValidator.parseSchema(new ByteArrayInputStream(baos.toByteArray()));
            if (schema == null)
                fail("conversion failed"); // unexpected result
        }
    }

    private SchemaTestCase current;

    public Test createCorrectSchemaTest(final File schema) {
        current = new SchemaTestCase(schema);
        return current;
    }

    public Test createValidDocumentTest(File document) {
        return createDocumentTest(document, true);
    }
    public Test createInvalidDocumentTest(File document) {
        return createDocumentTest(document, false);
    }

    private Test createDocumentTest(final File document, final boolean expectation) {
        final SchemaTestCase schema = current;

        return new TestCase(getName(document)) {
            public void runTest() throws Exception {
                if (schema.schema == null)
                    // abort. there was an error in the schema
                    return;

                boolean r = secondValidator.validate(schema.schema, document);
                if (r == expectation)
                    return; // OK

                if (expectation)
                    fail("validator failed to accept a valid document");
                else
                    fail("validator accepted an invalid document");
            }
        };
    }

    public Test createIncorrectSchemaTest(final File schema) {
        // incorrect schema is not tested
        return new TestSuite();
    }

    /** Computes the test name from a file name. */
    private static String getName(File f) {
        return f.getPath();
    }
}
