package com.sun.tranquilo.grammar;

import com.sun.tranquilo.datatype.NmtokenType;
import com.sun.tranquilo.datatype.ValidationContextProvider;

/**
 * very limited 'ID' type of XML Schema Part 2.
 */
public class IDType extends NmtokenType
{
	public static final IDType theInstance = new IDType();
	protected IDType()	{ super("ID"); }
	
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		Object o = super.convertToValue(content,context);
		if(o==null)		return null;

		if(!((IDContextProvider)context).onID(content))	return null;
		return o;
	}
}
