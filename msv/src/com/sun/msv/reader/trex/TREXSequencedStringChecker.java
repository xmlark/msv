/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import java.util.Set;
import java.util.Map;

/**
 * makes sure that there is no sequenced string.
 * 
 * "sequenced string" is something like this.
 * <XMP>
 * <oneOrMore>
 *   <string> abc </string>
 * </oneOrMore>
 * </XMP>
 * 
 * Also, TREX prohibits sequence of typed strings and elements.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TREXSequencedStringChecker implements ExpressionVisitor
{
	// each visitor method returns one of the following, or null.
	private static final Object typedString = new Object();
	private static final Object elements = new Object();
	private static final Object both = new Object();
	
	private final TREXBaseReader reader;
	TREXSequencedStringChecker( TREXBaseReader reader ) {
		this.reader = reader;
	}
	
	/**
	 * set of checked Expressions.
	 * 
	 * once an ElementExp/AttributeExp is checked, it will be added to this set.
	 * this set is used to prevent infinite recursion.
	 */
	private final Set checkedExps = new java.util.HashSet();
	
	/**
	 * set of checked ReferenceExps.
	 * 
	 * Once a ReferenceExp is checked, it will be added (with its result)
	 * to this map. This is useful to speed up the check.
	 */
	private final Map checkedRefExps = new java.util.HashMap();
	
	public Object onRef( ReferenceExp exp ) {
		Object r = checkedRefExps.get(exp);
		if(r!=null)	return r;
		checkedRefExps.put(exp, r=exp.exp.visit(this) );
		return r;
	}
	public Object onOther( OtherExp exp ) {
		return exp.exp.visit(this);
	}
	
	public Object onInterleave( InterleaveExp exp ) {
		Object l = exp.exp1.visit(this);
		Object r = exp.exp2.visit(this);
		
		if(isError(l,r)) {
			// where is the source of error?
			reader.reportError( reader.ERR_INTERLEAVED_STRING );
			return null;
		}
		
		return merge(l,r);
	}
	
	public Object onSequence( SequenceExp exp ) {
		Object l = exp.exp1.visit(this);
		Object r = exp.exp2.visit(this);
		
		if(isError(l,r)) {
			// where is the source of error?
			reader.reportError( reader.ERR_SEQUENCED_STRING );
			return null;
		}
		
		return merge(l,r);
	}
	
	public Object onEpsilon() { return null; }
	public Object onNullSet() { return null; }
	public Object onTypedString( TypedStringExp exp ) { return typedString; }
	public Object onList( ListExp exp )		{ return typedString; }
	
	public Object onAttribute( AttributeExp exp ) {
		if( checkedExps.add(exp) )
			exp.exp.visit(this);
		return null;
	}
	
	public Object onElement( ElementExp exp ) {
		if( checkedExps.add(exp) )
			// if this is the first visit
			// this has to be done before checking content model
			// otherwise it leads to the infinite recursion.
			exp.contentModel.visit(this);
		return elements;
	}
	
	private static Object merge( Object o1, Object o2 ) {
		if(o1==null)	return o2;
		if(o2==null)	return o1;
		if(o1==both || o2==both)	return both;
		
		if(o1!=o2)		return both;
		else			return o1;	// o1==o2
	}
	private static boolean isError( Object o1, Object o2 ) {
		if( (o1==both || o1==typedString) && o2!=null )	return true;
		if( (o2==both || o2==typedString) && o1!=null )	return true;
		
		return false;
	}
	
	public Object onChoice( ChoiceExp exp ) {
		return merge( exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	
	public Object onConcur( ConcurExp exp ) {
		return merge( exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	
	public Object onOneOrMore( OneOrMoreExp exp ) {
		Object o = exp.exp.visit(this);
		if( o==both || o==typedString ) {
			reader.reportError(reader.ERR_REPEATED_STRING);
			return null;
		}
		return o;
	}
	
	public Object onMixed( MixedExp exp )	{ return exp.exp.visit(this); }

	
	// anyString is the only string token that can be mixed with elements.
	public Object onAnyString()				{ return elements; }
}
