package com.sun.tranquilo.reader.datatype.xsd;

import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.DataTypeFactory;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.IgnoreState;
import com.sun.tranquilo.reader.datatype.TypeOwner;
import com.sun.tranquilo.util.StartTagInfo;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * State that parses &lt;union&gt; element and its children.
 */
public class UnionState extends TypeState implements TypeOwner
{
	protected final String newTypeName;
	protected UnionState( XSDVocabulary voc, String newTypeName )
	{
		super(voc);
		this.newTypeName = newTypeName;
	}
	
	private final ArrayList memberTypes = new ArrayList();
												  
	protected State createChildState( StartTagInfo tag )
	{
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState(vocabulary);
		
		return null;	// unrecognized
	}
	
	protected void startSelf()
	{
		super.startSelf();
		
		// if memberTypes attribute is used, load it.
		String memberTypes = startTag.getAttribute("memberTypes");
		if(memberTypes!=null)
		{
			StringTokenizer tokens = new StringTokenizer(memberTypes);
			while( tokens.hasMoreTokens() )
				onEndChild( reader.resolveDataType(tokens.nextToken()) );
		}
	}
	
	public void onEndChild( DataType type )	{ memberTypes.add(type); }
	
	protected final DataType makeType() throws BadTypeException
	{
		return DataTypeFactory.deriveByUnion( newTypeName, memberTypes );
	}

}
