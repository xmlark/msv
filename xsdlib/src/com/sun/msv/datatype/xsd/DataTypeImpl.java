package com.sun.tranquilo.datatype;

/**
 * default implementaion for DataType interface
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
	public boolean isAtomType() { return true; }

	protected final WhiteSpaceProcessor whiteSpace;
	
	protected DataTypeImpl( String typeName, WhiteSpaceProcessor whiteSpace )
	{
		this.typeName	= typeName;
		this.whiteSpace	= whiteSpace;
	}
	
	protected DataTypeImpl( String typeName )
	{
		this( typeName, WhiteSpaceProcessor.theCollapse );
	}

	final public Object convertToValueObject( String lexicalValue )
	{
		return convertToValue(whiteSpace.process(lexicalValue));
	}
	
	/**
	 * convert whitespace-processed lexical value into value object
	 */
	abstract protected Object convertToValue( String content );

	final public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// if no facet is specified, no need to create another object.
		if( facets.isEmpty() )	return this;
		
		// processed facets will be "consumed" (removed from the collection).
		// so we have to make a copy here.
		Facets localCopy = new Facets(facets);
		
		DataTypeImpl r = this;	// start from current datatype
		
		// TODO : length facet
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
		
		return r;
	}
	
//	/** derived classes should implement this method instead of derive method */
//	protected abstract DataType _derive( String newName, Facets facets )
//		throws BadTypeException;

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
}
