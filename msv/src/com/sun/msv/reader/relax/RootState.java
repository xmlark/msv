/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.reader.RunAwayExpressionChecker;
import com.sun.tranquilo.reader.relax.checker.*;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.relax.*;
import java.util.Iterator;
import org.xml.sax.Locator;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used only one when starting parsing a RELAX schema.
 * For included module/grammar, different states are used.
 */
class RootState extends SimpleState implements ExpressionOwner
{
	protected State createChildState( StartTagInfo tag )
	{
		if(tag.namespaceURI.equals(RELAXReader.RELAXNamespaceNamespace)
		&& tag.localName.equals("grammar") ) 
			// it is a grammar.
			return new GrammarState();
		
		if(tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)
		&& tag.localName.equals("module"))
		{// it turns out that this is a stand-alone module.
			standAloneModule = true;	// remember this fact so as to perform appropriate wrap-up.
			return new ModuleState(null);	// no expectation for target namespace
		}
		
		return null;
	}
	
	/** a flag that indicates stand-alone module is being loaded */
	protected boolean standAloneModule = false;

	
	private void detectCollision( ReferenceContainer col1, ReferenceContainer col2, String errMsg )
	{
		Iterator itr = col1.iterator();
		while( itr.hasNext() )
		{
			ReferenceExp r1	= (ReferenceExp)itr.next();
			ReferenceExp r2	= col2._get( r1.name );
			// if the grammar references elementRule by hedgeRef,
			// (or hedgeRule by ref),  HedgeRules object and ElementRules object
			// are created under the same name.
			// And it is inappropriate to report this situation as "label collision".
			// Therefore, we have to check both have definitions before reporting an error.
			if( r2!=null && r1.exp!=null && r2.exp!=null )
				reader.reportError(
					new Locator[]{ ((RELAXReader)reader).getDeclaredLocationOf(r1),
								   ((RELAXReader)reader).getDeclaredLocationOf(r2) },
					errMsg,	new Object[]{r1.name} );
		}
	}
	
	protected void endSelf()
	{// wrap-up.
		
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
	}
	
	// GrammarState implements ExpressionState,
	// so RootState has to implement ExpressionOwner.
	public final void onEndChild(Expression exp) {}
	
	
	
	
	/**
	 *  supply pseudo-module definitions for all referenced items in the module
	 */
	private void prepareStubModule( RELAXModule module )
	{
		throw new UnsupportedOperationException();
/*
		final ExpressionPool pool = reader.pool;
					
		// for attPool, accepts all attributes as long as it's in the specified namespace.
		// In short,
		// <zeroOrMore>
		//   <attribute>
		//     <nsName />
		//     <anyString />
		//   </attribute>
		// </zeroOrMore>
					
		Expression stubAttPoolExp =
			pool.createZeroOrMore(
				pool.createAttribute(
					new NamespaceNameClass(module.targetNamespace),
					Expression.anyString
				)
			);
					
		Iterator jtr;
					
		jtr = module.exportedAttPools.iterator();
		while( jtr.hasNext() )
		{
			AttPoolClause ac = (AttPoolClause)jtr.next();
			ac.exp = stubAttPoolExp;
		}
					
		// since stub modules are referenced only from external modules,
		// those two container may not have a child.
		// (tag and attPool are both domestic use only)
		if( module.attPools.iterator().hasNext() )
			throw new Error();	// assertion failed.
		if( module.tags.iterator().hasNext() )
			throw new Error();	// assertion failed.
					
		TagClause stubTag = new TagClause();
		stubTag.nameClass = new NamespaceNameClass(module.targetNamespace);
					
		HedgeRules stubElementRuleContentModel = new HedgeRules();
		Expression stubElementRule = new ElementRule( pool, stubTag, stubElementRuleContentModel );
					
						
		// for stubHedgeRule
		// <choice occurs="*">
		//   <ref label="stubElementRule" />
		// </choice>
					
		// TODO: make sure that those stub definitions are in fact correct.
		Expression stubHedgeRuleExp =
			pool.createZeroOrMore( stubElementRule );
								
					
		// for elementRule, accepts any tag name and any attributes
		// as long as they are in the target namespace of the stub module.
					
		// for attributes,
		// <sequence>
		//   <zeroOrMore>
		//     <attribute><nsName ns="" /><anyString /></attribute>
		//   </zeroOrMore>
		//   <optional><!-- for all exported attPools from all modules -->
		//     <ref name="that attPool" />
		//   </optional>
		// </sequence>
					
		// TODO: should it also contain stubAttPoolExp ?
					
		stubTag.exp = Expression.epsilon;
					
		jtr = reader.grammar.moduleMap.values().iterator();
		while( jtr.hasNext() )
		{
			RELAXModule m = (RELAXModule)jtr.next();
			Iterator ktr = m.exportedAttPools.iterator();
			while( ktr.hasNext() )
			{
				stubTag.exp = pool.createSequence( stubTag.exp,
					pool.createOptional( (ReferenceExp)ktr.next() ) );
				// do not memorize this link so that
				// when some of these references are undefined,
				// user will not confuse where it is actually referenced from.
			}
						
			ktr = m.elementRules.iterator();
			while( ktr.hasNext() )
			{
				ElementRules er = (ElementRules)ktr.next();
				if( er.isExported )
					stubElementRuleContentModel.addHedge(
						pool.createZeroOrMore(er) );
			}
			ktr = m.hedgeRules.iterator();
			while( ktr.hasNext() )
			{
				HedgeRules hr = (HedgeRules)ktr.next();
				if( hr.isExported )
					stubElementRuleContentModel.addHedge(
						pool.createZeroOrMore(hr) );
			}
		}
					
		stubTag.exp = pool.createSequence( stubTag.exp,
				pool.createZeroOrMore(
					pool.createAttribute(
						new NamespaceNameClass(""),
						Expression.anyString
					)
				)
			);
					
		// for content model,
		// <choice occurs="*">
		//   <ref label="stubElementRule" />
		//   <
					
		jtr = module.elementRules.iterator();
		while( jtr.hasNext() )
		{
			((ElementRules)jtr.next()).addElementRule( pool,
				new ElementRule( pool, tc, stubHedgeRuleExp ) );
		}
*/
	}

	/**
	 * fills nullSet to all undefined ReferenceExp.
	 * 
	 * This method is used for error recovery only.
	 */
	private void provideStubForReference( ReferenceContainer con )
	{
		Iterator itr = con.iterator();
		while( itr.hasNext() )
		{
			ReferenceExp ref = (ReferenceExp)itr.next();
			if( ref.exp==null )		ref.exp = Expression.nullSet;
		}
	}

	private Expression choiceOfExported( ReferenceContainer con )
	{
		Iterator itr = con.iterator();
		Expression r = Expression.nullSet;
		while( itr.hasNext() )
		{
			Exportable ex= (Exportable)itr.next();
			if( ex.isExported() )
				r = reader.pool.createChoice(r,(Expression)ex);
		}
		return r;
	}
	
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

	/** detects external references to unexported label, and reports error. */
	private void detectExternalReference( ReferenceContainer con, String errPropKey )
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
	
	/** detect two AttributeExps that share the same target name.
	 * 
	 * See {@link DblAttrConstraintChecker} for details.
	 */
	private void detectDoubleAttributeConstraints( RELAXModule module )
	{
		final DblAttrConstraintChecker checker = new DblAttrConstraintChecker();
		
		Iterator itr = module.tags.iterator();
		while( itr.hasNext() )
			// errors will be reported within this method
			// no recovery is necessary.
			checker.check( (TagClause)itr.next(), (RELAXReader)reader );
	}
	
	/** supply definitions for exported attPools. */
	private void defineExportedAttPools( RELAXModule module )
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
}
