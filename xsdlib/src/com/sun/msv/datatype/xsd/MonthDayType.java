package com.sun.tranquilo.datatype;

import com.sun.tranquilo.datatype.datetime.ISO8601Parser;
import com.sun.tranquilo.datatype.datetime.IDateTimeValueType;

/**
 * "monthDay" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#monthDay for the spec
 */
public class MonthDayType extends DateTimeBaseType
{
	public static final MonthDayType theInstance = new MonthDayType();
	private MonthDayType() { super("monthDay"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.monthDayTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.monthDayTypeV();
	}
}
