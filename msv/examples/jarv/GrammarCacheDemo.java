/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package jarv;

import org.iso_relax.verifier.*;
import com.sun.msv.verifier.util.IgnoreErrorHandler;

/**
 * uses ISO-RELAX validator API to cache a grammar and then validate documents
 * from multiple threads.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarCacheDemo
{
	public static void main( String args[] ) throws Exception {
		
		if( args.length<2 ) {
			System.out.println("GrammarCacheDemo <XSD schema> <instance file 1> <instance file 2> ...");
			return;
		};
		
		// load a validator engine for W3C XML Schema
		// if no implementation is available, an exception is thrown
		// to load a validator engine for RELAX NG, simply change
		// the argument to "http://relaxng.org/ns/structure/0.9"
		VerifierFactory factory = VerifierFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		
		// compile a schema.
		// other overloaded methods allows you to parse a schema from InputSource, URL, etc.
		final Schema schema = factory.compileSchema(args[0]);
		
		// launch a thread for each instance file.
		// this will simulate the multi-thread environment.
		for( int i=1; i<args.length; i++ ) {
			final String fileName = args[i];
			new Thread(new Runnable(){
				public void run() {
					try {
						// gets the verifier
						Verifier verifier = schema.newVerifier();
					
						// set the error handler. This object receives validation errors.
						// you can pass any class that implements org.sax.ErrorHandler.
						verifier.setErrorHandler( new IgnoreErrorHandler() );

						// use the verify method to validate documents.
						// or you can validate SAX events by using the getVerifierHandler method.
						if(verifier.verify(fileName))
							System.out.println(fileName+" is valid");
						else
							System.out.println(fileName+" is NOT valid");
					} catch( Exception e ) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
