/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.NameClass;
import com.sun.tranquilo.reader.SimpleState;

/**
 * Base implementation for NameClass primitives
 */
public abstract class NameClassState extends SimpleState
{
	public final void endSelf()
	{
		// pass the pattern to the parent
		((NameClassOwner)parentState).onEndChild(makeNameClass());
		super.endSelf();
	}
		
	/**
	 * This method is called from endElement method.
	 * Implementation has to provide NameClass object that represents the content of
	 * this element.
	 */
	protected abstract NameClass makeNameClass();
	
	protected final String getPropagatedNamespace()
	{
		return ((TREXGrammarReader)reader).targetNamespace;
	}
}
