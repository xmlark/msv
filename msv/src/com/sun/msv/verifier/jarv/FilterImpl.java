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

import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.VerificationErrorHandler;

/**
 * VerifierFilter implementation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class FilterImpl
	extends com.sun.msv.verifier.VerifierFilter
	implements org.iso_relax.verifier.VerifierFilter
{
	FilterImpl( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler )
	{ super(documentDecl,errorHandler); }
}
