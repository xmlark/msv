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

package com.sun.msv.reader.relax.core;

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.relax.AttPoolClause;

/**
 * parses &lt;attPool&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttPoolState extends ClauseState {
    
    protected void endSelf( ) {
    
        super.endSelf();
        
        final String role = startTag.getAttribute("role");
        if(role==null) {
            reader.reportError(RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "attPool","role");
            return;    // recover by ignoring this declaration
        }
        
        if( startTag.getAttribute("combine")==null ) {
            AttPoolClause c = getReader().module.attPools.getOrCreate(role);
        
            if(c.exp!=null) {
                // someone has already initialized this clause.
                // this happens when more than one attPool element declares the same role.
                reader.reportError(
                    new Locator[]{getReader().getDeclaredLocationOf(c),location},
                    RELAXCoreReader.ERR_MULTIPLE_ATTPOOL_DECLARATIONS, new Object[]{role} );
                // recover from error by ignoring previous tag declaration
            }
        
            c.exp = exp;    // exp holds a sequence of AttributeExp
            getReader().setDeclaredLocationOf(c);    // remember where this AttPool is declared
        } else {
            // this attPool has @combine
            
            ReferenceExp e = getReader().combinedAttPools._getOrCreate(role);
            if( e.exp==null )    e.exp = Expression.epsilon;
            // append newly found attributes.
            e.exp = reader.pool.createSequence( exp, e.exp );
            reader.setDeclaredLocationOf(e);
        }
        
        
        return;
    }
}
