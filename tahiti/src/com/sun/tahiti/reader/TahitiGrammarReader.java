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

import com.sun.tahiti.grammar.AnnotatedGrammar;

/**
 * Base interface that must be implemented by any GrammarReader
 * that can construct Tahiti-annotated AGM.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface TahitiGrammarReader
{
	public static final String TahitiNamespace = 
		"http://www.sun.com/xml/tahiti/";
	
	/**
	 * the same as the getResult method, but this one returns
	 * an AnnotatedGrammar object.
	 */
	AnnotatedGrammar getAnnotatedResult();

	
	public static final String ERR_INVALID_COLLECTION_TYPE = // arg:1
		"InvalidCollectionType";
	public static final String ERR_INVALID_ACCESS_MODIFIER = // arg:1
		"InvalidAccessModifier";
	public static final String ERR_INVALID_ACCESSOR = // arg:1
		"InvalidAccessor";
}
