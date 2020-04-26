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

/**
 * 
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Accessor {
	/**
	 * no need to create an instance.
	 * Use pre-defined values.
	 */
	private Accessor() {}
	
	public static Accessor field = new Accessor();
	public static Accessor readOnly = new Accessor();
	public static Accessor readWrite = new Accessor();
	
	/** gets the default accessor. */
	public static Accessor getDefault() {
		return readWrite;
	}
	
	/**
	 * parses the string representation of the accessor
	 * and returns one of the predefined value.
	 * 
	 * @return
	 *		If the value is not recognized, return null.
	 */
	public static Accessor parse( String value ) {
		value = value.trim();
		
		if(value.equalsIgnoreCase("field"))			return field;
		if(value.equalsIgnoreCase("readOnly"))		return readOnly;
		if(value.equalsIgnoreCase("readWrite"))		return readWrite;
		return null;	// unrecognized
	}
}
