/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schematron.jarv;

import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierFactoryLoader;

import com.sun.msv.schematron.reader.SRELAXNGReader;

/**
 * {@link VerifierFactoryLoader} implementation.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class FactoryLoaderImpl implements VerifierFactoryLoader {
    public VerifierFactory createFactory(String name) {
        if(name.equals(SRELAXNGReader.RNG_PLUS_SCHEMATRON_URI))
            return new RelamesFactoryImpl();
        else
            return null;
    }
}
