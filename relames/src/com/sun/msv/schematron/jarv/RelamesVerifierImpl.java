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

import javax.xml.parsers.ParserConfigurationException;

import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierHandler;
import org.iso_relax.verifier.impl.VerifierImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.schematron.verifier.RelmesVerifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * {@link org.iso_relax.verifier.Verifier} implementation.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class RelamesVerifierImpl extends VerifierImpl {

    private final RelmesVerifier verifier;
    
    public RelamesVerifierImpl(Grammar grammar) throws VerifierConfigurationException {
        try {
            this.verifier = new RelmesVerifier(new REDocumentDeclaration(grammar),null);
        } catch( ParserConfigurationException e ) {
            throw new VerifierConfigurationException(e);
        }
    }

    public void setErrorHandler( ErrorHandler handler ) {
        super.setErrorHandler(handler);
        verifier.setErrorHandler(handler);
    }

    public VerifierHandler getVerifierHandler() throws SAXException {
        return verifier;
    }

}
