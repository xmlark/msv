package com.sun.tahiti.grammar;

import com.sun.msv.grammar.ReferenceExp;

/**
 * the base class of all special ReferenceExps
 * that are used to annotate data-binding information
 * to AGM.
 */
public class JavaItem extends ReferenceExp {
	public JavaItem( String name ) {
		super(name);
	}
}
