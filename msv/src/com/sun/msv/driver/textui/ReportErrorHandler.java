package com.sun.tranquilo.driver.textui;

import com.sun.tranquilo.verifier.VerificationErrorHandler;
import com.sun.tranquilo.verifier.ValidityViolation;

public class ReportErrorHandler implements VerificationErrorHandler
{
	public void onError( ValidityViolation vv )
	{
		System.out.println(
			"*** Error at line:"+vv.locator.getLineNumber()+
			", column:"+vv.locator.getColumnNumber() );
//			" of " + vv.locator.getSystemId() );
		System.out.println(vv.getMessage());
	}
	public void onWarning( ValidityViolation vv )
	{
		System.out.println("wrn:"+vv.getMessage());
	}
}
