/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

import com.sun.msv.grammar.Expression;
import com.sun.msv.datatype.DatabindableDatatype;
import java.util.Set;

/**
 * represents "primitive" type.
 * 
 * Primitive types are those types which can construct itself from a string
 * and a DataType.
 */
public class PrimitiveItem extends JavaItem implements Type {
	
	public PrimitiveItem( DatabindableDatatype dt ) {
		super(calcType(dt).getName());
		this.type = SystemType.get(calcType(dt));
		this.dt = dt;
	}

	public PrimitiveItem( DatabindableDatatype type, Expression exp ) {
		this(type);
		this.exp = exp;
	}
	
	private static Class calcType( DatabindableDatatype dt ) {
		if(dt==null)	return String.class;
		else			return dt.getJavaObjectType();
	}
	
	/**
	 * actual type. This object works as a proxy to this field
	 */
	public final SystemType type;
	
	/**
	 * underlying datatype object.
	 * This field is null if the underlying datatype is not databindable.
	 */
	public final DatabindableDatatype dt;
	
	public String getTypeName()		{ return type.getTypeName(); }
	public Type[] getInterfaces()	{ return type.getInterfaces(); }
	public Type getSuperType()		{ return type.getSuperType(); }
	public String getPackageName()	{ return type.getPackageName(); }
	public String getBareName()		{ return type.getBareName(); }

	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onPrimitive(this);
	}
}
