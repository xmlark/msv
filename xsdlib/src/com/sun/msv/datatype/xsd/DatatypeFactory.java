/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import org.relaxng.datatype.DataTypeException;

/**
 * DataType object factory.
 *
 * <p>
 * Applications should use this class to get and derive DataType objects.
 * All methods are static.
 * 
 * <p>
 * Derivation by restriction should be done by using {@link TypeIncubator}.
 * 
 * @author	Kohsuke Kawaguchi
 */
public class DataTypeFactory {
	
	/** a map that contains all built in types */
	private static final Map builtinType = createStaticTypes();
	
	private DataTypeFactory(){}
	
	/**
	 * derives a new type by list.
	 *
	 * See http://www.w3.org/TR/xmlschema-2#derivation-by-list for
	 * what "derivation by list" means.
	 *
	 * @return
	 *		always return non-null value. If error occurs,
	 *		then an exception will be thrown.
	 * 
	 * @param newTypeName
	 *		name of the new type. it can be set to null for indicating an anonymous type.
	 * @param itemType
	 *		Type of the list item. It must be an atom type which is implemented
	 *		in this package or derived from types implemented in this package.
	 *		You cannot use your own DataType implementation here.
	 *
	 * @exception BadTypeException
	 *		this exception is thrown when the derivation is illegal.
	 *		For example, when you try to derive a type from non-atom type.
	 */
	public static DataTypeImpl deriveByList( String newTypeName, DataTypeImpl itemType )
		throws BadTypeException {
		return new ListType(newTypeName,itemType);
	}
	
	/**
	 * derives a new type by union.
	 *
	 * See http://www.w3.org/TR/xmlschema-2#derivation-by-union for
	 * what "derivation by union" means.
	 * 
	 * @param newTypeName
	 *		name of the new type. it can be set to null to
	 *		indicate an anonymous type.
	 * @param memberTypes
	 *		Types of the union member. It can be any type that implements DataType.
	 *
	 * @exception BadTypeException
	 *		this exception is thrown when the derivation is illegal.
	 */
	public static DataTypeImpl deriveByUnion( String newTypeName, DataTypeImpl[] memberTypes )
		throws BadTypeException {
		
		return new UnionType(newTypeName,memberTypes);
	}
	
	public static DataTypeImpl deriveByUnion( String newTypeName, Collection memberTypes )
		throws BadTypeException {
		DataTypeImpl[] m = new DataTypeImpl[memberTypes.size()];
		int n=0;
		for( Iterator itr=memberTypes.iterator(); itr.hasNext(); n++ )
		for( int i=0; i<m.length; i++ )
			m[i] = (DataTypeImpl)itr.next();
		
		return new UnionType(newTypeName,m);
	}
	
	/**
	 * obtain a built-in DataType object by its name
	 * 
	 * @return null	if DataType is not found
	 */
	public static DataTypeImpl getTypeByName( String dataTypeName ) {
		return (DataTypeImpl)builtinType.get(dataTypeName);
	}
	
	private static void add( Map m, DataTypeImpl type ) {
		final String name = type.getName();
		if( name==null )
			throw new IllegalArgumentException("anonymous type");
		
		if( m.containsKey(name) )
			// this error is considered as an assertion,
			// since this object doesn't allow external programs to
			// add types to the object.
			throw new IllegalArgumentException("multiple definition");
		
		m.put( name, type );
	}
	
	/** creates a map that contains all static types */
	private static Map createStaticTypes() {
		try {
			Map m = new java.util.HashMap();

			// missing types are noted inline.

			add( m, StringType.theInstance );
			add( m, BooleanType.theInstance );
			add( m, NumberType.theInstance );
			add( m, FloatType.theInstance );
			add( m, DoubleType.theInstance );
			add( m, DurationType.theInstance );
			add( m, DateTimeType.theInstance );
			add( m, TimeType.theInstance );
			add( m, DateType.theInstance );
			add( m, GYearMonthType.theInstance );
			add( m, GYearType.theInstance );
			add( m, GMonthDayType.theInstance );
			add( m, GDayType.theInstance );
			add( m, GMonthType.theInstance );
			add( m, HexBinaryType.theInstance );
			add( m, Base64BinaryType.theInstance );
			add( m, AnyURIType.theInstance );
	//		ID, IDREF
			add( m, EntityType.theInstance );
			add( m, QnameType.theInstance );
			add( m, NormalizedStringType.theInstance );
			add( m, TokenType.theInstance );
			add( m, LanguageType.theInstance );
	//		IDREFS
			add( m, new ListType("ENTITIES",EntityType.theInstance) );
			add( m, NmtokenType.theInstance );
			add( m, new ListType("NMTOKENS",NmtokenType.theInstance) );
			add( m, NameType.theInstance );
			add( m, NcnameType.theInstance );
	//		NOTATION
			add( m, new StringType("NOTATION", WhiteSpaceProcessor.theCollapse) );
			
			add( m, IntegerType.theInstance );
			add( m, NonPositiveIntegerType.theInstance );
			add( m, NegativeIntegerType.theInstance );
			add( m, LongType.theInstance );
			add( m, IntType.theInstance );
			add( m, ShortType.theInstance );
			add( m, ByteType.theInstance );
			add( m, NonNegativeIntegerType.theInstance );
			add( m, UnsignedLongType.theInstance );
			add( m, UnsignedIntType.theInstance );
			add( m, UnsignedShortType.theInstance );
			add( m, UnsignedByteType.theInstance );
			add( m, PositiveIntegerType.theInstance );
			return m;
		} catch( DataTypeException dte )	{
			// assertion failed
			throw new Error();
		}
	}
}
