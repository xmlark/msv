/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.*;
import com.sun.msv.verifier.Acceptor;

/**
 * calculates how character literals should be treated.
 * 
 * This class is thread-safe: multiple threads can simultaneously
 * access the same instance. Note that there is no guarantee that the
 * derived class is thread-safe.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringCareLevelCalculator implements ExpressionVisitorBoolean {
	
	protected StringCareLevelCalculator(){}
	
	/** singleton instance. */
	protected static final StringCareLevelCalculator theInstance = new StringCareLevelCalculator();
	
	// those expressions which are sensitive about string must return true
	public boolean onSequence( SequenceExp exp )		{ return exp.exp1.visit(this)||exp.exp2.visit(this); }
	public boolean onInterleave( InterleaveExp exp )	{ return exp.exp1.visit(this)||exp.exp2.visit(this); }
	public boolean onConcur( ConcurExp exp )			{ return exp.exp1.visit(this)||exp.exp2.visit(this); }
	public boolean onChoice( ChoiceExp exp )			{ return exp.exp1.visit(this)||exp.exp2.visit(this); }
	public boolean onAttribute( AttributeExp exp )		{ return false; }
	public boolean onElement( ElementExp exp )			{ return false; }
	public boolean onOneOrMore( OneOrMoreExp exp )		{ return exp.exp.visit(this); }
	public boolean onMixed( MixedExp exp )				{ return true; }
	public boolean onList( ListExp exp )				{ return true; }
	public boolean onRef( ReferenceExp exp )			{ return exp.exp.visit(this); }
	public boolean onOther( OtherExp exp )				{ return exp.exp.visit(this); }
	public boolean onEpsilon()							{ return false; }
	public boolean onNullSet()							{ return false; }
	public boolean onAnyString()						{ return true; }
	public boolean onData( DataExp exp )				{ return true; }
	public boolean onValue( ValueExp exp )				{ return true; }

	public static int calc( Expression exp )
	{
		// if and only if the top-level element is mixed,
		// it can ignores strings.
		if( exp instanceof MixedExp )	return Acceptor.STRING_IGNORE;
		
		if( exp.visit(theInstance) )
			// somebody claims that string is necessary.
			return Acceptor.STRING_STRICT;
		else
			// nobody claims that string is necessary.
			return Acceptor.STRING_PROHIBITED;
	}
}
