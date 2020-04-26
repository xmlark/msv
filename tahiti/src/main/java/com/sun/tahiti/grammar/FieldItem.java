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

import com.sun.msv.grammar.Expression;
import com.sun.tahiti.grammar.util.Multiplicity;
import java.util.Set;

/**
 * represents a field relationship between two objects.
 * 
 * <p>
 * Also keeps the information about how accessor should be generated.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldItem extends JavaItem {
	public FieldItem( String name ) {
		super(name);
	}
	
	public FieldItem( String name, Expression exp ) {
		this(name);
		this.exp = exp;
	}
	
	/** specified accessor value. If none is specified by the user, null. */
	public Accessor accessor;
	/** specified access modifier value. If none is specified by the user, null. */
	public AccessModifier accessModifier;
	/** specified collection type. If none is specified by the user, null. */
	public CollectionType collectionType;	
	
	/**
	 * multiplicity of this field to its children (field-class/interface).
	 * Note that this multiplicity and class-field multiplicity is completely
	 * a different thing.
	 * 
	 * This field is computed during the first pass of the normalization.
	 */
	public Multiplicity multiplicity;
	
	/**
	 * all Types that can appear as the children of this type.
	 * This field is computed during the first pass of the normalization.
	 */
	public final Set types = new java.util.HashSet();

	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onField(this);
	}
}
