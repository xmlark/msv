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

package com.sun.msv.reader.relax;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for HedgeRuleState and TopLevelState.
 * 
 * expects one and only one expression as its child.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class HedgeRuleBaseState extends SimpleState implements ExpressionOwner
{
    private Expression contentModel = null;
    
    public void onEndChild( Expression exp )
    {// this method is called after child expression is found and parsed
        if( contentModel!=null )
            reader.reportError( RELAXReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
            // recover by ignoring previous expression
        
        contentModel = exp;
    }
    
    protected final void endSelf()
    {
        super.endSelf();
        
        if( contentModel==null )
        {
            reader.reportError( RELAXReader.ERR_MISSING_CHILD_EXPRESSION );
            return;    // recover by ignoring this hedgeRule.
        }
        
        endSelf(contentModel);    // do something useful with child expression
    }
    
    /** derived class will receive child expression by this method */
    protected abstract void endSelf( Expression contentModel );

    
    protected State createChildState( StartTagInfo tag ) {
        // particles only
        return reader.createExpressionChildState(this,tag);
    }
}
