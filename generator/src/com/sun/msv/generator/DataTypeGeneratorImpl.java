/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.generator;

import com.sun.tranquilo.datatype.*;
import java.util.Random;
import com.sun.xml.util.XmlChars;
import com.sun.tranquilo.datatype.XmlNames;

/**
 * default implementation of DataTypeGenerator.
 * 
 * You may want to override this class to implement custom generator for
 * unimplemented datatype or datatype local to your schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeGeneratorImpl implements DataTypeGenerator
{
	private final Random random;
	
	public DataTypeGeneratorImpl( Random random ) { this.random = random; }
	public DataTypeGeneratorImpl() { this(new Random()); }
	
	/**
	 * if this flag is set to true, then non-ASCII characters will not be used.
	 */
	public boolean asciiOnly = false;

	public String generate( DataType dt )
	{
		
		if( dt instanceof AnyURIType )
		{// anyURI
			String r;
			do
			{
				r = generateString();	// any string should work
			}while(!dt.verify(r,null));
			return r;
		}
		
		if( dt instanceof NonNegativeIntegerType )
		{
			long r;
			do { r=random.nextLong(); }while(r<0);
			return Long.toString(r);
		}
		
		if( dt instanceof PositiveIntegerType )
		{
			long r;
			do { r=random.nextLong(); }while(r<=0);
			return Long.toString(r);
		}
		
		if( dt.getClass()==IntType.class )		return Long.toString( random.nextInt() );
		if( dt.getClass()==LongType.class )		return Long.toString( random.nextLong() );
		if( dt.getClass()==IntegerType.class )	return Long.toString( random.nextLong() );
		if( dt.getClass()==StringType.class )	return generateString();
		if( dt.getClass()==TokenType.class )	return generateString();
		if( dt.getClass()==NormalizedStringType.class )	return generateString();
		if( dt.getClass()==NmtokenType.class )	return generateNMTOKEN();
		if( dt.getClass()==NcnameType.class )	return generateNCName();
		
		if( dt instanceof EnumerationFacet )
		{	// if the outer most facet is enumeration,
			// then we have a good chance of generating a value.
			Object[] items = ((EnumerationFacet)dt).values.toArray();
			String s = items[random.nextInt(items.length)].toString();
			
			if( dt.verify(s,null) )		return s;
		}
		
		if( dt instanceof FinalComponent )	// ignore final component
			return generate( ((FinalComponent)dt).baseType );
		
		if( dt instanceof com.sun.tranquilo.grammar.relax.EmptyStringType )
			return "";
		
		if( dt instanceof com.sun.tranquilo.grammar.trex.TypedString )
			return ((com.sun.tranquilo.grammar.trex.TypedString)dt).value;
		
		
		throw new Error("unsupported datatype: " + dt.displayName() );
	}
	
	protected String generateNMTOKEN()
	{// string
		int len = random.nextInt(15)+1;
		String r = "";
		for( int i=0; i<len; i++ )
		{
			char ch;
			do
			{
				if( asciiOnly )
					ch = (char)random.nextInt(128);
				else
					ch = (char)random.nextInt(Character.MAX_VALUE);
			}while( !XmlChars.isNameChar(ch) );
			r += ch;
		}
		return r;
	}
	
	protected String generateNCName()
	{
		String r;
		do {
			r = generateNMTOKEN();
		}while( !XmlNames.isNCName(r) );
		return r;
	}
	
	protected String generateString()
	{// string
		int len = random.nextInt(16);
		String r = "";
		for( int i=0; i<len; i++ )
		{
			char ch;
			do
			{
				if( asciiOnly )
					ch = (char)random.nextInt(128);
				else
					ch = (char)random.nextInt(Character.MAX_VALUE);
			}while( !XmlChars.isChar(ch) );
			r += ch;
		}
		return r;
	}
}
