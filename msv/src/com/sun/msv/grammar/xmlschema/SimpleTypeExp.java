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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.datatype.xsd.LateBindDatatype;

/**
 * Simple type declaration.
 * 
 * XML Schema allows forward reference to simple types. therefore it must be
 * indirectionalized by ReferenceExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeExp extends XMLSchemaTypeExp {
	
	SimpleTypeExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	public void setType( XSDatatype dt, ExpressionPool pool ) {
		if(!(dt instanceof LateBindDatatype ))
			// do not create a TypedStringExp for late-bind object.
			// this will unnecessary "contaminate" the pool.
			this.exp = pool.createTypedString(dt);
		this.type = dt;
	}
	
	protected XSDatatype type;
	/** gets the XSDatatype object that represents this simple type. */
	public XSDatatype getType() {
		return type;
	}

	/**
	 * gets the value of the block constraint.
	 * SimpleTypeExp always returns 0 because it doesn't have the block constraint.
	 */
	public int getBlock() { return 0; }
	
	/** clone this object. */
	public RedefinableExp getClone() {
		SimpleTypeExp exp = new SimpleTypeExp(super.name);
		exp.redefine(this);
		return exp;
	}
	
	public void redefine( RedefinableExp _rhs ) {
		super.redefine(_rhs);
		
		((SimpleTypeExp)_rhs).type = this.type;
	}
	

}
