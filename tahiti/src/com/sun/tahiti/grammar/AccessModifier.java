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
 * represents the access modifier (public/protected/private).
 * 
 * <p>
 * The <code>toString</code> method can be used to obtain the string
 * representation of the access modifier.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AccessModifier {
	/** no need to create an instance. Use predefined values. */
	private AccessModifier( String name ) { this.name=name; }
	private final String name;
	public String toString() { return name; }
	
	public static AccessModifier _public = new AccessModifier("public");
	public static AccessModifier _protected = new AccessModifier("protected");
	public static AccessModifier _private = new AccessModifier("private");
	
	/** gets the default access modifier. */
	public static AccessModifier getDefault() {
		return _public;
	}
	
	/**
	 * parses the string representation of the access modifer
	 * and returns one of the predefined value.
	 * 
	 * @return
	 *		If the value is not recognized, return null.
	 */
	public static AccessModifier parse( String value ) {
		value = value.trim();
		
		if(value.equalsIgnoreCase("public"))		return _public;
		if(value.equalsIgnoreCase("protected"))		return _protected;
		if(value.equalsIgnoreCase("private"))		return _private;
		return null;	// unrecognized
	}
}
