/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime;

import org.xml.sax.*;
import org.relaxng.datatype.Datatype;
import java.util.Set;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.AbstractVerifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.AttributeToken;
import com.sun.msv.verifier.regexp.SimpleAcceptor;
import com.sun.msv.verifier.regexp.ComplexAcceptor;

/**
 * assign types to the incoming SAX2 events and reports them to the application handler.
 * 
 * This class "augment" infoset by adding type information. The application can
 * receive augmented infoset by implementing TypedContentHandler.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypeDetecter extends AbstractVerifier {
	
	protected Acceptor current;
	
	private static final class Context {
		final Context	previous;
		final Acceptor	acceptor;
		Context( Context prev, Acceptor acc ) {
			previous=prev; acceptor=acc;
		}
	};
	
	/** context stack */
	Context		stack = null;
	
	/** characters that were read (but not processed)  */
	private StringBuffer text = new StringBuffer();
	
	/** an object used to store start tag information.
	 * the same object is reused. */
	private final StartTagInfo sti = new StartTagInfo(null,null,null,null,null);
	
	/**
	 * Schema object against which the validation will be done.
	 * One cannot use generic DocumentDeclaration because 
	 * we need to access the type information.
	 */
	protected final REDocumentDeclaration docDecl;
	
	protected TypedContentHandler handler;
	
	public TypeDetecter( REDocumentDeclaration documentDecl ) {
		this.docDecl = documentDecl;
		this.handler = handler;
	}
	
	public TypeDetecter( REDocumentDeclaration documentDecl, TypedContentHandler handler ) {
		this(documentDecl);
		setContentHandler(handler);
	}
	
	public void setContentHandler( TypedContentHandler handler ) {
		this.handler = handler;
	}

	
	private final DatatypeRef characterType = new DatatypeRef();
	
	private void verifyText() throws SAXException {
		if(text.length()!=0) {
			final String txt = new String(text);
			if(!current.stepForward( txt, this, null, characterType )) {
				// error
				// diagnose error, if possible
				StringRef err = new StringRef();
				current.stepForward( txt, this, err, null );
					
				// report an error
				throw new InvalidDocumentException(err.str);
			}
			
			// characters are validated. report to the handler.
			reportCharacterChunks( txt, characterType.types );
			
			text = new StringBuffer();
		}
	}

	private void reportCharacterChunks( String text, Datatype[] types ) throws SAXException {
		
		if( types==null )
			// unable to assign type.
			throw new AmbiguousDocumentException();
		
		switch( types.length ) {
		case 0:
			return;	// this text is ignored.
		case 1:
			handler.characterChunk( text, types[0] );
			return;
		default:
			StringTokenizer tokens = new StringTokenizer(text);
			for( int i=0; i<types.length; i++ )
				handler.characterChunk( tokens.nextToken(), types[i] );
				
			if( tokens.hasMoreTokens() )	throw new Error();	// assertion failed
		}
	}
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
		throws SAXException {
		
		super.startElement( namespaceUri, localName, qName, atts );
		verifyText();		// verify PCDATA first.
		

		// push context
		stack = new Context( stack, current );
		
		sti.reinit(namespaceUri, localName, qName, atts, this );

		// get Acceptor that will be used to validate the contents of this element.
		Acceptor next = current.createChildAcceptor(sti,null);
		
		if( next==null ) {
			// no child element matchs this one. diagnose if possible.
			StringRef ref = new StringRef();
			current.createChildAcceptor(sti,ref);
			throw new InvalidDocumentException(ref.str);
		}
		
		handler.startElement( namespaceUri, localName, qName );
		{// report the types of attributes to the handler.
			int len = atts.getLength();
			for( int i=0; i<len; i++ ) {
				AttributeToken token = docDecl.startTag.getToken(i);
				handler.startAttribute(
					token.namespaceURI, token.localName, atts.getQName(i) );
				
				reportCharacterChunks( token.value.literal, token.value.refType.types );
				
				handler.endAttribute(
					token.namespaceURI, token.localName, atts.getQName(i), token.matchedExp );
			}
		}
		handler.endAttributePart();
		
		current = next;
	}
	
	public void endElement( String namespaceUri, String localName, String qName )
		throws SAXException {
		
		verifyText();

		if( !current.isAcceptState(null) ) {
			// error.
			StringRef errRef = new StringRef();
			current.isAcceptState(errRef);
			throw new InvalidDocumentException(errRef.str);
		}
		Acceptor child = current;
		
		{// report to the handler
			ElementExp type;
			if( child instanceof SimpleAcceptor ) {
				type = ((SimpleAcceptor)child).owner;
			} else
			if( child instanceof ComplexAcceptor ) {
				ElementExp[] exps = ((ComplexAcceptor)child).getSatisfiedOwners();
				if(exps.length!=1)
					throw new AmbiguousDocumentException();
				type = exps[0];
			} else
				throw new Error();	// assertion failed. not supported.
			
			handler.endElement( namespaceUri, localName, qName, type );
		}
		
		// pop context
		current = stack.acceptor;
		stack = stack.previous;
		
		if(!current.stepForward( child, null )) {
			// error
			StringRef ref = new StringRef();
			current.stepForward( child, ref );
			throw new InvalidDocumentException(ref.str);
		}
		
		super.endElement( namespaceUri, localName, qName );
	}
	
	public void characters( char[] buf, int start, int len ) throws SAXException {
		text.append(buf,start,len);
	}
	public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
		text.append(buf,start,len);
	}
	
	public void startDocument() throws SAXException {
		// reset everything.
		// since Verifier maybe reused, initialization is better done here
		// rather than constructor.
		init();
		// if Verifier is used without "divide&validate", 
		// this method is called and the initial acceptor
		// is set by this method.
		// When Verifier is used in IslandVerifierImpl,
		// then initial acceptor is set at the constructor
		// and this method is not called.
		current = docDecl.createAcceptor();
		
		handler.startDocument(this);
	}
	
	public void endDocument() throws SAXException {
		// ID/IDREF check
		Iterator itr = idrefs.keySet().iterator();
		while( itr.hasNext() ) {
			StringPair symbolSpace = (StringPair)itr.next();
			
			Set refs = (Set)idrefs.get(symbolSpace);
			Set keys = (Set)ids.get(symbolSpace);
			
			if(keys==null || !keys.containsAll(refs)) {
				throw new InvalidDocumentException("unmatched IDREF");
			}
		}
		handler.endDocument();
	}

	/**
	 * signals that the document is not valid.
	 * This exception is thrown when the incoming document is not valid 
	 * according to the grammar.
	 */
	public class InvalidDocumentException extends SAXException {
		public InvalidDocumentException(String msg) {
			super(msg);
		}
		/** returns the source of the error. */
		Locator getLocation() { return TypeDetecter.this.getLocator(); }
	};
	
	/**
	 * signals that the document is ambiguous.
	 * This exception is thrown when
	 * <ol>
	 *  <li>we cannot uniquely assign the type for given characters.
	 *  <li>or we cannot uniquely determine the type for the element
	 *		when we reached the end element.
	 * </ol>
	 * 
	 * The formar case happens for patterns like:
	 * <PRE><XMP>
	 * <choice>
	 *   <data type="xsd:string"/>
	 *   <data type="xsd:token"/>
	 * </choice>
	 * </XMP></PRE>
	 * 
	 * The latter case happens for patterns like:
	 * <PRE><XMP>
	 * <choice>
	 *   <element name="foo">
	 *     <text/>
	 *   </element>
	 *   <element>
	 *     <anyName/>
	 *     <text/>
	 *   </element>
	 * </choice>
	 * </XMP></PRE>
	 */
	public class AmbiguousDocumentException extends SAXException {
		public AmbiguousDocumentException() {
			super("");
		}
		/** returns the source of the error. */
		Locator getLocation() { return TypeDetecter.this.getLocator(); }
	};
}
