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

/**
 * attribute group declaration.
 * 
 * the inherited exp field contains the attributes defined in this declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeGroupExp extends RedefinableExp {
	
	/**
	 * name of this attribute group declaration.
	 * According to the spec, the name must be unique within one schema
	 * (in our object model, one XMLSchemaSchema object).
	 */
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
