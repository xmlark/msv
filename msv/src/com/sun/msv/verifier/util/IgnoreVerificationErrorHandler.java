/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.util;

import com.sun.msv.verifier.VerificationErrorHandler;
import com.sun.msv.verifier.ValidityViolation;

/**
 * do-nothing implementation of VerificationErrorHandler.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IgnoreVerificationErrorHandler implements VerificationErrorHandler
{
	public void onError( ValidityViolation error ) {}
	public void onWarning( ValidityViolation warning ) {}
}
