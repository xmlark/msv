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
import com.sun.msv.grammar.trex.TypedString;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.datatype.WhiteSpaceProcessor;

/**
 * parses &lt;string&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringState extends ExpressionWithoutChildState
{
	protected final StringBuffer text = new StringBuffer();
	
	public void characters( char[] buf, int from, int len )
	{
		text.append(buf,from,len);
	}
	public void ignorableWhitespace( char[] buf, int from, int len )
	{
		text.append(buf,from,len);
	}
	
	protected Expression makeExpression()
	{
		return reader.pool.createTypedString(
			new TypedString(new String(text),
			"preserve".equals(startTag.getAttribute("whiteSpace") ) ) );
	}
}
