/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.psvi.TypeDetector;
import com.sun.msv.verifier.psvi.TypedContentHandler;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.util.ErrorHandlerImpl;

/**
 * Records the resulting PSVI annotation into the specified
 * association manager.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class PSVIRecorder implements TypedContentHandler {
    
    private final DOMScanner scanner = new DOMScanner();
    private final TypeDetector verifier;
    private final XalanNodeAssociationManager manager;
    
    public PSVIRecorder( Grammar grammar, XalanNodeAssociationManager _manager ) {
        verifier = new TypeDetector(
            new REDocumentDeclaration(grammar),
            this,
            new ErrorHandlerImpl() );
        this.manager = _manager;
    }
    
    /**
     * Validates the node and records its PSVI annotation.
     * 
     * @return
     *      true if the validation was successful
     */
    public boolean annotate( Element e ) {
        try {
            scanner.parse( e, verifier );
            return true;
        } catch( SAXException ex ) {
            return false;
        }
    }
    
    
    
    public void startDocument(ValidationContext context) throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void characterChunk(String literal, Datatype type) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void endElement(String namespaceURI, String localName, String qName, ElementExp type) {
        manager.put( scanner.getCurrentLocation(), type );
    }

    public void startAttribute(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void endAttribute(String namespaceURI, String localName, String qName, AttributeExp type) {
        // TODO
    }

    public void endAttributePart() throws SAXException {
    }

}
