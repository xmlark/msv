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

import org.relaxng.datatype.ValidationContext;

/**
 * "short" type.
 * 
 * type of the value object is <code>java.lang.Short</code>.
 * See http://www.w3.org/TR/xmlschema-2/#short for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ShortType extends IntegerDerivedType {
	public static final ShortType theInstance = new ShortType("short");
	protected ShortType(String typeName) { super(typeName); }
	
	public XSDatatype getBaseType() {
		return IntType.theInstance;
	}
	
	public Object _createValue( String lexicalValue, ValidationContext context ) {
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try {
			lexicalValue = removeOptionalPlus(lexicalValue);
			return new Short(lexicalValue);
		} catch( NumberFormatException e ) {
			return null;
		}
	}
	public Class getJavaObjectType() {
		return Short.class;
	}
}
