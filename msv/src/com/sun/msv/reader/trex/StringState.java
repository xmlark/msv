package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.TypedString;
import com.sun.tranquilo.reader.ExpressionWithoutChildState;
import com.sun.tranquilo.datatype.WhiteSpaceProcessor;

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
