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

import java.io.Serializable;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DataTypeException;

/**
 * base implementaion for DataType interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class DataTypeImpl implements DataType {
	
	private final String typeName;
	public String getName()	{ return typeName; }
	
	// the majority is atom type
	abstract public boolean isAtomType();

	/** this field characterizes how this datatype treats white space. */
	public final WhiteSpaceProcessor whiteSpace;
	
	protected DataTypeImpl( String typeName, WhiteSpaceProcessor whiteSpace ) {
		this.typeName	= typeName;
		this.whiteSpace	= whiteSpace;
	}

	final public Object createValue( String lexicalValue, ValidationContext context ) {
		return convertToValue(whiteSpace.process(lexicalValue),context);
	}
	
	public Object createJavaObject( String literal, ValidationContext context ) {
		return createValue(literal,context);
	}
	
	/**
	 * converts whitespace-processed lexical value into value object
	 */
	abstract protected Object convertToValue( String content, ValidationContext context );

	
	final public DataTypeException diagnose(String content, ValidationContext context) {
		return diagnoseValue(whiteSpace.process(content),context);
	}
	
	/** actual 'meat' of diagnose method */
	abstract protected DataTypeException diagnoseValue(String content, ValidationContext context)
		throws UnsupportedOperationException;
	

	final public boolean allows( String literal, ValidationContext context ) {
		// step.1 white space processing
		literal = whiteSpace.process(literal);
		
		if( needValueCheck() )
			// constraint facet that needs computation of value is specified.
			return convertToValue(literal,context)!=null;
		else
			// lexical validation is enough.
			return checkFormat(literal,context);
	}
	
	abstract protected boolean checkFormat( String literal, ValidationContext context );
	protected boolean needValueCheck() { return false; }
	
	/**
	 * gets the facet object that restricts the specified facet
	 *
	 * @return null
	 *		if no such facet object exists.
	 */
	public DataTypeWithFacet getFacetObject( String facetName ) {
		return null;
	}

	/**
	 * gets the concrete type object of the restriction chain.
	 */
	abstract public ConcreteType getConcreteType();
	
	
	public final boolean sameValue( Object o1, Object o2 ) {
		return o1.equals(o2);
	}



	
	public static String localize( String prop, Object[] args ) {
		return java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.msv.datatype.Messages").getString(prop),
			args );
	}
	
	public static String localize( String prop ) {
		return localize( prop, null );
	}
	public static String localize( String prop, Object arg1 ) {
		return localize( prop, new Object[]{arg1} );
	}
	public static String localize( String prop, Object arg1, Object arg2 ) {
		return localize( prop, new Object[]{arg1,arg2} );
	}
	
	public static final String ERR_INAPPROPRIATE_FOR_TYPE =
		"DataTypeErrorDiagnosis.InappropriateForType";
	public static final String ERR_TOO_MUCH_PRECISION =
		"DataTypeErrorDiagnosis.TooMuchPrecision";
	public static final String ERR_TOO_MUCH_SCALE =
		"DataTypeErrorDiagnosis.TooMuchScale";
	public static final String ERR_ENUMERATION =
		"DataTypeErrorDiagnosis.Enumeration";
	public static final String ERR_ENUMERATION_WITH_ARG =
		"DataTypeErrorDiagnosis.Enumeration.Arg";
	public static final String ERR_OUT_OF_RANGE =
		"DataTypeErrorDiagnosis.OutOfRange";
	public static final String ERR_LENGTH =
		"DataTypeErrorDiagnosis.Length";
	public static final String ERR_MINLENGTH =
		"DataTypeErrorDiagnosis.MinLength";
	public static final String ERR_MAXLENGTH =
		"DataTypeErrorDiagnosis.MaxLength";
	public static final String ERR_PATTERN_1 =
		"DataTypeErrorDiagnosis.Pattern.1";
	public static final String ERR_PATTERN_MANY =
		"DataTypeErrorDiagnosis.Pattern.Many";
}
