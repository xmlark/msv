/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.jarv;

import org.iso_relax.verifier.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * tests JARV.
 * 
 * This class is <b>NOT</b> a part of the JUnit test cases.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class JARVTester
{
	protected abstract VerifierFactory getFactory(String language) throws Exception;
	
	public void run(java.lang.String[] args) throws Exception {
		if(args.length<3) {
			System.out.println(
				"Usage: FactoryLoaderTester <language> <schema> <instance> ...\n");
			return;
		}
		
		VerifierFactory factory = getFactory(args[0]);
		if(factory==null) {
			System.out.println("unable to find an implementation");
			return;
		}
		
		Schema schema = factory.compileSchema(args[1]);
		if(schema==null) {
			System.out.println("unable to parse this schema");
			return;
		}
		
		Verifier verifier = schema.newVerifier();
		verifier.setErrorHandler( new ErrorHandler(){
			public void fatalError( SAXParseException e ) {
				System.out.println("fatal:"+e);
			}
			public void error( SAXParseException e ) {
				System.out.println("error:"+e);
			}
			public void warning( SAXParseException e ) {
				System.out.println("warning:"+e);
			}
		});
		
		for( int i=2; i<args.length; i++ )
			verifier.verify(args[2]);
	}
}
