/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.*;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.xml.sax.Locator;

/**
 * makes sure that the expression does not run away.
 * 
 * "run-away" expressions are expressions like this.
 * 
 * &lt;hedgeRule label="foo" /&gt;
 *   &lt;hedgeRef label="foo" /&gt;
 * &lt;/hedgeRule&gt;
 * 
 * Apparently, those expressions cannot be expressed in string regular expression.
 * Therefore run-away expressions are prohibited in both RELAX and TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RunAwayExpressionChecker implements ExpressionVisitorVoid
{
	/** this exception is thrown to abort check when a error is found. */
	protected static final RuntimeException eureka = new RuntimeException();
		
	/** set of ElementExps which are already confirmed as being not a run-away exp. */
	private final Set testedExps = new java.util.HashSet();
	
	/** Expressions which are used as the content model of current element. */
	private Set contentModel = new java.util.HashSet();
	
	/** visited ReferenceExp.
	 * this information is useful for the user to figure out where did they make a mistake.
	 */
	private Stack refStack = new Stack();
	
	private final GrammarReader reader;
	
	protected RunAwayExpressionChecker( GrammarReader reader ) { this.reader = reader; }
	
	public static void check( GrammarReader reader, Expression exp ) {
		try {
			exp.visit( new RunAwayExpressionChecker(reader) );
		} catch( RuntimeException e ) {
			if(e!=eureka)	throw e;
		}
	}
	
	public void onAttribute( AttributeExp exp ) {
		enter(exp);
		exp.exp.visit(this);
		leave(exp);
	}
	public void onChoice( ChoiceExp exp )			{ binaryVisit(exp); }
	public void onOneOrMore( OneOrMoreExp exp )		{ unaryVisit(exp); }
	public void onMixed( MixedExp exp )				{ unaryVisit(exp); }
	public void onEpsilon()							{}
	public void onNullSet()							{}
	public void onAnyString()						{}
	public void onSequence( SequenceExp exp )		{ binaryVisit(exp); }
	public void onTypedString( TypedStringExp exp )	{}
	
	protected final void binaryVisit( BinaryExp exp ) {
		enter(exp);
		exp.exp1.visit(this);
		exp.exp2.visit(this);
		leave(exp);
	}
	protected final void unaryVisit( UnaryExp exp ) {
		enter(exp);
		exp.exp.visit(this);
		leave(exp);
	}
	
	private void enter( Expression exp ) {
		if(contentModel.contains(exp)) {
			// this indicates that we have reached the same expression object
			// without visiting any ElementExp.
			// so this one is a run-away expression.
				
			// check stack to find actual sequence of reference.
			String s = "";
			int i = refStack.indexOf(exp);
			int sz = refStack.size();
			
			Locator[] locs = new Locator[sz-i];
			
			for( ; i<sz; i++ ) {
				ReferenceExp e = (ReferenceExp)refStack.elementAt(i);
				s += e.name;
				if( i!=sz-1 )  s+= " > ";
				locs[sz-i-1] = reader.getDeclaredLocationOf(e);
			}
				
			reader.reportError( locs, GrammarReader.ERR_RUNAWAY_EXPRESSION, new Object[]{s} );
			
			// abort further run-away check.
			// usually, run-away expression error occurs by use of hedgeRules,
			// and those rules tend to be shared among multiple elementRules.
			
			// So it is highly likely that further check will generate too many errors.
			throw eureka;
		}
		contentModel.add(exp);
	}
	private void leave( Expression exp ) {
		contentModel.remove(exp);
	}

	public void onRef( ReferenceExp exp ) {
		refStack.push(exp);
		enter(exp);
		exp.exp.visit(this);
		leave(exp);
		refStack.pop();
	}
	
	public void onElement( ElementExp exp )
	{
		if( testedExps.contains(exp) )
			// this expression is already tested. no need to test it again.
			return;
		
		testedExps.add(exp);	// add it first to prevent infinite recursion.
		
		// restore the current basket, and use a fresh one to check this element.
		Set previousContentModel = contentModel;
		Stack previousRefStack = refStack;
		contentModel = new java.util.HashSet();
		refStack = new java.util.Stack();
		
		exp.contentModel.visit(this);
		
		contentModel = previousContentModel;
		refStack = previousRefStack;
	}
}
