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
 * Test case that consists of one valid pattern and several test documents.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGValidTestCase extends RNGTestCase {
	
	/** pattern to be tested. */
	public XMLDocument pattern;
	
	/** valid documents. */
	public XMLDocument[] validDocuments;
	
	/** invalid documents. */
	public XMLDocument[] invalidDocuments;
}
