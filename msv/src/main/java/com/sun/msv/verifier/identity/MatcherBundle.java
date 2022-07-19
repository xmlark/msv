/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.verifier.identity;

import org.relaxng.datatype.Datatype;
import org.xml.sax.SAXException;

/**
 * Base implementation of Matcher coordinator.
 * 
 * This class behaves as a parent of several other matchers, or as a composite
 * XPath matcher.
 * Those child matchers are not directly registered to IDConstraintChecker.
 * Instead, they receive notifications through this object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class MatcherBundle extends Matcher {
    
    /** child matchers. */
    protected Matcher[] children;
    /** depth. */
    private int depth = 0;
    protected final int getDepth() { return depth; }
    
    /**
     * the derived class must initialize the children field appropriately.
     */
    protected MatcherBundle( IDConstraintChecker owner ) {
        super(owner);
    }
    
    protected void startElement( String namespaceURI, String localName ) throws SAXException {
        
        depth++;
        for( int i=0; i<children.length; i++ )
            children[i].startElement(namespaceURI,localName);
    }
    
    protected void onAttribute( String namespaceURI, String localName, String value, Datatype type ) throws SAXException {
        for( int i=0; i<children.length; i++ )
            children[i].onAttribute(namespaceURI,localName,value,type);
    }
    
    protected void endElement( Datatype type ) throws SAXException {
        for( int i=0; i<children.length; i++ )
            children[i].endElement(type);
        if( depth-- == 0 ) {
            // traversal complete.
            owner.remove(this);
            onRemoved();
        }
    }

    protected void characters( char[] buf, int start, int len ) throws SAXException {
        for( int i=0; i<children.length; i++ )
            children[i].characters(buf,start,len);
    }
    
    /**
     * called when this bundle is deactivated.
     * This method is called by the endElement method when this bundle is
     * removed. A derived class can override this method to do whatever
     * necessary.
     */
    protected void onRemoved() throws SAXException {
    }
}
