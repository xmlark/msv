package com.sun.tranquilo.datatype;

/**
 * "int" and int-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#int for the spec
 */
public class IntType extends DataTypeImpl implements PrecisionScaleInterpreter
{
	/** singleton access to the plain int type */
	public static IntType theInstance =
		new IntType("int",null,null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks constraints over value space
		Integer value;
		try
		{
			value = (Integer)convertValue(content);
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
			return new Integer(lexicalValue);
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

		return new IntType( newName,
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
	 * constructor for derived-type from int by restriction.
	 * 
	 * To derive a datatype by restriction from int, call derive method.
	 * This method is only accessible within this class.
	 */
	protected IntType( String typeName, 
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
        int v = ((Integer)o).intValue();
        int p=0;
        while(v!=0)
        {
            v/=10;
            p++;
        }
        return p;
    }
}
