package com.sun.tranquilo.verifier.util;

import com.sun.tranquilo.verifier.VerificationErrorHandler;
import com.sun.tranquilo.verifier.ValidityViolation;
import org.xml.sax.SAXException;

/**
 * default implementation of VerificationErrorHandler.
 * 
 * If an error is found, throw ValidityViolation to stop further validation.
 * warnings are ignored.
 */
public class VerificationErrorHandlerImpl implements VerificationErrorHandler
{
	public void onError( ValidityViolation error ) throws SAXException
	{ throw error; }
	public void onWarning( ValidityViolation warning )
	{}
}
