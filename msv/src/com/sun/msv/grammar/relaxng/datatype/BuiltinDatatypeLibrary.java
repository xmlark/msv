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
public class BuiltinDatatypeLibrary implements DatatypeLibrary {
	
	public Datatype createDatatype( String name ) {
		if( name.equals("string") )
			return com.sun.msv.datatype.xsd.StringType.theInstance;
		if( name.equals("token") )
			return com.sun.msv.datatype.xsd.TokenType.theInstance;
		return null;
	}
	
	public DatatypeBuilder createDatatypeBuilder( String name ) {
		Datatype baseType = createDatatype(name);
		if(baseType==null)		return null;
		return new DatatypeBuilderImpl(baseType);
	}
}
