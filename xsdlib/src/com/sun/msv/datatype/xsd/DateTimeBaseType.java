/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import com.sun.msv.datatype.datetime.ISO8601Parser;
//import com.sun.msv.datatype.datetime.ParseException;
import com.sun.msv.datatype.datetime.IDateTimeValueType;
import java.io.ByteArrayInputStream;

/**
 * base implementation of dateTime and dateTime-truncated types.
 * this class uses IDateTimeValueType as the value object.
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DateTimeBaseType extends ConcreteType implements Comparator
{
	protected DateTimeBaseType(String typeName) { super(typeName); }
	
	private static final ISO8601Parser getParser( String content ) throws Exception
	{
		return new ISO8601Parser( new ByteArrayInputStream( content.getBytes("UTF8") ) );
	}
	
	protected final boolean checkFormat( String content, ValidationContextProvider context )
	{// string derived types should use convertToValue method to check its validity
		try
		{
			runParserL(getParser(content));
			return true;
		}
		catch( Throwable e )
		{
			return false;
		}
	}
	
	/** invokes the appropriate lexical parse method to check lexical format */
	abstract protected void runParserL( ISO8601Parser p ) throws Exception;

	
	public final Object convertToValue( String content, ValidationContextProvider context )
	{// for string, lexical space is value space by itself
		try
		{
			return runParserV(getParser(content));
		}
		catch( Throwable e )
		{
			return null;
		}
	}
	
	/** invokes the appropriate value creation method to obtain value object */
	abstract protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception;
	
	/** compare two DateTimeValueType */
	public int compare( Object lhs, Object rhs )
	{
		return ((IDateTimeValueType)lhs).compare((IDateTimeValueType)rhs);
	}
	
	public final int isFacetApplicable( String facetName )
	{
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
		||	facetName.equals(FACET_MAXINCLUSIVE)
		||	facetName.equals(FACET_MAXEXCLUSIVE)
		||	facetName.equals(FACET_MININCLUSIVE)
		||	facetName.equals(FACET_MINEXCLUSIVE) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
}
