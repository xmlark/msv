/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.reader.relaxng;

import javax.xml.parsers.SAXParserFactory;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.util.StartTagInfo;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SchmitRELAXNGReader extends RELAXNGReader {

    public SchmitRELAXNGReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool) {
        super(controller, parserFactory, new StateFactory(), pool);
    }
    
    protected static class StateFactory extends RELAXNGReader.StateFactory {
        public State attribute(State parent, StartTagInfo tag) {
            return new SchmitAttributeState();
        }
        public State element(State parent, StartTagInfo tag) {
            return new SchmitElementState();
        }
    }

}
