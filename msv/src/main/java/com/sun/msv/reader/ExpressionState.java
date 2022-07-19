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

/**
 * Base implementation for those states who read tags representing an expression.
 * 
 * <p>
 * Responsibility of derived classes are:
 * 
 * <ol>
 *  <li>if necessary, implement startSelf method to do something.
 *  <li>implement createChildState method, which is mandated by SimpleState.
 *  <li>implement makeExpression method to create Expression object
 *        as the outcome of parsing. This method is called at endElement.
 * </ol>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionState extends SimpleState
{
    protected void endSelf()
    {
        // creates a expression, then calls a hook of reader,
        Expression exp = reader.interceptExpression( this, makeExpression() );
        
        if( parentState!=null )
            // then finally pass it to the parent
            ((ExpressionOwner)parentState).onEndChild(exp);
        
        // interceptExpression is a hook by reader.
        // it is used to implement handling of occurs attribute in RELAX.
        // application-defined reader can also do something useful for them here.

        super.endSelf();
    }
        
    /**
     * This method is called from endElement method.
     * Implementation has to provide Expression object that represents the content of
     * this element.
     */
    protected abstract Expression makeExpression();
}
    
