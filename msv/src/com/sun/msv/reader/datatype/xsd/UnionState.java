/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.datatype.xsd;

import com.sun.msv.datatype.BadTypeException;
import com.sun.msv.datatype.DataTypeImpl;
import com.sun.msv.datatype.DataTypeFactory;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.datatype.TypeOwner;
import com.sun.msv.util.StartTagInfo;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.relaxng.datatype.DataType;

/**
 * State that parses &lt;union&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnionState extends TypeState implements TypeOwner
{
	protected final String newTypeName;
	protected UnionState( String newTypeName )
	{
		this.newTypeName = newTypeName;
	}
	
	private final ArrayList memberTypes = new ArrayList();
												  
	protected State createChildState( StartTagInfo tag )
	{
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState();
		
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
	
	protected final DataTypeImpl makeType() throws BadTypeException
	{
		return DataTypeFactory.deriveByUnion( newTypeName, memberTypes );
	}

}
