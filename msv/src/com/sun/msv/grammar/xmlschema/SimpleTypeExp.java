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

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ExpressionPool;

/*
XML Schema allows forward reference to simple types. therefore it must be
indirectionalized by ReferenceExp.
*/
public class SimpleTypeExp extends RedefinableExp {
	
	SimpleTypeExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	public void setType( DataType dt, ExpressionPool pool ) {
		this.exp = pool.createTypedString(dt);
		this.type = dt;
	}
	
	/** DataType object that validates this simple type. */
	protected DataType type;
	
	public DataType getType() {
		return type;
	}
	
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
