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

import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.SimpleNameClass;
import com.sun.tranquilo.grammar.relax.ElementRule;
import com.sun.tranquilo.grammar.relax.TagClause;
import com.sun.tranquilo.reader.ExpressionWithoutChildState;
	
public class InlineElementState extends ExpressionWithoutChildState
{
	protected TypeIncubator incubator;
	
	public TypeIncubator getIncubator() { return incubator; }
	
	protected void startSelf()
	{
		super.startSelf();
		String type		= startTag.getAttribute("type");
		if(type==null)	type="string";
		
		incubator = new TypeIncubator(
					((RELAXReader)reader).resolveDataType(type) );
	}
	protected Expression makeExpression()
	{
		try
		{
			final String name		= startTag.getAttribute("name");
			
			if( name==null )
			{
				reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE, "element","name" );
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
			c.nameClass = new SimpleNameClass( ((RELAXReader)reader).currentModule.targetNamespace, name );
			c.exp		= Expression.epsilon;	// no attribute
			
			// create anonymous ElementRule. this rule will never be added to
			// RefContainer.
			return new ElementRule( reader.pool, c, contentModel );
		}
		catch( BadTypeException e )
		{// derivation failed
			reader.reportError( e, RELAXReader.ERR_BAD_TYPE, e.getMessage() );
			// recover by using harmless expression. anything will do.
			return Expression.nullSet;
		}
	}
}
