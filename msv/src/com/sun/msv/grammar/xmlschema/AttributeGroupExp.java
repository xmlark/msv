package com.sun.tranquilo.grammar.xmlschema;

public class AttributeGroupExp extends XMLSchemaExp {
	
	public AttributeGroupExp( String typeLocalName ) {
		super(typeLocalName);
	}
	
	/** clone this object. */
	public XMLSchemaExp getClone() {
		XMLSchemaExp exp = new AttributeGroupExp(super.name);
		exp.redefine(this);
		return exp;
	}
}
