package com.sun.tahiti.grammar;

import java.util.Set;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Type. Several types are generated, and several types are built-in (such as String.)
 * Generated types are modeled by TypeItem. Built-in types are modeled by SystemType.
 */
public interface Type {
	
	/** gets the fully-qualified name of this type. */
	String getTypeName();
	
	/** gets the super class if any. Otherwise null. */
	Type getSuperType();
	
	/** gets directly implemented interfaces. */
	Type[] getInterfaces();
	
	/*
		Is this method necessary?
	*/
	// boolean isInterface();
}
