/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.ll;

import com.sun.msv.grammar.*;

public class Util
{
	public static boolean isTerminalSymbol( Expression exp ) {
		return	exp instanceof TypedStringExp
			||	exp==Expression.anyString
			||	exp==Expression.epsilon;
	}
}
