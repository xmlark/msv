package com.sun.msv.rngconverter;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.GrammarLoader;

/**
 * Tests for XSD-to-RNG conversion — exposes the DatatypeFactory$1 (Proxy)
 * Error thrown by {@link com.sun.msv.writer.relaxng.PatternWriter#serializeDataType} when an XSD
 * uses built-in list types like xs:IDREFS, xs:ENTITIES, or xs:NMTOKENS.
 */
public class RNGConverterTest extends TestCase {

    public RNGConverterTest(String name) { super(name); }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(RNGConverterTest.class);
    }

    /**
     * Converting an XSD that uses xs:IDREFS to RELAX NG must not throw
     * an Error.  Currently fails with:
     * <pre>java.lang.Error: com.sun.msv.datatype.xsd.DatatypeFactory$1</pre>
     * because {@code serializeDataType()} does not handle the Proxy wrapper
     * used by DatatypeFactory for built-in list types.
     */
    public void testConvertXsdWithIDREFS() throws Exception {
        URL xsdUrl = getClass().getClassLoader().getResource("testcases/idrefs.xsd");
        assertNotNull("idrefs.xsd test resource must be on classpath", xsdUrl);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);

        Grammar grammar = GrammarLoader.loadSchema(
                xsdUrl.toExternalForm(),
                new DebugController(false, true, System.err),
                factory);
        assertNotNull("Grammar should load successfully from idrefs.xsd", grammar);

        ByteArrayOutputStream rngOut = new ByteArrayOutputStream();
        Driver.writeGrammar(grammar, rngOut);

        String rng = rngOut.toString("UTF-8");
        assertTrue("RNG output should contain a <grammar> element",
                rng.contains("<grammar"));
    }
}
