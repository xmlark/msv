package com.sun.tranquilo.datatype;

public class BinaryValueType
{
	/** raw byte data */
	public byte[]	rawData;
	
	public boolean equals( Object o )
	{
		if( o.getClass()!=BinaryValueType.class )	return false;
		
		BinaryValueType rhs = (BinaryValueType)o;
		
		if( rawData.length != rhs.rawData.length )	return false;
		
		int len = rawData.length;
		
		for( int i=0; i<len; i++ )
			if( rawData[i]!=rhs.rawData[i] )	return false;
		
		return true;
	}
	
	public int hashCode()
	{
		if( rawData.length == 0 )	return 293;
		if( rawData.length == 1 )	return rawData[0];
		else	return rawData.length * rawData[0] * rawData[1];
	}
	
	public BinaryValueType( byte[] rawData )
	{
		this.rawData = rawData;
	}
}
