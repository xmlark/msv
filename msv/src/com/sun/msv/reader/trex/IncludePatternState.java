/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.reader.ExpressionOwner;

/**
 * &lt;include&gt; element in the pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludePatternState extends ExpressionWithoutChildState implements ExpressionOwner
{
	protected Expression included = Expression.nullSet;
	// assign a default value just in case something goes wrong and onEndChild is not called.
	
	public void onEndChild( Expression included )
	{
		this.included = included;
	}
	
	protected Expression makeExpression()
	{
		final String href = startTag.getAttribute("href");
		
		if(href==null)
		{// name attribute is required.
			reader.reportError( TREXBaseReader.ERR_MISSING_ATTRIBUTE,
				"include","href");
			// recover by returning something that can be interpreted as Pattern
			return Expression.nullSet;
		}
		
		
		reader.switchSource(href,new RootIncludedPatternState(this));
		
		// onEndChild method is called inside the above function call and
		// included will be set.
		
		return included;
	}
}
