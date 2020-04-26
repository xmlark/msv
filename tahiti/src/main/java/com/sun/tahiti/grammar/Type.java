/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

import java.util.Set;

/**
 * Type. Several types are generated, and several types are built-in (such as String.)
 * Generated types are modeled by TypeItem. Built-in types are modeled by SystemType.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface Type {
	
	/** gets the fully-qualified name of this type. */
	String getTypeName();
	
	/**
	 * gets the package name of this type.
	 * This method returns null if this class does not reside in any package.
	 */
	String getPackageName();
	/** gets the bare type name (without the package name.) */
	String getBareName();
	
	/** gets the super class if any. Otherwise null. */
	Type getSuperType();
	
	/** gets directly implemented interfaces. */
	Type[] getInterfaces();
	
	/*
		Is this method necessary?
	*/
	// boolean isInterface();
}
