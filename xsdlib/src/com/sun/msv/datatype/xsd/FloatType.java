package com.sun.tranquilo.datatype;

/**
 * "float" and float-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#float for the spec
 */
public class FloatType extends DataTypeImpl
{
	/** singleton access to the plain float type */
	public static FloatType theInstance =
		new FloatType("float",null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks constraints over value space
		FloatValueType value;
		try
		{
			value = (FloatValueType)convertValue(content);
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
	{// TODO : quick hack. Spec doesn't allow me directly to use FloatValueType.valueOf method
		
		/* Incompatibilities of XML Schema's float "xfloat" and Java's float "jfloat"
		
			* jfloat.valueOf ignores leading and trailing whitespaces,
			  whereas this is not allowed in xfloat.
			* jfloat.valueOf allows "float type suffix" (f, F) to be
			  appended after float literal (e.g., 1.52e-2f), whereare
			  this is not the case of xfloat.
		
			gray zone
			---------
			* jfloat allows ".523". And there is no clear statement that mentions
			  this case in xfloat. Although probably this is allowed.
			* 
		*/
		
		try
		{
			if(lexicalValue.equals("NaN"))	return new FloatValueType(FloatValueType.NaN);
			if(lexicalValue.equals("INF"))	return new FloatValueType(FloatValueType.POSITIVE_INFINITY);
			if(lexicalValue.equals("-INF"))	return new FloatValueType(FloatValueType.NEGATIVE_INFINITY);
			
			if(lexicalValue.length()==0)
				throw new ConvertionException();
			if(!isDigitOrPeriod(lexicalValue.charAt(0)))
				throw new ConvertionException();
			if(!isDigitOrPeriod(lexicalValue.charAt(lexicalValue.length()-1)))
				throw new ConvertionException();
			
			// these screening process is necessary due to the wobble of Float.valueOf method
			return FloatValueType.valueOf(lexicalValue);
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

		return new FloatType( newName,
			RangeFacet.merge(this,facets),
			PatternFacet.merge(this,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	private final RangeFacet range;
	private final PatternFacet pattern;
	private final EnumerationFacet enumeration;
	
	/**
	 * constructor for derived-type from float by restriction.
	 * 
	 * To derive a datatype by restriction from float, call derive method.
	 * This method is only accessible within this class.
	 */
	private FloatType( String typeName, 
					    RangeFacet range, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName );
		this.range		= range;
		this.pattern	= pattern;
		this.enumeration= enumeration;
	}
	
}
