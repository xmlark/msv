package com.sun.tahiti.compiler;

/**
 * used to resolve various objects to its id so that
 * they can be serialized.
 */
public interface Symbolizer {
	String getId( Object o );
}
