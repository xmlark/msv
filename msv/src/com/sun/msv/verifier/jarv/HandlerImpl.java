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

import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.VerificationErrorHandler;
import org.iso_relax.verifier.VerifierHandler;

/**
 * Verifier Handler implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class HandlerImpl
	extends Verifier
	implements VerifierHandler
{
	HandlerImpl( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler )
	{
		super(documentDecl,errorHandler);
	}
}
