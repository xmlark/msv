package com.sun.tahiti.runtime.sm;

public interface Marshaller {
	void startElement( String namespaceURI, String localName );
	void endElement( String namespaceURI, String localName );

	void startAttribute( String namespaceURI, String localName );
	void endAttribute( String namespaceURI, String localName );
	
	void data( Object data );
}
