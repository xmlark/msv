package com.sun.tahiti.grammar;

import com.sun.msv.grammar.Expression;
import com.sun.tahiti.grammar.util.Multiplicity;
import java.util.Set;

public class FieldItem extends JavaItem {
	public FieldItem( String name ) {
		super(name);
	}
	
	public FieldItem( String name, Expression exp ) {
		this(name);
		this.exp = exp;
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
