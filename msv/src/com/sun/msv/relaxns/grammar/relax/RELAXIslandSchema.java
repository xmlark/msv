package com.sun.tranquilo.relaxns.grammar.relax;

import org.iso_relax.dispatcher.Rule;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import com.sun.tranquilo.relaxns.verifier.IslandSchemaImpl;
import com.sun.tranquilo.relaxns.grammar.RuleImpl;
import com.sun.tranquilo.relaxns.grammar.ExternalElementExp;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import java.util.Set;
import java.util.Iterator;

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
		
		// elementRule is the only thing "officially" exported.
		ReferenceExp[] refs= module.elementRules.getAll();
		for( int i=0; i<refs.length; i++ )
			rules.put( refs[i].name, new RuleImpl(refs[i]) );
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
		
//		bind( module.attPools, binder );
//		bind( module.tags, binder );
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
			Rule[] rules = is.getRules();
			
			for( int j=0; j<rules.length; j++ )
				exp = module.pool.createChoice(exp,
					new ExternalElementExp(module.pool,namespace,rules[j].getName(),null));
		}
		
		return exp;
	}
}