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
 * <p>
 * Most of the properties of the simple type declaration component
 * is defined in the {@link XSDatatype} object, which is obtained by the
 * {@link #getType()} method.
 * 
 * <p>
 * Note: XML Schema allows forward reference to simple types.
 * Therefore it must be indirectionalized by ReferenceExp.
 * And this is the only reason this class exists.
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
			this.exp = pool.createData(dt);
		this.type = dt;
	}
	
	protected XSDatatype type;
	/** gets the XSDatatype object that represents this simple type. */
	public XSDatatype getType() {
		return type;
	}

	/**
	 * gets the value of the block constraint.
	 * SimpleTypeExp always returns 0 because it doesn't
	 * have the block constraint.
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
