/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import java.util.StringTokenizer;

/**
 * List type.
 * 
 * type of the value object is {@link ListValueType}.
 * 
 * @author	Kohsuke Kawaguchi
 */
public final class ListType extends ConcreteType implements Discrete {
	
	/**
	 * derives a new datatype from atomic datatype by list
	 */
	public ListType( String newTypeName, DataTypeImpl itemType )
		throws BadTypeException {
		super(newTypeName);
		
		if(itemType.isFinal( DERIVATION_BY_LIST ))
			// derivation by list is not applicable
			throw new BadTypeException( BadTypeException.ERR_INVALID_ITEMTYPE );
		
		this.itemType = itemType;
	}
	
	/** atomic base type */
	final public DataTypeImpl itemType;

	// list type is not an atom type.
	public final boolean isAtomType() { return false; }

	public final boolean isFinal( int derivationType ) {
		// cannot derive by list from list.
		if(derivationType==DERIVATION_BY_LIST)	return true;
		return itemType.isFinal(derivationType);
	}
	
	public final int isFacetApplicable( String facetName ) {
		// pattern facet is not appliable
		if( facetName.equals(FACET_LENGTH)
		||	facetName.equals(FACET_MINLENGTH)
		||	facetName.equals(FACET_MAXLENGTH)
		||	facetName.equals(FACET_ENUMERATION) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	protected final boolean checkFormat( String content, ValidationContextProvider context ) {
		// Are #x9, #xD, and #xA allowed as a separator, or not?
		StringTokenizer tokens = new StringTokenizer(content);
		
		while( tokens.hasMoreTokens() )
			if(!itemType.verify(tokens.nextToken(),context))	return false;
		
		return true;
	}
	
	public Object convertToValue( String content, ValidationContextProvider context ) {
		// StringTokenizer correctly implements the semantics of whiteSpace="collapse"
		StringTokenizer tokens = new StringTokenizer(content);
		
		Object[] values = new Object[tokens.countTokens()];
		int i=0;
		
		while( tokens.hasMoreTokens() ) {
			if( ( values[i++] = itemType.convertToValue(tokens.nextToken(),context) )==null )
				return null;
		}
			
		return new ListValueType(values);
	}
	
	public final int countLength( Object value ) {
		// for list type, length is a number of items.
		return ((ListValueType)value).values.length;
	}
	
	public String convertToLexicalValue( Object value, SerializationContextProvider context ) {
		if(!(value instanceof ListValueType))
			throw new IllegalArgumentException();
		
		ListValueType lv = (ListValueType)value;
	
		StringBuffer r = new StringBuffer();
		for( int i=0; i<lv.values.length; i++ ) {
			if(i!=0)	r.append(' ');
			r.append( itemType.convertToLexicalValue(lv.values[i],context) );
		}
		return r.toString();
	}
	
	/** The current implementation detects which list item is considered wrong. */
	protected DataTypeErrorDiagnosis diagnoseValue(String content, ValidationContextProvider context) {
		// StringTokenizer correctly implements the semantics of whiteSpace="collapse"
		StringTokenizer tokens = new StringTokenizer(content);
		
		while( tokens.hasMoreTokens() )
		{
			String token = tokens.nextToken();
			DataTypeErrorDiagnosis err = itemType.diagnose(token,context);
			if(err!=null) return err;
		}
		
		return null;	// accepted
	}

}
