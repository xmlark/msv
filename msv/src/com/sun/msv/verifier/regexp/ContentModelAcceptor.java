/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.verifier.*;
import com.sun.msv.verifier.regexp.ExpressionAcceptor;
import com.sun.msv.verifier.regexp.ElementToken;
import com.sun.msv.verifier.regexp.StartTagInfoEx;
import com.sun.msv.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.msv.datatype.ValidationContextProvider;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

/**
 * base implementation for SimpleAcceptor and ComplexAcceptor
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ContentModelAcceptor extends ExpressionAcceptor
{
	protected ContentModelAcceptor(
		REDocumentDeclaration docDecl, Expression exp )
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
		{
			ComplexAcceptor ca = (ComplexAcceptor)child;
			return stepForward(
				new ElementToken(
					(errRef!=null)?
						ca.owners:	// in error recovery mode, pretend that every owner is happy.
						ca.getSatisfiedOwners() ),
				errRef);
		}
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
				docDecl, combined,
				(primitives==null)?null:primitives.owner,
				continuation );
		}

		// TODO: implements MultipleAcceptor for cases that
		// combined expression is unnecessary but there are more than one primitive.
		
		if( com.sun.msv.driver.textui.Debug.debug )
			System.out.println("ComplexAcceptor is used");
		
		
		int i=0;
		for( CombinedChildContentExpCreator.OwnerAndContent o = primitives;
			 o!=null; o=o.next )	i++;
		
		Expression[] contents = new Expression[i];
		ElementExp[] owners = new ElementExp[i];
		
		i=0;
		for( CombinedChildContentExpCreator.OwnerAndContent o = primitives;
			 o!=null; o=o.next )
		{
			contents[i] = o.content;
			owners[i] = o.owner;
			i++;
		}
		
		return new ComplexAcceptor( docDecl, combined, contents, owners );
	}
	
	// ContentModelAcceptor does not support type-assignment.
	// This will be supported by SimpleAcceptor only.
	public Object getOwnerType() { return null; }
}
