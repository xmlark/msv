/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax.core;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.*;

/**
 * common part of &lt;tag&gt; and &lt;attPool&gt;.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class ClauseState extends SimpleState implements ExpressionOwner
{
	protected State createChildState( StartTagInfo tag )
	{
		if(!tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace))	return null;
		
		if(tag.localName.equals("ref"))			return new AttPoolRefState();
		if(tag.localName.equals("attribute"))	return new AttributeState();
		
		return null;	// unrecognized
	}
	
	protected Expression initialExpression()	{ return Expression.epsilon; }
	
	protected Expression castExpression( Expression exp, Expression child )
	{// attributes and references are combined in one sequence
		return reader.pool.createSequence(exp,child);
	}
	

	/** gets reader in type-safe fashion */
	protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }



	/**
	 * expression object that is being created.
	 * See {@link castPattern} and {@link annealPattern} methods
	 * for how will a pattern be created.
	 */
	protected Expression exp = initialExpression();
	
	/** receives a Pattern object that is contained in this element. */
	public final void onEndChild( Expression childExpression )
	{
		exp = castExpression( exp, childExpression );
	}
}
