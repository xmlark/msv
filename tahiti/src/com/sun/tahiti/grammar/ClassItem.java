package com.sun.tahiti.grammar;

import com.sun.msv.grammar.ReferenceExp;

public class ClassItem extends TypeItem {
	
	public ClassItem( String name ) {
		super(name);
	}
	
	public SuperClassItem superClass;
	
	public Type getSuperType() {
		if(superClass==null)	return null;
		return superClass.definition;
	}
}
