package com.sun.msv.schematron.reader;

import com.sun.msv.schematron.grammar.SRule;

public interface SRuleReceiver {
	void onRule( SRule rule );
}
