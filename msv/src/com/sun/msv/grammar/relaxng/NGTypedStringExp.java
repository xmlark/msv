/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relaxng;

import org.relaxng.datatype.*;
import com.sun.msv.grammar.TypedStringExp;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.util.StringPair;

/**
 * TypedStringExp with key/keyref constraint of RELAX NG.
 * 
 * Memorization of expressions of this type is not strictly enforced.
 * Therefore, an instance can be freely created.
 * 
 * <p>
 * This class implements the DataType interface. A NGTypedStringExp as a DataType
 * works as a proxy that performs key/keyref related handling.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NGTypedStringExp extends TypedStringExp {
	
	public NGTypedStringExp( Datatype dataType, String typeName, StringPair keyName, StringPair keyRefName, StringPair baseTypeName ) {
		super(dataType,typeName);
		this.keyName = keyName;
		this.keyrefName= keyRefName;
		this.baseTypeName = baseTypeName;
	}

	public boolean equals( Object o ) {
		if(o.getClass()!=this.getClass())	return false;
		NGTypedStringExp rhs = (NGTypedStringExp)o;
		
		if(!compare( keyName, rhs.keyName ))		return false;
		if(!compare( keyrefName, rhs.keyrefName ))	return false;
		
		return true;
	}
	
	/**
	 * compare two StringPairs.
	 * This method considers that a null string is equal to a null string.
	 */
	private boolean compare( StringPair a, StringPair b ) {
		if(a==null && b==null)	return true;
		if(a==null || b==null)	return false;
		return a.equals(b);
	}
	
	/**
	 * name of the symbol space that this expression acts a key.
	 * If this expression is not a key, then this field is set to null.
	 */
	public final StringPair keyName;
	
	/**
	 * name of the symbol space that this expression acts a keyref.
	 * If this expression is not a key, then this field is set to null.
	 */
	public final StringPair keyrefName;
	
	/**
	 * the base type name of this key/keyref.
	 * 
	 * This field will never used once the grammar is parsed.
	 */
	public transient StringPair baseTypeName;
/*	
	public final DataType baseType;
	
	
	// DataType proxy
	private static class Proxy implements DataType {
	
		public boolean accept( String literal, ContextProvider context ) {
			if( !baseType.accept(literal,context) )
				return false;
			
			if( keyName!=null )
				// report ID
				((IDContextProvider)context).onID( keyName,
					baseType.convertToValueObject(literal,context) );
			
			return true;
		}
	
		public String displayName() {
			return baseType.displayName();
		}
	
		public Object convertToValueObject( String literal, ContextProvider context ) {
			return baseType.convertToValueObject(literal,context);
		}
	
		public boolean testValueEquality( Object o1, Object o2 ) {
			return baseType.testValueEquality(o1,o2);
		}
	
		public DataTypeErrorDiagnosis diagnose( String literal, ContextProvider context ) {
			return baseType.diagnose(literal,context);
		}
	}
*/
}