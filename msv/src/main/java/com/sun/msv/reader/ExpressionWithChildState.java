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

package com.sun.msv.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses Expression which contains other expressions.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionWithChildState
    extends ExpressionState implements ExpressionOwner {
    
    /**
     * expression object that is being created.
     * See {@link #castPattern} and {@link #annealPattern} methods
     * for how will a pattern be created.
     */
    protected Expression exp;

    protected void startSelf() {
        super.startSelf();
        exp = initialExpression();
    }
    
    /** sets initial pattern */
    protected Expression initialExpression() {
        return null;
    }
    
    /**
     * computes default expression.
     * this method is called before annealExpression when no child expression
     * is given during parsing. return null to signal an error.
     */
    protected Expression defaultExpression() {
        return null;
    }
    
    /** receives a Pattern object that is contained in this element. */
    public final void onEndChild( Expression childExpression ) {
        exp = castExpression( exp, childExpression );
    }
    
    protected final Expression makeExpression() {
        if( exp==null )
            exp = defaultExpression();
        
        if( exp==null ) {
            reader.reportError( GrammarReader.ERR_MISSING_CHILD_EXPRESSION );
            exp = Expression.nullSet;
            // recover by assuming some pattern.
        }
        return annealExpression(exp);
    }
    
    protected State createChildState( StartTagInfo tag ) {
        return reader.createExpressionChildState(this,tag);
    }

        
    /**
     * combines half-made expression and newly found child expression into the expression.
     * 
     * <p>
     * Say this container has three child expression exp1,exp2, and exp3.
     * Then, the expression of this state will be made by the following method
     * invocations.
     * 
     * <pre>
     *   annealExpression(
     *     castExpression(
     *       castExpression(
     *         castExpression(null,exp1), exp2), exp3 ) )
     * </pre>
     */
    protected abstract Expression castExpression( Expression halfCastedExpression, Expression newChildExpression );
    
    /**
     * performs final wrap-up and returns a fully created Expression object
     * that represents this element.
     */
    protected Expression annealExpression( Expression exp ) {
        // default implementation do nothing.
        return exp;
    }
}
