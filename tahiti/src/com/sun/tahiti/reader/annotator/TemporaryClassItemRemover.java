/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.reader.annotator;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.tahiti.grammar.*;
import com.sun.tahiti.grammar.util.Multiplicity;
import com.sun.tahiti.grammar.util.MultiplicityCounter;
import java.util.Set;
import java.util.Iterator;

/**
 * removes temporarily added ClassItems (those ClassItems whose isTemporary field
 * is true) if they are unnecessary.
 * 
 * <p>
 * In this implementation, we don't propagate any information from the ancestor nodes
 * to child nodes. Therefore it is safe to rewrite the body of ReferenceExp (and
 * the content model field of ElementExps).
 */
class TemporaryClassItemRemover extends ExpressionCloner {
	
	public TemporaryClassItemRemover( ExpressionPool pool ) {
		super(pool);
	}
	
// assertions. these method may never be called.
	public Expression onNullSet()							{ throw new Error(); }
	public Expression onConcur( ConcurExp exp )				{ throw new Error(); }

// attribute/element.
	public Expression onAttribute( AttributeExp exp ) {
		Expression body = exp.exp.visit(this);
		if( body==exp.exp )	return exp;
		else	return pool.createAttribute( exp.nameClass, body );
	}
	
	private final Set visitedExps = new java.util.HashSet();
	
	public Expression onElement( ElementExp exp ) {
		if( !visitedExps.add(exp) )
			// this exp is already processed. this check will prevent infinite recursion.
			return exp;
		exp.contentModel = exp.contentModel.visit(this);
		return exp;
	}
	
	public Expression onRef( ReferenceExp exp ) {
		if( exp instanceof ClassItem ) {
			ClassItem ci = (ClassItem)exp;
			if( ci.isTemporary ) {
				// computes the multiplicity of child JavaItem.
				Multiplicity childOccurence = Multiplicity.calc(
					ci.exp, MultiplicityCounter.javaItemCounter );
				if( childOccurence.isAtMostOnce() )
					// this temporary class item is unnecessary. remove it.
					// but don't forget to recurse its descendants.
					return ci.exp.visit(this);
			}
			
			/*
			TODO:
			We can remove ClassItem if there is only one child JavaItem for it.
			For example,
			
			<class>
				<oneOrMore>
					<field/>
				</oneOrMore>
			</class>
			
			Although the multiplicity is (1,*), this class item can be still removed.
			*/
		}
		
		// update the definition and return self.
		exp.exp = exp.exp.visit(this);
		return exp;
	}
}
