/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.datatype.*;

/**
 * Datatype created by &lt;string&gt; element.
 */
public class TypedString implements DataType
{
	/** this type only matches this string */
	public final String value;
	/** true indicates that whiteSpace should be preserved. */
	protected final boolean preserveWhiteSpace;
	
	public TypedString( String value, boolean preserveWhiteSpace )
	{
		if(preserveWhiteSpace)
			this.value = value;
		else
			this.value = WhiteSpaceProcessor.theCollapse.process(value);
		
		this.preserveWhiteSpace = preserveWhiteSpace;
	}

	public int isFacetApplicable( String facetName )
	{
		// again derivation will never be required.
		throw new UnsupportedOperationException();
	}

	public Object convertToValueObject( String literal, ValidationContextProvider context )
	{
		if(!preserveWhiteSpace)
			literal = WhiteSpaceProcessor.theCollapse.process(literal);
		
		if(value.equals(literal))	return literal;
		else						return null;
	}
	
	public boolean isAtomType() { return true; }
	public boolean isFinal(int t) { return true; }
	
	public boolean verify( String literal, ValidationContextProvider context )
	{
		return convertToValueObject(literal,context)!=null;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content, ValidationContextProvider context )
	{
		if( convertToValueObject(content,context)!=null )	return null;
		
		return new DataTypeErrorDiagnosis(
			this, content,-1,
			Localizer.localize(DIAG_TYPED_STRING,value) );
	}
	
	public String getName() { return null; }
	public String displayName() { return "TREX built-in string"; }
	
	public static final String DIAG_TYPED_STRING =
		"TypedString.Diagnosis";
}
