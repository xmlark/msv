package com.sun.tranquilo.datatype;

/**
 * "decimal" and decimal-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#decimal for the spec
 */
public class DecimalType extends DataTypeImpl implements PrecisionScaleInterpreter
{
	/** singleton access to the plain decimal type */
	public static DecimalType theInstance =
		new DecimalType("decimal",null,null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks constraints over value space
		DecimalValueType value;
		try
		{
			value = (DecimalValueType)convertValue(content);
		}
		catch( ConvertionException e ) { return false; }
		
		
		if( range!=null   && !range.verify(value) )							return false;
		if( enumeration!=null && !enumeration.verify(value) )				return false;
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
		try
		{
			// TODO : performs pre-screening.
			// DecimalValueType allows literals like "5.1e-4", which is prohibited in XML Schema.
			return new DecimalValueType(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			throw new ConvertionException();
		}
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new DecimalType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	private final RangeFacet range;
	private final PrecisionScaleFacet precisionScale;
	private final PatternFacet pattern;
	private final EnumerationFacet enumeration;
	
	/**
	 * constructor for derived-type from decimal by restriction.
	 * 
	 * To derive a datatype by restriction from decimal, call derive method.
	 * This method is only accessible within this class.
	 */
	private DecimalType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName );
		this.range			= range;
		this.precisionScale	= precisionScale;
		this.pattern		= pattern;
		this.enumeration	= enumeration;
	}
	
	
	public int getPrecisionForValueObject( Object o )
	{
		return ((DecimalValueType)o).unscaledValue().abs().toString().length();
	}
	
	public int getScaleForValueObject( Object o )
	{
		return ((DecimalValueType)o).scale();
	}

}
