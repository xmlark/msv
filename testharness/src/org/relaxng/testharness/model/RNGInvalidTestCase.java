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
 * test case that consists of invalid RELAX NG patterns.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGInvalidTestCase extends RNGTestCase {

	/**
	 * patterns to be tested.
	 * 
	 * All the patterns are invalid.
	 */
	public XMLDocument[] patterns;
}
