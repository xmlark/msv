package com.sun.tahiti.reader.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.GroupDeclExp;
import com.sun.msv.reader.xmlschema.GroupState;
import com.sun.tahiti.grammar.ClassItem;

public class TGroupState extends GroupState {

	protected Expression annealExpression(Expression contentType) {
		final Expression body = super.annealExpression(contentType);
		final TXMLSchemaReader reader = (TXMLSchemaReader)this.reader;
		
		if(!isGlobal())
			// if it's not a global one, then it is a reference to a model group.
			// So do nothing.
			return body;
		
		if(!(body instanceof GroupDeclExp ))
			// if this is a valid global model group definition,
			// it should return GroupDeclExp.
			return body;
		
		
		// insert a temporary class item.
		// maybe it shouldn't be temporary...
		GroupDeclExp g = (GroupDeclExp)body;
		ClassItem cls = reader.annGrammar.createClassItem(
			reader.computeTypeName(this,"class"), g.exp );
		cls.isTemporary = true;
		reader.setDeclaredLocationOf(cls);
		g.exp = cls;
		
		return g;
	}
}
