package com.sun.tranquilo.datatype;

import com.sun.tranquilo.datatype.datetime.ISO8601Parser;
import com.sun.tranquilo.datatype.datetime.IDateTimeValueType;

/**
 * "month" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#month for the spec
 */
public class MonthType extends DateTimeBaseType
{
	public static final MonthType theInstance = new MonthType();
	private MonthType() { super("month"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.monthTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.monthTypeV();
	}
}
