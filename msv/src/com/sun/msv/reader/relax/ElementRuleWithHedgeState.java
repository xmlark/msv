/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * parses &lt;elementRule&gt; without 'type' attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRuleWithHedgeState extends ElementRuleBaseState implements ExpressionOwner
{
	protected Expression contentModel = null;
	
	public void onEndChild( Expression exp )
	{// this method is called after child expression is found and parsed
		if( contentModel!=null )
			reader.reportError( RELAXReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
			// recover by ignoring previous expression
		
		contentModel = exp;
	}
	
	protected Expression getContentModel()
	{
		if( contentModel==null )
		{
			reader.reportError( RELAXReader.ERR_MISSING_CHILD_EXPRESSION );
			// recover by assuming a harmless content model
			return Expression.epsilon;	// anything will do.
		}
		
		return contentModel;
	}
	
	protected State createChildState( StartTagInfo tag )
	{
		if( !tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace) )	return null;
		
		State next;
		
		// particles
		next = reader.createExpressionChildState(tag);
		if(next!=null)		return next;
		
		// or delegate to the base class
		return super.createChildState(tag);
	}
}
