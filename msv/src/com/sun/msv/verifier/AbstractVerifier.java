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
import org.relaxng.datatype.DataType;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import com.sun.msv.datatype.StringType;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.DataTypeRef;

/**
 * Base implementation for various Verifier implementations.
 * 
 * This implementation provides common service like:
 * 
 * <ol>
 *  <li>collecting ID/IDREFs.
 *  <li>storing Locator.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class AbstractVerifier implements
	ContentHandler,
	DTDHandler,
	IDContextProvider {
	
	/** document Locator that is given by XML reader */
	protected Locator locator;
	public final Locator getLocator() { return locator; }
	
	/** this set remembers every ID token encountered in this document */
	protected final Map ids = new java.util.HashMap();
	/** this map remembers every IDREF token encountered in this document */
	protected final Map idrefs = new java.util.HashMap();
	
	public void setDocumentLocator( Locator loc ) {
		this.locator = loc;
	}
	public void skippedEntity(String p) {}
	public void processingInstruction(String name,String data) {}
	
	public void startPrefixMapping( String prefix, String uri ) {
		namespaceSupport.declarePrefix( prefix, uri );
	}
	public void endPrefixMapping( String prefix )	{}
	
	protected void init() {
		ids.clear();
		idrefs.clear();
	}
	
	public void notationDecl( String name, String publicId, String systemId ) {}
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

	/** unparsed entities found in the document */
	private final Set unparsedEntities = new java.util.HashSet();
	
	// methods of ValidationContextProvider
	public String resolveNamespacePrefix( String prefix ) {
		return namespaceSupport.getURI(prefix);
	}
	public boolean isUnparsedEntity( String entityName ) {
		return unparsedEntities.contains(entityName);
	}
	
	public void onIDREF( String symbolSpace, Object token )	{
		Set tokens = (Set)idrefs.get(symbolSpace);
		if(tokens==null)	idrefs.put(symbolSpace,tokens = new java.util.HashSet());
		tokens.add(token);
	}
	public boolean onID( String symbolSpace, Object token ) {
		Set tokens = (Set)ids.get(symbolSpace);
		if(tokens==null)	ids.put(symbolSpace,tokens = new java.util.HashSet());
		
		if( tokens.contains(token) )	return false;	// not unique.
		tokens.add(token);
		return true;	// they are unique, at least now.
	}
}
