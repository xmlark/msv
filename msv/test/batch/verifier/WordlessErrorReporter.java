/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.verifier;

import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.ValidityViolation;

/**
 * {@link VerificationErrorHandler} that reports only the first error.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class WordlessErrorReporter implements VerificationErrorHandler {
	
	private boolean first = true;
	private ValidityViolation error = null;
	
	public ValidityViolation getError() { return error; }
	
	public void onError( ValidityViolation error ) {
		if( first ) {
			System.out.println(error.getMessage());
			this.error = error;
		}
		first = false;
	}
		
	public void onWarning( ValidityViolation warning ) {}
}
