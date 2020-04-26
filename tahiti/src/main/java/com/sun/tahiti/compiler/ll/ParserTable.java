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

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import com.sun.msv.grammar.Expression;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.compiler.Symbolizer;

/**
 * parser table.
 * 
 * A parser table is conceptually a two dimensional array, whose cell is a
 * set of Rule objects.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ParserTable
{
	/**
	 * actual storage.
	 * type:  Non-terminal -> ( terminal -> set(Rule) )
	 */
	private final Map impl = new HashMap();
	
	/** add a new rule. */
	public void addRule( Expression nonTerminal, Expression terminal, Rule rule ) {
		Set s = getOrCreateRuleSet(nonTerminal,terminal);
		s.add(rule);
	}
	
	/** add a new rule. */
	public void addRules( Expression nonTerminal, Set terminals, Rule rule ) {
		// TODO: could be better implemented
		Iterator itr = terminals.iterator();
		while( itr.hasNext() )
			getOrCreateRuleSet( nonTerminal, (Expression)itr.next() ).add(rule);
	}
	
	private Set getOrCreateRuleSet( Expression nonTerminal, Expression terminal ) {
		Map m = (Map)impl.get(nonTerminal);
		if(m==null)
			impl.put(nonTerminal,m=new HashMap());
		Set s = (Set)m.get(terminal);
		if(s==null)
			m.put(terminal,s=new HashSet());
		return s;
	}
	
	/**
	 * writes the contents of the table as XML.
	 */
	public void write( XMLWriter writer, Symbolizer symbolizer ) {
		writer.start("parserTable");
		int no = 0;
		
		Iterator itr = impl.keySet().iterator();
		while( itr.hasNext() ) {
			Expression nonTerm = (Expression)itr.next();
			Map t2r = (Map)impl.get(nonTerm);
			
			writer.start("action",
				new String[]{"stackTop",symbolizer.getId(nonTerm)});
			
			Rule defaultAction=null;
			{
				Set defaultActionSet = (Set)t2r.get(Expression.epsilon);
				if(defaultActionSet!=null) {
					if(defaultActionSet.size()>1)
						// only one default action is possible
						throw new Error();
					if(defaultActionSet.size()==1)
						defaultAction = (Rule)defaultActionSet.toArray()[0];
				}
			}
			
			
			Iterator jtr = t2r.keySet().iterator();
			while( jtr.hasNext() ) {
				Expression term = (Expression)jtr.next();
				// applicable rules.
				Rule[] rules = (Rule[])((Set)t2r.get(term)).toArray(new Rule[0]);
				
				if( rules.length==1 && rules[0]==defaultAction && term!=Expression.epsilon )
					continue;	// this token statement can be absorbed by the "otherwise" clause.
				
				if( term!=Expression.epsilon )
					writer.start( "token",
						new String[]{
							"id",	symbolizer.getId(term),
							"no",	Integer.toString(no++)});
				else
					writer.start( "otherwise" );
				
					
				
				for( int i=0; i<rules.length; i++ ) {
					writer.element("rule",
						new String[]{"no",symbolizer.getId(rules[i])});
				}
					
				if( term!=Expression.epsilon )
					writer.end("token");
				else
					writer.end("otherwise");
			}
			
			writer.end("action");
		}

		writer.end("parserTable");
	}
}
