/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

/**
 * "base64Binary" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#base64Binary for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class Base64BinaryType extends BinaryBaseType
{
	public static final Base64BinaryType theInstance = new Base64BinaryType();
	private Base64BinaryType() { super("base64Binary"); }
	
	
	
// base64 decoder
//====================================
	
	private static final byte[] decodeMap = initDecodeMap();
	private static final byte PADDING = 127;

	private static byte[] initDecodeMap()
	{
		byte[] map = new byte[256];
		int i;
		for( i=0; i<256; i++ )		map[i] = -1;

		for( i='A'; i<='Z'; i++ )	map[i] = (byte)(i-'A');
		for( i='a'; i<='z'; i++ )	map[i] = (byte)(i-'a'+26);
		for( i='0'; i<='9'; i++ )	map[i] = (byte)(i-'0'+52);
		map['+'] = 62;
		map['/'] = 63;
		map['='] = PADDING;

		return map;
	}

	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		final byte[] buf = lexicalValue.getBytes();

		final int outlen = calcLength(buf);
		if( outlen==-1 )	return null;
		final byte[] out = new byte[outlen];
		int o=0;

		final int len = buf.length;
		int i;

		final byte[] quadruplet = new byte[4];
		int q=0;

		// convert each quadruplet to three bytes.
		for( i=0; i<len; i++ )
		{
			byte v = decodeMap[buf[i]];
			if( v!=-1 )
				quadruplet[q++] = v;

			if(q==4)
			{// quadruplet is now filled.
				out[o++] = (byte)((quadruplet[0]<<2)|(quadruplet[1]>>4));
				if( quadruplet[2]!=PADDING )
					out[o++] = (byte)((quadruplet[1]<<4)|(quadruplet[2]>>2));
				if( quadruplet[3]!=PADDING )
					out[o++] = (byte)((quadruplet[2]<<6)|(quadruplet[3]));
				q=0;
			}
		}

		// assertion failed.
		if(q!=0)	throw new IllegalStateException();

		return new BinaryValueType(out);
	}

	protected boolean checkFormat( String lexicalValue, ValidationContextProvider context )
	{
		return calcLength( lexicalValue.getBytes() ) != -1;
	}

	/**
	 * computes the length of binary data.
	 * 
	 * This function also performs format check.
	 * @return	-1		if format is illegal.
	 * 
	 */
	private static int calcLength( final byte[] buf )
	{
		final int len = buf.length;
		int base64count=0, paddingCount=0;
		int i;

		for( i=0; i<len; i++ )
		{
			if( buf[i]=='=' )	// decodeMap['=']!=-1, so we have to check this first.
				break;
			if( decodeMap[buf[i]]!=-1 )
				base64count++;
		}

		// once we saw '=', nothing but '=' can be appeared.
		for( ; i<len; i++ )
		{
			if( buf[i]=='=' )
			{
				paddingCount++;
				continue;
			}
			if( decodeMap[buf[i]]!=-1 )
				return -1;
		}

		// no more than two paddings are allowed.
		if( paddingCount > 2 )		return -1;
		// characters must be a multiple of 4.
		if( (base64count+paddingCount)%4 != 0 )	return -1;

		return ((base64count+paddingCount)/4)*3-paddingCount;
	}
}
