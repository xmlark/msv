/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import com.sun.msv.util.StartTagInfo;
import com.sun.msv.reader.State;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.reader.relax.core.checker.*;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.relax.*;
import org.xml.sax.Locator;
import java.util.Iterator;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used to parse RELAX module referenced by RELAX Namespace.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class RootModuleState extends SimpleState {
	protected final String expectedNamespace;
	
	RootModuleState( String expectedNamespace ) {
		this.expectedNamespace = expectedNamespace;
	}
	
	protected State createChildState( StartTagInfo tag ) {
		if(tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace)
		&& tag.localName.equals("module"))
			return new ModuleState(expectedNamespace);
		
		return null;
	}
	
	// module wrap-up.
	protected void endSelf() {
		
		final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
		final RELAXModule module = reader.module;

		// combine expressions to their masters.
		// if no master is found, then create a new AttPool.
		{
			ReferenceExp[] combines = reader.combinedAttPools.getAll();
			for ( int i=0; i<combines.length; i++ ) {
				
				AttPoolClause ac = module.attPools.get(combines[i].name);
				if( ac!=null ) {
					// ac.exp==null means no master is found but someone
					// has a reference to this clause.
					// this is OK.
					if( ac.exp==null )		ac.exp=Expression.epsilon;
					ac.exp = reader.pool.createSequence( ac.exp, combines[i].exp );
					continue;
				}
				
				TagClause tc = module.tags.get(combines[i].name);
				if( tc!=null && tc.exp!=null ) {
					// tc.exp==null means no master is found.
					// In this case, we can't combine us to TagClause.
					tc.exp = reader.pool.createSequence( tc.exp, combines[i].exp );
					continue;
				}
				
				// no master is found. Create a new one.
				ac = module.attPools.getOrCreate(combines[i].name);
				ac.exp = combines[i].exp;
			}
		}

		// role collision check.
		detectCollision( module.tags, module.attPools, reader.ERR_ROLE_COLLISION );
		
		
		// detect undefined elementRules, hedgeRules, and so on.
		// dummy definitions are given for undefined ones.
		reader.detectUndefinedOnes( module.elementRules,reader.ERR_UNDEFINED_ELEMENTRULE );
		reader.detectUndefinedOnes( module.hedgeRules,	reader.ERR_UNDEFINED_HEDGERULE );
		reader.detectUndefinedOnes( module.tags,		reader.ERR_UNDEFINED_TAG );
		reader.detectUndefinedOnes( module.attPools,	reader.ERR_UNDEFINED_ATTPOOL );
		
		// label collision detection should be done after
		// undefined label detection because
		// sometimes people use <ref label/> for hedgeRule,
		
		// detect label collision.
		// it is prohibited for elementRule and hedgeRule to share the same label.
		detectCollision( module.elementRules, module.hedgeRules, reader.ERR_LABEL_COLLISION );
						
		detectDoubleAttributeConstraints( module );
						
		// checks ID abuse
		IdAbuseChecker.check( reader, module );
		
		// supply top-level expression.
		Expression exp =
			reader.pool.createChoice(
				choiceOfExported( module.elementRules ),
				choiceOfExported( module.hedgeRules ) );
			
		if( exp==Expression.nullSet )
			// at least one element must be exported or
			// the grammar accepts nothing.
			reader.reportError( reader.ERR_NO_EXPROTED_LABEL );
			
		module.topLevel = exp;
		
		// make sure that there is no recurisve hedge rules.
		RunAwayExpressionChecker.check( reader, module.topLevel );
			
		
		{// make sure that there is no exported hedgeRule that references a label in the other namespace.
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
						reader.ERR_EXPROTED_HEDGERULE_CONSTRAINT,
						dependency );
							
				}
			}
		}
		
		super.endSelf();
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
			checker.check( (TagClause)itr.next(), (RELAXCoreReader)reader );
	}

	
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
					new Locator[]{ reader.getDeclaredLocationOf(r1),
								   reader.getDeclaredLocationOf(r2) },
					errMsg,	new Object[]{r1.name} );
		}
	}
}
