package com.sun.tahiti.grammar;

import java.util.Set;

public class FieldItem extends JavaItem {
	public FieldItem( String name ) {
		super(name);
	}
	
	/**
	 * multiplicity of this field to its children (field-class/interface).
	 * Note that this multiplicity and class-field multiplicity is completely
	 * a different thing.
	 * 
	 * This field is computed during the first pass of the normalization.
	 */
	public Multiplicity multiplicity;
	
	/**
	 * all Types that can appear as the children of this type.
	 * This field is computed during the first pass of the normalization.
	 */
	public final Set types = new java.util.HashSet();
}
