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
