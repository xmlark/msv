/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.verifier;

/**
 * localizes messages.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class Localizer
{
	public static String localize( String propertyName, Object[] args )
	{
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.tranquilo.relaxns.verifier.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}
	
	public static String localize( String prop )
	{ return localize( prop, null ); }
	
	public static String localize( String prop, Object arg1 )
	{ return localize( prop, new Object[]{arg1} ); }

	public static String localize( String prop, Object arg1, Object arg2 )
	{ return localize( prop, new Object[]{arg1,arg2} ); }

	public static final String ERR_UNEXPORTED_RULE
		= "DivideAndValidate.UnexportedRule";			// arg:1
	public static final String ERR_UNDEFINED_NAMESPACE	// arg:1
		= "DivideAndValidate.UndefinedNamespace";

	
	
	
	
//	public static final String LANGUAGE_NOT_SUPPORTED	// arg:0
//		= "RELAXReader.LanguageNotSupported";
//	public static final String ERR_UNEXPORTED_ELEMENTRULE	// arg:1
//		= "RELAXReader.UnexportedElementRule";
//	public static final String ERR_UNEXPORTED_HEDGERULE	// arg:1
//		= "RELAXReader.UnexportedHedgeRule";
//	public static final String ERR_UNEXPORTED_ATTPOOL	// arg:1
//		= "RELAXReader.UnexportedAttPool";
}
