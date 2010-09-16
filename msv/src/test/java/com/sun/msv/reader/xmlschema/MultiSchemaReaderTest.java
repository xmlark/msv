package com.sun.msv.reader.xmlschema;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController2;

public class MultiSchemaReaderTest {

    private static class LocalController implements GrammarReaderController2 {

        public LSResourceResolver getLSResourceResolver() {
            return null;
        }

        public void error(Locator[] locs, String errorMessage, Exception nestedException) {
            StringBuffer errors = new StringBuffer();
            for (Locator loc : locs) {
                errors.append("in " + loc.getSystemId() + " " + loc.getLineNumber() + ":"
                              + loc.getColumnNumber());
            }
            throw new RuntimeException(errors.toString(), nestedException);
        }

        public void warning(Locator[] locs, String errorMessage) {
            StringBuffer errors = new StringBuffer();
            for (Locator loc : locs) {
                errors.append("in " + loc.getSystemId() + " " + loc.getLineNumber() + ":"
                              + loc.getColumnNumber());
            }
            // no warning allowed.
            throw new RuntimeException("warning: " + errors.toString());
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    };

    @Test
    public void testWsdlMultiSchema() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        URL wsdlUri = getClass().getResource("/test.wsdl");
        assertNotNull(wsdlUri);
        Document wsdl = documentBuilder.parse(wsdlUri.openStream());
        String wsdlSystemId = wsdlUri.toExternalForm();
        DOMSource source = new DOMSource(wsdl);
        source.setSystemId(wsdlSystemId);

        LocalController controller = new LocalController();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLSchemaGrammar result = WSDLSchemaReader.read(source, factory, controller);
        assertNotNull(result);
    }
    
    @Test
    public void testWsdlMultiRefSchema() throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        URL wsdlUri = getClass().getResource("/multireference.wsdl");
        assertNotNull(wsdlUri);
        Document wsdl = documentBuilder.parse(wsdlUri.openStream());
        String wsdlSystemId = wsdlUri.toExternalForm();
        DOMSource source = new DOMSource(wsdl);
        source.setSystemId(wsdlSystemId);

        LocalController controller = new LocalController();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLSchemaGrammar result = WSDLSchemaReader.read(source, factory, controller);
        assertNotNull(result);
    }
}
