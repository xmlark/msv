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
import com.sun.msv.grammar.trex.TREXGrammar;

/**
 * parses &lt;grammar&gt; element.
 * 
 * this state is used to parse top-level grammars and nested grammars.
 * grammars merged by include element are handled by MergeGrammarState.
 * 
 * <p>
 * this class provides a new TREXGrammar object to localize names defined
 * within this grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarState extends DivInGrammarState {
    protected TREXGrammar previousGrammar;
    protected TREXGrammar newGrammar;
    
    protected Expression makeExpression() {
        // start pattern is the grammar-as-a-pattern.
        return newGrammar;
    }

    protected void startSelf() {
        super.startSelf();
        
        previousGrammar = getReader().grammar;
        newGrammar = getReader().sfactory.createGrammar( reader.pool, previousGrammar );
        getReader().grammar = newGrammar;
    }

    public void endSelf() {
        final TREXGrammar grammar = getReader().grammar;
        
        // detect references to undefined pattterns
        reader.detectUndefinedOnes(
            grammar.namedPatterns, TREXBaseReader.ERR_UNDEFINED_PATTERN );

        // is start pattern defined?
        if( grammar.exp==null ) {
            reader.reportError( TREXBaseReader.ERR_MISSING_TOPLEVEL );
            grammar.exp = Expression.nullSet;    // recover by assuming a valid pattern
        }
        
        // this method is called when this State is about to be removed.
        // restore the previous grammar
        if( previousGrammar!=null )
            getReader().grammar = previousGrammar;
        
        // if the previous grammar is null, it means this grammar is the top-level
        // grammar. In that case, leave it there so that GrammarReader can access
        // the loaded grammar.
            
        super.endSelf();
    }
}
