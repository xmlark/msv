/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.ngimpl;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.NmtokenType;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeLibrary;

/**
 * DataTypeLibrary implementation for Sun XML Datatypes Library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeLibraryImpl implements DatatypeLibrary {
	
	public Datatype createDatatype( String typeName ) throws DatatypeException  {
		return getType(typeName);
	}
	
	private XSDatatype getType( String typeName) throws DatatypeException {
		return DatatypeFactory.getTypeByName(typeName);
	}
	
	public DatatypeBuilder createDatatypeBuilder( String typeName ) throws DatatypeException {
		return new DatatypeBuilderImpl( getType(typeName) );
	}
}
