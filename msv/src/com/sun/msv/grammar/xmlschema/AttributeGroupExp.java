package com.sun.tranquilo.grammar.xmlschema;

public class AttributeGroupExp extends RedefinableExp {
	
	public AttributeGroupExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	/** clone this object. */
	public RedefinableExp getClone() {
		RedefinableExp exp = new AttributeGroupExp(super.name);
		exp.redefine(this);
		return exp;
	}
}
