/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp.trex;

import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.regexp.*;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.trex.TREXPatternPool;
import java.util.Map;

/**
 * Adaptor between abstract grammar model and verifier's grammar model.
 * 
 * Grammar object can be shared among multiple threads, but this object
 * cannot be shared.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class TREXDocumentDeclaration extends REDocumentDeclaration
{
	protected final Expression topLevel;
	protected final TREXPatternPool pool;
	
	public TREXDocumentDeclaration( Grammar grammar ) {
		this( grammar.getTopLevel(), (TREXPatternPool)grammar.getPool() );
	}
	
	public TREXDocumentDeclaration( Expression topLevel, TREXPatternPool pool ) {
		this.topLevel = topLevel;
		this.pool = pool;
		
		resCalc		= new TREXResidualCalculator(pool);
		attFeeder	= new TREXAttributeFeeder(this);
		attPicker	= new TREXAttributePicker(this);
		attPruner	= new TREXAttributePruner(pool);
		attRemover	= new TREXAttributeRemover(pool);
		cccec		= new TREXCombinedChildContentExpCreator(pool,attFeeder);
		ecc			= new TREXElementsOfConcernCollector();
	}
	
	
	
	// for these function objects, one per a thread is enough.
	private final TREXResidualCalculator				resCalc;
	private final TREXCombinedChildContentExpCreator	cccec;
	private final TREXAttributeFeeder					attFeeder;
	private final TREXAttributePruner					attPruner;
	private final TREXAttributePicker					attPicker;
	private final TREXAttributeRemover					attRemover;
	private final TREXElementsOfConcernCollector		ecc;
								  
	public ExpressionPool getPool() { return pool; }
	
	public ResidualCalculator getResidualCalculator()					{ return resCalc; }
	public CombinedChildContentExpCreator getCombinedChildContentExp()	{ return cccec; }
	public AttributeFeeder getAttributeFeeder()							{ return attFeeder; }
	public AttributePruner getAttributePruner()							{ return attPruner; }
	public AttributeRemover getAttributeRemover()						{ return attRemover; }
	public AttributePicker getAttributePicker()							{ return attPicker; }
	public ElementsOfConcernCollector getElementsOfConcernCollector()	{ return ecc; }
	public StringCareLevelCalculator getStringCareLevelCalculator()		{ return TREXStringCareLevelCalculator.theInstance; }
	public AttributeFreeMarker getAttributeFreeMarker()					{ return TREXAttributeFreeMarker.theInstance; }

	public Acceptor createAcceptor()
	{
		// top-level Acceptor cannot have continuation.
		return new SimpleAcceptor(this, topLevel, null, Expression.epsilon);
	}
}
