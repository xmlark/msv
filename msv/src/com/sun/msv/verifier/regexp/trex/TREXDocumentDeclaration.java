package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.Acceptor;
import com.sun.tranquilo.verifier.regexp.*;
import com.sun.tranquilo.verifier.regexp.REDocumentDeclaration;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionPool;
import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.grammar.trex.TREXGrammar;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import java.util.Map;

/**
 * Adaptor between abstract grammar model and verifier's grammar model.
 * 
 * Grammar object can be shared among multiple threads, but this object
 * cannot be shared.
 */
public final class TREXDocumentDeclaration extends REDocumentDeclaration
{
	protected final Expression topLevel;
	protected final TREXPatternPool pool;
	protected final boolean ignoreUndeclaredAttribute;
	
	public TREXDocumentDeclaration( TREXGrammar grammar )
	{
		this( grammar.start, grammar.pool, false );
	}
	
	public TREXDocumentDeclaration( TREXGrammar grammar, TREXPatternPool pool )
	{
		this( grammar.start, pool, false );
	}
	
	public TREXDocumentDeclaration( Expression topLevel, TREXPatternPool pool, boolean ignoreUndeclaredAttribute )
	{
		this.topLevel = topLevel;
		this.pool = pool;
		this.ignoreUndeclaredAttribute = ignoreUndeclaredAttribute;
		
		resCalc		= new TREXResidualCalculator(pool);
		attFeeder	= new TREXAttributeFeeder(this);
		attPruner	= new TREXAttributePruner(pool);
		cccec		= new TREXCombinedChildContentExpCreator(pool,attFeeder);
		ecc			= new TREXElementsOfConcernCollector();
	}
	
	
	/** map from ElementExp to REElementDeclaration */
//	private final Map elementDecls = new java.util.Hashtable();
	// hashtable is synchronized
	
	/** obtains TREXElementDeclaration object that is associated with given ElementExp */
//	TREXElementDeclaration getElementDecl( ElementExp exp )
//	{
//		TREXElementDeclaration r = (TREXElementDeclaration)elementDecls.get(exp);
//		if(r!=null)		return r;
//
//		// it is possible for two different threads to reach here at the same time.
//		// For that rare case, the following caution is necessary to prevent
//		// double registration.
//
//		synchronized(elementDecls)
//		{
//			r = (TREXElementDeclaration)elementDecls.get(exp);
//			if(r==null)
//			{
//				elementDecls.put( exp, r = new TREXElementDeclaration(this,exp) );
//				// mark attribute-free parts of the content model as so.
//				// this will speed up validation.
//				exp.contentModel.visit( TREXAttributeFreeMarker.theInstance );
//			}
//			return r;
//		}
//	}
	
	// for these function objects, one per a thread is enough.
	private final TREXResidualCalculator				resCalc;
	private final TREXCombinedChildContentExpCreator	cccec;
	private final TREXAttributeFeeder					attFeeder;
	private final TREXAttributePruner					attPruner;
	private final TREXElementsOfConcernCollector		ecc;
								  
	public ExpressionPool getPool() { return pool; }
	
	public ResidualCalculator getResidualCalculator()					{ return resCalc; }
	public CombinedChildContentExpCreator getCombinedChildContentExp()	{ return cccec; }
	public AttributeFeeder getAttributeFeeder()							{ return attFeeder; }
	public AttributePruner getAttributePruner()							{ return attPruner; }
	public ElementsOfConcernCollector getElementsOfConcernCollector()	{ return ecc; }
	public StringCareLevelCalculator getStringCareLevelCalculator()		{ return TREXStringCareLevelCalculator.theInstance; }
	public AttributeFreeMarker getAttributeFreeMarker()					{ return TREXAttributeFreeMarker.theInstance; }

	protected boolean getIgnoreUndeclaredAttribute() { return ignoreUndeclaredAttribute; }
	
	public Acceptor createAcceptor()
	{
		// top-level Acceptor cannot have continuation.
		return new SimpleAcceptor(this, topLevel, null, Expression.epsilon);
	}
}
