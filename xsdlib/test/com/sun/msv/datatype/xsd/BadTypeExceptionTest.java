/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import junit.framework.*;
import util.*;

/**
 * tests BadTypeException.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BadTypeExceptionTest extends TestCase
{
	public BadTypeExceptionTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(BadTypeExceptionTest.class);
	}

	/** tests that every error message defined in the class is actually written in property file.
	 *
	 * TODO: multi-lingual support.
	 */
	public void testResource() throws Exception {
		ResourceChecker.check(
			BadTypeException.class,
			"ERR_",
			new Checker(){
				public void check( String propertyName ) {
					// if the specified property doesn't exist, this will throw an error
					System.out.println(
						(new BadTypeException( propertyName, "@@@", "@@@", "@@@" /** dummy parameters */ )).getMessage() );
				}
			});
	}
}
