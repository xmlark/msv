/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.grammar.relax;

import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import com.sun.tranquilo.relaxns.verifier.IslandSchemaImpl;
import com.sun.tranquilo.relaxns.grammar.DeclImpl;
import com.sun.tranquilo.relaxns.grammar.ExternalElementExp;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.grammar.relax.ElementRules;
import com.sun.tranquilo.grammar.relax.HedgeRules;
import com.sun.tranquilo.grammar.relax.AttPoolClause;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import java.util.Set;
import java.util.Iterator;

/**
 * IslandSchema implementation for RELXA module.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXIslandSchema extends IslandSchemaImpl
{
	/** underlying RELAX module which this IslandSchema is representing */
	protected final RELAXModule module;

	protected Set pendingAnyOtherElements;

	public RELAXIslandSchema( RELAXModule module, Set pendingAnyOtherElements )
	{
		super( new TREXDocumentDeclaration(module) );
		this.module = module;
		this.pendingAnyOtherElements = pendingAnyOtherElements;
		
		// export elementRules as ElementDecl
		ReferenceExp[] refs= module.elementRules.getAll();
		for( int i=0; i<refs.length; i++ )
			if( ((ElementRules)refs[i]).exported )
				elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
		
		// export hedgeRules as ElementDecl.
		// each exportable hedgeRule must be of length 1,
		// but it should have already checked.
		refs = module.hedgeRules.getAll();
		for( int i=0; i<refs.length; i++ )
			if ( ((HedgeRules)refs[i]).exported )
					elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
		
		// export attPools as AttributesDecl
		ExportedAttPoolGenerator expGen = new ExportedAttPoolGenerator( module.pool );
		refs = module.attPools.getAll();
		for( int i=0; i<refs.length; i++ )
			if( ((AttPoolClause)refs[i]).exported )
				attributesDecls.put( refs[i].name,
					new DeclImpl( refs[i].name, expGen.create(module,refs[i].exp) ) );
	}
	
	
	public void bind( SchemaProvider provider, ErrorHandler handler ) throws SAXException
	{
		{// wrap up anyOtherElements.
			Expression pseudoContentModel = createChoiceOfAllExportedRules(provider);
				
			Iterator itr = pendingAnyOtherElements.iterator();
			while( itr.hasNext() )
				((AnyOtherElementExp)itr.next()).wrapUp(module,pseudoContentModel,provider,handler);
			pendingAnyOtherElements = null;
		}
		
		Binder binder = new Binder(provider,handler,docDecl.getPool());
		bind( module.elementRules, binder );
		bind( module.hedgeRules, binder );
		bind( module.attPools, binder );
		bind( module.tags, binder );
	}
	
	/**
	 * creates a choice expression of all exported rules in the given provider.
	 * 
	 * this expression is used as a pseudo content model of anyOtherElement.
	 */
	private Expression createChoiceOfAllExportedRules( SchemaProvider provider ) {
		Expression exp = Expression.nullSet;
		
		Iterator itr = provider.iterateNamespace();
		while( itr.hasNext() ) {
			String namespace = (String)itr.next();
			IslandSchema is = provider.getSchemaByNamespace(namespace);
			ElementDecl[] rules = is.getElementDecls();
			
			for( int j=0; j<rules.length; j++ )
				exp = module.pool.createChoice(exp,
					new ExternalElementExp(module.pool,namespace,rules[j].getName(),null));
		}
		
		return exp;
	}
}