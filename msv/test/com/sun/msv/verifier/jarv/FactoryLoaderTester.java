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

import org.iso_relax.verifier.VerifierFactory;

/**
 * test <code>org/iso_relax/verifier/FactoryLoader</code>.
 * 
 * This class is <b>NOT</b> a part of the JUnit test cases.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FactoryLoaderTester extends JARVTester
{
	public static void main(java.lang.String[] args) throws Exception {
		new TheFactoryImplTester().run(args);
	}
	
	protected VerifierFactory getFactory(String language) throws Exception {
		return VerifierFactory.newInstance(language);
	}
}
