/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.verifier.*;
import com.sun.tranquilo.verifier.regexp.ExpressionAcceptor;
import com.sun.tranquilo.verifier.regexp.StartTagInfoEx;
import com.sun.tranquilo.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.util.StringRef;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

/**
 * base implementation for SimpleAcceptor and ComplexAcceptor
 */
class ContentModelAcceptor extends ExpressionAcceptor
{
	protected ContentModelAcceptor(
		TREXDocumentDeclaration docDecl, Expression exp )
	{
		super(docDecl,exp);
	}
	
	public boolean stepForward( Acceptor child, StringRef errRef )
	{
		// TODO: explicitly mention that where the error recovery should be done.
		if( child instanceof SimpleAcceptor )
		{
			SimpleAcceptor sa = (SimpleAcceptor)child;
			return stepForwardByContinuation( sa.continuation, errRef );
		}
		if( child instanceof ComplexAcceptor )
			return stepForward(
				((ComplexAcceptor)child).contents, ((ComplexAcceptor)child).owners, errRef ); 
		throw new Error();	// child must be either Simple or Complex.
	}
	
	protected Acceptor createAcceptor(
		Expression combined,
		Expression continuation,
		CombinedChildContentExpCreator.OwnerAndContent primitives )
	{
		if( primitives==null || primitives.next==null )
		{
			// primitives==null is possible when recovering from error.
			
			// in this special case, combined child pattern and primitive patterns are the same.
			// therefore we don't need to keep track of primitive patterns.
			return new SimpleAcceptor(
				(TREXDocumentDeclaration)docDecl,
				combined,
				(primitives==null)?null:primitives.owner,
				continuation );
		}

		// TODO: implements MultipleAcceptor for cases that combined expression is unnecessary
		
		if( com.sun.tranquilo.driver.textui.Debug.debug )
			System.out.println("ComplexAcceptor is used");
		
		return new ComplexAcceptor(
			(TREXDocumentDeclaration)docDecl,
			combined, primitives );
	}
	
	// ContentModelAcceptor does not support type-assignment.
	// This will be supported by SimpleAcceptor only.
	public Object getOwnerType() { return null; }
}
