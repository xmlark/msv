/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.util.PossibleNamesCollector;
import com.sun.msv.util.StringPair;
import java.util.Set;

/**
 * checks unambiguity constraint over the use of key/keyrefs.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IdentityAmbiguityChecker {
	
	private final Set checkedPatterns = new java.util.HashSet();

	private final Set names = new java.util.HashSet();
	
	private final Set constraints = new java.util.HashSet();
	
	private final ExpressionPool pool;
	private IdentityAmbiguityChecker( ExpressionPool pool ) {
		this.pool = pool;
	}
	
	public static boolean check( Grammar grammar ) {
		return check( grammar.getTopLevel(), grammar.getPool() );
	}
	
	public static boolean check( Expression exp, ExpressionPool pool ) {
		return new IdentityAmbiguityChecker(pool)._check(exp);
	}
	
	/** (uri,local) pair who is referenced by combineElement/AttributeExp. */
	private StringPair thePair;
	
	private boolean _check( Expression exp ) {
		
		if( checkedPatterns.contains(exp) )	return true;
		checkedPatterns.add(exp);
		
		// find what constraints are used.
		constraints.clear();
		exp.visit(constraintsCollector);
		if( constraints.size()>1 )
			// TODO: how to locate an error.
			return false;		// ambiguous.
		
		// find possible element names.
		names.clear();
		exp.visit(elementNameCollector);
		
		// check child elements.
		StringPair[] s = (StringPair[])names.toArray(new StringPair[names.size()]);
		for( int i=0; i<s.length; i++ ) {
			thePair = s[i];
			
			Expression filtered = exp.visit( combineElementExp );
			if( !_check(filtered) )		// check recursively.
				return false;
		}
		
		
		// find possible attribute names.
		names.clear();
		exp.visit(attributeNameCollector);
		
		// check child elements.
		s = (StringPair[])names.toArray(new StringPair[names.size()]);
		for( int i=0; i<s.length; i++ ) {
			thePair = s[i];
			
			Expression filtered = exp.visit(combineAttributeExp);
			if( !_check(filtered) )		// check recursively.
				return false;
		}
		
		return true;	// no problem.
	}
	
	/** visits all the content model. (but not child elements/attributes) */
	private static class ContentModelWalker extends ExpressionWalker {
		public void onAttribute( AttributeExp exp ) {}
		public void onElement( ElementExp exp ) {}
	}

	/** collects all identity constraints used with in the expression. */
	private final ContentModelWalker constraintsCollector = new ContentModelWalker() {
		// TODO: do the real work
		public void onKey( KeyExp exp ) { throw new Error(); }
	};
	
	/** collects all possible names for elements. */
	private final ContentModelWalker elementNameCollector = new ContentModelWalker() {
		public void onElement( ElementExp exp ) {
			names.addAll(PossibleNamesCollector.calc(exp.getNameClass()));
		}
	};

	/** collects all possible names for attributes. */
	private final ContentModelWalker attributeNameCollector = new ContentModelWalker() {
		public void onAttribute( AttributeExp exp ) {
			names.addAll(PossibleNamesCollector.calc(exp.nameClass));
		}
	};
	
	/** clones Expressions but only use Choice. */
	private abstract class CombinedExpressionCreator implements ExpressionVisitorExpression {
		
		public Expression onOneOrMore( OneOrMoreExp exp )	{ return exp.exp.visit(this); }
		public Expression onMixed( MixedExp exp )			{ return exp.exp.visit(this); }
		public Expression onRef( ReferenceExp exp )			{ return exp.exp.visit(this); }
		public Expression onOther( OtherExp exp )			{ return exp.exp.visit(this); }
		public Expression onEpsilon()						{ return Expression.epsilon; }
		public Expression onNullSet()						{ return Expression.epsilon; }
		public Expression onAnyString()						{ return Expression.epsilon; }
		public Expression onTypedString( TypedStringExp exp ){ return Expression.epsilon; }
		// list is ignorable. Just like TypedString.
		public Expression onList( ListExp exp )				{ return Expression.epsilon; }
		public Expression onKey( KeyExp exp )				{ return Expression.epsilon; }

		public Expression onChoice( ChoiceExp exp )			{ return onBinExp(exp); }
		public Expression onSequence( SequenceExp exp )		{ return onBinExp(exp); }
		public Expression onConcur( ConcurExp exp )			{ return onBinExp(exp); }
		public Expression onInterleave( InterleaveExp exp )	{ return onBinExp(exp); }
			
		private Expression onBinExp( BinaryExp exp ) {
			return pool.createChoice( exp.exp1.visit(this), exp.exp2.visit(this) );
		}
	}
	
	/** combines content model of elements whose name class accepts "thePair". */
	private final CombinedExpressionCreator combineElementExp = new CombinedExpressionCreator() {
		public Expression onAttribute( AttributeExp exp ) {
			return Expression.epsilon;
		}
		public Expression onElement( ElementExp exp ) {
			if( exp.getNameClass().accepts( thePair ) )
				return exp.contentModel;
			else
				return exp.epsilon;
		}
	};

	/** combines content model of attributes whose name class accepts "thePair". */
	private final CombinedExpressionCreator combineAttributeExp = new CombinedExpressionCreator() {
		public Expression onElement( ElementExp exp ) {
			return Expression.epsilon;
		}
		public Expression onAttribute( AttributeExp exp ) {
			if( exp.nameClass.accepts( thePair ) )
				return exp.exp;
			else
				return exp.epsilon;
		}
	};
}
