/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core.checker;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.relax.*;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.util.StringPair;
import java.util.Map;
import org.xml.sax.Locator;

/**
 * makes sure that no two AttributeExps have the same attribute name as their target.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DblAttrConstraintChecker implements RELAXExpressionVisitorVoid
{
	/** map of StringPairs to ReferenceExp(TagClause/AttPoolClause).
	 * 
	 * keys are names of visited AttributeExps, and its value is
	 * Clause object in which AttributeExp is declared. */
	private final Map atts = new java.util.HashMap();
	
	/** current clause. */
	private ReferenceExp current;
	
	public void check( TagClause clause, RELAXCoreReader reader )
	{
		atts.clear();
		current = clause;
		try
		{
			clause.visit(this);
		}
		catch( Eureka e )
		{
			reader.reportError(
				new Locator[]{reader.getDeclaredLocationOf(current),
							  reader.getDeclaredLocationOf((ReferenceExp)atts.get(e.name)) },
				reader.ERR_MULTIPLE_ATTRIBUTE_CONSTRAINT,
				new Object[]{ e.name.localName } );
		}
	}
	
	private static final class Eureka extends RuntimeException
	{
		final StringPair name;
		Eureka( StringPair an ) { name=an; }
	};
	
	public void onAttribute( AttributeExp exp )
	{
		if( exp.nameClass instanceof SimpleNameClass )
		{
			// this check is only appliable for those who constrains
			// one particular attribute.
			SimpleNameClass nc = (SimpleNameClass)exp.nameClass;
			StringPair p = new StringPair( nc.namespaceURI, nc.localName );
			
			if( atts.containsKey(p) )
				throw new Eureka(p);	// eureka! : find two AttributeExps that share the same name.
			atts.put(p,current);
		}
	}
	
	public void onAttPool( AttPoolClause exp )
	{
		ReferenceExp old = current;
		current = exp;
		exp.exp.visit(this);
		current = old;
	}
	public void onSequence( SequenceExp exp )	{ exp.exp1.visit(this);exp.exp2.visit(this); }
	public void onChoice( ChoiceExp exp )		{ exp.exp1.visit(this);exp.exp2.visit(this); }
	public void onEpsilon()							{;}
	public void onRef( ReferenceExp exp )			{;}
	
	// those methods should also never be called in case of RELAX.
	public void onElement( ElementExp exp )			{;}
	public void onOneOrMore( OneOrMoreExp exp )		{ exp.exp.visit(this); }
	public void onMixed( MixedExp exp )				{ exp.exp.visit(this); }
	public void onNullSet()							{;}
	public void onAnyString()						{;}
	public void onTypedString( TypedStringExp exp )	{;}
	public void onTag( TagClause exp )				{ exp.exp.visit(this); }
	public void onElementRules( ElementRules exp )	{ exp.exp.visit(this); }
	public void onHedgeRules( HedgeRules exp )		{ exp.exp.visit(this); }
	
	
}
