/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relax;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.ConcreteType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.SimpleURType;
import org.relaxng.datatype.ValidationContext;

/**
 * 'emptyString' type of RELAX.
 * 
 * this type accepts nothing but "".
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class EmptyStringType extends ConcreteType {
	
	public static final EmptyStringType theInstance = new EmptyStringType();
	private EmptyStringType() { super("emptyString"); }
	
	
	final public XSDatatype getBaseType() {
		return SimpleURType.theInstance;
	}
	
	public int isFacetApplicable( String facetName ) {
		return NOT_ALLOWED;
	}
	
	public boolean checkFormat( String literal, ValidationContext context ) {
		return literal.equals("");
	}

	public Object _createValue( String lexicalValue, ValidationContext context ) {
		if( lexicalValue.equals("") )	return lexicalValue;
		else							return null;
	}
	
	public String convertToLexicalValue( Object o, SerializationContext context ) {
		if( o.equals("") )	return "";
		else				throw new IllegalArgumentException();
	}
	
	public Class getJavaObjectType() {
		return String.class;
	}
}
