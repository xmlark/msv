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
 * base class for those types which can be used by itself (int,uriReference,string, etc) .
 * 
 * @author	Kohsuke Kawaguchi
 */
public abstract class ConcreteType extends DataTypeImpl
{
	protected ConcreteType( String typeName, WhiteSpaceProcessor whiteSpace )
	{
		super( typeName, whiteSpace );
	}
	
	protected ConcreteType( String typeName )
	{
		this( typeName, WhiteSpaceProcessor.theCollapse );
	}
	
	final protected ConcreteType getConcreteType()
	{
		return this;
	}

	public boolean isAtomType()
	{// all but ListType are atom types.
		return true;
	}
	
	public boolean isFinal( int derivationType )
	{// allow derivation by default.
		return false;
	}

	// default implementation for concrete type. somewhat shabby.
	protected DataTypeErrorDiagnosis diagnoseValue(String content, ValidationContextProvider context)
	{
		if(checkFormat(content,context))	return null;
		
		return new DataTypeErrorDiagnosis(this,content,-1,
			DataTypeErrorDiagnosis.ERR_INAPPROPRIATE_FOR_TYPE, getName() );
	}
}
