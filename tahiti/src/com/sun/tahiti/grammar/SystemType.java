package com.sun.tahiti.grammar;

import java.util.Map;

/**
 * built-in types.
 */
public class SystemType implements Type {
	
	/**
	 * this map is from java/lang/Class objects to the corresponding SystemType object.
	 * Ssed to unify the system types.
	 */
	private static Map systemTypes = new java.util.HashMap();
	
	/** gets the SystemType object that corresponds to the specified type. */
	public static synchronized SystemType get( Class c ) {
		SystemType t = (SystemType)systemTypes.get(c);
		if(t==null)
			systemTypes.put(c, t=new SystemType(c));
		return t;
	}
	
	private SystemType( Class c ) {
		this.theClass = c;
	}
	
	/** Class object which this object is representing. */
	private final Class theClass;
	
	
	public Type getSuperType() {
		Class su = theClass.getSuperclass();
		if(su==null)		return null;
		else				return get(su);
	}
	
	public String getTypeName() {
		return theClass.getName();
	}
	
	public Type[] getInterfaces() {
		Class[] is = theClass.getInterfaces();
		Type[] r = new Type[is.length];
		for( int i=0; i<r.length; i++ )
			r[i] = get(is[i]);
		return r;
	}
}
