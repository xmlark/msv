/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util;

import junit.framework.*;

/**
 * tests StringPair.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringPairTest extends TestCase {
	
	public StringPairTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(StringPairTest.class);
	}
	
	/** test equals and hashCode method */
	public void testEqualsAndHashCode()	{
		StringPair[] pairs = new StringPair[]{
				new StringPair("namespace","foo"),
				new StringPair("namespac-","foo"),
				new StringPair("namespace","bar") };
		
		for( int i=0; i<pairs.length; i++ )
			for( int j=0; j<pairs.length; j++ )
				if( i==j ) {
					assertEquals( pairs[i], pairs[j] );
					assertEquals( pairs[i].hashCode(), pairs[j].hashCode() );
				} else
					assertTrue( !pairs[i].equals(pairs[j]) );
	}
}
