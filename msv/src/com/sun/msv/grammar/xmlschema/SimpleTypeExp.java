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

/**
 * Simple type declaration.
 * 
 * XML Schema allows forward reference to simple types. therefore it must be
 * indirectionalized by ReferenceExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeExp extends RedefinableExp {
	
	SimpleTypeExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	public void setType( XSDatatype dt, ExpressionPool pool ) {
		this.exp = pool.createTypedString(dt);
		this.type = dt;
	}
	
	/** XSDatatype object that validates this simple type. */
	protected XSDatatype type;
	
	public XSDatatype getType() {
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
