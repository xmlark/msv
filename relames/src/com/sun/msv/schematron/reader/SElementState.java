package com.sun.msv.schematron.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.trex.ng.ElementState;
import com.sun.msv.schematron.grammar.SAction;
import com.sun.msv.schematron.grammar.SActions;
import com.sun.msv.schematron.grammar.SElementExp;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.util.StartTagInfo;

import java.util.ArrayList;
import java.util.List;

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
        if( tag.localName.equals("pattern") )
            return new SPatternState();
        if( tag.localName.equals("ns") )
            return new IgnoreState();
        return null;
	}
	
	private final List asserts = new ArrayList();
	public void onAssert( SAction action ) {
		asserts.add(action);
	}
	private final List reports = new ArrayList();
	public void onReport( SAction action ) {
		reports.add(action);
	}
	private final List rules = new ArrayList();
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
