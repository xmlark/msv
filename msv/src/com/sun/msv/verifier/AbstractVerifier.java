/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import org.xml.sax.*;
import org.xml.sax.helpers.NamespaceSupport;
import org.relaxng.datatype.Datatype;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.StringPair;
import com.sun.msv.util.DatatypeRef;

/**
 * Base implementation for various Verifier implementations.
 * 
 * This implementation provides common service like:
 * 
 * <ol>
 *  <li>collecting ID/IDREFs.
 *  <li>storing Locator.
 * 
 * <p>
 *	By setting <code>performIDcheck</code> variable, the ID/IDREF checking
 *  can be either turned on or turned off.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class AbstractVerifier implements
	ContentHandler, DTDHandler, IDContextProvider {
	
	/** document Locator that is given by XML reader */
	protected Locator locator;
	public final Locator getLocator() { return locator; }
	
	/**
	 * set this flag to true to perform ID/IDREF validation.
	 * this value cannot be modified in the middle of the validation.
	 */
	protected boolean performIDcheck = true;
	
	/** this map remembers every ID token encountered in this document */
	protected final Set ids = new java.util.HashSet();
	/** this map remembers every IDREF token encountered in this document */
	protected final Set idrefs = new java.util.HashSet();
	
	public void setDocumentLocator( Locator loc ) {
		this.locator = loc;
	}
	public void skippedEntity(String p) {}
	public void processingInstruction(String name,String data) {}
	
	private boolean contextPushed = false;
	public void startPrefixMapping( String prefix, String uri ) {
		if( !contextPushed ) {
			namespaceSupport.pushContext();
			contextPushed = true;
		}
		namespaceSupport.declarePrefix( prefix, uri );
	}
	public void endPrefixMapping( String prefix )	{}
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes atts ) throws SAXException {
		if( !contextPushed )
			namespaceSupport.pushContext();
		contextPushed = false;
	}
	
	public void endElement( String namespaceUri, String localName, String qName ) throws SAXException {
		namespaceSupport.popContext();
	}
	
	protected void init() {
		ids.clear();
		idrefs.clear();
	}
	
	public void notationDecl( String name, String publicId, String systemId ) {
		notations.add(name);
	}
	public void unparsedEntityDecl( String name, String publicId, String systemId, String notationName ) {
		// store name of unparsed entities to implement ValidationContextProvider
		unparsedEntities.add(name);
	}
									
	
	/**
	 * namespace prefix to namespace URI resolver.
	 * 
	 * this object memorizes mapping information.
	 */
	protected final NamespaceSupport namespaceSupport = new NamespaceSupport();

	/** unparsed entities found in the document. */
	private final Set unparsedEntities = new java.util.HashSet();
	
	/** declared notations. */
	private final Set notations = new java.util.HashSet();
	
	// methods of ValidationContextProvider
	public String resolveNamespacePrefix( String prefix ) {
		return namespaceSupport.getURI(prefix);
	}
	public boolean isUnparsedEntity( String entityName ) {
		return unparsedEntities.contains(entityName);
	}
	public boolean isNotation( String notationName ) {
		return notations.contains(notationName);
	}
	public String getBaseUri() {
		// TODO: Verifier should implement the base URI
		return null;
	}
	
	/** this method is called when a duplicate id value is found. */
	protected abstract void onDuplicateId( String id );
	
	public void onID( Datatype dt, String literal ) {
		if(!performIDcheck)		return;
		
		switch(dt.getIdType()) {
		case dt.ID_TYPE_ID:
			literal = literal.trim();
			if(!ids.add(literal))
				// duplicate id value
				onDuplicateId(literal);
			return;
		case dt.ID_TYPE_IDREF:
			idrefs.add(literal.trim());
			return;
		case dt.ID_TYPE_IDREFS:
			StringTokenizer tokens = new StringTokenizer(literal);
			while(tokens.hasMoreTokens())
				idrefs.add(tokens.nextToken());
			return;
		default:
			throw new Error();	// assertion failed. unknown Id type.
		}
	}
}
