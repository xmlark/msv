package com.sun.tranquilo.verifier;

import org.xml.sax.SAXException;

/**
 * Exception that signals error was fatal and recovery was not possible.
 */
public class ValidationUnrecoverableException extends SAXException
{
	public final ValidityViolation error;
	
	ValidationUnrecoverableException( ValidityViolation vv )
	{
		super( vv.getMessage() );
		error=vv;
	}
	
}
