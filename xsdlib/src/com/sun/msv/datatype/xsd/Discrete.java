package com.sun.tranquilo.datatype;

/**
 * base interface of types to which length-related facets are applicable.
 */
interface Discrete
{
	/** count the number of item in value type.
	 * 
	 * Actual semantics of this method varies.
	 */
	public int countLength( Object value );
}
