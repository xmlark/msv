package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.AttributeExp;

public class AttributeState extends NameClassAndExpressionState
{
	protected boolean firstChild=true;
	
	protected Expression initialExpression()
	{
		// <attribute> defaults to <anyString />
		return Expression.anyString;
	}

	protected String getNamespace()
	{
		final String ns = startTag.getAttribute("ns");
		final boolean global = "true".equals(startTag.getAttribute("global"));
		
		if( ns!=null )	return ns;	// "ns" attribute always has precedence.
		
		// if global="true" is specified, it defaults to propagated ns attribute.
		if( global )	return ((TREXGrammarReader)reader).targetNamespace;
		
		// otherwise, it defaults to ""
		return "";
	}
			

	protected Expression castExpression( Expression initialExpression, Expression newChild )
	{
		// <attribute> is allowed to have only one pattern
		if(!firstChild)
			reader.reportError( TREXGrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
			// recover by ignore the error
		firstChild = false;
		return newChild;
	}

	protected Expression annealExpression( Expression contentModel )
	{
		return reader.pool.createAttribute( nameClass, contentModel );
	}
}
