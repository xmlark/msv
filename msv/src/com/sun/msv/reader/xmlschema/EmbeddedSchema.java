package com.sun.msv.reader.xmlschema;

import java.io.StringWriter;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

/**
 * A schema in a DOM Element. This is used in the WSDLSchemaReader to handle
 * inter-schema cross-references. XS 
 *
 */
public class EmbeddedSchema {

	private String systemId;
	private Element schemaElement;
	private String schemaAsString;

	/**
	 * Create object to represent one of the schemas in a WSDL
	 * 
	 * @param systemId
	 *            schema system Id.
	 * @param schemaElement
	 *            Element for the schema.
	 */
	public EmbeddedSchema(String systemId, Element schemaElement) {
		this.systemId = systemId;
		this.schemaElement = schemaElement;
	}

	public String getSystemId() {
		return systemId;
	}

	public Element getSchemaElement() {
		return schemaElement;
	}

	public String getSchemaAsString() {
		synchronized (this) {
			if (schemaAsString == null) {
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				DOMSource source = new DOMSource(schemaElement);
				try {
					transformerFactory.newTransformer().transform(source,
							result);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				schemaAsString = writer.toString();
			}
		}
		return schemaAsString;
	}

}
