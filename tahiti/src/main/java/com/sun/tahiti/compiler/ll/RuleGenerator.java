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

import com.sun.msv.grammar.*;
import com.sun.tahiti.grammar.*;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;

/**
 * creates raw production rules for a grammar.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RuleGenerator
{
	private static java.io.PrintStream debug = null;
	
	/**
	 * creates production rules.
	 * 
	 * @return
	 *		a map from Expressions (as non-terminal symbols) to
	 *		Rule[] (possible expansion rules).
	 * 
	 *		<p>
	 *		Note that if
	 *		<code>map.get(x)=={r1,r2,...}<code> then
	 *		<code>r1.left==r2.left==x</code>.
	 */
	public static Rules create( Grammar g ) {
		return new RuleGenerator()._create(g);
	}
	
	
	private RuleGenerator() {}
	
	
	/**
	 * the production rules.
	 */
	private Rules rules = new Rules();
	
	/**
	 * pool object that can be used to create Expressions during this process.
	 */
	private ExpressionPool pool;
	
	/** grammar object that we are dealing with. */
	private Grammar g;
	
	private Rules _create( Grammar g ) {
		pool = g.getPool();
		this.g = g;
		
		g.getTopLevel().visit( new ExpressionVisitorVoid(){
			
			public void onElement( ElementExp exp ) {
				if(visit(exp)) {
					rules.add( exp, exp.contentModel );
					exp.contentModel.visit(this);
				}
			}
		
			public void onAttribute( AttributeExp exp ) {
				if(visit(exp)) {
					rules.add( exp, exp.exp );
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
					/*
					Large choices are very common in many grammars.
					Say we have a content model of A|B|C|D.
					
					This content model is internally held as (A|(B|(C|D))),
					which is binarized.
					
					So the following casual rule expansion:
					----------------------------------
					rules.put( exp, new Rule[]{
						new Rule( exp, new Expression[]{exp.exp1} ),
						new Rule( exp, new Expression[]{exp.exp2} ) } );
					exp.exp1.visit(this);
					exp.exp2.visit(this);
					----------------------------------
					will result in the following redundant rules:
					
					A|B|C|D		-> A
					A|B|C|D		-> B|C|D
					B|C|D		-> B
					B|C|D		-> C|D
					C|D			-> C
					C|D			-> D
					
					Instead, we'd like to have the following rules.
					
					A|B|C|D		-> A
					A|B|C|D		-> B
					A|B|C|D		-> C
					A|B|C|D		-> D
					
					To do this, we are scanning all branches of choice at once.
					
					Note that the modified rules look concise, but actually
					it is not always a good choice.
					*/
					rules.add( exp, exp.exp1 );
					rules.add( exp, exp.exp2 );
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
					rules.add( exp, new Expression[]{exp.exp1,exp.exp2}, false );
					exp.exp1.visit(this);
					exp.exp2.visit(this);
				}
			}
			public void onInterleave( InterleaveExp exp ){
				if(visit(exp)) {
					// add interleave rule.
					rules.add( exp, new Expression[]{exp.exp1,exp.exp2}, true );
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
					rules.add( exp, exp.exp );
					exp.exp.visit(this);
				}
			}
			
			// these are terminal symbols
			public void onData( DataExp exp ) {}
			public void onValue( ValueExp exp ) {}
			public void onAnyString()			{}
			
			public void onOneOrMore( OneOrMoreExp exp ) {
				if(visit(exp)) {
					Expression item = exp.exp;
					Expression intermediate = pool.createZeroOrMore(item);
					if( intermediate==exp ) {
						// if item is epsilon-reducible, then createZeroOrMore returns
						// the same expression as exp. This case has to be treated
						// differently.
						rules.add( exp, Expression.epsilon );
						rules.add( exp, new Expression[]{item,intermediate}, false );
					} else {
						rules.add( exp, new Expression[]{item,intermediate}, false );
					}
					item.visit(this);
					// this will create rules for the "intermediate"
					intermediate.visit(this);
				}
			}
			
			public void onRef( ReferenceExp exp ) {
				if(visit(exp)) {
					rules.add( exp, exp.exp );
					exp.exp.visit(this);
				}
			}
			
			public void onOther( OtherExp exp ) {
				if(visit(exp)) {
					rules.add( exp, exp.exp );
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
				return !rules.contains(exp);
			}
		});


		
		/*
			perform a little optimization.
		
			If there is a rule A := xyz (or x&y&z) that satisfies
			the following properties,
			then we can rewrite the occurence of A by xyz (with
			a restriction concerning the interleave property).
		
			1.	A := xyz is the only rule which has A as the left hand side.
		
			2.  A does not have associated action.
				That is, nothing is executed when A is expanded to B (or B is
				reduced to A).
			3.	A is not Element/Attribute. (these rules are significant and
				therefore cannot be removed.)
		*/
		// a map from Expression to Expression[]
		Map redundantSymbols = new java.util.HashMap();
		
		for( Iterator itr=rules.iterateKeys(); itr.hasNext(); ) {
			final Expression left = (Expression)itr.next();
			final Rule[] rs = rules.getAll(left);
			
			// if this rule is in the form of "A:=xyz" and this is the only rule
			// which has A as the left hand side,
			if( rs.length==1 ) {
				
				// if this rule does not have associated action.
				if( isActionlessNonTerminal(left)
				// and A is a pure non-terminal
				&&  !(left instanceof NameClassAndExpression) ) {
					
					redundantSymbols.put( left, rs[0] );
				}
			}
		}

		// rewrite rules
		rewriteRules( redundantSymbols );
		
		// clean-up.
		rules = rules.removeUnreachableRules(g.getTopLevel(),true);

		
		
		
		/*
			perform another optimization.
		
			If there is a rule A := xyz (or x&y&z) that satisfies
			the following properties,
			then we can rewrite the occurence of A by xyz (with
			a restriction concerning the interleave property).
		
			1.	A is used only once, and its occurence must be the left most.
				That is, there must be a rule X->Abcd... and this is the only
				occasion of A in the entire rules.
				
				The reason of this restriction is the ambiguity. If the
				rules are X->stA, A->b, and A->c and if we expand this A, the result
				will be X->stb and X->stc, which is ambiguous.
		
			2.  A does not have associated action.
				That is, nothing is executed when A is expanded to B (or B is
				reduced to A).
			3.	A is not Element/Attribute. (these rules are significant and
				therefore cannot be removed.)
		*/
		{
			Set occuredSymbols = new java.util.HashSet();
			Set candidates = new java.util.HashSet();
			
			for( Iterator itr=rules.iterateKeys(); itr.hasNext(); ) {
				final Expression left = (Expression)itr.next();
				Rule[] rs = rules.getAll(left);
				
				for( int i=0; i<rs.length; i++ ) {
					Expression[] right = rs[i].right;
					for( int j=0; j<right.length; j++ ) {
						if( j==0	// if this is the left most symbol,
						&& !occuredSymbols.contains(right[0])	// and this is the first occurence,
						
						// ... and if this rule does not have associated action.
						&& isActionlessNonTerminal(right[0])
						// and A is a non-terminal
						&& !(	right[0] instanceof NameClassAndExpression
							||	right[0] instanceof DataOrValueExp) )
							candidates.add(right[j]);
						else
							candidates.remove(right[j]);
						occuredSymbols.add(right[j]);
					}
				}
			}
			
			// candidates will hold all such redundant symbols.
			// now rewrite the rules.
			Rules result = new Rules();
			
			for( Iterator itr=rules.iterateKeys(); itr.hasNext(); ) {
				final Expression left = (Expression)itr.next();
				Rule[] rs = rules.getAll(left);
				
				for( int i=0; i<rs.length; i++ ) {
					
					// TODO: originally this was written to recursively
					// expand rules.
					if( !candidates.contains(rs[i].right[0]) )
						result.add(left,rs[i]);
					else {
						// this rule (rs[i]) has the redundant symbol as the
						// left most symbol of the right hand side.
						
						// obtain the definitions of the redundant symbols.
						Rule[] definitions = rules.getAll(rs[i].right[0]);
						if(debug!=null)
							assert( definitions!=null && definitions.length!=0 );
						
						Rule[] rewritten = new Rule[definitions.length];
						
						int j;
						for( j=0; j<definitions.length; j++ ) {
							// the rewriting may be failed at the later point.
							rewritten[j] = rs[i].copy();
							if(!rewritten[j].replaceRight( 0, definitions[j] ))
								break;
						}
						if(j!=definitions.length)
							// we've failed to rewrite the rule.
							// do not touch rs[i]
							result.add(left,rs[i]);
						else
							// add rewritten rules.
							result.addAll(left,rewritten);
					}
				}
			}
			
			// clean-up.
			rules = result.removeUnreachableRules(g.getTopLevel(),true);
		}
		
		
		/*
			remove rules of the form " X -> X".
			
			I'm not exactly sure if this is the right solution to the problem,
			but these production rules can be produced from the following pattern:
		
			<zeroOrMore>	(X)
				<optional>	(Y)
					A
				</optional>
			</zeroOrMore>
		
			raw rules:
				X -> Y X
				X -> \epsilon
		
				Y -> \epsilon
				Y -> A
		
			By the optimization, Y is absorbed
				X -> X
				X -> A X
				X -> \epsilon
		*/
		{
			Iterator nonTerms = rules.iterateKeys();
			while( nonTerms.hasNext() ) {
				Expression left = (Expression)nonTerms.next();
				Iterator/* of Rule*/ jtr = rules.get(left).iterator();
				
				while( jtr.hasNext() ) {
					final Rule r = (Rule)jtr.next();
					if( r.right.length==1 && r.right[0]==left ) {
						// this rule is the form of "X->X"
						if(debug!=null)
							debug.println("removing self-recursive rule");
						jtr.remove();
					}
				}
			}
		}
		
		rules.intern();
		return rules;
	}

	
	/**
	 * rewrite the entire rules by replacing redundant symbols by
	 * their definitions, as specified by the map.
	 * 
	 * @param redundantSymbols
	 *		a map from symbols to its definitions. Occurences of these symbols
	 *		in rules are replaced by its definitions.
	 */
	private void rewriteRules( Map redundantSymbols ) {
		for( Iterator itr=rules.iterateKeys(); itr.hasNext(); ) {
			final Expression symbol = (Expression)itr.next();
			final Rule[] rs = rules.getAll(symbol);
			for( int i=0; i<rs.length; i++ )
				rewriteRule( rs[i], redundantSymbols );
		}
	}
	
	/**
	 * replace redundant symbols in the array with its definition.
	 */
	private static void rewriteRule( Rule rule, Map redundantSymbols ) {
		
		for( int i=0; i<rule.right.length; i++ ) {
			while( redundantSymbols.containsKey(rule.right[i]) ) {
				Rule replace = (Rule)redundantSymbols.get(rule.right[i]);
				
				if(!rule.replaceRight( i, replace ))
					// if we failed to rewrite the rule, we need to break the loop
					// to avoid infinite loop.
					break;
				
				// when we succeeded in rewriting, we need to check rule.right[i]
				// again, since rule.right[i] is rewritten.
			}
		}
	}
	
	private static boolean isActionlessNonTerminal( Expression exp ) {
		return !(  exp instanceof ClassItem
				|| exp instanceof FieldItem
				|| exp instanceof PrimitiveItem
				|| exp instanceof IgnoreItem);
	}	
}
