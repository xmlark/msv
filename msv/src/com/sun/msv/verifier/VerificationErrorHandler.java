package com.sun.tranquilo.verifier;

import org.xml.sax.SAXException;

/**
 * receives errors and warning of verification.
 */
public interface VerificationErrorHandler
{
	/**
	 * this method is called whenever the error is found.
	 * 
	 * Throwing a SAXException will terminate verification. This will be useful
	 * when you don't want Verifier to recover from the error.
	 * 
	 * The caller of Verifier is responsible for catching any exception thrown
	 * from this method.
	 * 
	 * If no exception is thrown, Verifier will try to recover from the error.
	 */
	void onError( ValidityViolation error )
		throws SAXException;
	
	/**
	 * this method is called whenever the warning is found.
	 * 
	 * Throwing a SAXException will terminate verification. This will be useful
	 * when you don't want Verifier to recover from the error.
	 * 
	 * The caller of Verifier is responsible for catching any exception thrown
	 * from this method.
	 * 
	 * If no exception is thrown, Verifier will continue verification.
	 */
	void onWarning( ValidityViolation warning )
		throws SAXException;
}
