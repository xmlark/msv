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
 * RELAX NG DTD compatibility datatype library.
 * 
 * This implementation relies on Sun XML Datatypes Library.
 * Compatibility datatypes library available through
 * <code>http://relaxng.org/ns/compatibility/datatypes/0.9</code>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CompatibilityDatatypeLibrary implements DatatypeLibrary {
	
	/** namespace URI of the compatibility datatypes library. */
	public static final String namespaceURI = "http://relaxng.org/ns/compatibility/datatypes/0.9";
	
	public Datatype createDatatype( String name ) throws DatatypeException {
		if( name.equals("ID") )
			return com.sun.msv.datatype.xsd.NcnameType.theInstance;
		if( name.equals("IDREF") )
			return com.sun.msv.datatype.xsd.NcnameType.theInstance;
		if( name.equals("IDREFS") )
			return com.sun.msv.grammar.IDREFType.theIDREFSinstance;
		
		throw new DatatypeException("undefined built-in type:"+name);
	}
	
	public DatatypeBuilder createDatatypeBuilder( String name ) throws DatatypeException {
		return new DatatypeBuilderImpl( createDatatype(name) );
	}
}
