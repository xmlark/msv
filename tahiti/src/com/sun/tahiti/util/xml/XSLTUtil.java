package com.sun.tahiti.util.xml;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

public class XSLTUtil
{
	/** gets an XSLT engine. */
	public static TransformerHandler getTransformer( java.io.InputStream src )
			throws TransformerConfigurationException {
		
		SAXTransformerFactory xsltFactory = new org.apache.xalan.processor.TransformerFactoryImpl();
		return xsltFactory.newTransformerHandler(new StreamSource(src));
	}
}
