package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.datatype.xsd.FacetStateParent;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * &lt;elementRule&gt; with 'type' attribute.
 */
public class ElementRuleWithTypeState extends ElementRuleBaseState implements FacetStateParent
{
	protected TypeIncubator incubator;
	
	public TypeIncubator getIncubator()	{ return incubator; }
	
	protected void startSelf()
	{
		super.startSelf();
		
		// existance of type attribute has already checked before
		// this state is created.
		incubator = new TypeIncubator(
			((RELAXReader)reader).resolveDataType(
				startTag.getAttribute("type") ) );
	}
	
	protected Expression getContentModel()
	{
		try
		{
			// TODO: ValidationContextProvider is necessary for every facet member
			return reader.pool.createTypedString( incubator.derive(null) );
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
		State next = RELAXReader.createFacetState(tag);
		if(next!=null)		return next;			// facets
		
		return super.createChildState(tag);			// or delegate to the base class
	}
}
