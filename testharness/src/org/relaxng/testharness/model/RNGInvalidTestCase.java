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

import java.util.Vector;
import java.util.Iterator;

/**
 * test case that consists of invalid RELAX NG patterns.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGInvalidTestCase extends RNGTest {

	/** valid documents in this test case. */
	private final Vector patterns = new Vector();
	
	public void addPattern( XMLDocument doc ) {
		patterns.add(doc);
	}
	public Iterator iteratePatterns() {
		return patterns.iterator();
	}
	
	public Object visit( TestVisitor visitor ) {
		return visitor.onInvalidTest(this);
	}
}
