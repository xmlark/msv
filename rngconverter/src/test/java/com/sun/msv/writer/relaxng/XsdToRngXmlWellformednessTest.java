/*
 * Validates RNG converter output with the JDK DOM parser only
 * (no xml-apis on the test classpath — see pom exclusions).
 */
package com.sun.msv.writer.relaxng;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.GrammarLoader;

/**
 * Converts a bundled sample XSD to RELAX NG via the same API as {@link Driver}, then checks
 * the output is well-formed XML using {@link DocumentBuilderFactory} (JDK {@code java.xml} only).
 */
public class XsdToRngXmlWellformednessTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void convertsBundledSampleXsdToWellFormedRelaxNgXml() throws Exception {
        URL xsd = getClass().getResource("/testcases/any.xsd");
        Assert.assertNotNull("Classpath resource testcases/any.xsd must exist", xsd);

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        Grammar g = GrammarLoader.loadSchema(
            new File(xsd.toURI()).getAbsolutePath(),
            new DebugController(true, false, System.err),
            spf);
        Assert.assertNotNull(g);

        File out = folder.newFile("converted.rng");
        try (FileOutputStream fos = new FileOutputStream(out)) {
            Driver.writeGrammar(g, fos);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(out);
        Assert.assertNotNull(doc.getDocumentElement());
    }
}
