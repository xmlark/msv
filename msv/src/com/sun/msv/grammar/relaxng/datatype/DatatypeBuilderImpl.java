/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relaxng.datatype;

import org.relaxng.datatype.*;

/**
 * DataTypeBuilder implementation.
 * 
 * There is no paramater for any built-in types.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class DataTypeBuilderImpl implements DataTypeBuilder {
	
	private final DataType baseType;
	DataTypeBuilderImpl( DataType baseType ) {
		this.baseType = baseType;
	}
	
	public DataType derive() {
		return baseType;
	}
	
	public void add( String name, String value, ValidationContext context ) {
		throw new DataTypeException(
			localize(ERR_PARAMETER_UNSUPPORTED,null));
	}


	protected String localize( String propertyName, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.msv.grammar.relaxng.Messages").getString(propertyName);
		
	    return java.text.MessageFormat.format(format, args );
	}
	
	protected final static String ERR_PARAMETER_UNSUPPORTED = // arg:0
		"DataTypeBuilderImpl.ParameterUnsupported";
}
