/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.ll;

import com.sun.tahiti.runtime.TypeDetecter;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

public class Unmarshaller extends TypeDetecter {
	
	private final Binder binder;
	
	public Unmarshaller( BindableGrammar grammar ) {
		super(new REDocumentDeclaration(grammar));
		binder = new Binder(grammar);
		setContentHandler(binder);
	}
	
	public Object getResult() {
		return binder.getResult();
	}
}
