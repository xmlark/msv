/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.model;

/**
 * header information.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface RNGHeader {
	
	/**
	 * Gets the name of this test case.
	 * 
	 * The actual definition of the "name" depends on the source of the test suite.
	 */
	String getName();

	/**
	 * Gets the value of a property.
	 * If the specified property does not exist, null is returned.
	 */
	String getProperty( String uri, String local );
}
