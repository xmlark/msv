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
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;

/**
 * removes "xsi:schemaLocation" from AGM.
 * 
 * Because we don't want to see those attributes in generated instances.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaLocationRemover extends ExpressionCloner {
	
	/** set of visited ElementExps */
	private final Set visitedElements = new java.util.HashSet();
	
	public SchemaLocationRemover( ExpressionPool pool ) { super(pool); }
	
	public Expression onElement( ElementExp exp ) {
		// this check is necessary to prevent infinite recursion.
		if( visitedElements.contains(exp) )	return exp;
		visitedElements.add(exp);
		exp.contentModel = exp.contentModel.visit(this);
		return exp;
	}
	
	public Expression onAttribute( AttributeExp exp ) {
		return exp;
	}
	
	public Expression onRef( ReferenceExp exp ) {
		if( com.sun.msv.reader.xmlschema.XMLSchemaReader.XMLSchemaSchemaLocationAttributes
			== exp.name )
			// use == operator instead of equals method to
			// correctly compare the signature.
			
			// remove xsi:schemaLocation attributes from AGM.
			return Expression.epsilon;
		
		exp.exp = exp.exp.visit(this);
		return exp;
	}

	public Expression onOther( OtherExp exp ) {
		return exp.exp.visit(this);
	}
}
