package com.sun.tranquilo.reader.datatype.xsd;

import com.sun.tranquilo.reader.GrammarReader;
import com.sun.tranquilo.reader.IgnoreState;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.TypeIncubator;
import com.sun.tranquilo.util.StartTagInfo;

public class RestrictionState extends TypeWithOneChildState implements FacetStateParent
{
	protected final String newTypeName;
	protected RestrictionState( XSDVocabulary voc, String newTypeName )
	{
		super(voc);
		this.newTypeName = newTypeName;
	}
	
	protected TypeIncubator incubator;
	public final TypeIncubator getIncubator()	{ return incubator; }

	protected DataType annealType( DataType baseType ) throws BadTypeException
	{
		return incubator.derive(newTypeName);
	}
	
	public void onEndChild( DataType child )
	{
		super.onEndChild(child);
		incubator = new TypeIncubator(super.type);
	}

	
	protected void startSelf()
	{
		super.startSelf();
		
		// if base attribute is used, try to load it.
		String base = startTag.getAttribute("base");
		if(base!=null)
		{
			type = reader.resolveDataType(base);
			incubator = new TypeIncubator(type);
		}
	}

	protected State createChildState( StartTagInfo tag )
	{
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState(vocabulary);
		if( FacetState.facetNames.contains(tag.localName) )
		{
			if( incubator==null )
			{
				reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "restriction", "base" );
				type = reader.resolveDataType("string");	// assume some type
				incubator = new TypeIncubator(type);
			}
			return new FacetState();
		}
		
		return null;	// unrecognized
	}
}
