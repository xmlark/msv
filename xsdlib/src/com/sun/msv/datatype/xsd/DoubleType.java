package com.sun.tranquilo.datatype;

/**
 * "double" and double-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#double for the spec
 */
public class DoubleType extends DataTypeImpl
{
	/** singleton access to the plain double type */
	public static DoubleType theInstance =
		new DoubleType("double",null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks constraints over value space
		DoubleValueType value;
		try
		{
			value = (DoubleValueType)convertValue(content);
		}
		catch( ConvertionException e ) { return false; }
		
		
		if( range!=null   && !range.verify(value) )				return false;
		if( enumeration!=null && !enumeration.verify(value) )	return false;
		
		return true;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	private static boolean isDigitOrPeriod( char ch )
	{
		if( '0'<=ch && ch<='9' )	return true;
		return ch=='.';
	}
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// TODO : probably the same problems exist as in the case of float
		try
		{
			if(lexicalValue.equals("NaN"))	return new DoubleValueType(DoubleValueType.NaN);
			if(lexicalValue.equals("INF"))	return new DoubleValueType(DoubleValueType.POSITIVE_INFINITY);
			if(lexicalValue.equals("-INF"))	return new DoubleValueType(DoubleValueType.NEGATIVE_INFINITY);
			
			if(lexicalValue.length()==0)
				throw new ConvertionException();
			if(!isDigitOrPeriod(lexicalValue.charAt(0)))
				throw new ConvertionException();
			if(!isDigitOrPeriod(lexicalValue.charAt(lexicalValue.length()-1)))
				throw new ConvertionException();
			
			// these screening process is necessary due to the wobble of Float.valueOf method
			return DoubleValueType.valueOf(lexicalValue);
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

		return new DoubleType( newName,
			RangeFacet.merge(this,this.range,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	private final RangeFacet range;
	private final PatternFacet pattern;
	private final EnumerationFacet enumeration;
	
	/**
	 * constructor for derived-type from double by restriction.
	 * 
	 * To derive a datatype by restriction from double, call derive method.
	 * This method is only accessible within this class.
	 */
	private DoubleType( String typeName, 
					    RangeFacet range, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName );
		this.range		= range;
		this.pattern	= pattern;
		this.enumeration= enumeration;
	}
	
}
