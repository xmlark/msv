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

import com.sun.msv.datatype.BadTypeException;
import com.sun.msv.datatype.DataType;
import com.sun.msv.datatype.TypeIncubator;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.relax.TagClause;
import com.sun.msv.reader.State;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;element&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InlineElementState extends ExpressionState implements FacetStateParent
{
	protected TypeIncubator incubator;
	
	public TypeIncubator getIncubator() { return incubator; }
	
	protected State createChildState( StartTagInfo tag ) {
		return ((RELAXCoreReader)reader).createFacetState(this,tag);	// facets
	}
	
	protected void startSelf()
	{
		super.startSelf();
		String type		= startTag.getAttribute("type");
		if(type==null)
		{
			reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "element", "type" );
			type="string";
		}
		
		incubator = new TypeIncubator( reader.resolveDataType(type) );
	}
	protected Expression makeExpression()
	{
		try
		{
			final String name		= startTag.getAttribute("name");
			
			if( name==null )
			{
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "element","name" );
				// recover by ignoring this element.
				return Expression.nullSet;
			}
			
			Expression contentModel;

			if( !startTag.containsAttribute("type") && incubator.isEmpty() )
			{// we can use cheaper anyString
				contentModel = Expression.anyString;
			}
			else
			{
				contentModel = reader.pool.createTypedString(
					incubator.derive(null) );
			}
			
			TagClause c = new TagClause();
			c.nameClass = new SimpleNameClass( ((RELAXCoreReader)reader).module.targetNamespace, name );
			c.exp		= Expression.epsilon;	// no attribute
			
			// create anonymous ElementRule. this rule will never be added to
			// RefContainer.
			return new ElementRule( reader.pool, c, contentModel );
		}
		catch( BadTypeException e )
		{// derivation failed
			reader.reportError( e, reader.ERR_BAD_TYPE, e.getMessage() );
			// recover by using harmless expression. anything will do.
			return Expression.nullSet;
		}
	}
}
