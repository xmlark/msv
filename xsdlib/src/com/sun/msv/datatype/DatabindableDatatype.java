package com.sun.msv.datatype;

import org.relaxng.datatype.ValidationContext;

/**
 * Datatype interface that supports Java databinding.
 *
 * This interface can be used to do java/xml databinding.
 * 
 * @author	Kohsuke Kawaguchi
 */
public interface DatabindableDatatype extends org.relaxng.datatype.Datatype {
	/**
	 * converts lexcial value to a corresponding Java-friendly object
	 * by using the given context information.
	 * 
	 * <p>
	 * For the actual types returned by each type,
	 * see <a href="package-summary.html#javaType">here</a>.
	 * 
	 * <p>
	 * Note that due to the difference between those Java friendly types
	 * and actual XML Schema specification, the returned object sometimes
	 * loses accuracy,
	 * 
	 * @return	null
	 *		when the given lexical value is not a valid lexical value for this type.
	 */
	Object createJavaObject( String literal, ValidationContext context );
	
	/**
	 * gets the type of the objects that are created by the createJavaObject method.
	 */
	Class getJavaObjectType();
}
