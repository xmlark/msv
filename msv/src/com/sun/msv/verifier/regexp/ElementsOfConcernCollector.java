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
import java.util.Collection;

/**
 * Collects "elements of concern".
 * 
 * "Elements of concern" are ElementExps that are possibly applicable to
 * the next element. These gathered element declarations are then tested against
 * next XML element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementsOfConcernCollector implements ExpressionVisitor
{
	private Collection result;
	
	public ElementsOfConcernCollector() {}
	
	public final void collect( Expression exp, Collection result )
	{
		this.result = result;
		exp.visit(this);
	}
	
	public final Object onAttribute( AttributeExp exp )	{ return null; }
	
	public final Object onChoice( ChoiceExp exp )
	{
		exp.exp1.visit(this);
		exp.exp2.visit(this);
		return null;
	}
	
	public final Object onElement( ElementExp exp )
	{
		// found.
		result.add( exp );
		return null;
	}
	
	public final Object onOneOrMore( OneOrMoreExp exp )
	{
		exp.exp.visit(this);
		return null;
	}
	
	public final Object onMixed( MixedExp exp )
	{
		exp.exp.visit(this);
		return null;
	}
	
	public final Object onEpsilon()		{ return null; }
	public final Object onNullSet()		{ return null; }
	public final Object onAnyString()	{ return null; }
	public final Object onTypedString( TypedStringExp exp )	{ return null; }
	public final Object onList( ListExp exp )	{ return null; }
	public final Object onKey( KeyExp exp )	{ return null; }
	
	public final Object onRef( ReferenceExp exp ) {
		return exp.exp.visit(this);
	}
	
	public final Object onOther( OtherExp exp ) {
		return exp.exp.visit(this);
	}
	
	public final Object onSequence( SequenceExp exp )
	{
		exp.exp1.visit(this);
		if(exp.exp1.isEpsilonReducible())
			exp.exp2.visit(this);
		return null;
	}

	public final Object onConcur( ConcurExp exp )
	{
		exp.exp1.visit(this);
		exp.exp2.visit(this);
		return null;
	}
	
	public final Object onInterleave( InterleaveExp exp )
	{
		exp.exp1.visit(this);
		exp.exp2.visit(this);
		return null;
	}
	
}
