/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

import com.sun.msv.util.StringPair;
import com.sun.msv.grammar.util.NameClassCollisionChecker;
import com.sun.msv.grammar.util.NameClassSimplifier;

/**
 * validator of (namespaceURI,localPart) pair.
 * 
 * This is equivalent to RELAX NG's "name class".
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClass implements java.io.Serializable {
	/**
	 * checks if this name class accepts given namespace:localName pair.
	 * 
	 * @param namespaceURI
	 *		namespace URI to be tested. If this value equals to
	 *		NAMESPACE_WILDCARD, implementation must assume that
	 *		valid namespace is specified. this twist will be used for
	 *		error diagnosis.
	 * 
	 * @param localName
	 *		local part to be tested. As with namespaceURI, LOCALNAME_WILDCARD
	 *		will acts as a wild card.
	 * 
	 * @return
	 *		true if the pair is accepted,
	 *		false otherwise.
	 */
	public abstract boolean accepts( String namespaceURI, String localName );

	public final boolean accepts( StringPair name ) {
		return accepts( name.namespaceURI, name.localName );
	}
	
	/**
	 * visitor pattern support
	 */
	public abstract Object visit( NameClassVisitor visitor );
	
	/** wildcard should be accepted by any name class. */
	public static final String NAMESPACE_WILDCARD = "*";
	public static final String LOCALNAME_WILDCARD = "*";
	
	
	/** Computes the intersection of two name classes. */
	public static NameClass intersection( NameClass lhs, NameClass rhs ) {
		return NameClassSimplifier.simplify(
			new DifferenceNameClass( lhs, new NotNameClass(rhs) ) );
	}

	/** Computes the union of two name classes. */
	public static NameClass union( NameClass lhs, NameClass rhs ) {
		return NameClassSimplifier.simplify(
			new ChoiceNameClass(lhs,rhs) );
	}
    
    /** Returns true if this name class doesn't accept anything. */
    public boolean isNull() {
        return !new NameClassCollisionChecker().check(this,AnyNameClass.theInstance);
    }
}
