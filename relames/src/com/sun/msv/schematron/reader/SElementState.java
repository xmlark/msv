package com.sun.msv.schematron.reader;

import java.util.Vector;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.ng.ElementState;
import com.sun.msv.schematron.grammar.SAction;
import com.sun.msv.schematron.grammar.SActions;
import com.sun.msv.schematron.grammar.SElementExp;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.util.StartTagInfo;

public class SElementState extends ElementState implements SActionReceiver, SRuleReceiver {

	protected State createChildState( StartTagInfo tag ) {
		
		if(!tag.namespaceURI.equals(SRELAXNGReader.SchematronURI))
			return super.createChildState(tag);

		if( tag.localName.equals("rule") )
			return new SRuleState();
		if( tag.localName.equals("assert") )
			return new SActionState.SAssertState();
		if( tag.localName.equals("report") )
			return new SActionState.SReportState();
		return null;
	}
	
	private final Vector asserts = new Vector();
	public void onAssert( SAction action ) {
		asserts.add(action);
	}
	private final Vector reports = new Vector();
	public void onReport( SAction action ) {
		reports.add(action);
	}
	private final Vector rules = new Vector();
	public void onRule( SRule rule ) {
		rules.add(rule);
	}
	
	protected Expression annealExpression( Expression contentModel ) {

        return new SElementExp( nameClass, contentModel,
            new PrefixResolverImpl(this),
            rules,
            new SActions(asserts,reports) );
	}
}
