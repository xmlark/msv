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
 * RELAX NG built-in datatypes.
 * 
 * This implementation relies on Sun XML Datatypes Library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BuiltinDataTypeLibrary implements DataTypeLibrary {
	
	public DataType getType( String name ) {
		if( name.equals("string") )
			return com.sun.msv.datatype.StringType.theInstance;
		if( name.equals("token") )
			return com.sun.msv.datatype.TokenType.theInstance;
		return null;
	}
	
	public DataTypeBuilder createDataTypeBuilder( String name ) {
		DataType baseType = getType(name);
		if(baseType==null)		return null;
		return new DataTypeBuilderImpl(baseType);
	}
}
