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
	/**
	 * the same as the getResult method, but this one returns
	 * an AnnotatedGrammar object.
	 */
	AnnotatedGrammar getAnnotatedResult();
}
