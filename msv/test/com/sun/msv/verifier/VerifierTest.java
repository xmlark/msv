package com.sun.msv.verifier;

import junit.framework.*;
import util.*;

public class VerifierTest extends TestCase
{
	public VerifierTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(VerifierTest.class);
	}
	
	/** tests the existence of all messages */
	public void testMessages() throws Exception {

		Checker checker = new Checker(){
			public void check( String propertyName ) {
				// if the specified property doesn't exist, this will throw an error
				System.out.println(
					Verifier.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
			}
		};
		
		ResourceChecker.check( Verifier.class, "", checker );
	}
}
