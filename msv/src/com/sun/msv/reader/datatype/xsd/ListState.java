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

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.datatype.BadTypeException;
import com.sun.msv.datatype.DataType;
import com.sun.msv.datatype.DataTypeFactory;
import com.sun.msv.util.StartTagInfo;

/**
 * state that parses &lt;list&gt; element of XSD.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ListState extends TypeWithOneChildState
{
	protected final String newTypeName;
	protected ListState( String newTypeName )
	{
		this.newTypeName = newTypeName;
	}
	
	protected DataType annealType( DataType baseType )
		throws BadTypeException
	{
		return DataTypeFactory.deriveByList( newTypeName, baseType );
	}
	
	protected void startSelf()
	{
		super.startSelf();
		
		// if itemType attribute is used, try to load it.
		String itemType = startTag.getAttribute("itemType");
		if(itemType!=null)
			type = reader.resolveDataType(itemType);
	}

	protected State createChildState( StartTagInfo tag )
	{
		// accepts elements from the same namespace only.
		if( !startTag.namespaceURI.equals(tag.namespaceURI) )	return null;
		
		if( tag.localName.equals("annotation") )	return new IgnoreState();
		if( tag.localName.equals("simpleType") )	return new SimpleTypeState();
		
		return null;	// unrecognized
	}
}
