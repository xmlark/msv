package com.sun.msv.schematron.reader;

import com.sun.msv.schematron.grammar.SAction;

public interface SActionReceiver {
	public void onAssert( SAction action );
	public void onReport( SAction action );
}
