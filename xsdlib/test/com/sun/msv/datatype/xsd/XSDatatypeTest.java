/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import junit.framework.*;
import org.relaxng.datatype.DatatypeException;

/**
 * tests XSDatatype.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XSDatatypeTest extends TestCase
{
	public XSDatatypeTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(XSDatatypeTest.class);
	}
	
	/** test getBaseType method */
	public void testGetBaseType() throws DatatypeException {
		
		// check the type hierarchy of the built-in types.
		assert( SimpleURType.theInstance==DurationType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==DateTimeType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==TimeType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==DateType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==GYearMonthType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==GYearType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==GMonthDayType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==GDayType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==GMonthType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==BooleanType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==Base64BinaryType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==HexBinaryType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==FloatType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==DoubleType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==AnyURIType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==QnameType.theInstance.getBaseType() );
		// NOTATION type
		assert( SimpleURType.theInstance==StringType.theInstance.getBaseType() );
		assert( SimpleURType.theInstance==NumberType.theInstance.getBaseType() );

		assert( StringType.theInstance==NormalizedStringType.theInstance.getBaseType() );
		assert( NormalizedStringType.theInstance==TokenType.theInstance.getBaseType() );
		assert( TokenType.theInstance==LanguageType.theInstance.getBaseType() );
		assert( TokenType.theInstance==NameType.theInstance.getBaseType() );
		assert( TokenType.theInstance==NmtokenType.theInstance.getBaseType() );
		assert( NameType.theInstance==NcnameType.theInstance.getBaseType() );
		assert( NcnameType.theInstance==EntityType.theInstance.getBaseType() );
		// ID,IDREF

		assert( NumberType.theInstance==IntegerType.theInstance.getBaseType() );
		assert( IntegerType.theInstance==NonPositiveIntegerType.theInstance.getBaseType() );
		assert( NonPositiveIntegerType.theInstance==NegativeIntegerType.theInstance.getBaseType() );
		
		assert( IntegerType.theInstance==LongType.theInstance.getBaseType() );
		assert( LongType.theInstance==IntType.theInstance.getBaseType() );
		assert( IntType.theInstance==ShortType.theInstance.getBaseType() );
		assert( ShortType.theInstance==ByteType.theInstance.getBaseType() );
		
		assert( IntegerType.theInstance==NonNegativeIntegerType.theInstance.getBaseType() );
		assert( NonNegativeIntegerType.theInstance==PositiveIntegerType.theInstance.getBaseType() );
		assert( NonNegativeIntegerType.theInstance==UnsignedLongType.theInstance.getBaseType() );
		assert( UnsignedLongType.theInstance==UnsignedIntType.theInstance.getBaseType() );
		assert( UnsignedIntType.theInstance==UnsignedShortType.theInstance.getBaseType() );
		assert( UnsignedShortType.theInstance==UnsignedByteType.theInstance.getBaseType() );
		
	}
	
	/** test isDerivedTypeOf method. */
	public void testIsDerivedTypeOf() throws Exception {
		XSDatatype urType = SimpleURType.theInstance;
		
		// test reflexivity
		assert( NonPositiveIntegerType.theInstance.isDerivedTypeOf(NonPositiveIntegerType.theInstance,true) );
		
		// test multi-step derivation
		assert( ByteType.theInstance.isDerivedTypeOf(NumberType.theInstance,true) );
		assert( EntityType.theInstance.isDerivedTypeOf(TokenType.theInstance,true) );
		assert( EntityType.theInstance.isDerivedTypeOf(urType,true) );
		
		// test the simple ur-type
		assert( urType.isDerivedTypeOf(urType,true) );
		assert( !urType.isDerivedTypeOf(StringType.theInstance,true) );
		assert( UnsignedByteType.theInstance.isDerivedTypeOf(urType,true) );
		
		// test the list derivation
		XSDatatype longList = DatatypeFactory.deriveByList("name", LongType.theInstance );
		XSDatatype byteList = DatatypeFactory.deriveByList("name", ByteType.theInstance );
		assert( !byteList.isDerivedTypeOf(longList,true) );
		assert( !longList.isDerivedTypeOf(byteList,true) );
		assert( byteList.isDerivedTypeOf(urType,true) );
		assert( longList.isDerivedTypeOf(urType,true) );
		assert( !byteList.isDerivedTypeOf(ByteType.theInstance,true) );
		
		// test the union derivation
		XSDatatype union1 = DatatypeFactory.deriveByUnion("name",
			new XSDatatype[]{TokenType.theInstance,LongType.theInstance});
		XSDatatype union2 = DatatypeFactory.deriveByUnion("name",
			new XSDatatype[]{union1,longList});
		XSDatatype union3;
		{
			TypeIncubator inc = new TypeIncubator(union2);
			inc.addFacet( "enumeration", "52", false, null );
			union3 = inc.derive(null);
		}
		
		assert( union1.isDerivedTypeOf(urType,true) );
		assert( ShortType.theInstance.isDerivedTypeOf(union1,true) );
		assert( union3.isDerivedTypeOf(urType,true) );
		assert( union3.isDerivedTypeOf(union2,true) );
		assert( longList.isDerivedTypeOf(union2,true) );
		assert( union1.isDerivedTypeOf(union2,true) );
		assert( TokenType.theInstance.isDerivedTypeOf(union2,true) );
		assert( LongType.theInstance.isDerivedTypeOf(union2,true) );
		
		assert( TokenType.theInstance.isDerivedTypeOf(union3,true) );
	}
}
