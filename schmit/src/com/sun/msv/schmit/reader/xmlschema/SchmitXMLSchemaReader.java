/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.reader.xmlschema;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;

import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.State;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.util.StartTagInfo;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SchmitXMLSchemaReader extends XMLSchemaReader {
    
    /**
     * Document object that can be used to create DOM nodes.
     */
    protected final Document dom; 

    public SchmitXMLSchemaReader(
        GrammarReaderController controller,
        SAXParserFactory parserFactory,
        ExpressionPool pool) {
        super(controller, parserFactory, new StateFactory2(), pool);
        
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
    
    private static class StateFactory2 extends StateFactory {
        public State attribute(State parent, StartTagInfo tag) {
            return new SchmitAttributeState();
        }
        public State elementDecl(State parent, StartTagInfo tag) {
            return new SchmitElementDeclState();
        }
    }
}
