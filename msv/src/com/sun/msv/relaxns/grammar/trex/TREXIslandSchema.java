/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.grammar.trex;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.SchemaProvider;
import org.iso_relax.dispatcher.Rule;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import com.sun.tranquilo.relaxns.verifier.IslandSchemaImpl;
import com.sun.tranquilo.relaxns.grammar.RuleImpl;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import java.util.Iterator;
import java.util.Map;

/**
 * IslandSchema implementation for TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXIslandSchema extends IslandSchemaImpl
{
	/** underlying TREX pattern which this IslandSchema is representing */
	protected final TREXGrammar grammar;
	
	public TREXIslandSchema( TREXGrammar grammar )
	{
		super( new TREXDocumentDeclaration( grammar ) );

		this.grammar = grammar;
		
		// export all named patterns.
		// TODO: modify to export only those element declarations.
		ReferenceExp[] refs = grammar.namedPatterns.getAll();
		for( int i=0; i<refs.length; i++ )
			rules.put( refs[i].name, new RuleImpl(refs[i]) );
	}
	
	public void bind( SchemaProvider provider, ErrorHandler handler )
	{
		Binder binder = new TREXBinder(provider,handler,docDecl.getPool());
		bind( grammar.namedPatterns, binder );
	}
	
	protected class TREXBinder
		extends Binder
		implements TREXPatternVisitorExpression
	{
		protected TREXBinder(SchemaProvider provider,ErrorHandler errorHandler,ExpressionPool pool)
		{
			super(provider,errorHandler,pool);
		}
		
		public Expression onInterleave( InterleavePattern exp )
		{
			return ((TREXPatternPool)pool).createInterleave(
				exp.exp1.visit(this), exp.exp2.visit(this) );
		}
		
		public Expression onConcur( ConcurPattern exp )
		{
			return ((TREXPatternPool)pool).createConcur(
				exp.exp1.visit(this), exp.exp2.visit(this) );
		}
	}
}