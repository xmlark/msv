package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.ChoiceNameClass;
import java.util.ArrayList;
import java.util.List;

public class NameClassChoiceState extends NameClassWithChildState
{
	protected NameClass castNameClass( NameClass halfCasted, NameClass newChild )
	{
		if( halfCasted==null )	return newChild;	// first item
		
		else return new ChoiceNameClass( halfCasted, newChild );
	}
}
