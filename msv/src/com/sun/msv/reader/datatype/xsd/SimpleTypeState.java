/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.datatype.xsd;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.DataTypeImpl;
import com.sun.tranquilo.datatype.FinalComponent;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.IgnoreState;
import com.sun.tranquilo.reader.ExpressionState;
import com.sun.tranquilo.util.StartTagInfo;
import java.util.StringTokenizer;

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

	protected DataType annealType( DataType dt )
	{
		final String finalValue = startTag.getAttribute("final");
		if(finalValue!=null)
			// wrap it by FinalComponent
			return new FinalComponent( (DataTypeImpl)dt, getFinalValue(finalValue) );
		else
			return dt;
	}


	/** parses final attribute */
	public int getFinalValue( String list )
	{
		int finalValue = 0;
		StringTokenizer tokens = new StringTokenizer(list);
		while(tokens.hasMoreTokens())
		{
			String token = tokens.nextToken();
			
			if( token.equals("#all") )
				finalValue |=	DataType.DERIVATION_BY_LIST|
								DataType.DERIVATION_BY_RESTRICTION|
								DataType.DERIVATION_BY_UNION;
			else
			if( token.equals("restriction") )
				finalValue |= DataType.DERIVATION_BY_RESTRICTION;
			else
			if( token.equals("list") )
				finalValue |= DataType.DERIVATION_BY_LIST;
			else
			if( token.equals("union") )
				finalValue |= DataType.DERIVATION_BY_UNION;
			else
			{
				reader.reportError( 
					reader.ERR_ILLEGAL_FINAL_VALUE, token );
				return 0;	// abort
			}
		}
		return finalValue;
	}


}
