package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.trex.DifferenceNameClass;
import java.util.ArrayList;
import java.util.List;

public class NameClassDifferenceState extends NameClassWithChildState
{
	protected NameClass castNameClass( NameClass halfCasted, NameClass newChild )
	{
		if( halfCasted==null )	return newChild;	// first item
		else return new DifferenceNameClass( halfCasted, newChild );
	}
}
