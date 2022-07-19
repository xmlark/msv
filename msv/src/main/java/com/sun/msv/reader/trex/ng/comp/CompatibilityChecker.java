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

package com.sun.msv.reader.trex.ng.comp;

import org.xml.sax.Locator;

import com.sun.msv.grammar.relaxng.RELAXNGGrammar;

abstract class CompatibilityChecker {
    
    
    protected final RELAXNGCompReader reader;
    protected final RELAXNGGrammar grammar;
    
    protected CompatibilityChecker( RELAXNGCompReader _reader ) {
        this.reader = _reader;
        this.grammar = (RELAXNGGrammar)_reader.getGrammar();
    }
    
    protected abstract void setCompatibility( boolean val );
    
    /**
     * reports the compatibility related error.
     * 
     * <p>
     * Since the processor is required to validate a schema even if 
     * it's not compatible with some of the features, we cannot report
     * those errors as real "errors".
     */
    protected void reportCompError( Locator[] locs, String propertyName ) {
        // TODO: it maybe useful to implement a switch
        // that makes those warnings as errors.
        reportCompError(locs,propertyName,null);
    }
    protected void reportCompError( Locator[] locs, String propertyName, Object[] args ) {
        setCompatibility(false);
        reader.reportWarning(propertyName,args,locs);
    }
}
