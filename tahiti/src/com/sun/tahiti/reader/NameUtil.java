/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader;

public class NameUtil
{
	/**
	 * convert XML names (like element names) to the corresponding Java names.
	 * 
	 * This method should perform conversion like ("abc"->"Abc").
	 * 
	 * @param role
	 *		the role of this expression. One of "field","interface", and "class".
	 */
	public static String xmlNameToJavaName( String role, String xmlName ) {
		// TODO
		if( role.equals("class") )
			return capitalizeFirst(xmlName);
		
		if( role.equals("interface") )
			return "I"+capitalizeFirst(xmlName);
		
		return xmlName;
	}
	
	/**
	 * capitalizes the first character.
	 */
	public static String capitalizeFirst( String name ) {
			return Character.toUpperCase(name.charAt(0))+name.substring(1);
	}
}
