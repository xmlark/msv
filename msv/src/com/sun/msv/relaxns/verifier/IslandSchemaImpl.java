package com.sun.tranquilo.relaxns.verifier;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.SchemaProvider;
import org.iso_relax.dispatcher.Rule;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.tranquilo.relaxns.grammar.RuleImpl;
import com.sun.tranquilo.relaxns.grammar.ExternalElementExp;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public abstract class IslandSchemaImpl implements IslandSchema
{
	/** map from name to RuleImpl. */
	protected final Map rules = new java.util.HashMap();
	
	/** VGM to be used to create IslandVerifier. */
	protected final TREXDocumentDeclaration docDecl;
	
	protected IslandSchemaImpl( TREXDocumentDeclaration docDecl )
	{
		this.docDecl = docDecl;
	}
	
	public IslandVerifier createNewVerifier( String namespace, Rule[] rules )
	{
		RuleImpl[] ri = new RuleImpl[rules.length];
		System.arraycopy( rules,0, ri,0, rules.length );
		
		return new IslandVerifierImpl(
			new RulesAcceptor( docDecl, ri ) );
	}
	
	public Rule getRuleByName( String name )	{ return (Rule)rules.get(name); }
	public Iterator iterateRules()				{ return rules.values().iterator(); }
	public Rule[] getRules()
	{
		Rule[] r = new RuleImpl[rules.size()];
		rules.values().toArray(r);
		return r;
	}
	
	
	protected void bind( ReferenceContainer con, Binder binder )
	{
		ReferenceExp[] exps = con.getAll();
		for( int i=0; i<exps.length; i++ )
			exps[i].exp = exps[i].exp.visit(binder);
	}
	
	public static class Binder
		extends ExpressionCloner
	{
		protected final SchemaProvider provider;
		protected final ErrorHandler errorHandler;
		private final Set boundElements = new java.util.HashSet();
		
		public Binder(SchemaProvider provider,ErrorHandler errorHandler,ExpressionPool pool)
		{
			super(pool);
			this.provider=provider;
			this.errorHandler=errorHandler;
		}
		
		public Expression onAttribute(AttributeExp exp) { return exp; }
		public Expression onRef(ReferenceExp exp) { return exp; }
		public Expression onElement(ElementExp exp) {
			try {
				if(!(exp instanceof ExternalElementExp)) {
					// avoid visiting the same element twice to prevent infinite recursion.
					if( boundElements.contains(exp) )	return exp;
					boundElements.add(exp);
					
					// bind content model
					exp.contentModel = exp.contentModel.visit(this);
					return exp;
				}
			
				ExternalElementExp eexp = (ExternalElementExp)exp;
				IslandSchema is = provider.getSchemaByNamespace(eexp.namespaceURI);
				if(is==null)
				{
					errorHandler.error( new SAXParseException(
						Localizer.localize(Localizer.ERR_UNDEFINED_NAMESPACE, eexp.namespaceURI),
						eexp.source) );
					return exp;
				}
				eexp.rule = is.getRuleByName(eexp.ruleName);
				if(eexp.rule==null)
				{
					errorHandler.error( new SAXParseException(
						Localizer.localize(Localizer.ERR_UNEXPORTED_RULE, eexp.ruleName),
						eexp.source) );
					return exp;
				}
				if(eexp.rule instanceof RuleImpl)
				{
					// if this rule is from our own implementation,
					// we can bind "directly" so that we don't have to switch the island.
					return ((RuleImpl)eexp.rule).exp;
				}
			
				// all set.
				return exp;
			} catch( SAXException e ) {
				// ignore the exception
				return exp;
			}
		}
	}
}
