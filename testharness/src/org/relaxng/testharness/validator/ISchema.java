/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.validator;

/**
 * Represents a compiled shcmea.
 * 
 * The format of the compiled schema is implementation-dependent.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface ISchema
{
	/**
	 * Checks if this schema is compatible with the annotation feature.
	 * 
	 * <p>
	 * If the implementation does not support the DTD compatibility spec,
	 * return null.
	 */
	Boolean isAnnotationCompatible();
	
	/**
	 * Checks if this schema is compatible with the ID/IDREF feature.
	 * 
	 * <p>
	 * If the implementation does not support the DTD compatibility spec,
	 * return null.
	 */
	Boolean isIdIdrefCompatible();

	/**
	 * Checks if this schema is compatible with the attribute
	 * default value feature.
	 * 
	 * <p>
	 * If the implementation does not support the DTD compatibility spec,
	 * return null.
	 */
	Boolean isDefaultValueCompatible();
}
