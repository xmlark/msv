package com.sun.tranquilo.driver.textui;

import com.sun.tranquilo.verifier.VerificationErrorHandler;
import com.sun.tranquilo.verifier.ValidityViolation;
import com.sun.tranquilo.verifier.ValidationUnrecoverableException;

public class ReportErrorHandler implements VerificationErrorHandler
{
	private int counter = 0;
	public void onError( ValidityViolation vv )
		throws ValidationUnrecoverableException
	{
		countCheck(vv);
		print(vv,MSG_ERROR);
	}
	public void onWarning( ValidityViolation vv )
	{
		print(vv,MSG_WARNING);
	}
	
	private void print( ValidityViolation vv, String prop )
	{
		System.out.println(
			Driver.localize( prop, new Object[]{
				new Integer(vv.locator.getLineNumber()), 
				new Integer(vv.locator.getColumnNumber()),
				vv.locator.getSystemId(),
				vv.getMessage()} ) );
	}
	
	private void countCheck( ValidityViolation vv )
		throws ValidationUnrecoverableException
	{
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
}
