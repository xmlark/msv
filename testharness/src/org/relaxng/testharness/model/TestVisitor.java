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
 * Visitor pattern support for RNGTest.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface TestVisitor {
	Object onValidTest( RNGValidTestCase test );
	Object onInvalidTest( RNGInvalidTestCase test );
	Object onSuite( RNGTestSuite suite );
}
