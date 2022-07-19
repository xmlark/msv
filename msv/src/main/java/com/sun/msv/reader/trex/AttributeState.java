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

package com.sun.msv.reader.trex;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends NameClassAndExpressionState
{
    protected boolean firstChild=true;
    
    protected Expression initialExpression() {
        // <attribute> defaults to <anyString />
        return Expression.anyString;
    }

    protected String getNamespace() {
        final String ns = startTag.getAttribute("ns");
        final boolean global = "true".equals(startTag.getAttribute("global"));
        
        if( ns!=null )    return ns;    // "ns" attribute always has precedence.
        
        // if global="true" is specified, it defaults to propagated ns attribute.
        if( global )    return ((TREXBaseReader)reader).targetNamespace;
        
        // otherwise, it defaults to ""
        return "";
    }
            

    protected Expression castExpression( Expression initialExpression, Expression newChild ) {
        // <attribute> is allowed to have only one pattern
        if(!firstChild)
            reader.reportError( TREXBaseReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
            // recover by ignore the error
        firstChild = false;
        return newChild;
    }

    protected Expression annealExpression( Expression contentModel ) {
        Expression e = reader.pool.createAttribute( nameClass, contentModel );
        if(e instanceof AttributeExp)
            reader.setDeclaredLocationOf(e);
        return e;
    }
}
