package com.sun.tahiti.runtime.ll;


public class UnmarshallingException extends org.xml.sax.SAXException {
	public UnmarshallingException( Exception e ) {
		super(e);
	}
	public UnmarshallingException( String msg ) {
		super(msg);
	}
}
