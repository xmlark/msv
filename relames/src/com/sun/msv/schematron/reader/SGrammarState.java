package com.sun.msv.schematron.reader;

import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.ng.GrammarState;
import com.sun.msv.util.StartTagInfo;

/**
 * @author Kohsuke Kawaguchi
 */
public class SGrammarState extends GrammarState {
    protected State createChildState(StartTagInfo tag) {
        if(!tag.namespaceURI.equals(SRELAXNGReader.SchematronURI))
            return super.createChildState(tag);

        if( tag.localName.equals("rule") )
            return new SRuleState();
        if( tag.localName.equals("pattern") )
            return new SPatternState();
        if( tag.localName.equals("ns") )
            return new SNsState();
        return null;
    }
}
