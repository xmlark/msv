package com.sun.msv.schematron.verifier;

import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.VerifierFilter;
import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.schematron.grammar.SElementExp;
import com.sun.msv.schematron.grammar.SAction;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.schematron.util.DOMBuilder;
import org.apache.xpath.XPathContext;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.LocatorImpl;
import org.relaxng.datatype.Datatype;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class RelmesVerifier implements IVerifier {
	
	public RelmesVerifier( IVerifier core ) throws ParserConfigurationException {
		this.core = new VerifierFilter(core);
		this.schChecker = new SchematronVerifier();
		this.core.setContentHandler(schChecker);
	}
	public RelmesVerifier( DocumentDeclaration docDecl, VerificationErrorHandler handler )
				throws ParserConfigurationException {
		this(new Verifier(docDecl,handler));
	}
	
	/**
	 * this object performs RELAX NG validation,
	 * then pass type-information to the schematron checker.
	 */
	private final VerifierFilter core;
	
	/**
	 * this object will perform the schematron validation on the endDocument method.
	 */
	private final SchematronVerifier schChecker;
	
	/**
	 * this flag is set to true when all schematron validations are successful.
	 */
	private boolean schematronValid;

	/** performs schematron validation. */
	class SchematronVerifier extends DOMBuilder {
		
		SchematronVerifier() throws ParserConfigurationException {}
		
		public void startElement( String ns, String local, String qname, Attributes atts ) throws SAXException {
			super.startElement(ns,local,qname,atts);
			
			Object o = getCurrentElementType();
			if( o instanceof SElementExp )
				// memorize this node so that we can check it later.
				checks.add(new CheckItem( super.parent, (SElementExp)o, getLocator() ));
		}
		
		private class CheckItem {
			CheckItem( Node node, SElementExp type, Locator loc ) {
				this.node = node;
				this.type = type;
				// copy the location. since Locator object is owned by the parser.
				this.location = new LocatorImpl(loc);
			}
			public final Node			node;
			public final SElementExp	type;
			public final Locator		location;
		};
		
		/**
		 * list of CheckItems to be checked.
		 */
		private Vector checks = new Vector();
		
		public void startDocument() throws SAXException {
			super.startDocument();
			checks.clear();
		}
		
		public void endDocument() throws SAXException {
			super.endDocument();
			schematronValid = true;
			
			final int len = checks.size();
			for( int i=0; i<len; i++ ) {
				CheckItem item = (CheckItem)checks.get(i);
				try {
					testItem((CheckItem)checks.get(i));
				} catch( TransformerException e ) {
					getVErrorHandler().onError( new ValidityViolation(
						item.location, "XPath error:"+e.getMessage() ) );
					schematronValid = false;
					return;
				}
			}
		}
		
		private void testItem( CheckItem item )
					throws SAXException, TransformerException {
			// perform test
			for( int i=0; i<item.type.rules.length; i++ )
				testRule( item, item.type.rules[i] );
		}
		
		private void testRule( CheckItem item, SRule rule )
					throws SAXException, TransformerException {
			// the following fragment are basically copy&paste from
			// org.apache.xpath.XPathAPI
					
			synchronized(rule) {
				// I'm not sure whether XPath object is thread-safe.
				// so for precaution, synchronize it.
				NodeIterator nodes = rule.xpath.execute(
					new XPathContext(), item.node, item.type.prefixResolver ).nodeset();
			
				Node n;
				while( (n=nodes.nextNode())!=null ) {
							
					for( int i=0; i<rule.asserts.length; i++ )
						if(!rule.asserts[i].xpath.execute(
							new XPathContext(), n, item.type.prefixResolver ).bool() )
							reportError( item, rule.asserts[i] );
							
					for( int i=0; i<rule.reports.length; i++ )
						if(rule.reports[i].xpath.execute(
							new XPathContext(), n, item.type.prefixResolver ).bool() )
							reportError( item, rule.reports[i] );
				}
			}					
		}

		private void reportError( CheckItem item, SAction action ) throws SAXException {
			schematronValid = false;
			getVErrorHandler().onError( new ValidityViolation(
				item.location, action.document ));
		}
	}
	
	
	
	
	
	public boolean isValid() {
		return schematronValid && core.isValid();
	}
	public Object getCurrentElementType() {
		return core.getCurrentElementType();
	}
	public Datatype[] getLastCharacterType() {
		return core.getLastCharacterType();
	}
	public final Locator getLocator() {
		return core.getLocator();
	}
	public final VerificationErrorHandler getVErrorHandler() {
		return core.getVErrorHandler();
	}

	
	
	
	
	
	
    public void setDocumentLocator(Locator locator) {
		core.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
		core.startDocument();
    }

    public void endDocument() throws SAXException {
		core.endDocument();
    }

    public void startPrefixMapping( String prefix, String uri ) throws SAXException {
		core.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
		core.endPrefixMapping(prefix);
    }

    public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) throws SAXException {
		core.startElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(	String namespaceURI, String localName, String qName ) throws SAXException {
		core.endElement(namespaceURI, localName, qName);
    }

    public void characters( char ch[], int start, int length ) throws SAXException {
		core.characters(ch, start, length);
    }

    public void ignorableWhitespace( char ch[], int start, int length ) throws SAXException {
		core.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
		core.processingInstruction(target, data);
    }

    public void skippedEntity(String name) throws SAXException {
		core.skippedEntity(name);
    }
}
