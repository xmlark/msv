/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.datetime.ISO8601Parser;
import com.sun.msv.datatype.xsd.datetime.ITimeDurationValueType;
import java.io.ByteArrayInputStream;
import org.relaxng.datatype.ValidationContext;

/**
 * "duration" type.
 * 
 * type of the value object is {@link ITimeDurationValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#duration for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class DurationType extends BuiltinAtomicType implements Comparator {
	
	public static final DurationType theInstance = new DurationType();
	private DurationType() { super("duration"); }
	
	final public XSDatatype getBaseType() {
		return SimpleURType.theInstance;
	}

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
	
	public Object _createValue( String content, ValidationContext context ) {
		try {
			return getParser(content).durationTypeV();
		} catch( Throwable e ) {
			return null;
		}
	}
	public Class getJavaObjectType() {
		return ITimeDurationValueType.class;
	}
	
	/** compare two TimeDurationValueType */
	public int compare( Object lhs, Object rhs ) {
		return ((ITimeDurationValueType)lhs).compare((ITimeDurationValueType)rhs);
	}
	
	public final int isFacetApplicable( String facetName ) {
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
        ||  facetName.equals(FACET_WHITESPACE)
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

