package com.sun.tranquilo.datatype;

/**
 * base implementaion for DataType interface
 */
abstract class DataTypeImpl implements DataType
{
	// well-known facet name constants
	protected final static String	FACET_LENGTH		= "length";
	protected final static String	FACET_MINLENGTH		= "minLength";
	protected final static String	FACET_MAXLENGTH		= "maxLength";
	protected final static String	FACET_PATTERN		= "pattern";
	protected final static String	FACET_ENUMERATION	= "enumeration";
	protected final static String	FACET_PRECISION		= "precision";
	protected final static String	FACET_SCALE			= "scale";
	protected final static String	FACET_MININCLUSIVE	= "minInclusive";
	protected final static String	FACET_MAXINCLUSIVE	= "maxInclusive";
	protected final static String	FACET_MINEXCLUSIVE	= "minExclusive";
	protected final static String	FACET_MAXEXCLUSIVE	= "maxExclusive";
	protected final static String	FACET_WHITESPACE	= "whiteSpace";
	
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

	final public Object convertToValueObject( String lexicalValue )
	{
		return convertToValue(whiteSpace.process(lexicalValue));
	}
	
	/**
	 * converts whitespace-processed lexical value into value object
	 */
	abstract protected Object convertToValue( String content );


	final public boolean verify( String literal )
	{
		// step.1 white space processing
		literal = whiteSpace.process(literal);
		
		if( needValueCheck() )
		{// constraint facet that needs computation of value is specified.
			return convertToValue(literal)!=null;
		}
		else
		{// lexical validation is enough.
			return checkFormat(literal);
		}
	}
	
	abstract protected boolean checkFormat( String literal );
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
	
	
	
	
	final static private String[][] exclusiveFacetPairs =
		new String[][]{
			new String[]{	FACET_LENGTH,	FACET_MINLENGTH	},
			new String[]{	FACET_LENGTH,	FACET_MAXLENGTH },
			new String[]{	FACET_MAXINCLUSIVE, FACET_MAXEXCLUSIVE },
			new String[]{	FACET_MININCLUSIVE, FACET_MINEXCLUSIVE }
		};
	
	final public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// if no facet is specified, no need to create another object.
		if( facets.isEmpty() )	return this;
		
		// processed facets will be "consumed" (removed from the collection).
		// so we have to make a copy here.
		Facets localCopy = new Facets(facets);
		
		DataTypeImpl r = this;	// start from current datatype
		
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
			if( localCopy.contains( exclusiveFacetPairs[i][0])
			&&  localCopy.contains( exclusiveFacetPairs[i][1]) )
				throw new BadTypeException(
					BadTypeException.ERR_X_AND_Y_ARE_EXCLUSIVE,
					exclusiveFacetPairs[i][0],
					exclusiveFacetPairs[i][1] );
		
		if( localCopy.contains(FACET_PRECISION) )
			r = new PrecisionFacet		( newName, r, localCopy );
		if( localCopy.contains(FACET_SCALE) )
			r = new ScaleFacet			( newName, r, localCopy );
		if( localCopy.contains(FACET_MININCLUSIVE) )
			r = new MinInclusiveFacet	( newName, r, localCopy );
		if( localCopy.contains(FACET_MAXINCLUSIVE) )
			r = new MaxInclusiveFacet	( newName, r, localCopy );
		if( localCopy.contains(FACET_MINEXCLUSIVE) )
			r = new MinExclusiveFacet	( newName, r, localCopy );
		if( localCopy.contains(FACET_MAXEXCLUSIVE) )
			r = new MaxExclusiveFacet	( newName, r, localCopy );
		if( localCopy.contains(FACET_LENGTH) )
			r = new LengthFacet			( newName, r, localCopy );
		if( localCopy.contains(FACET_MINLENGTH) )
			r = new MinLengthFacet		( newName, r, localCopy );
		if( localCopy.contains(FACET_MAXLENGTH) )
			r = new MaxLengthFacet		( newName, r, localCopy );
		if( localCopy.contains(FACET_WHITESPACE) )
			r = new WhiteSpaceFacet		( newName, r, localCopy );
		if( localCopy.contains(FACET_PATTERN) )
			r = new PatternFacet		( newName, r, localCopy );
		if( localCopy.contains(FACET_ENUMERATION) )
			r = new EnumerationFacet	( newName, r, localCopy );
		
		if( !localCopy.isEmpty() )
		{// there exists some unconsumed facet. So signal error.
			throw new BadTypeException(
				BadTypeException.ERR_UNCONSUMED_FACET,
				localCopy.getFacetNames() );
		}
		
					
		// additional facet consistency check
		{
			DataTypeWithFacet o1,o2;
			
			// check that minLength <= maxLength
			o1 = r.getFacetObject(FACET_MAXLENGTH);
			o2 = r.getFacetObject(FACET_MINLENGTH);
			
			if( o1!=null && o2!=null
			&& ((MaxLengthFacet)o1).maxLength < ((MinLengthFacet)o2).minLength )
				throw reportFacetInconsistency(
					newName, o1,FACET_MAXLENGTH, o2,FACET_MINLENGTH );
			
			
			// check that scale <= precision
			o1 = r.getFacetObject(FACET_SCALE);
			o2 = r.getFacetObject(FACET_PRECISION);
			
			if( o1!=null && o2!=null
			&& ((ScaleFacet)o1).scale > ((PrecisionFacet)o2).precision )
				throw reportFacetInconsistency(
					newName, o1,FACET_SCALE, o2,FACET_PRECISION );
			
			// check that minInclusive <= maxInclusive
			checkRangeConsistency( r, FACET_MININCLUSIVE, FACET_MAXINCLUSIVE );
			checkRangeConsistency( r, FACET_MINEXCLUSIVE, FACET_MAXEXCLUSIVE );
			
			// TODO : I'm not sure that the following tow checks should be done or not.
			//			since the spec doesn't have these constraints
			checkRangeConsistency( r, FACET_MININCLUSIVE, FACET_MAXEXCLUSIVE );
			checkRangeConsistency( r, FACET_MINEXCLUSIVE, FACET_MAXINCLUSIVE );
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
	private static void checkRangeConsistency( DataTypeImpl newType,
		String facetName1, String facetName2 )
		throws BadTypeException
	{
		DataTypeWithFacet o1 = newType.getFacetObject(FACET_MININCLUSIVE);
		DataTypeWithFacet o2 = newType.getFacetObject(FACET_MAXINCLUSIVE);
			
		if( o1!=null && o2!=null )
		{
			final int c = ((Comparator)o1.getConcreteType()).compare(
				((RangeFacet)o1).limitValue, ((RangeFacet)o2).limitValue );
			if( c==Comparator.GREATER )
				throw reportFacetInconsistency(
					newType.getName(), o1,FACET_MININCLUSIVE, o2,FACET_MAXINCLUSIVE );
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
		DataTypeWithFacet o2, String facetName2 )
	{
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
				facetName1,  o2typeName, facetName2 );
		
		if(o2typeName.equals(newName))
			// vice versa
			return new BadTypeException(
				BadTypeException.ERR_INCONSISTENT_FACETS_2,
				facetName2,  o1typeName, facetName1 );
		
		// this is not possible
		// because facet consistency check is done by every derivation.
		throw new IllegalStateException();
	}
}
