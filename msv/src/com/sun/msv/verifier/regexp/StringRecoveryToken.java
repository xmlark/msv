/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.TypedStringExp;
import java.util.Set;

/**
 * special StringToken that acts as a wild card.
 * 
 * This object is used for error recovery. It collects all TypedStringExps
 * that ate the token.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class StringRecoveryToken extends StringToken {
	
	StringRecoveryToken( StringToken base ) {
		super( base.literal, base.context );
	}
	
	/**
	 * TypedStringExps that rejected this token are collected into this set.
	 */
	final Set failedExps = new java.util.HashSet();
	
	boolean match( TypedStringExp exp ) {
		if( super.match(exp) )
			return true;
		
		// this datatype didn't accept me. so record it for diagnosis.
		failedExps.add( exp );
		return true;
	}
}
