/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.grammar;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;

/**
 * Implementation of Rule interface by Tranquilo grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RuleImpl implements org.iso_relax.dispatcher.Rule
{
	/** "meat" of this Rule. */
	public final Expression exp;
	
	/** name of this rule */
	protected final String name;
	
	public RuleImpl( ReferenceExp exp ) { this(exp.name,exp.exp); }
	public RuleImpl( String name, Expression exp )
	{
		this.exp=exp;
		this.name=name;
	}
	
	public String getName() { return name; }
}
