package com.sun.tranquilo.relaxns.verifier;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;
import com.sun.tranquilo.verifier.Acceptor;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.relaxns.grammar.RuleImpl;
import com.sun.tranquilo.util.StringRef;
import com.sun.tranquilo.util.DataTypeRef;
import org.iso_relax.dispatcher.Rule;

public class RulesAcceptor
	extends com.sun.tranquilo.verifier.regexp.trex.ComplexAcceptorBaseImpl
{
	protected final RuleImpl[]		owners;
	
	/** helper function for constructor */
	private static Expression createCombined( ExpressionPool pool, RuleImpl[] rules )
	{
		Expression exp = Expression.nullSet;
		for( int i=0; i<rules.length; i++ )
			exp = pool.createChoice( exp, rules[i].exp );
		return exp;
	}
	
	/** helper function for constructor */
	private static Expression[] getContents( RuleImpl[] rules )
	{
		Expression[] r = new Expression[rules.length];
		for( int i=0; i<rules.length; i++ )
			r[i] = rules[i].exp;
		return r;
	}
	
	public RulesAcceptor(
		TREXDocumentDeclaration docDecl,
		RuleImpl[] rules )
	{
		super( docDecl, createCombined(docDecl.getPool(),rules), getContents(rules) );
		owners = rules;
	}
	
	/**
	 * collects satisfied RuleImpls.
	 * 
	 * @see com.sun.tranquilo.verifier.regexp.trex.ComplexAcceptor#getSatisfiedOwners
	 */
	Rule[] getSatisfiedRules()
	{
		int cnt=0;
		for( int i=0; i<owners.length; i++ )
			if( contents[i].isEpsilonReducible() )
				cnt++;
		
		Rule[] r = new RuleImpl[cnt];
		cnt=0;
		for( int i=0; i<owners.length; i++ )
			if( contents[i].isEpsilonReducible() )
				r[cnt++] = owners[i];
		
		return r;
	}
}
