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
	
	private Rules rules;

	// actual type:  Non-terminal -> ( terminal -> set(Rule) )
	final ParserTable table = new ParserTable();
	
	/**
	 * @param allRules
	 *		all rules in this grammar.
	 * @param startSymbol
	 *		ElementExp or AttributeExp which is used as the start symbol.
	 */
	public static ParserTable calc( Expression startSymbol, Rules allRules, ExpressionPool pool, Symbolizer symbolizer ) {
		return new LLTableCalculator()._calc(startSymbol,allRules,pool,symbolizer);
	}
	
	private ParserTable _calc( Expression startSymbol, Rules allRules, final ExpressionPool pool, Symbolizer symbolizer ) {
	/*
	Step.1
	======	
		compute all rules reachable from the current start symbol,
		and store them into the "rules" field.
	*/
		rules = allRules.removeUnreachableRules(startSymbol,false);
		
	/*
	Step.3
	======	
		compute ParserTable[A,a] and store them into the "parserTable" field.
	*/
		// for each "X->rule"...
		for( Iterator itr=rules.iterateKeys(); itr.hasNext(); ) {
			final Expression nonTerminal = (Expression)itr.next();
			Rule[] rs = rules.getAll(nonTerminal);
			
			if( rs.length==1 )  {
				// if there is only one rule to expand, the only possible action is
				// to expand it (no matter what the input token is.
				table.addRule( nonTerminal, Expression.epsilon, rs[0] );
			} else {
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
				// then we need to add an entry to the rule
				for( int i=0; i<rs.length; i++ ) {
					if( rs[i].isEpsilonReducible() ) {
						// use epsilon as the special token.
						table.addRule( nonTerminal, Expression.epsilon, rs[i] );
						break;
					}
				}
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
