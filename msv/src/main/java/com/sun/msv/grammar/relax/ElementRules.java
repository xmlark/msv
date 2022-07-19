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

package com.sun.msv.grammar.relax;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Set of ElementRule objects that share the label name.
 * 
 * ReferenceExp.exp contains choice of ElementRule objects.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRules extends ReferenceExp implements Exportable {
    
    protected ElementRules( String label, RELAXModule ownerModule ) {
        super(label);
        this.ownerModule = ownerModule;
    }
    
    public boolean equals( Object o ) {
        return this==o;
    }
    
    protected boolean calcEpsilonReducibility() {
        // elementRules are always not epsilon-reducible.
        return false;
    }
    
    public void addElementRule( ExpressionPool pool, ElementRule newRule ) {
        newRule.parent = this;
        if( exp==null )        // the first element
            exp = newRule;
        else
            exp = pool.createChoice(exp,newRule);
    }

    public Object visit( RELAXExpressionVisitor visitor )
    { return visitor.onElementRules(this); }

    public Expression visit( RELAXExpressionVisitorExpression visitor )
    { return visitor.onElementRules(this); }
    
    public boolean visit( RELAXExpressionVisitorBoolean visitor )
    { return visitor.onElementRules(this); }

    public void visit( RELAXExpressionVisitorVoid visitor )
    { visitor.onElementRules(this); }

    /**
     * a flag that indicates this elementRule is exported and
     * therefore accessible from other modules.
     */
    public boolean exported = false;
    public boolean isExported() { return exported; }
    
    /** RELAXModule object to which this object belongs */
    public final RELAXModule ownerModule;
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
