/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import org.xml.sax.Attributes;

/**
 * Feeds AttributeToken to the expression and obtains the residual (content model).
 * 
 * AttributeTokens are fed in order-less fashion.
 */
public class AttributeFeeder implements ExpressionVisitorExpression
{
	protected final REDocumentDeclaration	docDecl;
	protected final ExpressionPool			pool;
	/**
	 * a flag that enables RELAX semantics of attribute treatment.
	 * 
	 * When this flag is set, those Attributes which are not defined
	 * in the schema are allowed without any error.
	 */
	private final boolean					ignoreUndeclaredAttribute;
	
	private final AttributeFreeMarker		marker;
	
	private Token							token;
		
	protected AttributeFeeder( REDocumentDeclaration docDecl )
	{
		this.docDecl	= docDecl;
		this.pool		= docDecl.getPool();
		this.ignoreUndeclaredAttribute	= docDecl.getIgnoreUndeclaredAttribute();
		this.marker		= docDecl.getAttributeFreeMarker();
	}

	/** computes a residual without any attribute nodes,
	 * after feeding all attributes and pruning all unused attributes
	 */
	public final Expression feedAll( Expression exp, StartTagInfoEx tagInfo )
	{
//		Object o = exp.verifierTag;
//		if( o==null )
//			exp.verifierTag = o = new OptimizationTag();
		
//		if( ((OptimizationTag)o).isAttributeFree==null )	marker.mark(exp);
		
		// feed attributes and obtain content model without attribute nodes.
		final int len = tagInfo.attTokens.length;
		for( int i=0; i<len; i++ )
		{
			exp = feed( exp, tagInfo.attTokens[i] );
			// fails to consume an attribute
			if(exp==Expression.nullSet)	return Expression.nullSet;
		}
		
		// prune unused attributes
		return docDecl.getAttributePruner().prune(exp);
	}
	
	public final Expression feed( Expression exp, AttributeToken token )
	{
		this.token = token;
		Expression r = exp.visit(this);
		
		if(r!=Expression.nullSet || !ignoreUndeclaredAttribute)	return r;
		
		// if ignoreUndeclaredAttribute==true and expression is nullSet,
		// we have to check which of the following is the case.
		//   (1) attribute is undefined
		//   (2) value of the attribute was rejected.
		
		this.token = token.createRecoveryAttToken();
		r = exp.visit(this);
		
		// if wild card token is rejected, then it must be the absence of declaration.
		if(r==Expression.nullSet)	return exp;
		// otherwise the value was wrong.
		return Expression.nullSet;
		
//			if( com.sun.tranquilo.driver.textui.Debug.debug )
//				System.out.println("after feeding "+atts.getQName(i)+" attribute");
//				System.out.println(com.sun.tranquilo.grammar.trex.util.TREXPatternPrinter.printContentModel(exp));
	}
	

	public Expression onAttribute( AttributeExp exp )
	{
		if( token.match(exp) )	return Expression.epsilon;
		else					return Expression.nullSet;
	}
	
	/**
	 * checks if the given expression is attribute-free.
	 * 
	 * if a expression is attribute free, then the residual must be nullSet.
	 */
	protected final boolean isAttributeFree( Expression exp )
	{
		Object o = exp.verifierTag;
		return o!=null && ((OptimizationTag)o).isAttributeFree==Boolean.TRUE;
	}
	
	public Expression onChoice( ChoiceExp exp )
	{
		if( isAttributeFree(exp) )	return Expression.nullSet;
		return pool.createChoice( exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	public Expression onElement( ElementExp exp )
	{
		return Expression.nullSet;
	}
	public Expression onOneOrMore( OneOrMoreExp exp )
	{
		if( isAttributeFree(exp) )	return Expression.nullSet;
		return pool.createSequence(
			exp.exp.visit(this),
			pool.createZeroOrMore(exp.exp) );
	}
	public Expression onMixed( MixedExp exp )
	{
		return pool.createMixed( exp.exp.visit(this) );
	}
	public Expression onEpsilon()		{ return Expression.nullSet; }
	public Expression onNullSet()		{ return Expression.nullSet; }
	public Expression onAnyString()		{ return Expression.nullSet; }
	public Expression onRef( ReferenceExp exp )
	{
		return exp.exp.visit(this);
	}
	public Expression onSequence( SequenceExp exp )
	{
		if( isAttributeFree(exp) )	return Expression.nullSet;
		// for attributes only, sequence acts as orderless
		return pool.createChoice(
			pool.createSequence( exp.exp1.visit(this), exp.exp2 ),
			pool.createSequence( exp.exp1, exp.exp2.visit(this) ) );
	}
	public Expression onTypedString( TypedStringExp exp )
	{
		return Expression.nullSet;
	}
}
