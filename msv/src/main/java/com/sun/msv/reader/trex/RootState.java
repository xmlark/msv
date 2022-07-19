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
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * invokes State object that parses the document element.
 * 
 * This class is used to parse the first grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootState extends RootIncludedPatternState {
    
    public RootState() { super(null); }

    protected State createChildState( StartTagInfo tag ) {
        final TREXBaseReader reader = (TREXBaseReader)this.reader;

        // grammar has to be treated separately so as not to
        // create unnecessary TREXGrammar object.
        if(tag.localName.equals("grammar"))
            return reader.sfactory.grammar(null,tag);
        
        State s = super.createChildState(tag);
        if(s!=null) {
            // other pattern element is specified.
            // create wrapper grammar
            reader.grammar = reader.sfactory.createGrammar( reader.pool, null );
            simple = true;
        }
        
        return s;
    }
    
    /**
     * a flag that indicates 'grammar' element was not used.
     * In that case, this object is responsible to set start pattern.
     */
    private boolean simple = false;
        
    // GrammarState implements ExpressionState,
    // so RootState has to implement ExpressionOwner.
    public void onEndChild(Expression exp) {
        super.onEndChild(exp);

        final TREXBaseReader reader = (TREXBaseReader)this.reader;

        if( simple )
            // set the top-level expression if that is necessary.
            reader.grammar.exp = exp;
        
        // perform final wrap-up.
        reader.wrapUp();
    }
}
