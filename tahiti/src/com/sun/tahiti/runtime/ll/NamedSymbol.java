package com.sun.tahiti.runtime.ll;

/**
 * Non-terminal name symbol for LL grammar.
 * immutable.
 */
public class NamedSymbol extends NonTerminalSymbol
{
	public final String name;
	public NamedSymbol( String name ) {
		this.name = name;
	}
	public String toString() {
		return "N<"+name+">";
	}
	
	public LLParser.Receiver createReceiver( final LLParser.Receiver parent ) {
		return new LLParser.ObjectReceiver() {
			public void start() throws Exception {}
			public void end() throws Exception {}
			public void action( Object item ) throws Exception {
				((LLParser.FieldReceiver)parent).action( item, NamedSymbol.this );
			}
		};
	}
}
