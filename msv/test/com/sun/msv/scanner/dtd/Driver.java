package com.sun.tranquilo.scanner.dtd;

public class Driver {
	public static void main( String[] args ) throws Exception {
		DTDParser parser = new DTDParser();
		parser.setDtdHandler( new DumpHandler() );
		parser.parse(args[0]);
	}
}
