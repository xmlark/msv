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

package com.sun.msv.verifier.regexp;

import java.util.Set;

import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.util.DatatypeRef;

/**
 * special StringToken that acts as a wild card.
 * 
 * This object is used for error recovery. It collects all TypedStringExps
 * that ate the token.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class StringRecoveryToken extends StringToken {
    
    StringRecoveryToken( StringToken base ) {
        this( base, new java.util.HashSet() );
    }
    
    StringRecoveryToken( StringToken base, Set failedExps ) {
        super( base.resCalc, base.literal, base.context, null );
        this.failedExps = failedExps;
    }
    
    /**
     * TypedStringExps and ListExps that
     * rejected this token are collected into this set.
     */
    final Set failedExps;
    
    public boolean match( DataExp exp ) {
        if( super.match(exp) )
            return true;
        
        // this datatype didn't accept me. so record it for diagnosis.
        failedExps.add( exp );
        return true;
    }
    
    public boolean match( ValueExp exp ) {
        if( super.match(exp) )
            return true;
        
        // this datatype didn't accept me. so record it for diagnosis.
        failedExps.add( exp );
        return true;
    }
    
    public boolean match( ListExp exp ) {
        super.match(exp);
        return true;
    }
        
    protected StringToken createChildStringToken( String literal, DatatypeRef dtRef ) {
        return new StringRecoveryToken(
            new StringToken( resCalc, literal, context, dtRef ) );
    }

}
