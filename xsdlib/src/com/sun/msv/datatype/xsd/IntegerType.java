package com.sun.tranquilo.datatype;

/**
 * "integer" and integer-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#integer for the spec
 */
public class IntegerType extends DataTypeImpl implements PrecisionScaleInterpreter
{
	/** singleton access to the plain integer type */
	public static IntegerType theInstance =
		new IntegerType("integer",null,null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks constraints over value space
		IntegerValueType value;
		try
		{
			value = (IntegerValueType)convertValue(content);
		}
		catch( ConvertionException e ) { return false; }
		
		
		if( range!=null   && !range.verify(value) )					        return false;
		if( enumeration!=null && !enumeration.verify(value) )		        return false;
		if( precisionScale!=null && !precisionScale.verify(value,this) )	return false;
		
		return true;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// conformance of the lexicalValue with respect to integer's lexical space
		// will be tested in IntegerValueType.
		return new IntegerValueType(lexicalValue);
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new IntegerType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	protected final RangeFacet range;
	protected final PrecisionScaleFacet precisionScale;
	protected final PatternFacet pattern;
	protected final EnumerationFacet enumeration;
	
	
	/**
	 * constructor for derived-type from integer by restriction.
	 * 
	 * To derive a datatype by restriction from integer, call derive method.
	 * This method is only accessible within this class.
	 */
	protected IntegerType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName );
		this.range			= range;
		this.precisionScale	= precisionScale;
		this.pattern		= pattern;
		this.enumeration	= enumeration;
	}
	
    public int getScaleForValueObject( Object o )
    {
        // TODO : do we consider negative scale?
        return 0;
    }
    
    public int getPrecisionForValueObject( Object o )
    {
        return ((IntegerValueType)o).precision();
    }
}
