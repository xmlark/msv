package com.sun.tranquilo.datatype;

import com.sun.tranquilo.datatype.datetime.ISO8601Parser;
import com.sun.tranquilo.datatype.datetime.IDateTimeValueType;

/**
 * "yearMonth" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#yearMonth for the spec
 */
public class YearMonthType extends DateTimeBaseType
{
	public static final YearMonthType theInstance = new YearMonthType();
	private YearMonthType() { super("yearMonth"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.yearMonthTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.yearMonthTypeV();
	}
}
