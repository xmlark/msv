package com.sun.msv.reader.xmlschema;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import com.sun.msv.reader.GrammarReaderController2;

/**
 * Catch error messages and resolve schema locations.
 */
public class WSDLGrammarReaderController implements
		GrammarReaderController2, LSResourceResolver {
	
	private GrammarReaderController2 nextController;
	
	private static class StringLSInput implements LSInput {
		private String baseURI;
		private String data;
		private String systemId;
		
		public StringLSInput(String baseURI, String systemId, String data) {
			this.baseURI = baseURI;
			this.data = data;
			this.systemId = systemId;
		}

		public String getBaseURI() {
			return baseURI;
		}

		public InputStream getByteStream() {
			return null;
		}

		public boolean getCertifiedText() {
			return false;
		}

		public Reader getCharacterStream() {
			return null; 
		}

		public String getEncoding() {
			return null;
		}

		public String getPublicId() {
			return null;
		}

		public String getStringData() {
			return data;
		}

		public String getSystemId() {
			return systemId;
		}

		public void setBaseURI(String baseURI) {
			this.baseURI = baseURI;
		}

		public void setByteStream(InputStream byteStream) {
			throw new UnsupportedOperationException();
		}

		public void setCertifiedText(boolean certifiedText) {
			throw new UnsupportedOperationException();
		}

		public void setCharacterStream(Reader characterStream) {
			throw new UnsupportedOperationException();
		}

		public void setEncoding(String encoding) {
			throw new UnsupportedOperationException();
		}

		public void setPublicId(String publicId) {
			throw new UnsupportedOperationException();
			
		}

		public void setStringData(String stringData) {
			this.data = stringData;
		}

		public void setSystemId(String systemId) {
			this.systemId = systemId;
		}
	}
	
	private Map<String, EmbeddedSchema> schemas;
	private String baseURI;

	/**
	 * create the resolving controller.
	 * @param baseURI URI of the WSDL.
	 * @param sources
	 */
	public WSDLGrammarReaderController(GrammarReaderController2 nextController,
									   String baseURI, Map<String, EmbeddedSchema> sources) {
		this.nextController = nextController;
		this.baseURI = baseURI;
		this.schemas = sources;
	}

	public void error(Locator[] locs, String msg, Exception nestedException) {
		if (nextController != null) {
			nextController.error(locs, msg, nestedException);
		}
	}

	public void warning(Locator[] locs, String errorMessage) {
		if (nextController != null) {
			nextController.warning(locs, errorMessage);
		}
	}

	public InputSource resolveEntity(String publicId, String systemId) {
		return null;
	}

	public LSResourceResolver getLSResourceResolver() {
		return this;
	}

	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		EmbeddedSchema schema = schemas.get(namespaceURI);
		if (schema != null) {
			return new StringLSInput(this.baseURI, schema.getSystemId(), 
					schema.getSchemaAsString());
		} else {
			return null;
		}
	}
}
