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

import java.util.Set;
import java.util.Map;

/**
 * base class of the generated type.
 */
public abstract class TypeItem extends JavaItem implements Type {
	
	public TypeItem( String name ) {
		super(name);
		// everyone inherites MarshallableObject
		interfaces.add( SystemType.get(com.sun.tahiti.runtime.sm.MarshallableObject.class) );
	}
	
	public final Set interfaces = new java.util.HashSet();
	
	/** a map of field name to FieldUse */
	public final Map fields = new java.util.HashMap();
	public FieldUse getFieldUse( String name ) {
		FieldUse r = (FieldUse)fields.get(name);
		if(r==null)	fields.put(name,r=new FieldUse(name));
		return r;
	}

	public String getTypeName() { return name; }
	public Type[] getInterfaces() {
		return (Type[])interfaces.toArray(new Type[interfaces.size()]);
	}

	public String getPackageName() {
		int idx = name.lastIndexOf('.');
		if(idx<0)	return null;
		else		return name.substring(0,idx);
	}
	public String getBareName() {
		int idx = name.lastIndexOf('.');
		if(idx<0)	return name;
		else		return name.substring(idx+1);
	}
}
