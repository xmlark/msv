package com.sun.tranquilo.generator;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

public class DOM2toSAX2 {
	
	public DOM2toSAX2() {}
	
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
			sa.addAttribute( a.getNamespaceURI(), a.getLocalName(), a.getName(),
				null, a.getValue() );
		}
		return sa;
	}
	
	protected void onElement( Element e ) throws SAXException {
		
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
	}
	
	protected void onText( Text t ) throws SAXException {
		String s = t.getData();
		handler.characters( s.toCharArray(), 0, s.length() );
	}
}
