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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibrary;

/**
 * DataTypeLibrary implementation for Sun XML Datatypes Library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeLibraryImpl implements DatatypeLibrary {
	
	public Datatype createDatatype( String typeName ) {
		return DataTypeFactory.getTypeByName(typeName);
	}
	
	public DatatypeBuilder createDatatypeBuilder( String typeName ) {
		DataTypeImpl base = DataTypeFactory.getTypeByName(typeName);
		if(base==null)	return null;
		return new TypeIncubator(base);
	}
}
