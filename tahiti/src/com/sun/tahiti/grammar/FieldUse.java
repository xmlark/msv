package com.sun.tahiti.grammar;

import java.util.Set;

public class FieldUse {
	
	public FieldUse( String name ) {
		this.name = name;
	}
	
	/** field name */
	public final String name;
	
	/**
	 * item type of this field.
	 * For example, if this field is a set of Object then this field is set to 'Object'.
	 * This field is computed in the 2nd pass.
	 */
	public Type type;
	
	/**
	 * set of FieldItems that shares the same name.
	 * This field is computed in the 1st pass.
	 */
	public final Set items = new java.util.HashSet();
	
	public FieldItem[] getItems() {
		return (FieldItem[])items.toArray(new FieldItem[0]);
	}
	
	/**
	 * total multiplicity from the parent class to items of this field.
	 * This field is computed in the 2nd pass.
	 */
	public Multiplicity multiplicity;
}
