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

import java.io.File;
import org.iso_relax.verifier.*;
import com.sun.msv.driver.textui.ReportErrorHandler;

/**
 * Uses <a href="http://iso-relax.sourceforge.net/apiDoc/">JARV</a> to validate documents.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class JARVDemo
{
	public static void main( String args[] ) throws Exception {
		
		if( args.length<2 ) {
			System.out.println("JARVDemo <schema file> <instance file 1> <instance file 2> ...");
			return;
		};
		/*
			Implementation independent way to create a VerifierFactory.
			This method will discover an appropriate JARV implementation and
			returns the factory of that implementation.
		 
			To load a validator engine for RELAX NG, simply change
			the argument to "http://relaxng.org/ns/structure/0.9"
		*/		
		// VerifierFactory factory = VerifierFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		
		/*
			MSV dependent way to create a VerifierFactory.
			But this allows MSV to detect the schema language.
		*/
		VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
		 
		// parse a schema.
		// other overloaded methods allows you to parse a schema from InputSource, URL, etc.
		Verifier verifier = factory.newVerifier(new File(args[0]));
		
		// set the error handler. This object receives validation errors.
		// you can pass any class that implements org.sax.ErrorHandler.
		verifier.setErrorHandler( new ReportErrorHandler() );
		
		// use the verify method to validate documents.
		// or you can validate SAX events by using the getVerifierHandler method.
		for( int i=1; i<args.length; i++ )
			if(verifier.verify(args[i]))
				System.out.println(args[i]+" is valid");
			else
				System.out.println(args[i]+" is NOT valid");
	}
}
