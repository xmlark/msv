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
import com.sun.tahiti.compiler.Symbolizer;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

/**
 * computes LL parser table from annotated AGM.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LLTableCalculator
{
	// private use only. call the calc method.
	private LLTableCalculator() {}
	
	// actual type: non-terminal -> Rule[]
	private final Map rules = new java.util.HashMap();

	// actual type: non-terminal -> set(terminal symbols).
	final Map follows = new java.util.HashMap();

	// actual type:  Non-terminal -> ( terminal -> set(Rule) )
	final ParserTable table = new ParserTable();
	
	/**
	 * @param allRules
	 *		all rules in this grammar (actual type:  non-terminal -> Rule[] )
	 * @param startSymbol
	 *		ElementExp or AttributeExp which is used as the start symbol.
	 */
	public static ParserTable calc( Expression startSymbol, Map allRules, ExpressionPool pool, Symbolizer symbolizer ) {
		return new LLTableCalculator()._calc(startSymbol,allRules,pool,symbolizer);
	}
	
	private ParserTable _calc( Expression startSymbol, Map allRules, final ExpressionPool pool, Symbolizer symbolizer ) {
	/*
	Step.1
	======	
		compute all rules reachable from the current start symbol,
		and store them into the "rules" field.
	*/
		{
			Set workQueue = new java.util.HashSet();
			workQueue.add(startSymbol);
			while(!workQueue.isEmpty()) {
				// get the first one in the queue.
				Expression symbol = (Expression)workQueue.iterator().next();
				workQueue.remove(symbol);
				
				Rule[] r = (Rule[])allRules.get(symbol);
				assert(r!=null);
				
				rules.put(symbol,r);
				
				for( int i=0; i<r.length; i++ ) {
					for( int j=0; j<r[i].right.length; j++ ) {
						Expression e = r[i].right[j];
						if(!Util.isTerminalSymbol(e)
						&& !rules.containsKey(e)
						&& !(e instanceof NameClassAndExpression)) {
							// recursively add rules reachable from this rule
							// but do not recurse into ElementExp/AttributeExp.
							workQueue.add(e);
							assert( e!=null );
						}
					}
				}
			}
		}
		
	/*
	Step.2
	======	
		compute FOLLOWS set for each rule in "rules" and store them
		into the "follows" set.
	*/
		{
			// since AttributeExp/ElementExps are ususally considered as
			// terminals, we can't start from them. So start with their content model.
			Expression root;
			if( startSymbol instanceof NameClassAndExpression )
				root = ((NameClassAndExpression)startSymbol).getContentModel();
			else
				root = startSymbol;
			
			root.visit( new ExpressionVisitorVoid() {
				
				private Set s = createInitialSet();
				private Set createInitialSet() {
					Set s = new java.util.HashSet();
					s.add(null);	// null is used as "EOF" token.
					return s;
				}
				
				public void onRef( ReferenceExp exp ) {
					recordFollows(exp);
					exp.exp.visit(this);
				}
				public void onOther( OtherExp exp ) {
					recordFollows(exp);
					exp.exp.visit(this);
				}
				public void onChoice( ChoiceExp exp ) {
					recordFollows(exp);
					Set _s = new java.util.HashSet(s);
					exp.exp1.visit(this);
					Set s1 = s;
					s = _s;
					exp.exp2.visit(this);
					s.addAll(s1);
				}
				public void onList( ListExp exp ) {
					recordFollows(exp);
					exp.exp.visit(this);
				}
				public void onKey( KeyExp exp ) {
					recordFollows(exp);
					exp.exp.visit(this);
				}
				public void onMixed( MixedExp exp ) {
					recordFollows(exp);
					exp.exp.visit(this);
				}
				public void onSequence( SequenceExp exp ) {
					recordFollows(exp);
					exp.exp2.visit(this);
					exp.exp1.visit(this);
				}
				public void onInterleave( InterleaveExp exp ) {
					recordFollows(exp);
					exp.exp2.visit(this);
					exp.exp1.visit(this);
				}
				public void onOneOrMore( OneOrMoreExp exp ) {
					recordFollows( exp );
					recordFollows( pool.createZeroOrMore(exp.exp) );
					
					s.addAll( calcFIRST(exp.exp) );
					exp.exp.visit(this);
				}
				public void onEpsilon() {
					return;
				}
				public void onTypedString( TypedStringExp exp ) {
					if( !exp.isEpsilonReducible() )	s.clear();
					s.add(exp);
				}
				public void onNullSet() {
					// <notAllowed/> should have been removed already.
					throw new Error();
				}
				public void onConcur( ConcurExp exp ) {
					// not supported
					throw new Error();
				}
				public void onAttribute( AttributeExp exp ) {
					s.clear();
					s.add(exp);
				}
				public void onElement( ElementExp exp ) {
					s.clear();
					s.add(exp);
				}
				public void onAnyString() {
					s.add( Expression.anyString );
				}
				private void recordFollows( Expression exp ) {
					Set current = (Set)follows.get(exp);
					if(current==null)
						follows.put(exp,current=new java.util.HashSet());
					current.addAll(s);
				}
			});
		}
		
	/*
	Step.3
	======	
		compute ParserTable[A,a] and store them into the "parserTable" field.
	*/
		for( Iterator itr=rules.keySet().iterator(); itr.hasNext(); ) {
			
			final Expression nonTerminal = (Expression)itr.next();
			Rule[] rs = (Rule[])rules.get(nonTerminal);
			
			for( int i=0; i<rs.length; i++ ) {
				Rule r = rs[i];
				
				if(r.isInterleave) {
					for( int j=0; j<r.right.length; j++ ) {
						table.addRules( nonTerminal,
							calcFIRST(r.right[j])/*terminals*/,
							r );
					}
				} else { // if it's a sequence
					for( int j=0; j<r.right.length; j++ ) {
						table.addRules( nonTerminal,
							calcFIRST(r.right[j])/*terminals*/,
							r );
						if( !r.right[j].isEpsilonReducible() )
							break;
					}
				}
			}
			
			
			
			// if there exists an epsilon-reducible rule,
			// then we need to add them to the table.
			int i;
			for( i=0; i<rs.length; i++ ) {
				if( rs[i].isEpsilonReducible() )
					break;
			}
			if(i!=rs.length) {
				Set f;
				// if the nonTerminal is AttribtueExp or ElementExp,
				// FOLLOWS must be null (end of the input stream).
				if( nonTerminal instanceof NameClassAndExpression ) {
					f = new java.util.HashSet();
					f.add(null);
				} else
					f = (Set)follows.get( nonTerminal );
				
				assert(f!=null);	// FOLLOWS must have been computed.
					
				table.addRules( nonTerminal, f, rs[i] );
			}
		}
		
		return table;
	}

	/**
	 * computes a set of non-terminal symbols that can appear as the first non-terminal
	 * of this specified expression.
	 */
	private static Set calcFIRST( Expression exp ) {
		final Set r = new java.util.HashSet();
		exp.visit(new ExpressionVisitorVoid(){
			public void onRef( ReferenceExp exp ) {
				exp.exp.visit(this);
			}
			public void onOther( OtherExp exp ) {
				exp.exp.visit(this);
			}
			public void onChoice( ChoiceExp exp ) {
				exp.exp1.visit(this);
				exp.exp2.visit(this);
			}
			public void onList( ListExp exp ) {
				exp.exp.visit(this);
			}
			public void onKey( KeyExp exp ) {
				exp.exp.visit(this);
			}
			public void onMixed( MixedExp exp ) {
				exp.exp.visit(this);
			}
			public void onSequence( SequenceExp exp ) {
				exp.exp1.visit(this);
				if(exp.exp1.isEpsilonReducible())
					exp.exp2.visit(this);
			}
			public void onInterleave( InterleaveExp exp ) {
				exp.exp1.visit(this);
				exp.exp2.visit(this);
			}
			public void onOneOrMore( OneOrMoreExp exp ) {
				exp.exp.visit(this);
			}
			public void onEpsilon() {
				// we don't add epsilon to FIRST.
				return;
			}
			public void onTypedString( TypedStringExp exp ) {
				r.add(exp);
			}
			public void onNullSet() {
				// <notAllowed/> should have been removed already.
				throw new Error();
			}
			public void onConcur( ConcurExp exp ) {
				// not supported
				throw new Error();
			}
			public void onAttribute( AttributeExp exp ) {
				r.add(exp);
			}
			public void onElement( ElementExp exp ) {
				r.add(exp);
			}
			public void onAnyString() {
				r.add( Expression.anyString );
			}
		});
		
		return r;
	}
	
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
