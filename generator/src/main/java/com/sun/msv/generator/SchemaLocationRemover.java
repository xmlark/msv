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
