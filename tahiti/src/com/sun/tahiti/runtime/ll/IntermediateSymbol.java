package com.sun.tahiti.runtime.ll;

import com.sun.msv.datatype.DatabindableDatatype;
import org.relaxng.datatype.ValidationContext;

/** Intermediate non-terminal symbols for LL grammar. */
public final class IntermediateSymbol extends NonTerminalSymbol
{
	/** purely for the debug purpose. */
	public final String identifier;
	public IntermediateSymbol( String identifier ) {
		this.identifier = identifier;
	}
	public String toString() {
		return "I<"+identifier+">";
	}
	
	/**
	 * Intermediate non-terminal does not have its own action.
	 */
	public LLParser.Receiver createReceiver( final LLParser.Receiver parent ) {
		// so it can simply return the parent receiver.
		return parent;
	}
}
