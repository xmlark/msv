/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.sm;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.w3c.dom.Document;
import com.sun.tahiti.util.xml.SAXEventGenerator;

/**
 * High-level marshalling APIs.
 * 
 * Applications should use methods in this class.
 * Currently, this class relies on the XML serializer of Apache.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MarshallerAPI
{
	// do not allow instanciation. it is unncessary.
	private MarshallerAPI() {}
	
	/**
	 * marshalls an object tree into DOM.
	 * 
	 * @return
	 *		This method will never return null. If an error happens,
	 *		an exception will be thrown.
	 */
	public static Document marshallToDOM( MarshallableObject obj ) throws ParserConfigurationException {
		
		DOMMarshaller dm = new DOMMarshaller();
		obj.marshall(dm);
		return dm.getResult();
	}
	
	/**
	 * marshalls an object and produces SAX2 events.
	 */
	public static void marshall( MarshallableObject obj, ContentHandler handler )
			throws ParserConfigurationException, SAXException {
		
		Document dom = marshallToDOM(obj);
		SAXEventGenerator.parse(dom,handler);
	}
	
	
	/**
	 * marshalls an object to an OutputStream.
	 * 
	 * UTF-8 is used for the encoding. The output will be indented.
	 * 
	 * @param out
	 *		this object will receive XML.
	 */
	public static void marshall( MarshallableObject obj, OutputStream out )
							throws ParserConfigurationException, IOException {
		
		marshall(obj,out,"UTF-8",true);
	}
	
	/**
	 * marshalls an object to an OutputStream.
	 * 
	 * @param out
	 *		this object will receive XML.
	 * @param encoding
	 *		the encoding of the produced XML.
	 * @param indent
	 *		If true, the produced XML is indented. This makes it easier
	 *		to read but bigger. If set to false, the produced XML will not
	 *		contain unnecessary whitespaces. This makes it smaller but illegible.
	 */
	public static void marshall( MarshallableObject obj, OutputStream out,
			String encoding, boolean indent ) throws ParserConfigurationException, IOException {
		
		Document dom = marshallToDOM(obj);
		new XMLSerializer(out,new OutputFormat("xml",encoding,indent)).serialize(dom);
	}
	
	/**
	 * marshalls an object to an XML file.
	 */
	public static void marshall( MarshallableObject obj, File out )
				throws ParserConfigurationException, IOException {
		marshall( obj, new FileOutputStream(out) );
	}
	
	/**
	 * marshalls an object to an XML file.
	 */
	public static void marshall( MarshallableObject obj, File out,
			String encoding, boolean indent ) throws ParserConfigurationException, IOException {
		marshall( obj, new FileOutputStream(out), encoding, indent );
	}

	/**
	 * marshalls an object to an XML file.
	 */
	public static void marshall( MarshallableObject obj, String fileName )
				throws ParserConfigurationException, IOException {
		marshall( obj, new FileOutputStream(fileName) );
	}
	
	/**
	 * marshalls an object to an XML file.
	 */
	public static void marshall( MarshallableObject obj, String fileName,
			String encoding, boolean indent ) throws ParserConfigurationException, IOException {
		marshall( obj, new FileOutputStream(fileName), encoding, indent );
	}
}
