package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.AttributeExp;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;

final class AttributeRecoveryToken extends AttributeToken
{
	AttributeRecoveryToken( ExpressionPool pool,
		String namespaceURI, String localName,
		StringToken value, ResidualCalculator residual )
	{
		super( pool, namespaceURI, localName, value, residual );
	}
	
	private Expression failedExp = Expression.nullSet;
	
	boolean match( AttributeExp exp )
	{
		// Attribute name must meet the constraint of NameClass
		if(!exp.nameClass.accepts(namespaceURI,localName))	return false;
		
		// content model of the attribute must consume the value
		if(residual.calcResidual(exp.exp, value).isEpsilonReducible())
			failedExp = pool.createChoice( failedExp, exp.exp );
		
		// accept AttributeExp regardless of its content restriction
		return true;
	}

	Expression getFailedExp() { return failedExp; }
}
