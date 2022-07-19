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
