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

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * "final" component.
 * 
 * @author	Kohsuke Kawaguchi
 */
public final class FinalComponent extends DataTypeImpl {
	/** immediate base type, which may be a concrete type or DataTypeWithFacet */
	public final DataTypeImpl baseType;
	
	protected final int finalValue;
	
	public boolean isAtomType() { return baseType.isAtomType(); }
	
	public FinalComponent( DataTypeImpl baseType, int finalValue ) {
		this( baseType.getName(), baseType, finalValue );
	}
	
	public FinalComponent( String newTypeName, DataTypeImpl baseType, int finalValue ) {
		super( newTypeName, baseType.whiteSpace );
		this.baseType = baseType;
		this.finalValue = finalValue;
	}
	
	public boolean isFinal( int derivationType ) {
		if( (finalValue&derivationType) != 0 )	return true;
		return baseType.isFinal(derivationType);
	}
	
	public ConcreteType getConcreteType() {
		return baseType.getConcreteType();
	}
	
	public String displayName() {
		return baseType.displayName();
	}
	
	public int isFacetApplicable( String facetName ) {
		return baseType.isFacetApplicable(facetName);
	}
	
	public boolean checkFormat( String content, ValidationContext context ) {
		return baseType.checkFormat(content,context);
	}
	
	public Object convertToValue( String content, ValidationContext context ) {
		return baseType.convertToValue(content,context);
	}
	public Class getJavaObjectType() {
		return baseType.getJavaObjectType();
	}
	
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		return baseType.convertToLexicalValue(value,context);
	}
	
	public void diagnoseValue( String content, ValidationContext context ) throws DatatypeException {
		baseType.diagnoseValue(content,context);
	}
	
}
