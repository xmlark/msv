package com.sun.tranquilo.datatype;

/**
 * "hexBinary" and hexBinary-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#hexBinary for the spec
 */
public class HexBinaryType extends BinaryImpl
{
	/** singleton access to the plain hexBinary type */
	public static HexBinaryType theInstance =
		new HexBinaryType("hexBinary",null,null,null);
	
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;
		
		return new HexBinaryType( newName,
			LengthFacet.merge(this,facets),
			PatternFacet.merge(this,facets),
			EnumerationFacet.create(this,facets) );
	}
	
	/**
	 * constructor for derived-type from double by restriction.
	 * 
	 * To derive a datatype by restriction from double, call derive method.
	 * This method is only accessible within this class.
	 */
	private HexBinaryType( String typeName,
					    LengthFacet lengths, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, lengths, pattern, enumeration );
	}
	
	
// hex decoder
//====================================
	
	private static int hexToBin( char ch )
	{
		if( '0'<=ch && ch<='9' )	return ch-'0';
		if( 'A'<=ch && ch<='F' )	return ch-'A'+10;
		if( 'a'<=ch && ch<='f' )	return ch-'a'+10;
		return -1;
	}

	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		final int len = lexicalValue.length();

		// "111" is not a valid hex encoding.
		if( len%2 != 0 )	throw new ConvertionException();

		byte[] out = new byte[len/2];

		for( int i=0; i<len; i+=2 )
		{
			int h = hexToBin(lexicalValue.charAt(i  ));
			int l = hexToBin(lexicalValue.charAt(i+1));
			if( h==-1 || l==-1 )
				throw new ConvertionException();	// illegal character

			out[i/2] = (byte)(h*16+l);
		}

		return new BinaryValueType(out);
	}

	protected boolean checkFormat( String lexicalValue )
	{
		final int len = lexicalValue.length();

		// "111" is not a valid hex encoding.
		if( len%2 != 0 )	return false;

		for( int i=0; i<len; i++ )
			if( hexToBin(lexicalValue.charAt(i))==-1 )
				return false;

		return true;
	}
}
