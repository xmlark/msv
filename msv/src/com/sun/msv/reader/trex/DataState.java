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
import com.sun.tranquilo.reader.ExpressionWithoutChildState;

/**
 * parses &lt;data&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataState extends ExpressionWithoutChildState
{
	protected Expression makeExpression()
	{
		if( !startTag.containsAttribute("type") )
		{// type attribute is required
			reader.reportError( TREXGrammarReader.ERR_MISSING_ATTRIBUTE,
				startTag.qName, "type" );
			
			// recover from error by assuming anyString.
			return Expression.anyString;
		}
		else
		{
			return reader.pool.createTypedString(
				((TREXGrammarReader)reader).resolveDataType(
					startTag.getAttribute("type") ) );
		}
	}
}
