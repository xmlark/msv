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

import java.util.Map;

/**
 * RELAX NG Test Suite
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGTestSuite {
	
	/** header */
	public RNGHeader header;
	
	/** test cases contained in this suite. */
	public RNGValidTestCase[] validTestCases;
	public RNGInvalidTestCase[] invalidTestCases;
	
	/**
	 * additional "resource"s found in the test suite.
	 */
	private final Map resources = new java.util.HashMap();
	
	/**
	 * obtains the resource from the specified name.
	 */
	public XMLDocument getResource( String resourceName ) {
		return (XMLDocument)resources.get(resourceName);
	}
	
	/**
	 * adds a new resource.
	 */
	public void addResource( String name, XMLDocument resource ) {
		resources.put(name,resource);
	}
}
