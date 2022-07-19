/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.generator;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.relaxng.datatype.Datatype;

import com.sun.msv.datatype.xsd.AnyURIType;
import com.sun.msv.datatype.xsd.BooleanType;
import com.sun.msv.datatype.xsd.DoubleType;
import com.sun.msv.datatype.xsd.EnumerationFacet;
import com.sun.msv.datatype.xsd.FinalComponent;
import com.sun.msv.datatype.xsd.FloatType;
import com.sun.msv.datatype.xsd.IntType;
import com.sun.msv.datatype.xsd.IntegerType;
import com.sun.msv.datatype.xsd.LengthFacet;
import com.sun.msv.datatype.xsd.ListType;
import com.sun.msv.datatype.xsd.LongType;
import com.sun.msv.datatype.xsd.MaxLengthFacet;
import com.sun.msv.datatype.xsd.MinLengthFacet;
import com.sun.msv.datatype.xsd.NcnameType;
import com.sun.msv.datatype.xsd.NmtokenType;
import com.sun.msv.datatype.xsd.NonNegativeIntegerType;
import com.sun.msv.datatype.xsd.NormalizedStringType;
import com.sun.msv.datatype.xsd.NumberType;
import com.sun.msv.datatype.xsd.PositiveIntegerType;
import com.sun.msv.datatype.xsd.QnameType;
import com.sun.msv.datatype.xsd.ShortType;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.TokenType;
import com.sun.msv.datatype.xsd.UnionType;
import com.sun.msv.datatype.xsd.UnsignedIntType;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.datatype.xsd.XmlNames;
import com.sun.msv.datatype.xsd.Base64BinaryType;
import com.sun.msv.datatype.xsd.ByteType;
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
	
	public DataTypeGeneratorImpl() { this(new Random()); }
	public DataTypeGeneratorImpl( Random random ) {
		this.random = random;
	}
	
	/**
	 * if this flag is set to true, then non-ASCII characters will not be used.
	 */
	public boolean asciiOnly = false;
	
	/**
	 * map from DataType to Set that holds generated values for types.
	 * This map is used when we fail to generate an appropriate value for a type.
	 */
	protected Map generatedValues = new java.util.HashMap();
	
	/**
	 * set that contains tokens that are found in example files.
	 * This set is used as the last resort to generate a value for a type.
	 */
	protected Set tokens;

	public String generate( Datatype dt, ContextProviderImpl context ) {
		String s=null; int i;

		// obtain previously generated values.
		Set vs = (Set)generatedValues.get(dt);
		if(vs==null) {
			generatedValues.put(dt, vs=new java.util.HashSet() );
			
			// copy values from examples.
			Iterator itr = tokens.iterator();
			while(itr.hasNext()) {
				String token = (String)itr.next();
				try {// we have to be able to verify this without depending on the context.
					if(dt.isValid(token,null))
						vs.add(token);
				}catch(Exception e){}
			}
		}

		if(vs.size()<32 || random.nextBoolean() ) {
			// we need more diversity. generate more.
			// we have to continue producing values, especially for
			// ID values.
			
			for( i=0; i<100; i++ ) {
				s = _generate(dt,context);
				if( s!=null && dt.isValid(s,context) ) {
					// memorize generated values so that we can use them later.
					vs.add(s);
					break;	// this value is OK.
				}
			}
			if(i==100) {
				if( vs.size()==0 )
					// we retried 10 times but failed to generate a value.
					// and no example is available.
					// So this situation is an absolute failure.
					fail(dt);
				else
					s = (String)vs.toArray()[random.nextInt(vs.size())];
			}
		} else {
			// we have enough diversity. use it.
			s = (String)vs.toArray()[random.nextInt(vs.size())];
		}
		
		return s;
	}
		
	/**
	 * actual generation.
	 * this method can return an invalid value.
	 */
	protected String _generate( Datatype dt, ContextProviderImpl context ) {
		if( dt instanceof AnyURIType ) {
			// anyURI
			String r;
			do {
				r = generateString();	// any string should work
			}while(!dt.isValid(r,context));
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
		
		if( dt.getClass()==UnsignedIntType.class )
			return Long.toString( random.nextLong()&0x7FFFFFFF );
		
		if( dt.getClass()== ByteType.class )     return Long.toString( random.nextInt(256) );
		if( dt.getClass()==ShortType.class )	return Long.toString( (short)random.nextInt() );
		if( dt.getClass()==IntType.class )		return Long.toString( random.nextInt() );
		if( dt.getClass()==LongType.class )		return Long.toString( random.nextLong() );
		if( dt instanceof IntegerType )			return Long.toString( random.nextLong() );
		if( dt.getClass()==StringType.class )	return generateString();
		if( dt.getClass()==TokenType.class )	return generateString();
		if( dt.getClass()==NormalizedStringType.class )	return generateString();
		if( dt.getClass()==NmtokenType.class )	return generateNMTOKEN();
		if( dt.getClass()==NcnameType.class )	return generateNCName();
		if( dt.getClass()==NumberType.class )	return generateDecimal();
		if( dt.getClass()==BooleanType.class )	return generateBoolean();
        if( dt.getClass()==Base64BinaryType.class )	return generateBase64Binary();
		if( dt instanceof FloatType || dt instanceof DoubleType )
			return generateFloating();
		
		// TODO: implement this method better.
		if( dt.getClass()==QnameType.class )	return generateNCName();
		
		if( dt instanceof FinalComponent )	// ignore final component
			return generate( ((FinalComponent)dt).baseType, context );
		
		if( dt instanceof com.sun.msv.grammar.relax.EmptyStringType )
			return "";
		
		
		
		// getting desparate...
		
		if( dt instanceof XSDatatypeImpl ) {
			// if it contains EnumerationFacet, we can try that.
			XSDatatypeImpl dti = (XSDatatypeImpl)dt;
			EnumerationFacet e = (EnumerationFacet)dti.getFacetObject( XSDatatype.FACET_ENUMERATION );
			if(e!=null) {
				Object[] items = e.values.toArray();
				for( int i=0; i<10; i++ ) {
					try {
						return dti.convertToLexicalValue(items[random.nextInt(items.length)],context);
					} catch( Exception x ) {  }
				}
			}
			
			XSDatatype baseType = dti.getConcreteType();
			
			if( baseType instanceof ListType )
				return generateList(dti,context);
			if( baseType instanceof UnionType )
				return generateUnion((UnionType)baseType,context);
			
			if( baseType!=dti )
				return generate(baseType,context);
		}
		
		// use previously generated value if such a thing exists.
		Set vs = (Set)generatedValues.get(dt);
		if(vs!=null && vs.size()!=0 )
			return (String)vs.toArray()[random.nextInt(vs.size())];
		
		
		return null;
	}

    private String generateBase64Binary() {
        int len = random.nextInt(16)*4;
        StringBuffer b = new StringBuffer(len);
        for( int i=0; i<len; i++ )
            b.append(random.nextInt(26)+'A');
        return b.toString();
    }

    protected void fail( Datatype dt ) {
		
		throw new GenerationException("unable to generate value for this datatype: " +
			(( dt instanceof XSDatatype )?((XSDatatype)dt).displayName():dt.toString()) );
		
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
	
	protected String generateUnion(UnionType ut, ContextProviderImpl context ) {
		try {
			return generate( ut.memberTypes[random.nextInt(ut.memberTypes.length)], context );
		} catch( GenerationException ge ) { return null; }
	}
		
	protected String generateList(XSDatatypeImpl dti, ContextProviderImpl context) {
		try {
			ListType base = (ListType)dti.getConcreteType();
			LengthFacet lf = (LengthFacet)dti.getFacetObject(XSDatatype.FACET_LENGTH);
			int n;	// compute # of items into this value.
		
			if(lf!=null) {
				n = lf.length;
			} else {
				MaxLengthFacet xlf = (MaxLengthFacet)dti.getFacetObject(XSDatatype.FACET_MAXLENGTH);
				int max = (xlf!=null)?xlf.maxLength:16;
				MinLengthFacet nlf = (MinLengthFacet)dti.getFacetObject(XSDatatype.FACET_MINLENGTH);
				int min = (nlf!=null)?nlf.minLength:0;
				
				n = random.nextInt(max-min)+min;
			}
		
			String s="";
			for( int i=0; i<n; i++ )
				s += " " + generate(base.itemType,context) + " ";
			return s;
		} catch( GenerationException ge ) { return null; }
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

	protected String generateFloating() {
		return Float.toString(random.nextFloat());
	}
}
