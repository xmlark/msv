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

package com.sun.msv.relaxns.reader.relax;

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.relaxns.grammar.relax.AnyOtherElementExp;

/**
 * parses &lt;anyOtherElement&gt; state.
 * 
 * To create an expression that implements the semantics of anyOtherElement,
 * the entire grammar must be parsed first.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyOtherElementState extends ExpressionWithoutChildState
{
    protected Expression makeExpression() {
        // when makeExpression is called, return only a skelton.
        // later, after the entire grammar is parsed, we'll provide
        // actual expression.
        
        String in = startTag.getAttribute("includeNamespace");
        String ex = startTag.getAttribute("excludeNamespace");

        if( in!=null && ex!=null ) {
            reader.reportError(
                new Locator[]{this.location},
                RELAXCoreIslandSchemaReader.ERR_CONFLICTING_ATTRIBUTES,
                new Object[]{"includeNamespace", "excludeNamespace"} );
            ex=null;
        }
        
        if( in==null && ex==null )
            ex="";    // this will correctly implement the semantics.
        
        final AnyOtherElementExp exp = new AnyOtherElementExp( this.location, in, ex );
        ((RELAXCoreIslandSchemaReader)reader).pendingAnyOtherElements.add(exp);
        return exp;
    }
    
}
