/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.util.xml;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 * utility methods for XSLT related tasks.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class XSLTUtil
{
	/** gets an XSLT engine. */
	public static TransformerHandler getTransformer( java.io.InputStream src )
			throws TransformerConfigurationException {
		
		SAXTransformerFactory xsltFactory = new org.apache.xalan.processor.TransformerFactoryImpl();
		return xsltFactory.newTransformerHandler(new StreamSource(src));
	}
}
