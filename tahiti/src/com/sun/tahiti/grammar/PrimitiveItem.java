package com.sun.tahiti.grammar;

import java.util.Set;

/**
 * represents "primitive" type.
 * 
 * Primitive types are those types which can construct itself from a string
 * and a DataType.
 */
public class PrimitiveItem extends JavaItem implements Type {
	
	public PrimitiveItem( Class type ) {
		super(type.getName());
		this.type = SystemType.get(type);
	}
	
	/** actual type. This object works as a proxy to this field */
	public final SystemType type;
	
	public String getTypeName()		{ return type.getTypeName(); }
	public Type[] getInterfaces()	{ return type.getInterfaces(); }
	public Type getSuperType()		{ return type.getSuperType(); }
	public String getPackageName()	{ return type.getPackageName(); }
	public String getBareName()		{ return type.getBareName(); }
}
