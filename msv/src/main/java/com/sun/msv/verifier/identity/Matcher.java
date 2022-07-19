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
 * Base abstract implementation of XPath matcher.
 * 
 * XPath mathcer tracks the startElement event and the endElement event.
 * The characters event is also used by some derived classes.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class Matcher {
    
    protected final IDConstraintChecker owner;
    Matcher( IDConstraintChecker owner ) {
        this.owner = owner;
    }
    
    protected abstract void startElement( String namespaceURI, String localName ) throws SAXException;
    protected abstract void onAttribute( String namespaceURI, String localName, String value, Datatype type ) throws SAXException;
    protected abstract void endElement( Datatype type ) throws SAXException;
    
    protected void characters( char[] buf, int start, int len ) throws SAXException {
        // do nothing by default.
    }
}
