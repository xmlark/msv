package com.sun.tranquilo.verifier.jarv;

import com.sun.tranquilo.verifier.Verifier;
import com.sun.tranquilo.verifier.DocumentDeclaration;
import com.sun.tranquilo.verifier.VerificationErrorHandler;
import org.iso_relax.verifier.VerifierHandler;

class HandlerImpl
	extends Verifier
	implements VerifierHandler
{
	HandlerImpl( DocumentDeclaration documentDecl, VerificationErrorHandler errorHandler )
	{
		super(documentDecl,errorHandler);
	}
}
