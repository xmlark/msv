/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import com.sun.msv.datatype.*;
import java.util.Random;
import java.util.Map;
import java.util.Set;
import com.sun.xml.util.XmlChars;

/**
 * default implementation of DataTypeGenerator.
 * 
 * You may want to override this class to implement custom generator for
 * unimplemented datatype or datatype local to your schema.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeGeneratorImpl implements DataTypeGenerator {
	private final Random random;
	
	public DataTypeGeneratorImpl( Random random ) { this.random = random; }
	public DataTypeGeneratorImpl() { this(new Random()); }
	
	/**
	 * if this flag is set to true, then non-ASCII characters will not be used.
	 */
	public boolean asciiOnly = false;
	
	
	protected Map generatedValues = new java.util.HashMap();

	public String generate( DataType dt ) {
		String s = _generate(dt);
		
		// memorize generated values so that we can use them later.
		Set vs = (Set)generatedValues.get(dt);
		if(vs==null)
			generatedValues.put(dt, vs=new java.util.HashSet() );
		vs.add(s);
		
		return s;
	}
		
	protected String _generate( DataType dt ) {
		if( dt instanceof AnyURIType ) {
			// anyURI
			String r;
			do {
				r = generateString();	// any string should work
			}while(!dt.verify(r,null));
			return r;
		}
		
		if( dt instanceof NonNegativeIntegerType ) {
			long r;
			do { r=random.nextLong(); }while(r<0);
			return Long.toString(r);
		}
		
		if( dt instanceof PositiveIntegerType ) {
			long r;
			do { r=random.nextLong(); }while(r<=0);
			return Long.toString(r);
		}
		
		if( dt.getClass()==ShortType.class )	return Long.toString( (short)random.nextInt() );
		if( dt.getClass()==IntType.class )		return Long.toString( random.nextInt() );
		if( dt.getClass()==LongType.class )		return Long.toString( random.nextLong() );
		if( dt.getClass()==IntegerType.class )	return Long.toString( random.nextLong() );
		if( dt.getClass()==StringType.class )	return generateString();
		if( dt.getClass()==TokenType.class )	return generateString();
		if( dt.getClass()==NormalizedStringType.class )	return generateString();
		if( dt.getClass()==NmtokenType.class )	return generateNMTOKEN();
		if( dt.getClass()==NcnameType.class )	return generateNCName();
		if( dt.getClass()==NumberType.class )	return generateDecimal();
		if( dt.getClass()==BooleanType.class )	return generateBoolean();
		
		if( dt instanceof FinalComponent )	// ignore final component
			return generate( ((FinalComponent)dt).baseType );
		
		if( dt instanceof com.sun.msv.grammar.relax.EmptyStringType )
			return "";
		
		if( dt instanceof com.sun.msv.grammar.trex.TypedString )
			return ((com.sun.msv.grammar.trex.TypedString)dt).value;
		
		
		
		// getting desparate...
		
		if( dt instanceof DataTypeImpl ) {
			// if it contains EnumerationFacet, we can try that.
			DataTypeImpl dti = (DataTypeImpl)dt;
			EnumerationFacet e = (EnumerationFacet)dti.getFacetObject( dti.FACET_ENUMERATION );
			if(e!=null) {
				Object[] items = e.values.toArray();
				for( int i=0; i<10; i++ ) {
					try {
						return dt.convertToLexicalValue(items[random.nextInt(items.length)],null);
					} catch( Exception x ) { ; }
				}
			}
			
			DataType baseType = dti.getConcreteType();
			
			if( baseType.getClass()==ListType.class )	return generateList(dti);
			
			if( baseType!=dti ) {
				for( int i=0; i<10; i++ ) {
					String s = generate(baseType);
					if(dti.verify(s,null))	return s;
				}
			}
		}
		
		// use previously generated value if such a thing exist.
		Set vs = (Set)generatedValues.get(dt);
		if(vs!=null)
			return (String)vs.toArray()[random.nextInt(vs.size())];
		
		
		
		throw new Error("unable to generate value for this datatype: " + dt.displayName() );
	}
	
	protected String generateNMTOKEN() {
		// string
		int len = random.nextInt(15)+1;
		String r = "";
		for( int i=0; i<len; i++ ) {
			char ch;
			do {
				if( asciiOnly )
					ch = (char)random.nextInt(128);
				else
					ch = (char)random.nextInt(Character.MAX_VALUE);
			}while( !XmlChars.isNameChar(ch) );
			r += ch;
		}
		return r;
	}
	
	protected String generateList(DataTypeImpl dti) {
		ListType base = (ListType)dti.getConcreteType();
		LengthFacet lf = (LengthFacet)dti.getFacetObject(dti.FACET_LENGTH);
		int n;	// compute # of items into this value.
		
		if(lf!=null) {
			n = lf.length;
		} else {
			MaxLengthFacet xlf = (MaxLengthFacet)dti.getFacetObject(dti.FACET_MAXLENGTH);
			int max = (xlf!=null)?xlf.maxLength:16;
			MinLengthFacet nlf = (MinLengthFacet)dti.getFacetObject(dti.FACET_MINLENGTH);
			int min = (nlf!=null)?nlf.minLength:0;
			
			n = random.nextInt(max-min)+min;
		}
		
		String s="";
		for( int i=0; i<n; i++ )
			s += " " + generate(base.itemType) + " ";
		return s;
	}
	
	protected String generateNCName() {
		String r;
		do {
			r = generateNMTOKEN();
		}while( !XmlNames.isNCName(r) );
		return r;
	}
	
	protected String generateDecimal() {
		return random.nextLong()+"."+random.nextInt(1000);
	}
		
	protected String generateBoolean() {
		switch(random.nextInt(4)) {
		case 0:		return "true";
		case 1:		return "false";
		case 2:		return "0";
		case 3:		return "1";
		default:	throw new Error();
		}
	}
		
	protected String generateString() {
		// string
		int len = random.nextInt(16);
		String r = "";
		for( int i=0; i<len; i++ ) {
			char ch;
			do {
				if( asciiOnly )
					ch = (char)random.nextInt(128);
				else
					ch = (char)random.nextInt(Character.MAX_VALUE);
			}while( !XmlChars.isChar(ch) || Character.isISOControl(ch) );
			r += ch;
		}
		return r;
	}
}
