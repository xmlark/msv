package com.sun.tahiti.reader.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.xmlschema.ComplexTypeDeclState;
import com.sun.tahiti.reader.TahitiGrammarReader;

public class TComplexTypeDeclState extends ComplexTypeDeclState {
	
	protected Expression annealExpression(Expression contentType) {
		final Expression body = super.annealExpression(contentType);
		final TXMLSchemaReader reader = (TXMLSchemaReader)this.reader;
		
		if( !isGlobal() )
			// we assign ClassItem only when the complex type is a global one.
			return body;
		
		if( startTag.getAttribute(TahitiGrammarReader.TahitiNamespace,"role")
				.equals("none") )
			// if "none" is specified, suppress a ClassItem.
			return body;
		
		
		// insert a ClassItem.
		decl.self.exp = reader.annGrammar.createClassItem( reader.computeTypeName(this,"class"), decl.self.exp );
		
		return body;
	}
}
