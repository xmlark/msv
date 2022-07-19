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

import com.sun.msv.grammar.xmlschema.IdentityConstraint;

/**
 * XPath matcher that tests the selector of an identity constraint.
 * 
 * This object is created whenever an element with identity constraints is found.
 * XML Schema guarantees that we can see if an element has id constraints at the
 * startElement method.
 * 
 * This mathcer then monitor startElement/endElement and find matches to the
 * specified XPath. Every time it finds a match ("target node" in XML Schema
 * terminology), it creates a FieldsMatcher.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SelectorMatcher extends PathMatcher {
    
    protected IdentityConstraint idConst;

    SelectorMatcher(
                IDConstraintChecker owner, IdentityConstraint idConst,
                String namespaceURI, String localName ) throws SAXException {
        super(owner, idConst.selectors );
        this.idConst = idConst;
        
        // register this scope as active.
        owner.pushActiveScope(idConst,this);
        
        if(com.sun.msv.driver.textui.Debug.debug) {
            System.out.println("new id scope is available for {"+idConst.localName+"}");
        }

        super.start(namespaceURI,localName);
    }

    protected void onRemoved() throws SAXException {
        super.onRemoved();
        // this scope is no longer active.
        owner.popActiveScope(idConst,this);
    }

    
    protected void onElementMatched( String namespaceURI, String localName ) throws SAXException {
        if( com.sun.msv.driver.textui.Debug.debug )
            System.out.println("find a match for a selector: "+idConst.localName);
            
        // this element matches the path.
        owner.add( new FieldsMatcher(this, namespaceURI,localName) );
    }
    
    protected void onAttributeMatched(
        String namespaceURI, String localName, String value, Datatype type ) {
        
        // assertion failed:
        // selectors cannot contain attribute steps.
        throw new Error();
    }
    
}
