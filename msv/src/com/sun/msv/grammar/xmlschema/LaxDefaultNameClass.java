/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.trex.DifferenceNameClass;
import com.sun.msv.util.StringPair;
import java.util.Set;


public class LaxDefaultNameClass implements NameClass {
	
	public LaxDefaultNameClass() {
		allowedNames.add( new StringPair(NAMESPACE_WILDCARD,LOCALNAME_WILDCARD) );
	}
	
	public Object visit( NameClassVisitor visitor ) {
		// create equivalent name class and let visitor visit it.
		if( equivalentNameClass==null ) {
			NameClass nc = AnyNameClass.theInstance;
			StringPair[] items = (StringPair[])allowedNames.toArray(new StringPair[0]);
			for( int i=0; i<items.length; i++ ) {
				if( items[i].namespaceURI==NAMESPACE_WILDCARD
				 || items[i].localName==LOCALNAME_WILDCARD )
					continue;
				
				nc = new DifferenceNameClass(nc,
					new SimpleNameClass(items[i].namespaceURI,items[i].localName));
			}
			equivalentNameClass = nc;
		}
		
		return equivalentNameClass.visit(visitor);
	}
	
	/**
	 * equivalent name class by conventional primitives.
	 * Initially null, and created on demand.
	 */
	protected NameClass equivalentNameClass;
	
	public boolean accepts( String namespaceURI, String localName ) {
		return !allowedNames.contains( new StringPair(namespaceURI,localName) );
	}
	
	/** set of StringPair.
	 * each item represents one allowed name.
	 * it also contains WILDCARD as entry.
	 */
	private final Set allowedNames = new java.util.HashSet();
	
	public void addAllowedName( String namespaceURI, String localName ) {
		allowedNames.add( new StringPair(namespaceURI,localName) );
		allowedNames.add( new StringPair(namespaceURI,LOCALNAME_WILDCARD) );
		allowedNames.add( new StringPair(NAMESPACE_WILDCARD,localName) );
	}
}
