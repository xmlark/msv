package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.*;

public class ElementPattern extends ElementExp
{
	public final NameClass nameClass;
	public final NameClass getNameClass() { return nameClass; }
	
	public ElementPattern( NameClass nameClass, Expression contentModel )
	{
		super(contentModel);
		this.nameClass = nameClass;
	}
}
