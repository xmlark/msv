/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader;

import java.util.Iterator;
import org.iso_relax.dispatcher.IslandSchema;
import org.xml.sax.ErrorHandler;
import com.sun.msv.relaxns.verifier.SchemaProviderImpl;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;
import com.sun.msv.relaxns.grammar.relax.RELAXIslandSchema;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.reader.State;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.util.GrammarReaderControllerAdaptor;
import com.sun.msv.grammar.Expression;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used only one when starting parsing a RELAX schema.
 * For included module/grammar, different states are used.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootGrammarState extends SimpleState implements ExpressionOwner
{
	protected State createChildState( StartTagInfo tag ) {
		if(tag.localName.equals("grammar") ) 
			// it is a grammar.
			return new GrammarState();
		
		return null;
	}
	
	protected void endSelf()
	{// wrap-up.
		final RELAXNSReader reader = (RELAXNSReader)this.reader;
		
		SchemaProviderImpl schemaProvider = new SchemaProviderImpl(reader.grammar);
		reader.schemaProvider = schemaProvider;
		
		ErrorHandler handler = new GrammarReaderControllerAdaptor(reader.controller);
		
		// then bind it as the final wrap-up.
		if( !schemaProvider.bind(handler) )
			reader.hadError = true;
		
		// also bind top-level expression
		if( reader.grammar.topLevel!=null )
			// this 'if' clause is necessary when
			// <topLevel> is not specified.
			reader.grammar.topLevel = 
				reader.grammar.topLevel.visit(
					new IslandSchemaImpl.Binder(schemaProvider, handler, reader.pool ) );
		
/* binding code has to go somewhere.
		
		final RELAXReader reader = (RELAXReader)this.reader;
		final ExpressionPool pool = reader.pool;
		
		{
			Iterator itr = reader.grammar.moduleMap.values().iterator();
			while(itr.hasNext())
			{// for each module that is referenced
				RELAXModule module = (RELAXModule)itr.next();
					
				if( reader.isStubModule(module) )
				{// if the module is a stub module
					
					prepareStubModule(module);
				}
				else
				{// if the module is normally defined module
					if( !reader.isInitializedModule(module) )
					{// uninitialized module = reference to an undeclared namespace
						reader.reportError( reader.backwardReference.getReferer(module,true),
							RELAXReader.ERR_UNDEFINED_NAMESPACE,
							new Object[]{module.targetNamespace} );
						provideStubForReference( module.elementRules );
						provideStubForReference( module.hedgeRules );
						provideStubForReference( module.attPools );
						provideStubForReference( module.exportedAttPools );
						provideStubForReference( module.tags );
						continue;
					}
						
					// detect label collision.
					// it is prohibited for elementRule and hedgeRule to share the same label.
					detectCollision( module.elementRules, module.hedgeRules, RELAXReader.ERR_LABEL_COLLISION );
					// the same restriction applies for tag/attPool
					detectCollision( module.tags, module.attPools, RELAXReader.ERR_ROLE_COLLISION );
					
					// detect undefined elementRules, hedgeRules, and so on.
					// dummy definitions are given for undefined ones.
					reader.detectUndefinedOnes( module.elementRules,RELAXReader.ERR_UNDEFINED_ELEMENTRULE );
					reader.detectUndefinedOnes( module.hedgeRules,	RELAXReader.ERR_UNDEFINED_HEDGERULE );
					reader.detectUndefinedOnes( module.tags,		RELAXReader.ERR_UNDEFINED_TAG );
					reader.detectUndefinedOnes( module.attPools,	RELAXReader.ERR_UNDEFINED_ATTPOOL );
					
					// detect references to unexported labels.
					detectExternalReference( module.elementRules, RELAXReader.ERR_UNEXPORTED_ELEMENTRULE );
					detectExternalReference( module.hedgeRules, RELAXReader.ERR_UNEXPORTED_HEDGERULE );
					
					// supply definitions of exported attPools,
					// and detect references to unexported ones.
					defineExportedAttPools( module );
					
					detectDoubleAttributeConstraints( module );
					
					// checks ID abuse
					IdAbuseChecker.check( reader, module );
				}
			}
		}		
		
		
		if( standAloneModule )
		{
			// supply top-level expression.
			
			// for stand-alone module, choice of all exported elementRules 
			// and hedgeRules are considered as top-level.
			
			Expression exp =
				reader.pool.createChoice(
					choiceOfExported( reader.currentModule.elementRules ),
					choiceOfExported( reader.currentModule.hedgeRules ) );
			
			if( exp==Expression.nullSet )
				// at least one element must be exported or
				// the grammar accepts nothing.
				reader.reportError( reader.ERR_NO_EXPROTED_LABEL );
			
			reader.grammar.topLevel = exp;
		}
		else
		{
			if( reader.grammar.topLevel==null )
			{
				reader.reportError( reader.ERR_MISSING_TOPLEVEL );
				reader.grammar.topLevel = Expression.nullSet;
			}
		}
		
		{// perform wrap-up for anyOtherElements
			
			// create choice of all exported labels of known modules.
			Iterator itr = reader.grammar.moduleMap.values().iterator();
			Expression choiceOfAllLabels = Expression.nullSet;
			
			while(itr.hasNext())
			{// for each module...
				RELAXModule module = (RELAXModule)itr.next();
				
				choiceOfAllLabels = reader.pool.createChoice( choiceOfAllLabels,
					reader.pool.createChoice(
						choiceOfExported( module.elementRules ),
						choiceOfExported( module.hedgeRules ) ) );
			}
			
			itr = reader.pendingAnyOtherElements.iterator();
			while(itr.hasNext())
				((AnyOtherElementState)itr.next()).wrapUp(choiceOfAllLabels);
		}
		
		// make sure that there is no recurisve hedge rules.
		reader.grammar.topLevel.visit( new RunAwayExpressionChecker(reader) );
		
		{// make sure that there is no exported hedgeRule that references a label in the other namespace.
			Iterator itr = reader.grammar.moduleMap.values().iterator();
			while(itr.hasNext())
			{// for each module...
				RELAXModule module = (RELAXModule)itr.next();
				
				Iterator jtr = module.hedgeRules.iterator();
				while(jtr.hasNext())
				{
					HedgeRules hr = (HedgeRules)jtr.next();
					if(!hr.exported)	continue;
					
					ExportedHedgeRuleChecker ehrc = new ExportedHedgeRuleChecker(module);
					if(!hr.visit( ehrc ))
					{
						// this hedgeRule directly/indirectly references exported labels.
						// report it to the user.
						
						// TODO: source information?
						String dependency="";
						for( int i=0; i<ehrc.errorSnapshot.length-1; i++ )
							dependency+= ehrc.errorSnapshot[i].name + " > ";
						
						dependency += ehrc.errorSnapshot[ehrc.errorSnapshot.length-1].name;
						
						reader.reportError(
							RELAXReader.ERR_EXPROTED_HEDGERULE_CONSTRAINT,
							dependency );
						
					}
				}
			}
		}
		super.endSelf();
*/		
	}
	
	// GrammarState implements ExpressionState,
	// so RootState has to implement ExpressionOwner.
	public final void onEndChild(Expression exp) {}
	
	
	
	

	/**
	 * fills nullSet to all undefined ReferenceExp.
	 * 
	 * This method is used for error recovery only.
	 */
/*	private void provideStubForReference( ReferenceContainer con )
	{
		Iterator itr = con.iterator();
		while( itr.hasNext() )
		{
			ReferenceExp ref = (ReferenceExp)itr.next();
			if( ref.exp==null )		ref.exp = Expression.nullSet;
		}
	}
*/
	
	
/*	
	private class ExportedAttPoolGenerator extends ExpressionCloner implements RELAXExpressionVisitorExpression
	{
		ExportedAttPoolGenerator( ExpressionPool pool ) { super(pool); }
		
		private String targetNamespace;
		public Expression create( RELAXModule module, Expression exp )
		{
			targetNamespace = module.targetNamespace;
			return exp.visit(this);
		}
		
		public Expression onAttribute( AttributeExp exp )
		{
			if(!(exp.nameClass instanceof SimpleNameClass ))
				return exp;	// leave it as is. or should we consider this as a failed assertion?
			
			SimpleNameClass nc = (SimpleNameClass)exp.nameClass;
			if( !nc.namespaceURI.equals("") )
				return exp;	// externl attributes. leave it as is.
			
			return pool.createAttribute(
				new SimpleNameClass( targetNamespace, nc.localName ),
				exp.exp );
		}
		
		// we are traversing attPools. thus these will never be possible.
		public Expression onElement( ElementExp exp )			{ throw new Error(); }
		public Expression onTag( TagClause exp )				{ throw new Error(); }
		public Expression onElementRules( ElementRules exp )	{ throw new Error(); }
		public Expression onHedgeRules( HedgeRules exp )		{ throw new Error(); }
		
		public Expression onRef( ReferenceExp exp )
		{
			// this class implements RELAXbrabraVisitor. So this method should never be called
			throw new Error();
		}
		
		public Expression onAttPool( AttPoolClause exp )
		{// create exported version for them, too.
			
			// note that thsi exp.exp may be a AttPool of a different module.
			// In that case, calling visit method is no-op. But at least
			// it doesn't break anything.
			return exp.exp.visit(this);
		}
	}
*/
	
	
	/** detects external references to unexported label, and reports error. */
/*	private void detectExternalReference( ReferenceContainer con, String errPropKey )
	{
		Iterator itr = con.iterator();
		while(itr.hasNext())
		{
			Exportable exp = (Exportable)itr.next();
			if(exp.isExported())		continue;
			
			Locator[] locs = reader.backwardReference.getReferer(exp,true);
			if(locs==null)	continue;	// no external link to this expression.
			
			// otherwise, error
			reader.reportError( locs, errPropKey, new Object[]{((ReferenceExp)exp).name} );
		}
	}
*/
	
	
	/** supply definitions for exported attPools. */
/*	private void defineExportedAttPools( RELAXModule module )
	{
		final ExportedAttPoolGenerator expAttPoolGenerator =
			new ExportedAttPoolGenerator(reader.pool);
		
		Iterator jtr = module.exportedAttPools.iterator();
		while( jtr.hasNext() )
		{
			final AttPoolClause expAc = (AttPoolClause)jtr.next();
							
			// locate the corresponding domestic attPool.
			final AttPoolClause domAc = module.attPools.get(expAc.name);
							
			if(domAc==null)
			{// if a domestic one doesn't exist, it is a reference to the undefined attPool
				reader.reportError(
					reader.backwardReference.getReferer(expAc,true),
					RELAXReader.ERR_UNDEFINED_ATTPOOL, new Object[]{expAc.name} );
				continue;
			}
							
			if(domAc.exp==null)
			{
				// if attPool 'x' is not defined but is referenced from
				// both the same module and external modules,
				// domAc and expAc exists for 'x'.
				// but there is no definition for 'x'.
								
				// in this case, user has already received "undefined x" error.
				// so simply stop processing this case.
				continue;
			}
							
			if(expAc.exp==null)
			{
				// expAc.exp is assigned a non-null value
				// when it is declared as exported.
				// Thus expAc.exp==null implies that this
				// attPool has not declared as so.
				reader.reportError(
					reader.backwardReference.getReferer(expAc,true),
					RELAXReader.ERR_UNEXPORTED_ATTPOOL, new Object[]{expAc.name} );
				continue;
			}
							
			// now we've made sure that
			//   (1) referenced attPool is declared as exported,
			//   (2) it has definition
			// so all the conditions are met.
							
			// create exported version of the definition.
			expAc.exp = expAttPoolGenerator.create(module,domAc.exp);
		}
	}
*/
}
