/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
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
