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
import com.sun.msv.util.StringPair;

/**
 * creates "combined child content expression" and gathers "elements of concern"
 * and its "attribute-pruned" content model.
 * 
 * Intuitively, "combined child content expression" is a set of 
 * content models of "elements of concern",
 * which is appropriately combined to express dependency between elements.
 * 
 * "Elements of concern" are ElementExps that are possibly applicable to
 * the next element. These gathered element declarations are then tested against
 * next XML element.
 * 
 * "Attribute-pruned" content model is a content model after consuming
 * AttributeTokens and removing unused AttributeExp nodes.
 * 
 * <p>
 * For example, when the current expression is
 * <PRE><XMP>    <!-- javadoc escape -->
 *   <choice>
 *     <concur>
 *       <element> ..(A).. </element>
 *       <group>
 *         <element> ..(B).. </element>
 *         ...
 *       </group>
 *     </concur>
 *     <group>
 *       <element> ..(C).. </element>
 *       ....
 *     </group>
 *   </choice>
 * </XMP></PRE>
 * 
 * then the combined child expression is
 * 
 * <PRE><XMP>
 *   <choice>
 *     <concur>
 *       ..(A').. 
 *       ..(B').. 
 *     </concur>
 *     ..(C').. 
 *   </choice>
 * </XMP></PRE>
 * 
 * and elements of concern and its attribute-pruned content models are
 * 
 * <XMP>
 * <element> ..(A).. </element>  ->   ..(A')..
 * <element> ..(B).. </element>  ->   ..(B')..
 * <element> ..(C).. </element>  ->   ..(C')..
 * </XMP>
 * 
 * (A'),(B'), and (C') are attribute-pruned content models of (A),(B), and (C)
 * respectively.
 * 
 * Note that combined child pattern contains only &lt;choice&gt; and &lt;concur&gt; as 
 * its grue (of course, except ..(A').. , ..(B').. , and ..(C').. ).
 * 
 * 
 * This function object also calculates "continuation", which is the residual
 * expression after eating elements of concern.
 * 
 * For example, say the expression is "(A|(B,C))?,D".
 * 
 * When EoC is B, then the continuation will be C,D.
 * When EoC is A, then the continuation will be D.
 * When EoC is D, then the continuation will be epsilon.
 * 
 * When there are multiple EoC, (say A and B), then
 * the continuation will be meaningless (because it depends on which EoC will
 * be accepted), and thus won't be used.
 * 
 * However, the implementator must be aware that it is possible for a
 * binary operator to have EoC on both branch and EoC is still unique.
 * The following expression is an example.
 * 
 * (A|B)*,C?,(A|B)*
 * 
 * when A is EoC, SequenceExp of (A|B)* and C?,(A|B)* has EoC on both branch.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CombinedChildContentExpCreator implements ExpressionVisitor
{
	protected final ExpressionPool	pool;
	protected final AttributeFeeder	feeder;

	// these variables are set each time 'get' method is called
	private StartTagInfoEx			tagInfo;
	private OwnerAndContent			result;
	private int						numElements;
	/** counts number of ElementExps that accepts given tag name. */
	private int						numTagMatch;
	private boolean					feedAttributes;
	private boolean					checkTagName;

	// TODO: do we gain some performance if we stop creating combined child content expression
	// (for RELAX, c.c.c.p is unnecessary)
	
	// TODO: how many object instanciation can we avoid
	// if we keep one local reusable copy of OwnerAndContent?
	
	public static class ExpressionPair
	{
		public final Expression		content;
		public final Expression		continuation;
		public ExpressionPair( Expression content, Expression continuation )
		{ this.content=content; this.continuation=continuation; }
	}
	public class OwnerAndContent
	{
		public final OwnerAndContent next;	// pointer as a linked list.
		public final ElementExp	owner;			// element of concern
		public final Expression		content;
		
		private OwnerAndContent( OwnerAndContent next, ElementExp owner, Expression content )
		{ this.next=next;this.owner=owner;this.content=content; }
	}
	
	protected CombinedChildContentExpCreator(
		ExpressionPool pool, AttributeFeeder feeder )
	{
		this.pool = pool;
		this.feeder = feeder;
	}

	/** computes a combined child content pattern, with error recovery
	 * 
	 * @param feedAttributes
	 *		if this flag is false, Attribute feeding & pruning are skipped and 
	 *		AttributeExps are fully remained in the resulting expression.
	 * @param checkTagName
	 *		if this flag is false, tag name check is skipped.
	 */
	public ExpressionPair get( Expression combinedPattern, StartTagInfoEx info,
		boolean feedAttributes, boolean checkTagName )
	{
		result = null;
		numElements = 0;
		numTagMatch = 0;
		return continueGet( combinedPattern, info, feedAttributes, checkTagName );
	}
	
	public final ExpressionPair continueGet( Expression combinedPattern, StartTagInfoEx info,
		boolean feedAttributes, boolean checkTagName )
	{
		this.tagInfo = info;
		this.feedAttributes = feedAttributes;
		this.checkTagName = checkTagName;
		ExpressionPair result = (ExpressionPair)combinedPattern.visit(this);
		if( numElements==1 )	return result;
		else					return new ExpressionPair(result.content,null);
		
		// when more than one element of concern is found,
		// continuation cannot be used.
	}
	
	/** computes a combined child content pattern and (,if possible,) its continuation. */
	public ExpressionPair get( Expression combinedPattern, StartTagInfoEx info )
	{
		StringPair sp=null;
		
		// cache
		if( combinedPattern.verifierTag!=null )
		{
			OptimizationTag ot = (OptimizationTag)combinedPattern.verifierTag;
			sp = new StringPair(info.namespaceURI,info.localName);
			OptimizationTag.OwnerAndCont cache = (OptimizationTag.OwnerAndCont)ot.transitions.get(sp);
			
			if(cache!=null)
			{// cache hit
				numElements = 1;
				result = new OwnerAndContent(
					null,cache.owner,
					feeder.feedAll(
						cache.owner.contentModel,
						info,
						cache.owner.ignoreUndeclaredAttributes) );
				return new ExpressionPair(result.content,cache.continuation);
			}
		}
		
		ExpressionPair r = (ExpressionPair)get( combinedPattern, info, true, true );
		
		if( numTagMatch==1 && numElements==1 )
		{// only one element matchs this tag name. cache this result
			OptimizationTag ot = (OptimizationTag)combinedPattern.verifierTag;
			if(ot==null)
				combinedPattern.verifierTag = ot = new OptimizationTag();
			
			if(sp==null)
				sp = new StringPair(info.namespaceURI,info.localName);
			
			ot.transitions.put( sp, new OptimizationTag.OwnerAndCont(result.owner,r.continuation) );
		}
		return r;
	}
	
	/**
	 * obtains elements of concern and their attribute-pruned content models.
	 * 
	 * This method should be called after calling get method. The result is
	 * in effect until next invocation of get method.
	 * Apparently this is a bad design, but this design gives us better performance.
	 */
	protected final OwnerAndContent getElementsOfConcern() {
		return result;
	}
	
	/** gets the number of elements of concern.
	 * 
	 * This method should be called after calling get method. The result is
	 * in effect until next invocation of get method.
	 * Apparently this is a bad design, but this design gives us better performance.
	 */
	protected final int numElementsOfConcern() { return numElements; }
	
	/**
	 * checks if the number of elements of concern is only one.
	 * If more than one elements of concern was found in the previous call to
	 * get method, this method returns null.
	 * Otherwise, returns the ElementExp, which is the only element of concern.
	 */
//	public final ElementExp isSingle()
//	{
//		if( result.next==null )		return result.owner;
//		else						return null;
//	}

	/**
	 * checks if the result of 'get' method is not the union of all
	 * elements of concern.
	 * 
	 * Within this class, combined child content expression is
	 * always the union of all elements of concern. However, some derived
	 * class does not guarantee this property.
	 * 
	 * @return
	 *		true if the combined child content expression is not
	 *			the union of all elements of concern.
	 *		false if otherwise.
	 */
	public boolean isComplex() { return false; }

	
	private static final ExpressionPair nullPair = new ExpressionPair(Expression.nullSet,Expression.nullSet);

	
	
	public Object onAttribute( AttributeExp exp )	{ return nullPair; }
	
	public Object onChoice( ChoiceExp exp )
	{
		ExpressionPair p1 = (ExpressionPair)exp.exp1.visit(this);
		ExpressionPair p2 = (ExpressionPair)exp.exp2.visit(this);
		
		return new ExpressionPair(
			pool.createChoice(p1.content, p2.content ),
			pool.createChoice(p1.continuation,p2.continuation));
	}
	public Object onElement( ElementExp exp )
	{
		// TODO: may check result and remove duplicate result
		
		// if tag name is invalid, then remove this element from candidate.
		if(checkTagName && !exp.getNameClass().accepts(tagInfo.namespaceURI,tagInfo.localName))
			return nullPair;
		
		// check result and see if the same element is already registered.
		// this will reduce the complexity of the result.
		// some RELAX grammar may contain something like
		// (A|B)* C? (A|B)* to implement interleaving of (A|B)* and C.
		// this check becomes important for cases like this.
		for( OwnerAndContent o=result; o!=null; o=o.next )
			if(o.owner==exp)	// the same element is found.
				return new ExpressionPair(o.content,Expression.epsilon);
		
		// also, feeding and pruning attributes are relatively expensive operation.
		// so this is the good place to check other redundancy.
		
		
		Expression prunedContentModel;
		numTagMatch++;
		
		if( feedAttributes )
		{// feed and prune attributes
			prunedContentModel = feeder.feedAll(
				exp.contentModel,
				tagInfo,
				exp.ignoreUndeclaredAttributes);
			if( prunedContentModel==Expression.nullSet )
				return nullPair;	// this content model didn't accept attributes 
		}
		else
		{
			prunedContentModel = exp.contentModel;
		}
		
		numElements++;
		
		// create a new result object
		result = new OwnerAndContent(result,exp,prunedContentModel);
		
		return new ExpressionPair(prunedContentModel,Expression.epsilon);	// content model is simply copied. (not recurisively processed)
	}
	public Object onOneOrMore( OneOrMoreExp exp )
	{
		ExpressionPair p = (ExpressionPair)exp.exp.visit(this);
		return new ExpressionPair(
			p.content,
			// in this class, we are interested only in the content model of child elements.
			// therefore, repeatable parts can be simply ignored.
	
			// continuation of (AB)+ after A will be B,(AB)*
			pool.createSequence( p.continuation, pool.createZeroOrMore(exp.exp) )
		);
	}
	public Object onMixed( MixedExp exp )
	{
		ExpressionPair p = (ExpressionPair)exp.exp.visit(this);
		return new ExpressionPair(
			p.content,
		// for the same reason as above, <mixed> part can be also ignorable.
	
			pool.createMixed(p.continuation)
		);
	}
	public Object onEpsilon()		{ return nullPair; }
	public Object onNullSet()		{ return nullPair; }
	public Object onAnyString()		{ return nullPair; }
	public Object onRef( ReferenceExp exp )
	{
		return exp.exp.visit(this);
	}
	public Object onSequence( SequenceExp exp )
	{
		ExpressionPair p1 = (ExpressionPair)exp.exp1.visit(this);
		
		if(exp.exp1.isEpsilonReducible())
		{
			ExpressionPair p2 = (ExpressionPair)exp.exp2.visit(this);
			if( p2.content!=Expression.nullSet )
			{
				if(p1.content==Expression.nullSet)		return p2;

				// now, we have candidates in both left and right.
				// say,
				// exp = (A,X),(A,Y) and AX can be nullable.
				// continuation will be (X,A,Y)|Y.
				return new ExpressionPair( 
					pool.createChoice( p1.content, p2.content ),
					
					pool.createChoice(
						pool.createSequence(p1.continuation, exp.exp2 ),
						p2.continuation ) );
			}
		}

		return new ExpressionPair( p1.content,
			pool.createSequence( p1.continuation, exp.exp2 ) );
			
	}
	public Object onTypedString( TypedStringExp exp )	{ return nullPair; }
}
