/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.trex;

class Localizer
{
	public static String localize( String prop, Object[] args )
	{
		return java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.grammar.trex.Messages").getString(prop),
			args );
	}
	
	public static String localize( String prop )
	{ return localize( prop, null ); }
	
	public static String localize( String prop, Object arg1 )
	{ return localize( prop, new Object[]{arg1} ); }
}
