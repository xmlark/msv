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

import com.sun.msv.datatype.DatabindableDatatype;
import org.relaxng.datatype.ValidationContext;

/**
 * base class of the non-terminal symbols of LL grammar.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DefaultNonTerminalSymbol extends NonTerminalSymbol {
	
	public DefaultNonTerminalSymbol( String symbolName ) {
		this.symbolName = symbolName;
	}
	
	private final String symbolName;
	
	public LLParser.Receiver createReceiver( final LLParser.Receiver parent ) {
		return new LLParser.CharacterReceiver(){
			public void action(DatabindableDatatype dt, String literal, ValidationContext context ) throws Exception {
				((LLParser.ObjectReceiver)parent).action(
					dt.createJavaObject(literal,context) );
			}
			public void start() throws Exception {}
			public void end() throws Exception {}
		};
	}
	
	public String toString() { return symbolName; }
}