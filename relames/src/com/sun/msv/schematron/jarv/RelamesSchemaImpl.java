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

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;

import com.sun.msv.grammar.Grammar;

/**
 * {@link Schema} implementation.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class RelamesSchemaImpl implements Schema {
    
    private final Grammar grammar;

    RelamesSchemaImpl( Grammar _grammar ) {
        this.grammar = _grammar;
    }
    
    public Verifier newVerifier() throws VerifierConfigurationException {
        return new RelamesVerifierImpl(grammar);
    }

}
