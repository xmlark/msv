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
 * tests DataTypeImpl.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeImplTest extends TestCase
{
	public DataTypeImplTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(DataTypeImplTest.class);
	}
	
	/** tests the existence of all messages */
	public void testMessages() throws Exception {
		ResourceChecker.check(
			XSDatatypeImpl.class,
			"",
			new Checker(){
				public void check( String propertyName ) {
					// if the specified property doesn't exist, this will throw an error
					System.out.println(
						XSDatatypeImpl.localize(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
				}
			});
	}
}
