/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.xmlschema;

import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.grammar.ReferenceExp;

/**
 * Element declaration.
 * 
 * ElementDeclExp as a ReferenceExp holds an expression that
 * also matches to substituted element declarations.
 * 
 * <code>self</code> field contains an expression that matches
 * only to this element declaration without no substituted element decls.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclExp extends ReferenceExp
{
	public ElementDeclExp( String typeLocalName ) {
		super(typeLocalName);
		this.exp = Expression.nullSet;
	}
	
	/**
	 * those who set the value to this field is also responsible to
	 * add self into this#exp.
	 */
	public ElementExp self;
}
