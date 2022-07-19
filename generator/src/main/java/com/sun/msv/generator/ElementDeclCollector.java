/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.generator;

import java.util.Set;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.util.ExpressionWalker;

/**
 * collects all distinct element declaration in the grammar.
 * As a side effect, it also collects all distinct attribute declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclCollector extends ExpressionWalker {
	public void onConcur( ConcurExp exp ) {
		throw new Error("concur is not supported");
	}
	
	private final Set elements = new java.util.HashSet();
	public void onElement( ElementExp exp ) {
		if( elements.contains(exp) )	return;	// prevent infinite recursion
		elements.add(exp);
		super.onElement(exp);
	}
	
	private final Set attributes = new java.util.HashSet();
	public void onAttribute( AttributeExp exp ) {
		attributes.add(exp);
		super.onAttribute(exp);
	}
	
	private ElementDeclCollector(){}
	
	/**
	 * collects all element and attribute declarations.
	 * 
	 * @return
	 *		r[0] : set of all distinct ElementExps.<br>
	 *		r[1] : set of all distinct AttributeExps.
	 */
	public static Set[] collect( Expression exp ) {
		Set[] r = new Set[2];
		ElementDeclCollector col = new ElementDeclCollector();
		exp.visit(col);
		
		r[0] = col.elements;
		r[1] = col.attributes;
		return r;
	}
}
