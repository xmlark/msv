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
 * test <code>org/iso_relax/verifier/FactoryLoader</code>.
 * 
 * This class is <b>NOT</b> a part of the JUnit test cases.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FactoryLoaderTester
{
	public static void main(java.lang.String[] args) {
		new TheFactoryImplTester().run(args);
	}
	
	protected VerifierFactory getFactory(String language) {
		return VerifierFactory.newInstance(args[0]);
	}
}
