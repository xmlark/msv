/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex.typed;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.trex.ElementState;
import com.sun.tranquilo.grammar.trex.typed.TypedElementPattern;

/**
 * reads &lt;element&gt; element with 'label' annotation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypedElementState extends ElementState
{
	protected Expression annealExpression( Expression contentModel )
	{
		final String label = startTag.getAttribute( TypedTREXGrammarReader.LABEL_NAMESPACE, "label" );
		if( label==null )
			return super.annealExpression( contentModel );
		else
			return new TypedElementPattern( nameClass, contentModel, label );
	}
}
