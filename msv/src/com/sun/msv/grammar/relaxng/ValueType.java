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

/**
 * Datatype created by &lt;value&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValueType implements DataType, java.io.Serializable {
	
	/** this datatype matches this value only. */
	public final Object value;
	
	/** this type is responsible for the equality test. */
	public final DataType baseType;
	
	public ValueType( DataType type, Object value ) {
		this.baseType = type;
		this.value = value;
	}
	
	public boolean allows( String literal, ValidationContext context ) {
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
		if(allows(literal,context))		return value;
		else							return null;
	}
	
	public DataTypeException diagnose( String literal, ValidationContext context ) {
		DataTypeException diag;
		diag = baseType.diagnose(literal,context);
		if(diag!=null)		return diag;
		
		// this literal is OK as the underlying type. That means it is not equal to
		// the specified value
		return new DataTypeException(
			this,literal,DataTypeException.UNKNOWN,
			localize( ERR_BAD_VALUE, new Object[]{value}) );
	}
	
	public boolean sameValue( Object o1, Object o2 ) {
		return baseType.sameValue(o1,o2);
	}
	
	
	protected String localize( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.grammar.relaxng.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}
	
	protected final static String ERR_BAD_VALUE = // arg:1
		"ValueType.BadValue";
}
