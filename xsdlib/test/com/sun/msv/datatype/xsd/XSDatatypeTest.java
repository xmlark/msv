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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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
        assertTrue( SimpleURType.theInstance==DurationType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==DateTimeType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==TimeType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==DateType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==GYearMonthType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==GYearType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==GMonthDayType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==GDayType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==GMonthType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==BooleanType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==Base64BinaryType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==HexBinaryType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==FloatType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==DoubleType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==AnyURIType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==QnameType.theInstance.getBaseType() );
        // NOTATION type
        assertTrue( SimpleURType.theInstance==StringType.theInstance.getBaseType() );
        assertTrue( SimpleURType.theInstance==NumberType.theInstance.getBaseType() );

        assertTrue( StringType.theInstance==NormalizedStringType.theInstance.getBaseType() );
        assertTrue( NormalizedStringType.theInstance==TokenType.theInstance.getBaseType() );
        assertTrue( TokenType.theInstance==LanguageType.theInstance.getBaseType() );
        assertTrue( TokenType.theInstance==NameType.theInstance.getBaseType() );
        assertTrue( TokenType.theInstance==NmtokenType.theInstance.getBaseType() );
        assertTrue( NameType.theInstance==NcnameType.theInstance.getBaseType() );
        assertTrue( NcnameType.theInstance==EntityType.theInstance.getBaseType() );
        // ID,IDREF

        assertTrue( NumberType.theInstance==IntegerType.theInstance.getBaseType() );
        assertTrue( IntegerType.theInstance==NonPositiveIntegerType.theInstance.getBaseType() );
        assertTrue( NonPositiveIntegerType.theInstance==NegativeIntegerType.theInstance.getBaseType() );
        
        assertTrue( IntegerType.theInstance==LongType.theInstance.getBaseType() );
        assertTrue( LongType.theInstance==IntType.theInstance.getBaseType() );
        assertTrue( IntType.theInstance==ShortType.theInstance.getBaseType() );
        assertTrue( ShortType.theInstance==ByteType.theInstance.getBaseType() );
        
        assertTrue( IntegerType.theInstance==NonNegativeIntegerType.theInstance.getBaseType() );
        assertTrue( NonNegativeIntegerType.theInstance==PositiveIntegerType.theInstance.getBaseType() );
        assertTrue( NonNegativeIntegerType.theInstance==UnsignedLongType.theInstance.getBaseType() );
        assertTrue( UnsignedLongType.theInstance==UnsignedIntType.theInstance.getBaseType() );
        assertTrue( UnsignedIntType.theInstance==UnsignedShortType.theInstance.getBaseType() );
        assertTrue( UnsignedShortType.theInstance==UnsignedByteType.theInstance.getBaseType() );
        
    }
    
    /** test isDerivedTypeOf method. */
    public void testIsDerivedTypeOf() throws Exception {
        XSDatatype urType = SimpleURType.theInstance;
        
        // test reflexivity
        assertTrue( NonPositiveIntegerType.theInstance.isDerivedTypeOf(NonPositiveIntegerType.theInstance,true) );
        
        // test multi-step derivation
        assertTrue( ByteType.theInstance.isDerivedTypeOf(NumberType.theInstance,true) );
        assertTrue( EntityType.theInstance.isDerivedTypeOf(TokenType.theInstance,true) );
        assertTrue( EntityType.theInstance.isDerivedTypeOf(urType,true) );
        
        // test the simple ur-type
        assertTrue( urType.isDerivedTypeOf(urType,true) );
        assertTrue( !urType.isDerivedTypeOf(StringType.theInstance,true) );
        assertTrue( UnsignedByteType.theInstance.isDerivedTypeOf(urType,true) );
        
        // test the list derivation
        XSDatatype longList = DatatypeFactory.deriveByList("","name", LongType.theInstance );
        XSDatatype byteList = DatatypeFactory.deriveByList("","name", ByteType.theInstance );
        assertTrue( !byteList.isDerivedTypeOf(longList,true) );
        assertTrue( !longList.isDerivedTypeOf(byteList,true) );
        assertTrue( byteList.isDerivedTypeOf(urType,true) );
        assertTrue( longList.isDerivedTypeOf(urType,true) );
        assertTrue( !byteList.isDerivedTypeOf(ByteType.theInstance,true) );
        
        // test the union derivation
        XSDatatype union1 = DatatypeFactory.deriveByUnion("","name",
            new XSDatatype[]{TokenType.theInstance,LongType.theInstance});
        XSDatatype union2 = DatatypeFactory.deriveByUnion("","name",
            new XSDatatype[]{union1,longList});
        XSDatatype union3;
        {
            TypeIncubator inc = new TypeIncubator(union2);
            inc.addFacet( "enumeration", "52", false, null );
            union3 = inc.derive(null,null);
        }
        
        assertTrue( union1.isDerivedTypeOf(urType,true) );
        assertTrue( ShortType.theInstance.isDerivedTypeOf(union1,true) );
        assertTrue( union3.isDerivedTypeOf(urType,true) );
        assertTrue( union3.isDerivedTypeOf(union2,true) );
        assertTrue( longList.isDerivedTypeOf(union2,true) );
        assertTrue( union1.isDerivedTypeOf(union2,true) );
        assertTrue( TokenType.theInstance.isDerivedTypeOf(union2,true) );
        assertTrue( LongType.theInstance.isDerivedTypeOf(union2,true) );
        
        assertTrue( TokenType.theInstance.isDerivedTypeOf(union3,true) );
    }
}
