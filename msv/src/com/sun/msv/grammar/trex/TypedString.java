/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex;

import com.sun.msv.datatype.*;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DataTypeException;

/**
 * Datatype created by &lt;string&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypedString implements DataType {
	/** this type only matches this string */
	public final String value;
	/** true indicates that whiteSpace should be preserved. */
	public final boolean preserveWhiteSpace;
	
	public TypedString( String value, boolean preserveWhiteSpace ) {
		if(preserveWhiteSpace)
			this.value = value;
		else
			this.value = WhiteSpaceProcessor.theCollapse.process(value);
		
		this.preserveWhiteSpace = preserveWhiteSpace;
	}

	public int isFacetApplicable( String facetName ) {
		// again derivation will never be required.
		throw new UnsupportedOperationException();
	}

	public Object createValue( String literal, ValidationContext context ) {
		if(!preserveWhiteSpace)
			literal = WhiteSpaceProcessor.theCollapse.process(literal);
		
		if(value.equals(literal))	return literal;
		else						return null;
	}
	
	public Object createJavaObject( String literal, ValidationContext context ) {
		return createValue(literal,context);
	}


	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if( value instanceof String )
			return (String)value;
		else
			throw new IllegalArgumentException();
	}
	
	public boolean isAtomType() { return true; }
	public boolean isFinal(int t) { return true; }
	
	public boolean allows( String literal, ValidationContext context ) {
		return createValue(literal,context)!=null;
	}
	
	public DataTypeException diagnose( String content, ValidationContext context ) {
		if( createValue(content,context)!=null )	return null;
		
		return new DataTypeException(
			this, content,-1,
			Localizer.localize(DIAG_TYPED_STRING,value) );
	}
	
// stubs
	public final boolean sameValue( Object o1, Object o2 ) {
		return o1.equals(o2);
	}
	
	
	
	public String getName() { return null; }
	public String displayName() { return "TREX built-in string"; }
	
	public static final String DIAG_TYPED_STRING =
		"TypedString.Diagnosis";
}
