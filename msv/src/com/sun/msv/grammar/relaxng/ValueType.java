/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relaxng;

import org.relaxng.datatype.*;
import org.relaxng.datatype.helpers.StreamingValidatorImpl;

/**
 * Datatype created by &lt;value&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValueType implements Datatype, java.io.Serializable {
	
	/** this datatype matches this value only. */
	public final Object value;
	
	/** this type is responsible for the equality test. */
	public final Datatype baseType;
	
	public ValueType( Datatype type, Object value ) {
		this.baseType = type;
		this.value = value;
	}
	
	public boolean isValid( String literal, ValidationContext context ) {
		return baseType.sameValue(
			value,
			baseType.createValue(literal,context) );
	}
	
	public String displayName() {
		String rep = value.toString();
		if( rep.length()<10 )	return "$NG-value("+rep+")";
		else					return "$NG-value";
	}
	
	public Object createValue( String literal, ValidationContext context ) {
		if(isValid(literal,context))		return value;
		else							return null;
	}
	
	public void checkValid( String literal, ValidationContext context ) throws DatatypeException {
		
		baseType.checkValid(literal,context);
		
		// this literal is OK as the underlying type. That means it is not equal to
		// the specified value
		throw new DatatypeException(
			DatatypeException.UNKNOWN, localize( ERR_BAD_VALUE, new Object[]{value}) );
	}
	
	public boolean sameValue( Object o1, Object o2 ) {
		return baseType.sameValue(o1,o2);
	}
	
	public int valueHashCode( Object o1 ) {
		return o1.hashCode();
	}
	
	public DatatypeStreamingValidator createStreamingValidator( ValidationContext context ) {
		return new StreamingValidatorImpl(this,context);
	}
	
	public int getIdType() {
		return baseType.getIdType();
	}
	
	public boolean isContextDependent() {
		return baseType.isContextDependent();
	}
	
	
	protected String localize( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.grammar.relaxng.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}
	
	protected final static String ERR_BAD_VALUE = // arg:1
		"ValueType.BadValue";
}
