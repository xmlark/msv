/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.ElementPattern;

public class ElementState extends NameClassAndExpressionState
{
	protected Expression annealExpression( Expression contentModel )
	{
		return new ElementPattern( nameClass, contentModel );
	}
}
