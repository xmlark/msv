package com.sun.tranquilo.datatype;

/**
 * "long" and long-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#long for the spec
 */
public class LongType extends DataTypeImpl implements PrecisionScaleInterpreter
{
	/** singleton access to the plain long type */
	public static LongType theInstance =
		new LongType("long",null,null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks constraints over value space
		Long value;
		try
		{
			value = (Long)convertValue(content);
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
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			return new Long(lexicalValue);
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

		return new LongType( newName,
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
	 * constructor for derived-type from long by restriction.
	 * 
	 * To derive a datatype by restriction from long, call derive method.
	 * This method is only accessible within this class.
	 */
	protected LongType( String typeName, 
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
    {// TODO : better implementation could be possible
        long v = ((Long)o).longValue();
        int p=0;
        while(v!=0)
        {
            v/=10;
            p++;
        }
        return p;
    }
}
