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

import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.math.BigInteger;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

/**
 * derives a new type by adding facets.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class TypeIncubator {
	
	/** storage for non-repeatable facets */
	private final Map impl = new java.util.HashMap();
	
	/** base type */
	private final XSDatatypeImpl baseType;
	
	private static boolean isRepeatable( String facetName ) {
		return facetName.equals("enumeration") || facetName.equals("pattern");
	}
	
	public TypeIncubator( XSDatatype baseType ) {
		this.baseType = (XSDatatypeImpl)baseType;
		if( baseType==null )
			throw new IllegalArgumentException();
	}
	

	/** adds a facet to this set.
	 *
	 * @exception	BadTypeException
	 *		when given facet is already specified
	 */
	public void add( String name, String strValue, boolean fixed,
					 ValidationContext context )
		throws BadTypeException {
		// checks applicability of the facet
		switch( baseType.isFacetApplicable(name) ) {
		case XSDatatypeImpl.APPLICABLE:	break;
		case XSDatatypeImpl.FIXED:
			throw new BadTypeException( BadTypeException.ERR_OVERRIDING_FIXED_FACET, name );
		case XSDatatypeImpl.NOT_ALLOWED:
			throw new BadTypeException( BadTypeException.ERR_NOT_APPLICABLE_FACET, name );
		default:
			throw new Error();
		}
		
		Object value;
		
		if( isValueFacet(name) ) {
			value = baseType.createValue(strValue,context);
			if(value==null)
				throw new BadTypeException(
					BadTypeException.ERR_INVALID_VALUE_FOR_THIS_TYPE,
					strValue, baseType.displayName() );
		}
		else
			value = strValue;
		
		if( isRepeatable(name) ) {
			FacetInfo fi;
			if( impl.containsKey(name) )
				fi = (FacetInfo)impl.get(name);
			else
				impl.put(name, fi=new FacetInfo(new Vector(),fixed));
			
			((Vector)fi.value).add(value);
			// TODO : what shall we do if
			// <enumeration value="a" fixed="true" />
			// <enumeration value="b" fixed="false" />
			fi.fixed |= fixed;
		} else {
			if( impl.containsKey(name) )
				throw new BadTypeException(
					BadTypeException.ERR_DUPLICATE_FACET, name );
			impl.put(name, new FacetInfo(value,fixed));
		}
	}
	
	
	final static private String[][] exclusiveFacetPairs =
		new String[][]{
			new String[]{	XSDatatypeImpl.FACET_LENGTH, XSDatatypeImpl.FACET_MINLENGTH	},
			new String[]{	XSDatatypeImpl.FACET_LENGTH, XSDatatypeImpl.FACET_MAXLENGTH },
			new String[]{	XSDatatypeImpl.FACET_MAXINCLUSIVE, XSDatatypeImpl.FACET_MAXEXCLUSIVE },
			new String[]{	XSDatatypeImpl.FACET_MININCLUSIVE, XSDatatypeImpl.FACET_MINEXCLUSIVE }
		};
	

	/**
	 * derives a new datatype from a datatype by facets that were set.
	 * 
	 * It is completely legal to use null as the newTypeName paratmer,
	 * which means deriving anonymous datatype.
	 *
	 * @exception BadTypeException
	 *		BadTypeException is thrown if derivation is somehow invalid.
	 *		For example, not applicable facets are applied, or enumeration
	 *		has invalid values, ... things like that.
	 */
	public XSDatatypeImpl derive( String newName )
		throws BadTypeException {
		if( baseType.isFinal(XSDatatypeImpl.DERIVATION_BY_RESTRICTION) )
			throw new BadTypeException(BadTypeException.ERR_INVALID_BASE_TYPE, baseType.displayName() );
		
		if( isEmpty() ) {
			// if no facet is specified, and user wants anonymous type,
			// then no need to create another object.
			if( newName==null )	return baseType;
			
			// using FinalComponent as a wrapper.
			return new FinalComponent(newName,baseType,0);
		}
		
		XSDatatypeImpl r = baseType;	// start from current datatype
		
// TODO : make sure that the following interpretation is true
		/*
			several facet consistency check is done here.
			those which are done in this time are:

				- length and (minLength/maxLength) are exclusive
				- maxInclusive and maxExclusive are exclusive
				- minInclusive and minExclusive are exclusive


			those are exclusive within the one restriction;
			that is, it is legal to derive types in the following way:

			<simpleType name="foo">
				<restriction baseType="string">
					<minLength value="3" />
				</restrction>
			</simpleType>

			<simpleType name="bar">
				<restriction baseType="foo">
					<length value="5" />
				</restrction>
			</simpleType>

			although the following is considered as an error

			<simpleType name="bar">
				<restriction baseType="foo">
					<length value="5" />
					<minLength value="3" />
				</restrction>
			</simpleType>


			This method is the perfect place to perform this kind of check. 
		*/
		
		// makes sure that no mutually exclusive facets are specified
		for( int i=0; i<exclusiveFacetPairs.length; i++ )
			if( contains( exclusiveFacetPairs[i][0])
			&&  contains( exclusiveFacetPairs[i][1]) )
				throw new BadTypeException(
					BadTypeException.ERR_X_AND_Y_ARE_EXCLUSIVE,
					exclusiveFacetPairs[i][0],
					exclusiveFacetPairs[i][1] );
		
		if( contains(XSDatatypeImpl.FACET_TOTALDIGITS) )
			r = new TotalDigitsFacet	( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_FRACTIONDIGITS) )
			r = new FractionDigitsFacet	( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_MININCLUSIVE) )
			r = new MinInclusiveFacet	( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_MAXINCLUSIVE) )
			r = new MaxInclusiveFacet	( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_MINEXCLUSIVE) )
			r = new MinExclusiveFacet	( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_MAXEXCLUSIVE) )
			r = new MaxExclusiveFacet	( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_LENGTH) )
			r = new LengthFacet			( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_MINLENGTH) )
			r = new MinLengthFacet		( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_MAXLENGTH) )
			r = new MaxLengthFacet		( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_WHITESPACE) )
			r = new WhiteSpaceFacet		( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_PATTERN) )
			r = new PatternFacet		( newName, r, this );
		if( contains(XSDatatypeImpl.FACET_ENUMERATION) )
			r = new EnumerationFacet	( newName, r, this );
		
					
		// additional facet consistency check
		{
			DataTypeWithFacet o1,o2;
			
			// check that minLength <= maxLength
			o1 = r.getFacetObject(XSDatatypeImpl.FACET_MAXLENGTH);
			o2 = r.getFacetObject(XSDatatypeImpl.FACET_MINLENGTH);
			
			if( o1!=null && o2!=null
			&& ((MaxLengthFacet)o1).maxLength < ((MinLengthFacet)o2).minLength )
				throw reportFacetInconsistency(
					newName, o1,XSDatatypeImpl.FACET_MAXLENGTH, o2,XSDatatypeImpl.FACET_MINLENGTH );
			
			
			// check that scale <= precision
			o1 = r.getFacetObject(XSDatatypeImpl.FACET_FRACTIONDIGITS);
			o2 = r.getFacetObject(XSDatatypeImpl.FACET_TOTALDIGITS);
			
			if( o1!=null && o2!=null
			&& ((FractionDigitsFacet)o1).scale > ((TotalDigitsFacet)o2).precision )
				throw reportFacetInconsistency(
					newName, o1,XSDatatypeImpl.FACET_FRACTIONDIGITS, o2,XSDatatypeImpl.FACET_TOTALDIGITS );
			
			// check that minInclusive <= maxInclusive
			checkRangeConsistency( r, XSDatatypeImpl.FACET_MININCLUSIVE, XSDatatypeImpl.FACET_MAXINCLUSIVE );
			checkRangeConsistency( r, XSDatatypeImpl.FACET_MINEXCLUSIVE, XSDatatypeImpl.FACET_MAXEXCLUSIVE );
			
			// TODO : I'm not sure that the following two checks should be done or not.
			//			since the spec doesn't have these constraints
			checkRangeConsistency( r, XSDatatypeImpl.FACET_MININCLUSIVE, XSDatatypeImpl.FACET_MAXEXCLUSIVE );
			checkRangeConsistency( r, XSDatatypeImpl.FACET_MINEXCLUSIVE, XSDatatypeImpl.FACET_MAXINCLUSIVE );
		}
		
		return r;
	}
	
	/**
	 * check (min,max) facet specification and makes sure that
	 * they are consistent
	 * 
	 * @exception BadTypeException
	 *		when two facets are inconsistent
	 */
	private static void checkRangeConsistency( XSDatatypeImpl newType,
		String facetName1, String facetName2 )
		throws BadTypeException {
		
		DataTypeWithFacet o1 = newType.getFacetObject(facetName1);
		DataTypeWithFacet o2 = newType.getFacetObject(facetName2);
			
		if( o1!=null && o2!=null ) {
			final int c = ((Comparator)o1.getConcreteType()).compare(
				((RangeFacet)o1).limitValue, ((RangeFacet)o2).limitValue );
			if( c==Comparator.GREATER )
				throw reportFacetInconsistency(
					newType.displayName(), o1,facetName1, o2,facetName2 );
		}
	}

	/**
	 * creates a BadTypeException with appropriate error message.
	 *
	 * this method is only useful for reporting facet consistency violation.
	 */
	private static BadTypeException reportFacetInconsistency(
		String newName,
		DataTypeWithFacet o1, String facetName1,
		DataTypeWithFacet o2, String facetName2 ) {
		// analyze the situation further so as to
		// provide better error messages

		String o1typeName = o1.getName();
		String o2typeName = o2.getName();

		if(o1typeName.equals(o2typeName))
			// o1typeName==o2typeName==newName
			return new BadTypeException(
				BadTypeException.ERR_INCONSISTENT_FACETS_1,
				facetName1, facetName2 );
		
		if(o1typeName.equals(newName))
			// o2 must be specified in somewhere in the derivation chain
			return new BadTypeException(
				BadTypeException.ERR_INCONSISTENT_FACETS_2,
				facetName1,  o2.displayName(), facetName2 );
		
		if(o2typeName.equals(newName))
			// vice versa
			return new BadTypeException(
				BadTypeException.ERR_INCONSISTENT_FACETS_2,
				facetName2,  o1.displayName(), facetName1 );
		
		// this is not possible
		// because facet consistency check is done by every derivation.
		throw new IllegalStateException();
	}
	
	private boolean isValueFacet( String facetName ) {
		return facetName.equals(XSDatatypeImpl.FACET_ENUMERATION)
			|| facetName.equals(XSDatatypeImpl.FACET_MAXEXCLUSIVE)
			|| facetName.equals(XSDatatypeImpl.FACET_MINEXCLUSIVE)
			|| facetName.equals(XSDatatypeImpl.FACET_MAXINCLUSIVE)
			|| facetName.equals(XSDatatypeImpl.FACET_MININCLUSIVE);
	}
	
	/**
	 * returns true if that facet is fixed.
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public boolean isFixed( String facetName ) {
		return ((FacetInfo)impl.get(facetName)).fixed;
	}
	
	/**
	 * gets a value of non-repeatable facet
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public Object getFacet( String facetName ) {
		return ((FacetInfo)impl.get(facetName)).value;
	}
	
	/**
	 * gets a value of repeatable facet
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public Vector getVector(String facetName) {
		return (Vector)((FacetInfo)impl.get(facetName)).value;
	}
	
	/**
	 * gets a value of non-repeatable facet as a positive integer
	 *
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 * 
	 * @exception BadTypeException
	 *		if the parameter cannot be parsed as a positive integer
	 */
	public int getPositiveInteger( String facetName )
		throws BadTypeException {
		try {
			// TODO : is this implementation correct?
			int value = Integer.parseInt((String)getFacet(facetName));
			if( value>0 )	return value;
		} catch( NumberFormatException e ) {
			// let's try BigInteger to see if the value is actually positive
			try {
				// if we can parse it in BigInteger, then treat is as Integer.MAX_VALUE
				// this will work for most cases, I suppose.
				if(new BigInteger((String)getFacet(facetName)).signum()>0)
					return Integer.MAX_VALUE;
			} catch(NumberFormatException ee) {;}
		}
		
		throw new BadTypeException(
			BadTypeException.ERR_FACET_MUST_BE_POSITIVE_INTEGER,
			facetName );
	}
	
	/**
	 * gets a value of non-repeatable facet as a non-negative integer
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 * 
	 * @exception BadTypeException
	 *		if the parameter cannot be parsed as a non-negative integer
	 */
	public int getNonNegativeInteger( String facetName )
		throws BadTypeException {
		try {
			// TODO : is this implementation correct? Can I use Integer.parseInt?
			int value = Integer.parseInt((String)getFacet(facetName));
			if( value>=0 )		return value;
		} catch( NumberFormatException e ) { ; }
		
		throw new BadTypeException(
			BadTypeException.ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER,
			facetName );
	}
	
	/** checks if the specified facet was added to this map  */
	private boolean contains( String facetName ) {
		return impl.containsKey(facetName);
	}
	
	/** returns true if no facet is added */
	public boolean isEmpty() {
		return impl.isEmpty();
	}
	
	
	private static class FacetInfo {
		public Object value;
		public boolean fixed;
		public FacetInfo( Object value, boolean fixed ) {
			this.value = value;
			this.fixed = fixed;
		}
	}
	
	/** dumps the contents to the given object.
	 * 
	 * this method is for debug use only.
	 */
	public void dump( java.io.PrintStream out ) {
		Iterator itr = impl.keySet().iterator();
		while(itr.hasNext()) {
			String facetName = (String)itr.next();
			FacetInfo fi = (FacetInfo)impl.get(facetName);
			
			if(fi.value instanceof Vector) {
				out.println( facetName + " :");
				Vector v = (Vector)fi.value;
				for( int i=0; i<v.size(); i++ )
					out.println( "  " +v.elementAt(i) );
			}
			else
				out.println( facetName + " : " + fi.value );
		}
	}
	
	/** gets names of the facets in this object
	 *
	 * this method is used to produce error messages
	 */
	public String getFacetNames() {
		String r="";
		Iterator itr = impl.keySet().iterator();
		while(itr.hasNext()) {
			if(r.length()!=0)	r+=", ";
			r += (String)itr.next();
		}
		
		return r;
	}
}
