package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.NamespaceNameClass;

public class NameClassNsNameState extends NameClassWithoutChildState
{
	protected NameClass makeNameClass()
	{
		return new NamespaceNameClass( getPropagatedNamespace() );
	}
}
