package com.sun.tahiti.runtime.ll;

public class IgnoreSymbol extends NonTerminalSymbol {

	private final String name;
	public IgnoreSymbol( String _name ) {
		this.name = _name;
	}
	
	public LLParser.Receiver createReceiver( final LLParser.Receiver parent ) {
		return LLParser.ignoreReceiver;
	}
	public String toString() {
		return name;
	}
};
