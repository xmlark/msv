package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.SimpleNameClass;
import com.sun.tranquilo.datatype.WhiteSpaceProcessor;

public class NameClassNameState extends NameClassWithoutChildState
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

	protected NameClass makeNameClass()
	{
		return new SimpleNameClass(
			getPropagatedNamespace(),
			WhiteSpaceProcessor.theReplace.process(new String(text)) );
			
	}
}
