package com.sun.tahiti.reader.xmlschema;

import com.sun.msv.reader.xmlschema.ComplexContentBodyState;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.tahiti.grammar.SuperClassItem;
	
public class TComplexContentBodyState extends ComplexContentBodyState {
	protected TComplexContentBodyState( ComplexTypeExp parentDecl, boolean extension ) {
		super(parentDecl,extension);
	}

	/**
	 * combines the base type content model and this content model
	 */
	protected Expression combineToBaseType( ComplexTypeExp baseType, Expression addedExp ) {
		Expression body = super.combineToBaseType( baseType, addedExp );
		
		if( extension )
			// make this complex type a derived class of the base type.
			return new SuperClassItem(body);
		else
			return body;
	}
}
