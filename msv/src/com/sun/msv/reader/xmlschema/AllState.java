package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.ExpressionWithChildState;

/**
 * parses &lt;interleave&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AllState extends ExpressionWithChildState {
	// TODO: only element is allowed in all
	protected Expression castExpression( Expression exp, Expression child ) {
		// first one.
		if( exp==null )		return child;
		return ((XMLSchemaReader)reader).getPool().createInterleave(exp,child);
	}
}
