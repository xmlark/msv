package com.sun.tahiti.grammar;

public class CollectionType {
	/** no need to create an instance. Use predefined values. */
	private CollectionType() {}
	
	public static CollectionType set = new CollectionType();
	public static CollectionType list = new CollectionType();
	public static CollectionType vector = new CollectionType();
	
	public static CollectionType getDefault() {
		return list;
	}
	
	/**
	 * parses the string representation of the collection type
	 * and returns one of the predefined value.
	 * 
	 * @return
	 *		If the value is not recognized, return null.
	 */
	public static CollectionType parse( String value ) {
		value = value.trim();
		
		if(value.equalsIgnoreCase("set"))		return set;
		if(value.equalsIgnoreCase("list"))		return list;
		if(value.equalsIgnoreCase("vector"))	return vector;
		return null;	// unrecognized
	}
}
