package com.sun.tahiti.compiler.ll;

import com.sun.msv.grammar.Expression;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;
import java.util.Set;

public class Rule {
	
	public Expression		left;
	
	/**
	 * right-hand side of the rules.
	 * 
	 * <code>Expression.epsilon</code> is used to denote A -> $epsilon.
	 * TypedStringExp is used instead of Datatype.
	 */
	public Expression[]		right;
	
	public boolean			isInterleave;
	
	public Rule( Expression left, Expression[] right ) {
		this(left,right,false);
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
	
	public Rule( Expression left, Expression[] right, boolean isInterleave ) {
		this.left = left;
		this.right = right;
		this.isInterleave = isInterleave;
		
		assert(left!=null);
		assert(right!=null);
		for( int i=0; i<right.length; i++ )
			assert( right[i]!=null );
	}
	
	/** creates a deep-copy of this object. */
	public Rule copy() {
		Expression[] e = new Expression[right.length];
		System.arraycopy( right, 0, e, 0, right.length );
		return new Rule(left,e,isInterleave);
	}
	
	/**
	 * replaces the idx-th symbol of the right field by the definition
	 * of the specified rule.
	 * 
	 * This method checks the interleaving property of two rules and
	 * returns false if such a replacement is impossible.
	 */
	public boolean replaceRight( int idx, Rule replace ) {
		
		// if the length of the right hand side is just 1,
		// (i.e., A -> B), then we can change the interleaveness of the rule.
		if(replace.right.length==1)
			replace.isInterleave = this.isInterleave;
		if(this.right.length==1)
			this.isInterleave = replace.isInterleave;
				
		// if one is an interleaving rule and the other is a normal rule,
		// then we can't rewrite rules.
		if((replace.isInterleave && this.isInterleave)
		|| (!replace.isInterleave && !this.isInterleave)) {
			Expression[] seq = new Expression[right.length+replace.right.length-1];
		
			System.arraycopy( right, 0, seq, 0, idx );
			System.arraycopy( replace.right, 0, seq, idx, replace.right.length );
			System.arraycopy( right, idx+1, seq, idx+replace.right.length, right.length-(idx+1) );
		
			right = seq;
			return true;
		}
		
		return false;
	}
	
	/**
	 * writes the contents of the table as XML.
	 */
	public void write( XMLWriter writer, Symbolizer symbolizer ) {
		writer.start("rule",
			new String[]{
				"interleave",isInterleave?"true":"false",
				"id",symbolizer.getId(this)
			});
		
		writer.element("left",new String[]{"symbolRef",symbolizer.getId(left)});
		
		writer.start("right");
		for( int i=0; i<right.length; i++ )
			if( right[i]!=Expression.epsilon )
				// we don't write epsilon explicitly.
				writer.element("item",new String[]{"symbolRef",symbolizer.getId(right[i])});
		
		writer.end("right");
		
		writer.end("rule");
	}
	
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
