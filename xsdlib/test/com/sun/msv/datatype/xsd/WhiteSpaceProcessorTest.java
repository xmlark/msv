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
import java.io.*;
import org.relaxng.datatype.DatatypeException;

/**
 * tests WhiteSpaceProcessor.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class WhiteSpaceProcessorTest extends TestCase
{
	public WhiteSpaceProcessorTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(WhiteSpaceProcessorTest.class);
	}
	
	/** test get method */
	public void testGet() throws DatatypeException
	{
		assertSame( "whitespace in parameter must be allowed",
			WhiteSpaceProcessor.get("preserve"),
			WhiteSpaceProcessor.get("preserve  \t \n \r ")
		);
		assertSame(
			WhiteSpaceProcessor.get("collapse"),
			WhiteSpaceProcessor.get("    \r\n  collapse \t ")
		);
		assertSame(
			WhiteSpaceProcessor.get("replace"),
			WhiteSpaceProcessor.get(" \r\n\r\nreplace") );
		
		try
		{
			WhiteSpaceProcessor.get("unknown");
			fail("should throw exception");
		}catch(DatatypeException e){;}
	}
	
	/** test behavior of preserve */
	public void testPreserve() throws DatatypeException
	{
		WhiteSpaceProcessor target = WhiteSpaceProcessor.get("preserve");
		
		String[] tests = new String[] {
			"test",
			"  a  b  c  ",
			"\r\n \ta bb \t\t c   \r\r\n\r  " };
		
		for( int i=0; i<tests.length; i++ )
			assertEquals( tests[i], target.process(tests[i]) );
	}
	
	/** tests behavior of replace */
	public void testReplace() throws DatatypeException
	{
		WhiteSpaceProcessor target = WhiteSpaceProcessor.get("replace");
		
		assertEquals( target.process(
			"test"),
			"test");
		assertEquals( target.process(
			"  a  b  c  "),
			"  a  b  c  ");
		assertEquals( target.process(
			"\r\n \ta bb \t\t c   \r\r\n\r  "),
			"    a bb    c         ");
	}

	/** tests behavior of collapse */
	public void testCollapse() throws DatatypeException
	{
		WhiteSpaceProcessor target = WhiteSpaceProcessor.get("collapse");
		
		assertEquals( target.process(
			"test"),
			"test");
		assertEquals( target.process(
			"  a  b  c  "),
			"a b c");
		assertEquals( target.process(
			"\r\n \ta bb \t\t c   \r\r\n\r  "),
			"a bb c");
		assertEquals( target.process(
			"abc  "),
			"abc");
		assertEquals( target.process(
			"abc "),
			"abc");
	}
	
	/** serializes o and then returns de-serialized object. */
	public Object freezeDry( Object o ) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		// serialize it
		oos.writeObject( o );
		oos.flush();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		return ois.readObject();
	}
	
	/** test serialization. */
	public void testSerialization() throws Exception {
		
		// ensure that serialization doesn't break
		assertSame( WhiteSpaceProcessor.theCollapse,
			freezeDry(WhiteSpaceProcessor.theCollapse) );

		assertSame( WhiteSpaceProcessor.thePreserve,
			freezeDry(WhiteSpaceProcessor.thePreserve) );
		
		assertSame( WhiteSpaceProcessor.theReplace,
			freezeDry(WhiteSpaceProcessor.theReplace) );
	}
}
