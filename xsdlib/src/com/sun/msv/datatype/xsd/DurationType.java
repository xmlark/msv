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
import com.sun.msv.datatype.datetime.ITimeDurationValueType;
import java.io.ByteArrayInputStream;
import org.relaxng.datatype.ValidationContext;

/**
 * "duration" type.
 * 
 * type of the value object is {@link ITimeDurationValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#duration for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public final class DurationType extends ConcreteType implements Comparator {
	
	public static final DurationType theInstance = new DurationType();
	private DurationType() { super("duration"); }

	private final ISO8601Parser getParser( String content ) throws Exception {
		return new ISO8601Parser( new ByteArrayInputStream( content.getBytes("UTF8") ) );
	}
	
	protected boolean checkFormat( String content, ValidationContext context ) {
		try {
			getParser(content).durationTypeL();
			return true;
		} catch( Throwable e ) {
			return false;
		}
	}
	
	public Object convertToValue( String content, ValidationContext context ) {
		try {
			return getParser(content).durationTypeV();
		} catch( Throwable e ) {
			return null;
		}
	}
	
	/** compare two TimeDurationValueType */
	public int compare( Object lhs, Object rhs ) {
		return ((ITimeDurationValueType)lhs).compare((ITimeDurationValueType)rhs);
	}
	
	public final int isFacetApplicable( String facetName ) {
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
	
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if(!(value instanceof ITimeDurationValueType))
			throw new IllegalArgumentException();
		
		return ((ITimeDurationValueType)value).getBigValue().toString();
	}
}

