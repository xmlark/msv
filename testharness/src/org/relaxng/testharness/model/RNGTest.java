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
 * base class of the RELAX NG test.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class RNGTest {
	
	/** header */
	public RNGHeader header;
	
	/**
	 * visitor pattern support for RELAX NG tests.
	 */
	public abstract Object visit( TestVisitor visitor );
}
