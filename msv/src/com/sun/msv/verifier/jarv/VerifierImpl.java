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
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.util.VerificationErrorHandlerImpl;
import com.sun.msv.util.xml.SAXEventGenerator;
import java.io.IOException;

/**
 * Verifier implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class VerifierImpl implements Verifier
{
	private final DocumentDeclaration grammar;
	private final XMLReader reader;
	
	VerifierImpl( DocumentDeclaration grammar, XMLReader reader ) {
		this.grammar = grammar;
		this.reader	= reader;
	}
	
	public boolean isFeature(String featureName) throws SAXNotRecognizedException {
		if( featureName.equals(FEATURE_HANDLER) )	return true;
		if( featureName.equals(FEATURE_FILTER) )	return true;

		throw new SAXNotRecognizedException(featureName);
	}
	
	public void setFeature( String feature, boolean value ) throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(feature);
	}
	
	public Object getProperty( String property )
				throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(property);
	}
	
	public void setProperty( String property, Object value )
				throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException(property);
	}
	
	public void setErrorHandler( ErrorHandler handler ) {
		reader.setErrorHandler(handler);
	}
	
	public void setEntityResolver( EntityResolver handler ) {
		reader.setEntityResolver(handler);
	}

    public boolean verify(String uri)
			throws SAXException, IOException {
		reader.setContentHandler(getVerifierHandler());
		try {
			reader.parse(uri);
			return true;
		} catch(ValidityViolation v) {
			return false;
		}
	}

    public boolean verify(InputSource source) throws SAXException, IOException {
		reader.setContentHandler(getVerifierHandler());
		try {
			reader.parse(source);
			return true;
		} catch(ValidityViolation v) {
			return false;
		}
	}

	public boolean verify(Node node) throws SAXException {
		if(!(node instanceof Document))
			throw new UnsupportedOperationException("nothing but Document is supported");
		
		try {
			SAXEventGenerator.parse((Document)node,getVerifierHandler());
			return true;
		} catch(ValidityViolation v) {
			return false;
		}
	}

    public VerifierHandler getVerifierHandler() {
		return new HandlerImpl( grammar, new VerificationErrorHandlerImpl() );
    }

    public VerifierFilter getVerifierFilter() {
		return new FilterImpl( grammar, new VerificationErrorHandlerImpl() );
    }
}
