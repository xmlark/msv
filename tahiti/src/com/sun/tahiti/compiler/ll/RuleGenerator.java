package com.sun.tahiti.compiler.ll;

import com.sun.msv.grammar.*;
import com.sun.tahiti.grammar.*;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/** creates raw production rules for a grammar. */
public class RuleGenerator
{
	/**
	 * creates production rules.
	 * 
	 * @return
	 *		a map from Expressions (as non-terminal symbols) to
	 *		Rule[].
	 * 
	 *		<p>
	 *		Note that if
	 *		<code>map.get(x)=={r1,r2,...}<code> then
	 *		<code>r1.left==r2.left==x</code>.
	 */
	public static Map create( Grammar g ) {
		// this map will receive the production rules.
		// this will be a map from Expression to Rule[].
		// That is, a map from X to X->abcd.
		final Map r = new java.util.HashMap();

		final ExpressionPool pool = g.getPool();
		
		g.getTopLevel().visit( new ExpressionVisitorVoid(){
			
			public void onElement( ElementExp exp ) {
				if(visit(exp)) {
					addRule( exp, new Expression[]{exp.contentModel} );
					exp.contentModel.visit(this);
				}
			}
		
			public void onAttribute( AttributeExp exp ) {
				if(visit(exp)) {
					addRule( exp, new Expression[]{exp.exp} );
					exp.exp.visit(this);
				}
			}
			
			public void onMixed( MixedExp exp ) {
				throw new Error();
				// treat this as 
				// <interleave> <oneOrMore><string/></oneOrMore> p </interleave>
			}
			
			public void onChoice( ChoiceExp exp ) {
				if(visit(exp)) {
					r.put( exp, new Rule[]{
						new Rule( exp, new Expression[]{exp.exp1} ),
						new Rule( exp, new Expression[]{exp.exp2} ) } );
					exp.exp1.visit(this);
					exp.exp2.visit(this);
				}
			}
			
			public void onConcur( ConcurExp exp ) {
				// not supported.
				throw new Error();
			}
			
			public void onSequence( SequenceExp exp ) {
				if(visit(exp)) {
					addRule( exp, new Expression[]{exp.exp1,exp.exp2} );
					exp.exp1.visit(this);
					exp.exp2.visit(this);
				}
			}
			public void onInterleave( InterleaveExp exp ){
				if(visit(exp)) {
					// add interleave rule.
					r.put( exp, new Rule( exp, new Expression[]{exp.exp1,exp.exp2}, true ) );
					exp.exp1.visit(this);
					exp.exp2.visit(this);
				}
			}
			
			public void onNullSet() {
				// nullSet must be removed before this process.
				throw new Error();
			}
			
			public void onEpsilon() {
				// there is no rule associated with the epsilon
				// this is a terminal symbol.
			}
			
			public void onList( ListExp exp ) {
				if(visit(exp)) {
					addRule( exp, new Expression[]{exp.exp} );
					exp.exp.visit(this);
				}
			}
			
			public void onTypedString( TypedStringExp exp ) {
				// this is a terminal symbol.
			}
			
			public void onAnyString() {
				// this is a terminal symbol
			}
			
			public void onOneOrMore( OneOrMoreExp exp ) {
				if(visit(exp)) {
					Expression item = exp.exp;
					Expression intermediate = pool.createZeroOrMore(item);
					addRule( exp, new Expression[]{item,intermediate} );
					item.visit(this);
					// this will create rules for the "intermediate"
					intermediate.visit(this);
				}
			}
			
			public void onRef( ReferenceExp exp ) {
				if(visit(exp)) {
					addRule( exp, new Expression[]{exp.exp} );
					exp.exp.visit(this);
				}
			}
			
			/**
			 * this method returns true if the specified expression
			 * is not visited before.
			 * 
			 * Care has to be taken not to traverse children before
			 * adding production rules for the parent, because this method
			 * assumes that visited expressions have associated production rules.
			 */
			private boolean visit( Expression exp ) {
				return !r.containsKey(exp);
			}
			
			/** helper method to add a single production rule. */
			private void addRule( Expression left, Expression[] right ) {
				r.put( left, new Rule[]{ new Rule(left,right) } );
			}
		});
		
		
		/*
			perform a little optimization.
		
			If there is a rule A := xyz (or x&y&z) that satisfies
			the following properties,
			then we can rewrite the occurence of A by xyz (with
			a restriction concerning the interleave property).
		
			1.  A does not have associated action.
				That is, nothing is executed when A is expanded to B (or B is
				reduced to A).
			2.	A := xyz is the only rule which has A as the left hand side.
			3.	A is not Element/Attribute. (these rules are significant and
				therefore cannot be removed.)
		*/
		// a map from Expression to Expression[]
		Map redundantSymbols = new java.util.HashMap();
		
		for( Iterator itr=r.values().iterator(); itr.hasNext(); ) {
			Rule[] rules = (Rule[])itr.next();
			
			// if this rule is in the form of "A:=xyz" and this is the only rule
			// which has A as the left hand side,
			if( rules.length==1 ) {
				
				// if this rule does not have associated action.
				if(!(	rules[0].left instanceof ClassItem
					||	rules[0].left instanceof FieldItem
					||	rules[0].left instanceof PrimitiveItem)
				// and A is a non-terminal
				&&  !(rules[0].left instanceof NameClassAndExpression) ) {
					
					redundantSymbols.put( rules[0].left, rules[0] );
				}
			}
		}
		
		// rewrite rules
		for( Iterator itr=r.values().iterator(); itr.hasNext(); ) {
			final Rule[] rules = (Rule[])itr.next();
			for( int i=0; i<rules.length; i++ )
				rewriteRule( rules[i], redundantSymbols );
		}
		
		/*
			another a little optimization.
		
			If there is a rule A -> xyz (or A -> x&y&z ) that satisfied
			the following properties,
			then we can rewrite all the occurence of A by xyz
		
			1.  A does not have associated action.
				That is, nothing is executed when A is expanded to B (or B is
				reduced to A).
			2.	A := xyz is the only rule which has A as the left hand side.
			3.	A is not Element/Attribute. (these rules are significant and
				therefore cannot be removed.)
		*/
		
		
		
		// final clean-up.
		// remove unreachable rules from "r" and store them  to "rules".
		
		Map rules = new java.util.HashMap();
		{
			Set workQueue = new java.util.HashSet();
			workQueue.add(g.getTopLevel());
			while(!workQueue.isEmpty()) {
				// get the first one in the queue.
				Expression symbol = (Expression)workQueue.iterator().next();
				workQueue.remove(symbol);
				
				Rule[] rs = (Rule[])r.get(symbol);
				assert(rs!=null);
				
				rules.put(symbol,rs);
				
				
				for( int i=0; i<rs.length; i++ ) {
					for( int j=0; j<rs[i].right.length; j++ ) {
						Expression e = rs[i].right[j];
						if(!Util.isTerminalSymbol(e) && !rules.containsKey(e)) {
							// recursively add rules reachable from this rule
							workQueue.add(e);
							assert( e!=null );
						}
					}
				}
			}
		}
		
		return rules;
	}

	/**
	 * replace redundant symbols in the array with its definition.
	 */
	private static void rewriteRule( Rule rule, Map redundantSymbols ) {
		
		for( int i=0; i<rule.right.length; i++ ) {
			while( redundantSymbols.containsKey(rule.right[i]) ) {
				Rule replace = (Rule)redundantSymbols.get(rule.right[i]);
				
				if((replace.isInterleave && rule.isInterleave)
				|| (!replace.isInterleave && !rule.isInterleave)) {
				
					// this symbol is redundant.
					Expression[] n = new Expression[rule.right.length+replace.right.length-1];
				
					System.arraycopy( rule.right, 0, n, 0, i );
					
					System.arraycopy( replace.right, 0, n, i, replace.right.length );
					
					System.arraycopy( rule.right, i+1, n, i+replace.right.length, rule.right.length-(i+1) );
				
					rule.right = n;
				}
			}
		}
	}
	
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
	
}
