/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

final public class UnionType extends ConcreteType
{
	/**
	 * derives a new datatype from atomic datatypes by union
	 */
	public UnionType( String newTypeName, DataTypeImpl[] memberTypes )
		throws BadTypeException
	{
		super(newTypeName);
		
		if(memberTypes.length==0)
			throw new BadTypeException(BadTypeException.ERR_EMPTY_UNION);
		
		this.memberTypes = memberTypes;
	}
	
	/** member types */
	final private DataTypeImpl[] memberTypes;

	// union type is not an atom type.
	public final boolean isAtomType() { return false; }
	
	public final int isFacetApplicable( String facetName )
	{
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	protected final boolean checkFormat( String content, ValidationContextProvider context )
	{
		for( int i=0; i<memberTypes.length; i++ )
			if( memberTypes[i].checkFormat(content,context) )	return true;
		
		return false;
	}
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		Object o;
		for( int i=0; i<memberTypes.length; i++ )
		{
			o = memberTypes[i].convertToValue(content,context);
			if(o!=null)		return o;
		}
		
		return null;
	}
	
	protected DataTypeErrorDiagnosis diagnoseValue(String content, ValidationContextProvider context)
	{// what is the appropriate implementation for union?
		throw new UnsupportedOperationException();
	}

}