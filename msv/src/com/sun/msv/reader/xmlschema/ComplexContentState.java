package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.reader.ExpressionWithChildState;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.xmlschema.ComplexTypeExp;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * parses &lt;complexContent&gt; element.
 * 
 * the expression created by this state is used as ComplexTypeExp.self field.
 */
public class ComplexContentState extends ExpressionWithChildState {
	
	/** ComplexType object that we are now constructing. */
	protected ComplexTypeExp parentDecl;
	
	protected ComplexContentState( ComplexTypeExp decl ) {
		this.parentDecl = decl;
	}
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if( super.exp!=null )
			// we have already parsed restriction/extension.
			return null;
		
		if( tag.localName.equals("restriction") )	return reader.sfactory.complexRst(this,tag,parentDecl);
		if( tag.localName.equals("extension") )		return reader.sfactory.complexExt(this,tag,parentDecl);
		
		return super.createChildState(tag);
	}

	protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
		if( halfCastedExpression!=null )
			// assertion failed.
			// this situation should be prevented by createChildState method.
			throw new Error();
		
		return newChildExpression;
	}
}
