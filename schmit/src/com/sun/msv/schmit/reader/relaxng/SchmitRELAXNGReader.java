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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;

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
    
    /**
     * Document object that can be used to create DOM nodes.
     */
    protected final Document dom; 
    
    public SchmitRELAXNGReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool) {
        super(controller, parserFactory, new StateFactory(), pool);
        
        Document _dom;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            _dom = dbf.newDocumentBuilder().newDocument();
        } catch( ParserConfigurationException e ) {
            e.printStackTrace();    // can't happen
            _dom = null;
        }
        this.dom = _dom;
        
    }

    /**
     * Reads external attributes in the given start tag and add them as
     * {@link Attr} nodes.
     */
    protected final void parseAttributeAnnotation(StartTagInfo tag, AnnotationParent state) {
        int len = tag.attributes.getLength();
        for( int i=0; i<len; i++ ) {
            String uri = tag.attributes.getURI(i);
            if( uri.length()==0 )   continue;   // not a foreign attribute
            
            // create a DOM attribute node
            Attr a = dom.createAttributeNS( uri, tag.attributes.getQName(i) );
            a.setValue( tag.attributes.getValue(i) );
            
            state.onEndAnnotation(a);
        }
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
