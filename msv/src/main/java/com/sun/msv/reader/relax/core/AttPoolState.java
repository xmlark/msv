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
