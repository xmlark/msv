package com.sun.msv.generator;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

public class DOM2toSAX2 {
	
	public DOM2toSAX2() {}

	private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
	
	protected ContentHandler handler;
	public void setContentHandler( ContentHandler handler ) {
		this.handler = handler;
	}
	public ContentHandler getContentHandler() {
		return this.handler;
	}
	
	public void traverse(Document dom) throws SAXException {
		if(handler==null)
			throw new IllegalArgumentException("content handler is not set");
		
		handler.startDocument();
		onElement(dom.getDocumentElement());
		handler.endDocument();
	}
	
	/** converts DOM attributes into SAX attributes. */
	protected Attributes convertAttributes( Element e ) {
		NamedNodeMap atts =	e.getAttributes();
		AttributesImpl sa = new AttributesImpl();
		for( int i=0; i<atts.getLength(); i++ ) {
			Attr a = (Attr)atts.item(i);
			if(a.getNamespaceURI().equals(XMLNS_URI))	continue;
			
			String value = a.getValue();
			if(value==null)	value="";
			sa.addAttribute( a.getNamespaceURI(), a.getLocalName(), a.getName(),
				"CDATA", value );
		}
		return sa;
	}
	
	protected void onElement( Element e ) throws SAXException {
		
		// declare namespace prefix
		NamedNodeMap atts =	e.getAttributes();
		for( int i=0; i<atts.getLength(); i++ ) {
			Attr a = (Attr)atts.item(i);
			if(!a.getName().startsWith("xmlns"))	continue;
			
			String prefix = a.getName();
			int idx = prefix.indexOf(':');
			if(idx<0)	handler.startPrefixMapping("",a.getValue());
			else		handler.startPrefixMapping(prefix.substring(idx+1),a.getValue());
		}
		
		handler.startElement(
			e.getNamespaceURI(), e.getLocalName(), e.getTagName(),
			convertAttributes(e) );
		
		NodeList children = e.getChildNodes();
		for( int i=0; i<children.getLength(); i++ ) {
			Node n = children.item(i);
			if( n instanceof Element )
				onElement((Element)n);
			if( n instanceof Text )
				onText((Text)n);
		}
		
		handler.endElement( e.getNamespaceURI(), e.getLocalName(), e.getTagName() );

		// end namespace mapping
		for( int i=0; i<atts.getLength(); i++ ) {
			Attr a = (Attr)atts.item(i);
			if(!a.getName().startsWith("xmlns"))	continue;
			
			String prefix = a.getName();
			int idx = prefix.indexOf(':');
			if(idx<0)	handler.endPrefixMapping("");
			else		handler.endPrefixMapping(prefix.substring(idx+1));
		}
	}
	
	protected void onText( Text t ) throws SAXException {
		String s = t.getData();
		if(s!=null)
			handler.characters( s.toCharArray(), 0, s.length() );
	}
}
