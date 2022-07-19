/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
