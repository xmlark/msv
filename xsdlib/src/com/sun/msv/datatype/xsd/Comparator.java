package com.sun.tranquilo.datatype;

public interface Comparator
{
	static final int LESS			= -1;	// lhs < rhs
	static final int EQUAL			= 0;	// lhs = rhs
	static final int GREATER		= 1;	// lhs > rhs
	static final int UNDECIDABLE	= 999;	// lhs ? rhs
	
	/**
	 * compare to value types and decides its order relation
	 */
	int compare( Object lhs, Object rhs );
}
