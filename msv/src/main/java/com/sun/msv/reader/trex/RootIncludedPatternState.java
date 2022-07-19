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

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses the root state of a grammar included as a pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootIncludedPatternState extends SimpleState implements ExpressionOwner {

    protected State createChildState( StartTagInfo tag ) {
        // grammar has to be treated separately so as not to
        // create unnecessary TREXGrammar object.
//        if(tag.localName.equals("grammar"))
//            return new GrammarState();
        
        State s = reader.createExpressionChildState(this,tag);
//        if(s!=null) {
//            // other pattern element is specified.
//            // create wrapper grammar
//            final TREXBaseReader reader = (TREXBaseReader)this.reader;
//            reader.grammar = new TREXGrammar( reader.pool, null );
//            simple = true;
//        }
        
        return s;
    }
    
    
    /**
     * parsed external pattern will be reported to this object.
     * This state parses top-level, so parentState is null.
     */
    private final IncludePatternState grandParent;
    
    protected RootIncludedPatternState( IncludePatternState grandpa ) {
        this.grandParent = grandpa;
    }
        
    public void onEndChild(Expression exp) {
        if( grandParent!=null )
            // this must be from grammar element. pass it to the IncludePatternState.
            grandParent.onEndChild(exp);

    }
}
