package com.sun.tranquilo.reader.datatype.xsd;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.IgnoreState;
import com.sun.tranquilo.reader.ExpressionState;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * State that parses &lt;simpleType&gt; element and its children.
 */
class SimpleTypeState extends TypeWithOneChildState
{
	SimpleTypeState( XSDVocabulary voc ) { super(voc); }
	
	protected State createChildState( StartTagInfo tag )
	{
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		final String name = startTag.getAttribute("name");
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("restriction") )	return new RestrictionState(vocabulary,name);
		if( tag.localName.equals("list") )			return new ListState(vocabulary,name);
		if( tag.localName.equals("union") )			return new UnionState(vocabulary,name);
		
		return null;	// unrecognized
	}
}
