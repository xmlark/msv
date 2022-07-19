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
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * Used to parse merged grammars. Also &lt;div&gt; element in the grammar element
 * (of RELAX NG).
 * 
 * DivInGrammarState itself should not be a ExpressionState. However, GrammarState,
 * which is a derived class of this class, is a ExpressionState.
 * 
 * Therefore this class has to extend ExpressionState.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DivInGrammarState extends ExpressionState implements ExpressionOwner {
    
    protected final TREXBaseReader getReader() { return (TREXBaseReader)reader; }
    
    protected Expression makeExpression() {
        // this method doesn't provide any pattern
        return null;
    }

    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("start"))    return getReader().sfactory.start(this,tag);
        if(tag.localName.equals("define"))    return getReader().sfactory.define(this,tag);
        if(tag.localName.equals("include"))    return getReader().sfactory.includeGrammar(this,tag);
        // div is available only for RELAX NG.
        // The default implementation of divInGrammar returns null.
        if(tag.localName.equals("div"))        return getReader().sfactory.divInGrammar(this,tag);
        return null;
    }
    
    // DefineState and StartState is implemented by using ExpressionState.
    // By contract of that interface, this object has to implement ExpressionOwner.
    public void onEndChild( Expression exp ) {}    // do nothing.
}
