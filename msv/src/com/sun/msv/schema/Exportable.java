package com.sun.tranquilo.schema;

/**
 * base interface of exportable classes
 * 
 * ElementRules, Clauses, and HedgeRule are the exportable classes
 */
public interface Exportable
{
	/** examines whether the object is exported or not */
	boolean isExported();
}
