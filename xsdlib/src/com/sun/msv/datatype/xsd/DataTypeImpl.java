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

/**
 * base implementaion for DataType interface.
 */
public abstract class DataTypeImpl implements DataType
{
	private final String typeName;
	public String getName()	{ return typeName; }
	
	// the majority is atom type
	abstract public boolean isAtomType();

	protected final WhiteSpaceProcessor whiteSpace;
	
	protected DataTypeImpl( String typeName, WhiteSpaceProcessor whiteSpace )
	{
		this.typeName	= typeName;
		this.whiteSpace	= whiteSpace;
	}

	final public Object convertToValueObject( String lexicalValue, ValidationContextProvider context )
	{
		return convertToValue(whiteSpace.process(lexicalValue),context);
	}
	
	/**
	 * converts whitespace-processed lexical value into value object
	 */
	abstract protected Object convertToValue( String content, ValidationContextProvider context );

	
	final public DataTypeErrorDiagnosis diagnose(String content, ValidationContextProvider context)
	{
		return diagnoseValue(whiteSpace.process(content),context);
	}
	
	/** actual 'meat' of diagnose method */
	abstract protected DataTypeErrorDiagnosis diagnoseValue(String content, ValidationContextProvider context)
		throws UnsupportedOperationException;
	

	final public boolean verify( String literal, ValidationContextProvider context )
	{
		// step.1 white space processing
		literal = whiteSpace.process(literal);
		
		if( needValueCheck() )
		{// constraint facet that needs computation of value is specified.
			return convertToValue(literal,context)!=null;
		}
		else
		{// lexical validation is enough.
			return checkFormat(literal,context);
		}
	}
	
	abstract protected boolean checkFormat( String literal, ValidationContextProvider context );
	protected boolean needValueCheck() { return false; }
	
	/**
	 * gets the facet object that restricts the specified facet
	 *
	 * @return null
	 *		if no such facet object exists.
	 */
	protected DataTypeWithFacet getFacetObject( String facetName )
	{
		return null;
	}

	/**
	 * gets the concrete type object of the restriction chain.
	 */
	abstract protected ConcreteType getConcreteType();
	
	
	
	
}
