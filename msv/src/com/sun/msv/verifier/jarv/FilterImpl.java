package com.sun.tranquilo.verifier.jarv;

import org.iso_relax.verifier.VerifierFilter;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

class FilterImpl
	extends XMLFilterImpl
	implements VerifierFilter
{
	private final VerifierHandler verifier;

	FilterImpl( VerifierHandler verifier ) { this.verifier=verifier; }
	
	public boolean isValid() { return verifier.isValid(); }

    public void setDocumentLocator(Locator locator)
	{
		verifier.setDocumentLocator(locator);
		super.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException
	{
		verifier.startDocument();
		super.startDocument();
    }

    public void endDocument() throws SAXException
	{
		verifier.endDocument();
		super.endDocument();
    }

    public void startPrefixMapping( String prefix, String uri ) throws SAXException
	{
		verifier.startPrefixMapping(prefix, uri);
		super.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException
	{
		verifier.endPrefixMapping(prefix);
		super.endPrefixMapping(prefix);
    }

    public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) throws SAXException
	{
		verifier.startElement(namespaceURI, localName, qName, atts);
		super.startElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(	String namespaceURI, String localName, String qName ) throws SAXException
	{
		verifier.endElement(namespaceURI, localName, qName);
		super.endElement(namespaceURI, localName, qName);
    }

    public void characters( char ch[], int start, int length ) throws SAXException
	{
		verifier.characters(ch, start, length);
		super.characters(ch, start, length);
    }

    public void ignorableWhitespace( char ch[], int start, int length ) throws SAXException
	{
		verifier.ignorableWhitespace(ch, start, length);
		super.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException
	{
		verifier.processingInstruction(target, data);
		super.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException
	{
		verifier.skippedEntity(name);
		super.skippedEntity(name);
    }
}
