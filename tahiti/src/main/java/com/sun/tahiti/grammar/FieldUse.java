/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

import java.util.Set;
import java.util.Iterator;
import com.sun.tahiti.grammar.util.Multiplicity;

/**
 * aggregated field information.
 * 
 * Information about one field can be possibly spanned across
 * multiple FieldItems. This object serves as a bundle of those FieldItems
 * that share the same name.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldUse {
	
	public FieldUse( String name ) {
		this.name = name;
	}
	
	/** field name */
	public final String name;
	
	/**
	 * item type of this field.
	 * For example, if this field is a set of Object then this field is set to 'Object'.
	 * This field is computed in the 2nd pass.
	 */
	public Type type;

	/**
	 * computes the value of the accessor of this field.
	 */
	public Accessor getAccessor() {
		// use the first one found.
		// this means that if two different things are specified,
		// one will be ignored and we cannot tell which one is ignored.
		Iterator itr = items.iterator();
		while(itr.hasNext()) {
			Accessor acc = ((FieldItem)itr.next()).accessor;
			if( acc!=null )	return acc;
		}
		return Accessor.getDefault();
	}
	
	/**
	 * computes the value of the access modifier of this field.
	 */
	public AccessModifier getAccessModifier() {
		// use the first one found.
		// this means that if two different things are specified,
		// one will be ignored and we cannot tell which one is ignored.
		Iterator itr = items.iterator();
		while(itr.hasNext()) {
			AccessModifier acc = ((FieldItem)itr.next()).accessModifier;
			if( acc!=null )	return acc;
		}
		return AccessModifier.getDefault();
	}
	
	/**
	 * computes the value of the collection type of this field.
	 */
	public CollectionType getCollectionType() {
		// use the first one found.
		// this means that if two different things are specified,
		// one will be ignored and we cannot tell which one is ignored.
		Iterator itr = items.iterator();
		while(itr.hasNext()) {
			CollectionType acc = ((FieldItem)itr.next()).collectionType;
			if( acc!=null )	return acc;
		}
		return CollectionType.getDefault();
	}
	
	
	/**
	 * set of FieldItems that shares the same name.
	 * This field is computed in the 1st pass.
	 */
	public final Set items = new java.util.HashSet();
	
	public FieldItem[] getItems() {
		return (FieldItem[])items.toArray(new FieldItem[0]);
	}
	
	/**
	 * total multiplicity from the parent class to items of this field.
	 * This field is computed in the 2nd pass.
	 */
	public Multiplicity multiplicity;
}
