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

/**
 * Identifier-related utility methods.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameUtil
{
	/** convert a name to Java-compatible identifier name. */
	public static String toIdentifier( String name ) {
		return xmlNameToJavaName("field",name);
	}
	
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
			return convertToCamelNotation(xmlName,true);
		
		if( role.equals("interface") )
			return "I"+convertToCamelNotation(xmlName,true);
		
		return convertToCamelNotation(xmlName,false);
	}
	
	/**
	 * converts a given string to the camel notation.
	 */
	private static String convertToCamelNotation( String name, boolean capitalizeFirstLetter ) {
		StringBuffer r = new StringBuffer();
		boolean capitalize = capitalizeFirstLetter;
		
		int len = name.length();
		for( int i=0; i<len; i++ ) {
			char ch = name.charAt(i);
			if( isValidCharacterAsIdentifier( ch, r.length()==0 ) ) {
				if( capitalize )	r.append( Character.toUpperCase(ch) );
				else				r.append( ch );
				capitalize = false;
			} else {
				// invalid character. Skip it, but capitalize the next valid char.
				capitalize = true;
			}
		}
		return r.toString();
	}
	
	/**
	 * decides if the specified character is a valid character as an identifier.
	 */
	private static boolean isValidCharacterAsIdentifier( char ch, boolean isFirstChar ) {
		if( "$_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(ch)>=0 )
			return true;
		
		if( !isFirstChar
		&&  "1234567890".indexOf(ch)>=0 )
			return true;
		
		return false;
	}
	
	/**
	 * capitalizes the first character.
	 */
	public static String capitalizeFirst( String name ) {
			return Character.toUpperCase(name.charAt(0))+name.substring(1);
	}
}
