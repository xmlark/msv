package com.sun.tranquilo.verifier.jarv;

import com.sun.tranquilo.verifier.DocumentDeclaration;
import com.sun.tranquilo.verifier.VerificationErrorHandler;

class FilterImpl
	extends com.sun.tranquilo.verifier.VerifierFilter
	implements org.iso_relax.verifier.VerifierFilter
{
	FilterImpl( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler )
	{ super(documentDecl,errorHandler); }
}
