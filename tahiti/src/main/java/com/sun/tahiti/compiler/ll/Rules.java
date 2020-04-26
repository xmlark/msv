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
import com.sun.msv.grammar.NameClassAndExpression;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

/**
 * a set of rules.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class Rules {
	
	/**
	 * set of all distinctive rules.
	 * This map can possibly contain unreachable object.
	 */
	private final Map distinctiveRules = new java.util.HashMap();
	
	/**
	 * a map from left side symbol (Object) to the set of Rules.
	 */
	private final Map rules = new java.util.HashMap();
	
	/** gets a Set that stores the values associated with the specified key. */
	public Set get( Expression symbol ) {
		Set s = (Set)rules.get(symbol);
		if(s==null)
			rules.put(symbol,s=new java.util.HashSet());
		return s;
	}
	
	/** gets all values associated with the specified symbol. */
	public Rule[] getAll( Expression symbol ) {
		return (Rule[])get(symbol).toArray(new Rule[0]);
	}
	
	/** returns true if this container has a rule that expands the specified symbol
	 */
	public boolean contains( Expression symbol ) {
		Set s = (Set)rules.get(symbol);
		if(s==null)		return false;
		return s.size()!=0;
	}
	
	/** iterates non-terminal symbols in this rules. */
	public Iterator iterateKeys() {
		return rules.keySet().iterator();
	}
	
	
	Rule unifyRule( Rule r ) {
		Rule unified = (Rule)distinctiveRules.get(r);
		if(unified==null)
			distinctiveRules.put( unified=r, r );
		return unified;
	}
	
	/** adds a new rule. */
	public void add( Expression left, Rule right ) {
		get(left).add(unifyRule(right));
	}
	
	/** adds a new rule. */
	public void add( Expression left, Expression[] right, boolean isInterleave ) {
		add(left, new Rule(right,isInterleave));
	}
	
	/** adds a new rule. */
	public void add( Expression left, Expression right ) {
		add(left, new Expression[]{right}, false );
	}
	
	/** adds all rules. */
	public void addAll( Expression left, Rule[] rules ) {
		for( int i=0; i<rules.length; i++ )
			add( left, rules[i] );
	}
	
	/** merges redundant rules and makes the rule set smaller. */
	public void intern() {
		final Map distinctRules = new java.util.HashMap();
		final Set temp = new java.util.HashSet();
		Iterator itr = iterateKeys();
		while( itr.hasNext() ) {
			
			Set s = get( (Expression)itr.next() );
			temp.clear();
			Iterator jtr = s.iterator();
			while( jtr.hasNext() ) {
				// unify 'r'
				Rule r = (Rule)jtr.next();
				Rule unified = (Rule)distinctiveRules.get(r);
				if(unified==null) {
					distinctiveRules.put(r,r);
					unified = r;
				}
				
				temp.add(unified);
			}
			
			// replace the contents of 's' by the unified rules.
			s.clear();
			s.addAll(temp);
		}
	}
	
	/**
	 * computes a new rule set by removing unreachable rules from the rule set.
	 * 
	 * <p>
	 * A rule which is unreachable from the specified <code>rootSymbol</code>
	 * will be removed.
	 * 
	 * @param	recurseElement
	 *		<p>
	 *		if true, this method recursively visits element and attribute and
	 *		discover all reachable rules. This is useful for global optimization.
	 *		
	 *		<p>
	 *		if set to false, this method treats element and attribute as terminal
	 *		symbols. This is useful to extract per-element/per-attribute rules.
	 */
	public Rules removeUnreachableRules( Expression rootSymbol, boolean recurseElement ) {
		Rules result = new Rules();
		
		Set workQueue = new java.util.HashSet();
		workQueue.add(rootSymbol);
		while(!workQueue.isEmpty()) {
			// get the first one in the queue.
			Expression symbol = (Expression)workQueue.iterator().next();
			workQueue.remove(symbol);
				
			Rule[] rs = this.getAll(symbol);
			assert(rs!=null && rs.length!=0);
				
			result.addAll(symbol,rs);
				
			for( int i=0; i<rs.length; i++ ) {
				for( int j=0; j<rs[i].right.length; j++ ) {
					Expression e = rs[i].right[j];
					if(!Util.isTerminalSymbol(e) && !result.contains(e)) {
						if(!recurseElement && (e instanceof NameClassAndExpression) )
							// if this is ElementExp or AttributeExp and
							// we don't want to recursively visit them.
							continue;
						
						// recursively add rules reachable from this rule
						workQueue.add(e);
						assert( e!=null );
					}
				}
			}
		}
		return result;
	}
}
