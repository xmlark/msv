package com.sun.tahiti.reader.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.xmlschema.AnyAttributeState;
import com.sun.tahiti.grammar.FieldItem;
import com.sun.tahiti.grammar.IgnoreItem;
import com.sun.tahiti.reader.TahitiGrammarReader;

public class TAnyAttributeState extends AnyAttributeState {

	protected Expression createExpression( String namespace, String process ) {
		return TAnyElementState.wrap( process, this, "otherAttributes",
			super.createExpression(namespace,process) );
	}
}
