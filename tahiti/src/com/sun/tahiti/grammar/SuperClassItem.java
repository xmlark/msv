package com.sun.tahiti.grammar;

import com.sun.msv.grammar.ReferenceExp;

public class SuperClassItem extends JavaItem {
	public SuperClassItem() {
		super("superClass-marker");
	}
	
	/** actual super class definition. */
	public ClassItem definition = null;
}
