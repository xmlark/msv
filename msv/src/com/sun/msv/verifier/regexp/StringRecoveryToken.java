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
 * This object is used for error recovery.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class StringRecoveryToken extends StringToken
{
	StringRecoveryToken( StringToken base )
	{
		super( base.literal, base.context );
	}
	
	/**
	 * DataTypes that signals an error is collected into this object.
	 */
	final Set failedTypes = new java.util.HashSet();
	
	boolean match( TypedStringExp exp )
	{
		if( exp.dt.verify( literal, context ) )
			return true;
		
		// this datatype didn't accept me. so record it for diagnosis.
		failedTypes.add( exp.dt );
		return true;
	}
}
