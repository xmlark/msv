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

import com.sun.tranquilo.reader.ExpressionWithoutChildState;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.relax.ElementRule;
import com.sun.tranquilo.grammar.relax.TagClause;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.xml.sax.Locator;

/**
 * parses &lt;anyOtherElement&gt; state.
 * 
 * To create an expression that implements the semantics of anyOtherElement,
 * the entire grammar must be parsed first.
 */
public class AnyOtherElementState extends ExpressionWithoutChildState
{
	// when makeExpression is called, return only a skelton.
	// later, after the entire grammar is parsed, we'll provide
	// actual expression.
	protected Expression makeExpression()
	{
		final RELAXReader reader = (RELAXReader)this.reader;
		
		// register myself to the pending list
		reader.pendingAnyOtherElements.add(this);
		
		skelton =  reader.currentModule.createAnyOtherElementSkelton();
		return skelton;
	}
	
	protected ReferenceExp skelton;
	
	/** creates actual expression and sets it into the skelton.
	 * 
	 * This function is called from RootState.
	 * Therefore, line information is not automatically available
	 * when reporting error.
	 * Implementator should keep this in mind and manually pass Locator to reportError method.
	 */
	protected void wrapUp( Expression choiceOfAllExportedLabels )
	{
		final RELAXReader reader = (RELAXReader)this.reader;
		final String includeNamespace = startTag.getAttribute("includeNamespace");
		final String excludeNamespace = startTag.getAttribute("excludeNamespace");
		
		StringTokenizer st;
		if( includeNamespace!=null )
			st = new StringTokenizer(includeNamespace);
		else
			st = new StringTokenizer(excludeNamespace);
		
		if( includeNamespace!=null && excludeNamespace!=null )
			reader.reportError(
				new Locator[]{this.location},
				reader.ERR_CONFLICTING_ATTRIBUTES,
				new Object[]{"includeNamespace", "excludeNamespace"} );
			// recovery has already done by ignoring excludeNamespace.
		
		NameClass nc =null;
		
		while(st.hasMoreTokens())
		{
			String uri = st.nextToken();
			
			if(uri.equals("##local"))	uri="";
			
			if( reader.grammar.moduleMap.containsKey(uri) )
			{
				// one cannot specify defined URI.
				reader.reportWarning( 
					RELAXReader.WRN_ANYOTHER_NAMESPACE_IGNORED,
					new Object[]{uri}, new Locator[]{this.location} );
				continue;
			}
			
			NamespaceNameClass nsnc = new NamespaceNameClass(uri);
			if( nc==null )	nc = nsnc;
			else			nc = new ChoiceNameClass(nc,nsnc);
		}
		
		if( excludeNamespace!=null )
		{
			// in case of 'excludeNamespace',
			// all defined namespace is considered as illegal.
			
			Iterator itr = reader.grammar.moduleMap.keySet().iterator();
			while( itr.hasNext() )
			{
				NamespaceNameClass nsnc = new NamespaceNameClass( (String)itr.next() );
				if( nc==null )	nc = nsnc;
				else			nc = new ChoiceNameClass(nc,nsnc);
			}
			
			nc = new NotNameClass(nc);
		}
		
		// now NameClass part is ready.
		
		// TODO: semantics of anyOtherElement?
		TagClause tc = new TagClause();
		tc.nameClass = nc;
		tc.exp = reader.pool.createZeroOrMore(
			reader.pool.createAttribute( AnyNameClass.theInstance, Expression.anyString ) );
		
		skelton.exp =
			new ElementRule( reader.pool, tc,
				reader.pool.createMixed(
					reader.pool.createZeroOrMore(
						reader.pool.createChoice( skelton, choiceOfAllExportedLabels ) ) ) );
	}
}
