package com.sun.tranquilo.datatype;

/**
 * "hexBinary" and hexBinary-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#hexBinary for the spec
 */
public class HexBinaryType extends BinaryBaseType
{
	public static final HexBinaryType theInstance = new HexBinaryType();
	private HexBinaryType() { super("hexBinary"); }
	
	
// hex decoder
//====================================
	
	private static int hexToBin( char ch )
	{
		if( '0'<=ch && ch<='9' )	return ch-'0';
		if( 'A'<=ch && ch<='F' )	return ch-'A'+10;
		if( 'a'<=ch && ch<='f' )	return ch-'a'+10;
		return -1;
	}

	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		final int len = lexicalValue.length();

		// "111" is not a valid hex encoding.
		if( len%2 != 0 )	return null;

		byte[] out = new byte[len/2];

		for( int i=0; i<len; i+=2 )
		{
			int h = hexToBin(lexicalValue.charAt(i  ));
			int l = hexToBin(lexicalValue.charAt(i+1));
			if( h==-1 || l==-1 )
				return null;	// illegal character

			out[i/2] = (byte)(h*16+l);
		}

		return new BinaryValueType(out);
	}

	protected boolean checkFormat( String lexicalValue, ValidationContextProvider context )
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
