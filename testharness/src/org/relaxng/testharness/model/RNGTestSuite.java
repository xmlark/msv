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
import java.util.Vector;
import java.util.Iterator;

/**
 * RELAX NG Test Suite
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGTestSuite extends RNGTest {
	
	/** tests in this suite. */
	private final Vector tests = new Vector();
	
	/** add a new test to this suite. */
	public void addTest( RNGTest test ) {
		tests.add(test);
	}
	
	/** iterates all tests in this suite. */
	public Iterator iterateTests() {
		return tests.iterator();
	}
	
	/**
	 * gets all tests in this suite.
	 */
	public RNGTest[] getAllTests() {
		return (RNGTest[])tests.toArray(new RNGTest[tests.size()]);
	}
	
	
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

	
	public Object visit( TestVisitor visitor ) {
		return visitor.onSuite(this);
	}
}
