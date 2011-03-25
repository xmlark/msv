package com.sun.msv.schematron.reader;

import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.schematron.grammar.SRule;
import com.sun.msv.util.StartTagInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class SPatternState extends SimpleState implements SRuleReceiver {

    private final List rules = new ArrayList();

    protected State createChildState( StartTagInfo tag ) {

        if(!tag.namespaceURI.equals(SRELAXNGReader.SchematronURI))
            return null;

        if( tag.localName.equals("rule") )
            return new SRuleState();
        return null;
    }

    public void onRule(SRule rule) {
        rules.add(rule);
    }

    public void endSelf() {
        for (Iterator itr = rules.iterator(); itr.hasNext();) {
            SRule rule = (SRule) itr.next();
            ((SRuleReceiver)parentState).onRule(rule);
        }

        super.endSelf();
    }
}
