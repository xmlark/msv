package com.sun.tahiti.grammar;

public class InterfaceItem extends TypeItem {
	public InterfaceItem( String name ) {
		super(name);
	}

	public Type getSuperType() { return null; } // interfaces do not have the super type.
}
