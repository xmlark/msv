package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.reader.RunAwayExpressionChecker;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import java.util.Set;

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
 */
class TREXSequencedStringChecker implements TREXPatternVisitor
{
	private static final Object typedString = new Object();
	private static final Object elements = new Object();
	private static final Object both = new Object();
	
	private final TREXGrammarReader reader;
	TREXSequencedStringChecker( TREXGrammarReader reader )
	{ this.reader = reader;	}
	
	/**
	 * set of checked elementExps.
	 * 
	 * once an ElementExp is checked, it will be added to this set.
	 * this set is used to prevent infinite recursion.
	 */
	private final Set checkedElements = new java.util.HashSet();
	
	public Object onRef( ReferenceExp exp )
	{
		return exp.exp.visit(this);
	}
	
	public Object onInterleave( InterleavePattern exp )
	{
		Object l = exp.exp1.visit(this);
		Object r = exp.exp2.visit(this);
		
		if(isError(l,r))
		{
			// where is the source of error?
			reader.reportError( reader.ERR_INTERLEAVED_STRING );
			return null;
		}
		
		return merge(l,r);
	}
	public Object onSequence( SequenceExp exp )
	{
		Object l = exp.exp1.visit(this);
		Object r = exp.exp2.visit(this);
		
		if(isError(l,r))
		{
			// where is the source of error?
			reader.reportError( reader.ERR_SEQUENCED_STRING );
			return null;
		}
		
		return merge(l,r);
	}
	
	public Object onEpsilon() { return null; }
	public Object onNullSet() { return null; }
	public Object onTypedString( TypedStringExp exp ) { return typedString; }
	
	public Object onAttribute( AttributeExp exp )
	{
		exp.exp.visit(this);
		return null;
	}
	
	public Object onElement( ElementExp exp )
	{
		if( !checkedElements.contains(exp) )
		{// if this is the first visit
			// this has to be done before checking content model
			// otherwise it leads to the infinite recursion.
			checkedElements.add(exp);
			
			exp.contentModel.visit(this);
		}
		return elements;
	}
	
	private static Object merge( Object o1, Object o2 )
	{
		if(o1==null)	return o2;
		if(o2==null)	return o1;
		if(o1==both || o2==both)	return both;
		
		if(o1!=o2)		return both;
		else			return o1;	// o1==o2
	}
	private static boolean isError( Object o1, Object o2 )
	{
		if( (o1==both || o1==typedString) && o2!=null )	return true;
		if( (o2==both || o2==typedString) && o1!=null )	return true;
		
		return false;
	}
	
	public Object onChoice( ChoiceExp exp )
	{
		return merge( exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	
	public Object onConcur( ConcurPattern exp )
	{
		return merge( exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	
	public Object onOneOrMore( OneOrMoreExp exp )
	{
		Object o = exp.exp.visit(this);
		if( o==both || o==typedString )
		{
			reader.reportError(reader.ERR_REPEATED_STRING);
			return null;
		}
		return o;
	}
	
	public Object onMixed( MixedExp exp )	{ return exp.exp.visit(this); }
	// anyString is the only string token that can be mixed with elements.
	public Object onAnyString()				{ return elements; }
}
