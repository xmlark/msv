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

import com.sun.msv.datatype.DatabindableDatatype;
import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.helpers.StreamingValidatorImpl;

/**
 * Datatype created by &lt;string&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypedString implements DatabindableDatatype {
	
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

	public Object createValue( String literal, ValidationContext context ) {
		if(!preserveWhiteSpace)
			literal = WhiteSpaceProcessor.theCollapse.process(literal);
		
		if(value.equals(literal))	return literal;
		else						return null;
	}
	
	public Object createJavaObject( String literal, ValidationContext context ) {
		return createValue(literal,context);
	}
	public Class getJavaObjectType() {
		return String.class;
	}
	
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if( value instanceof String )
			return (String)value;
		else
			throw new IllegalArgumentException();
	}
	
	public boolean isValid( String literal, ValidationContext context ) {
		return createValue(literal,context)!=null;
	}
	
	public void checkValid( String content, ValidationContext context ) throws DatatypeException {
		if( createValue(content,context)!=null )	return;
		
		throw new DatatypeException(
			DatatypeException.UNKNOWN,
			Localizer.localize(DIAG_TYPED_STRING,value) );
	}
	
	public DatatypeStreamingValidator createStreamingValidator( ValidationContext context ) {
		return new StreamingValidatorImpl(this,context);
	}
	
// stubs
	public final boolean sameValue( Object o1, Object o2 ) {
		return o1.equals(o2);
	}
	public final int valueHashCode( Object o ) {
		return o.hashCode();
	}
	
	
	
	public String getName() { return null; }
	public String displayName() { return "TREX built-in string"; }
	
	public static final String DIAG_TYPED_STRING =
		"TypedString.Diagnosis";
}
