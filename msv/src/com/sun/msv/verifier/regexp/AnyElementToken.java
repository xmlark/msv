/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.ElementExp;

/**
 * special Token that matchs any element.
 * 
 * this token is used only for error recovery, to compute
 * "residual of elements of concern"(EoCR).
 * 
 * EoCR is defined as follows
 * 
 * <PRE>
 * EoCR(exp) := exp/e1 | exp/e2 | ... | exp/en
 * 
 * {ei} = elements of concern
 * exp/ei = residual(exp,ei)
 * '|' represents choice
 * </PRE>
 */
final class AnyElementToken extends ElementToken
{
	private AnyElementToken(){ super(null); }
	public static final Token theInstance = new AnyElementToken();
	boolean match( ElementExp exp ) { return true; }
}
