package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.SimpleNameClass;
import com.sun.tranquilo.reader.ExpressionState;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.datatype.xsd.FacetStateParent;
import com.sun.tranquilo.util.StartTagInfo;

public class AttributeState extends ExpressionState implements FacetStateParent
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
			final String required	= startTag.getAttribute("required");
			
			if( name==null )
			{
				reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE, "attribute","name" );
				// recover by ignoring this attribute.
				// since attributes are combined by sequence, so epsilon is appropriate.
				return Expression.epsilon;
			}
			
			Expression value;
			
			if( !startTag.containsAttribute("type") && incubator.isEmpty() )
				// we can use cheaper anyString
				value = Expression.anyString;
			else
				value = reader.pool.createTypedString( incubator.derive(null) );
			
			Expression exp = reader.pool.createAttribute(
				new SimpleNameClass("",name),
				value );
			
			// unless required attribute is specified, it is considered optional
			if(! "true".equals(required) )
				exp = reader.pool.createOptional(exp);
			
			return exp;
		}
		catch( BadTypeException e )
		{// derivation failed
			reader.reportError( e, RELAXReader.ERR_BAD_TYPE, e.getMessage() );
			// recover by using harmless expression. anything will do.
			return Expression.anyString;
		}
	}
	
	protected State createChildState( StartTagInfo tag )
	{
		return RELAXReader.createFacetState(tag);	// facets
	}
}
