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

import java.io.Serializable;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.DatatypeStreamingValidator;
import org.relaxng.datatype.helpers.StreamingValidatorImpl;
import com.sun.msv.datatype.SerializationContext;

/**
 * base implementaion for DataType interface.
 * 
 * This class should be considered as the implementation-detail, and 
 * applications should not access on this class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class XSDatatypeImpl implements XSDatatype {
	
	private final String typeName;
	public String getName()	{ return typeName; }
	
	/** this field characterizes how this datatype treats white space. */
	public final WhiteSpaceProcessor whiteSpace;
	
	protected XSDatatypeImpl( String typeName, WhiteSpaceProcessor whiteSpace ) {
		this.typeName	= typeName;
		this.whiteSpace	= whiteSpace;
	}

	final public Object createValue( String lexicalValue, ValidationContext context ) {
		return convertToValue(whiteSpace.process(lexicalValue),context);
	}
	
//
// DatabindableDatatype implementation
//===========================================
// The default implementation yields to the createValue method and
// the convertToLexicalValue method. If a derived class overrides the
// createJavaObject method, then it must also override the serializeJavaObject method.
//
	public Object createJavaObject( String literal, ValidationContext context ) {
		return createValue(literal,context);
	}
	public String serializeJavaObject( Object value, SerializationContext context ) {
		String literal = convertToLexicalValue( value, context );
		if(!isValid( literal, serializedValueChecker ))
			return null;
		else
			return literal;
	}
	private static final ValidationContext serializedValueChecker =
		new ValidationContext(){
			public boolean isNotation( String s ) { return true; }
			public boolean isUnparsedEntity( String s ) { return true; }
			public String resolveNamespacePrefix( String ns ) { return "abc"; }
		};
	
	/**
	 * converts whitespace-processed lexical value into value object
	 */
	abstract protected Object convertToValue( String content, ValidationContext context );

	
	final public void checkValid(String content, ValidationContext context) throws DatatypeException {
		diagnoseValue(whiteSpace.process(content),context);
	}
	
	/** actual 'meat' of diagnose method */
	abstract protected void diagnoseValue(String content, ValidationContext context) throws DatatypeException;
	

	final public boolean isValid( String literal, ValidationContext context ) {
		// step.1 white space processing
		literal = whiteSpace.process(literal);
		
		if( needValueCheck() )
			// constraint facet that needs computation of value is specified.
			return convertToValue(literal,context)!=null;
		else
			// lexical validation is enough.
			return checkFormat(literal,context);
	}
	
	public DatatypeStreamingValidator createStreamingValidator( ValidationContext context ) {
		return new StreamingValidatorImpl(this,context);
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
		if(o1==null || o2==null)	return false;
		return o1.equals(o2);
	}
	public final int valueHashCode( Object o ) {
		return o.hashCode();
	}


	
	protected final boolean isAtomType() { return false; }
	
	

	
	public static String localize( String prop, Object[] args ) {
		return java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.msv.datatype.xsd.Messages").getString(prop),
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
	public static String localize( String prop, Object arg1, Object arg2, Object arg3 ) {
		return localize( prop, new Object[]{arg1,arg2,arg3} );
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


	
	public static final String ERR_INVALID_ITEMTYPE =
		"BadTypeException.InvalidItemType";
	public static final String ERR_INVALID_MEMBER_TYPE =
		"BadTypeException.InvalidMemberType";
	public static final String ERR_INVALID_BASE_TYPE =
		"BadTypeException.InvalidBaseType";
	public static final String ERR_INVALID_WHITESPACE_VALUE =
		"WhiteSpaceProcessor.InvalidWhiteSpaceValue";
	public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
	
	public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE =
		"EnumerationFacet.InvalidValueForThisType";
	public final static String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER =
		"BadTypeException.FacetMustBeNonNegativeInteger";
	public final static String ERR_FACET_MUST_BE_POSITIVE_INTEGER =
		"BadTypeException.FacetMustBePositiveInteger";
	public final static String ERR_OVERRIDING_FIXED_FACET =
		"BadTypeException.OverridingFixedFacet";
	public final static String ERR_INCONSISTENT_FACETS_1 =
		"InconsistentFacets.1";
	public final static String ERR_INCONSISTENT_FACETS_2 =
		"InconsistentFacets.2";
	public final static String ERR_X_AND_Y_ARE_EXCLUSIVE =
		"XAndYAreExclusive";
	public final static String ERR_LOOSENED_FACET =
		"LoosenedFacet";
	public final static String ERR_SCALE_IS_GREATER_THAN_PRECISION =
		"PrecisionScaleFacet.ScaleIsGraterThanPrecision";
	public static final String ERR_DUPLICATE_FACET =
		"BadTypeException.DuplicateFacet";
	public static final String ERR_NOT_APPLICABLE_FACET =
		"BadTypeException.NotApplicableFacet";
	public static final String ERR_EMPTY_UNION =
		"BadTypeException.EmptyUnion";

}
