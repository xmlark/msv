package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.NotNameClass;

public class NameClassNotState extends NameClassWithChildState
{
	protected NameClass castNameClass( NameClass halfCastedNameClass, NameClass child )
	{
		// this parameter is null only for the first time invocation.
		if( halfCastedNameClass!=null )	// <not> only allows one child.
		{
			reader.reportError( TREXGrammarReader.ERR_MORE_THAN_ONE_NAMECLASS );
			// recovery can be done by simply doing nothing at all.
			return halfCastedNameClass;
		}
		else
			return new NotNameClass(child);
	}
}
