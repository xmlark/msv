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
 * run-away expressions are prohibited in both RELAX and TREX.
 */
public class RunAwayExpressionChecker implements ExpressionVisitorVoid
{
	/** number of ElementExp found */
	private int depth = 0;
	private final Map referenceDepth = new java.util.HashMap();
	private final Stack refStack = new Stack();
	private final Set testedExps = new java.util.HashSet();
	private final GrammarReader reader;
	
	public RunAwayExpressionChecker( GrammarReader reader ) { this.reader = reader; }
	
	public void onAttribute( AttributeExp exp )		{ exp.exp.visit(this); }
	public void onChoice( ChoiceExp exp )			{ binaryVisit(exp); }
	public void onOneOrMore( OneOrMoreExp exp )		{ exp.exp.visit(this); }
	public void onMixed( MixedExp exp )				{ exp.exp.visit(this); }
	public void onEpsilon()							{}
	public void onNullSet()							{}
	public void onAnyString()						{}
	public void onSequence( SequenceExp exp )		{ binaryVisit(exp); }
	public void onTypedString( TypedStringExp exp )	{}
	
	protected final void binaryVisit( BinaryExp exp )	{ exp.exp1.visit(this); exp.exp2.visit(this); }

	// TODO: make sure that the algorithm is correct.
	
	public void onRef( ReferenceExp exp )
	{
		if( testedExps.contains(exp) )
			// this expression is already tested. no need to test it again.
			return;
		
		Integer d = (Integer)referenceDepth.get(exp);
		if(d==null)
		{// this is the first visit.
			referenceDepth.put(exp, new Integer(depth));
			refStack.push(exp);
			exp.exp.visit(this);
			refStack.pop();
			testedExps.add(exp);	// this one was finished testing
			return;
		}
		
		if( d.intValue()==depth )
		{
			// this indicates that we have reached the same ReferenceExp
			// without visiting any ElementExp.
			// so this one is a run-away expression.
				
			// check stack to find actual sequence of reference.
			String s = "";
			int i = refStack.indexOf(exp);
			int sz = refStack.size();
			
			Locator[] locs = new Locator[sz-i];
			
			for( ; i<sz; i++ )
			{
				ReferenceExp e = (ReferenceExp)refStack.elementAt(i);
				s += e.name + " > ";
				locs[sz-i-1] = reader.getDeclaredLocationOf(e);
			}
			s += exp.name;
				
			reader.reportError( locs, GrammarReader.ERR_RUNAWAY_EXPRESSION, new Object[]{s} );
				
			testedExps.add(exp);	// mark it as tested
									// so that we don't report the same error again.
		}
		
		// this route is not recursive, but other routes may not.
		// so we cannot mark it as tested yet.
	}
	
	public void onElement( ElementExp exp )
	{
		depth++;
		exp.contentModel.visit(this);
		depth--;
	}
}
