/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.driver.textui;

import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.ValidationUnrecoverableException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 * {@link VerificationErrorHandler} that reports all errors and warnings.
 * 
 * SAX parse errors are also handled.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ReportErrorHandler
	implements VerificationErrorHandler, ErrorHandler
{
	private int counter = 0;
	public boolean hadError = false;
	
	public void error( SAXParseException spe ) {
		hadError = true;
		printSAXParseException( spe, MSG_SAXPARSEEXCEPTION_ERROR );
	}
	
	public void fatalError( SAXParseException spe ) throws SAXException {
		hadError = true;
		printSAXParseException( spe, MSG_SAXPARSEEXCEPTION_FATAL );
		throw new ValidationUnrecoverableException();
	}
	
	public void warning( SAXParseException spe ) {
		printSAXParseException( spe, MSG_SAXPARSEEXCEPTION_WARNING );
	}
	
	protected static void printSAXParseException( SAXParseException spe, String prop ) {
		System.out.println(
			Driver.localize( prop, new Object[]{
				new Integer(spe.getLineNumber()), 
				new Integer(spe.getColumnNumber()),
				spe.getSystemId(),
				spe.getLocalizedMessage()} ) );
	}
	
	public void onError( ValidityViolation vv )
		throws ValidationUnrecoverableException {
		countCheck(vv);
		print(vv,MSG_ERROR);
	}
	
	public void onWarning( ValidityViolation vv ) {
		print(vv,MSG_WARNING);
	}
	
	private void print( ValidityViolation vv, String prop ) {
		System.out.println(
			Driver.localize( prop, new Object[]{
				new Integer(vv.locator.getLineNumber()), 
				new Integer(vv.locator.getColumnNumber()),
				vv.locator.getSystemId(),
				vv.getMessage()} ) );
	}
	
	private void countCheck( ValidityViolation vv )
		throws ValidationUnrecoverableException	{
		if( counter++ < 20 )	return;
		
		System.out.println( Driver.localize(MSG_TOO_MANY_ERRORS) );
		throw new ValidationUnrecoverableException(vv);
	}
	
	public static final String MSG_TOO_MANY_ERRORS = //arg:1
		"ReportErrorHandler.TooManyErrors";
	public static final String MSG_ERROR = // arg:4
		"ReportErrorHandler.Error";
	public static final String MSG_WARNING = // arg:4
		"ReportErrorHandler.Warning";
	public static final String MSG_SAXPARSEEXCEPTION_FATAL = // arg:4
		"ReportErrorHandler.SAXParseException.Fatal";
	public static final String MSG_SAXPARSEEXCEPTION_ERROR = // arg:4
		"ReportErrorHandler.SAXParseException.Error";
	public static final String MSG_SAXPARSEEXCEPTION_WARNING = // arg:4
		"ReportErrorHandler.SAXParseException.Warning";
}
