package com.sun.tahiti.reader.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.xmlschema.AnyElementState;
import com.sun.tahiti.grammar.FieldItem;
import com.sun.tahiti.grammar.IgnoreItem;
import com.sun.tahiti.reader.TahitiGrammarReader;

public class TAnyElementState extends AnyElementState {

	protected Expression createExpression( String namespace, String process ) {
		return wrap( process, this, "otherElements",
			super.createExpression(namespace,process) );
	}
	
	static Expression wrap( String process, State owner, String defaultFieldName, Expression body ) {
		if( process.equals("skip") ) {
			// if the skip is specified, probably these elements are unnecessary.
			// so ignore about them.
			return new IgnoreItem( body );
		}
		
		if( process.equals("strict") || process.equals("lax") ) {
			// place them into a special field.
			// an "any" corresponds to the global element declarations.
			// global element declarations are automatically wrapped by
			// (temporary) class items.
			String name = owner.getStartTag().getAttribute(
				TahitiGrammarReader.TahitiNamespace,"name");
			if(name==null)	name=defaultFieldName;
			
			return new FieldItem(name, body);
		}
		
		throw new Error();	// unknown processing mode.
	}
}
