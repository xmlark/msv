/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.xmlschema;

public class AttributeGroupExp extends RedefinableExp {
	
	public AttributeGroupExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	/** clone this object. */
	public RedefinableExp getClone() {
		RedefinableExp exp = new AttributeGroupExp(super.name);
		exp.redefine(this);
		return exp;
	}
}
