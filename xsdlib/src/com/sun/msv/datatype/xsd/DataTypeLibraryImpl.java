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

import org.relaxng.datatype.DataType;
import org.relaxng.datatype.DataTypeBuilder;
import org.relaxng.datatype.DataTypeLibrary;

/**
 * DataTypeLibrary implementation for Sun XML Datatypes Library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeLibraryImpl implements DataTypeLibrary {
	
	public DataType getType( String typeName ) {
		return DataTypeFactory.getTypeByName(typeName);
	}
	
	public DataTypeBuilder createDataTypeBuilder( String typeName ) {
		DataTypeImpl base = DataTypeFactory.getTypeByName(typeName);
		if(base==null)	return null;
		return new TypeIncubator(base);
	}
}
