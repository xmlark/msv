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
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.util.ErrorHandlerImpl;
import java.io.IOException;
import jp.gr.xml.relax.sax.DOMSAXProducer;

/**
 * Verifier implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class VerifierImpl extends org.iso_relax.verifier.impl.VerifierImpl
{
	private final IVerifier verifier;
	
	VerifierImpl( IVerifier verifier, XMLReader reader ) throws VerifierConfigurationException {
		this.verifier = verifier;
		super.reader	= reader;
	}
	
	// we obtain XMLReader through the constructor.
	protected void prepareXMLReader() {}
	
	
	public void setErrorHandler( ErrorHandler handler ) {
		reader.setErrorHandler(handler);
		verifier.setErrorHandler(handler);
	}
	
    public VerifierHandler getVerifierHandler() {
		return verifier;
    }
}
