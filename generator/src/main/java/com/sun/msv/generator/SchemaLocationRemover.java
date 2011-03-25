/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
