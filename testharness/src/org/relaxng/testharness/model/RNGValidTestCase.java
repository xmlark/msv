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
 * Test case that consists of one valid pattern and several test documents.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGValidTestCase extends RNGTest {
	
	/** Pattern to be tested. */
	public XMLDocument pattern;
	
	/** Compatibility of this document with the annotation feature. */
	public boolean isAnnotationCompatible = true;
	/** Compatibility of this document with the ID/IDREF feature. */
	public boolean isIdIdrefCompatible = true;
	/** Compatibility of this document with the attribute default value feature. */
	public boolean isDefaultValueCompatible = true;
	
	
	/** valid documents in this test case. */
	private final Vector valid = new Vector();
	
	public void addValidDocument( ValidDocument doc ) {
		valid.add(doc);
	}
	public Iterator iterateValidDocuments() {
		return valid.iterator();
	}

	
	/** invalid documents in this test case. */
	private final Vector invalid = new Vector();
	
	public void addInvalidDocument( XMLDocument doc ) {
		invalid.add(doc);
	}
	public Iterator iterateInvalidDocuments() {
		return invalid.iterator();
	}
	
	public Object visit( TestVisitor visitor ) {
		return visitor.onValidTest(this);
	}
}
