package com.sun.tranquilo.grammar.xmlschema;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ExpressionPool;

/*
XML Schema allows forward reference to simple types. therefore it must be
indirectionalized by ReferenceExp.
*/
public class SimpleTypeExp extends ReferenceExp {
	
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
}
