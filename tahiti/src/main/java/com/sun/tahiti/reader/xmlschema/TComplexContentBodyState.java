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
		if( extension ) {
			SuperClassItem si = new SuperClassItem(baseType.body);
			reader.setDeclaredLocationOf(si);
			return reader.pool.createSequence( si, addedExp );
		} else
			// what shall we do if it's a restriciton.
			return addedExp;
	}
}
