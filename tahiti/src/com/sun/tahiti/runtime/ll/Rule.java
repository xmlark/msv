/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.ll;

/**
 * one production rule of LL grammar (for example A -> xBCy)
 * 
 * immutable.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Rule {
	/** 
	 * The non-terminal symbol that forms the left hand side of the rule.
	 * 'A' in the above example.
	 */
	public final Object			left;
	
	/**
	 * The symbol sequence that forms the right hand side of the rule.
	 * 'xBCy' in the above example. Mixture of terminal and non-terminal symbols.
	 */
	public final Object[]		right;
	
	/**
	 * filters that will be used to hide symbols that match the other branches
	 * of the &lt;interleave&gt;.
	 * 
	 * <p>
	 * filters[0] must be always null. filters[1] is used to hide symbols that
	 * can appear in right[1], and so on.
	 * 
	 * <p>
	 * Please note, that the use of interleave has a restriction.
	 * Specifically, symbols that can appear in each branch must be disjoint.
	 * See the documentation for details.
	 */
	public final Filter[]	filters;
	
	/**
	 * If this field is false, then this rule is a ordinary sequence.
	 * So A -> xyz matchs to xyz but not zyx.
	 * 
	 * If this field is true, then this rule is an interleave sequence.
	 * In this case, A->xyz matches to zxy as well as xyz. (but not xxy).
	 */
	public final boolean		isInterleave;

	/**
	 * An array that contains this rule itself.
	 */
	public final Rule[]			selfArray;
	
	/** constructor for a normal rule. */
	public Rule( Object left, Object[] right ) {
		this(left,right,null);
	}
	
	/** constructor for an interleave rule. */
	public Rule( Object left, Object[] right, Filter[] filters ) {
		this.left=left; this.right=right;
		this.filters = filters;
		this.isInterleave = (filters!=null);
		selfArray = new Rule[]{this};
	}
}
