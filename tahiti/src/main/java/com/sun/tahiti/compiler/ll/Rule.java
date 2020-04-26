/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.ll;

import com.sun.msv.grammar.Expression;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;
import java.util.Iterator;
import java.util.Set;

/**
 * a production rule of context-free grammar (CFG).
 * 
 * Rule object is unified.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class Rule {
	
	/**
	 * right-hand side of the rules.
	 * 
	 * <code>Expression.epsilon</code> is used to denote A -> $epsilon.
	 * TypedStringExp is used instead of Datatype.
	 */
	public Expression[]		right;
	
	public boolean			isInterleave;
	
	public Rule( Expression[] right, boolean isInterleave ) {
		this.right = right;
		this.isInterleave = isInterleave;
		
		assert(right!=null);
		for( int i=0; i<right.length; i++ )
			assert( right[i]!=null );
	}
	
	/**
	 * creates a deep copy.
	 */
	public Rule copy() {
		Expression[] r = new Expression[right.length];
		System.arraycopy(right,0,r,0,right.length);
		return new Rule(r,isInterleave);
	}
	
	/**
	 * two Rules are considered equal if their contents are exactly the same.
	 */
	public boolean equals( Object o ) {
		if(!(o instanceof Rule))		return false;
		Rule rhs = (Rule)o;
		
		// compare the contents of the array
		if( this.right.length!=rhs.right.length )	return false;
		for( int i=0; i<this.right.length; i++ )
			if( this.right[i]!=rhs.right[i] )
				return false;
		
		// compare the interleave property
		if( this.isInterleave!=rhs.isInterleave )
			return false;
		
		return true;
	}
	
	public int hashCode() {
		int hashCode = 0;
		for( int i=0; i<right.length; i++ )
			hashCode ^= right[i].hashCode();
		return hashCode;
	}
	
	/**
	 * computes if the right-hand side of this rule is epsilon-reducible.
	 * 
	 * The right-hand side is epsilon-reducible if and only if all of the
	 * symbols on the right-hand side is epsilon-reducible.
	 */
	public boolean isEpsilonReducible() {
		for( int i=0; i<right.length; i++ )
			if( !right[i].isEpsilonReducible() )
				return false;
		return true;
	}
	
	/**
	 * computes a new rule object by replacing the idx-th symbol of
	 * the right field by the definition of the specified rule.
	 * 
	 * This method checks the interleaving property of two rules and
	 * returns null if such a replacement is impossible.
	 */
	public boolean replaceRight( int idx, Rule replace ) {
		
		boolean interleave;
		
		// if the length of the right hand side is just 1,
		// (i.e., A -> B), then we can change the interleaveness of the rule.
		if(replace.right.length==1)
			interleave = this.isInterleave;
		else
		if(this.right.length==1)
			interleave = replace.isInterleave;
		else
		if(this.isInterleave!=replace.isInterleave)
			// if one is an interleaving rule and the other is a normal rule,
			// then we can't rewrite rules.
			return false;
		else
			interleave = this.isInterleave;
		
		int rlen = replace.right.length;
		if(rlen==1 && replace.right[0]==Expression.epsilon)
			rlen=0;	// coerce epsilon
			
		Expression[] seq = new Expression[right.length+rlen-1];
			
		if(seq.length==0)
			seq = new Expression[]{Expression.epsilon};
		else {
			System.arraycopy( right, 0, seq, 0, idx );
			System.arraycopy( replace.right, 0, seq, idx, rlen );
			System.arraycopy( right, idx+1, seq, idx+rlen, right.length-(idx+1) );
		}
			
		this.right = seq;
		this.isInterleave = interleave;
		return true;
	}
	
	/**
	 * produces the string representation of this rule.
	 * Mainly for debugging.
	 */
	public String toString( Symbolizer symbolizr ) {
		StringBuffer buf = new StringBuffer();
		buf.append( " --> ");
		if(isInterleave)
			buf.append("(interleave) ");
		for( int i=0; i<right.length; i++ ) {
			if(i!=0)	buf.append(' ');
			buf.append(symbolizr.getId(right[i]));
		}
		return buf.toString();
	}
	
	/**
	 * writes the contents of the table as XML.
	 */
	public void write( XMLWriter writer, Symbolizer symbolizer ) {
		writer.start("rule",
			new String[]{
				"interleave",isInterleave?"true":"false",
				"no",symbolizer.getId(this)
			});
		
		// TODO: check the disjointness of the interleave.
		
		writer.start("right");
		for( int i=0; i<right.length; i++ )
			// we don't write epsilon explicitly.
			if( right[i]!=Expression.epsilon ) {
				writer.start("item",new String[]{"symbolRef",symbolizer.getId(right[i])});
				if( isInterleave && i!=0 ) {
					// for <interleave>, we have to spit the filter definition.
					// But we don't need a filter for the first one symbol.
					writer.start("filter");
					Iterator itr = FilterCalculator.calc(right[i]).iterator();
					while(itr.hasNext())
						writer.element("item",new String[]{"symbolRef",symbolizer.getId(itr.next())});
					writer.end("filter");
				}
				writer.end("item");
			}
		
		writer.end("right");
		
		writer.end("rule");
	}	
}
