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

package com.sun.msv.relaxns.grammar.relax;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.ElementRules;
import com.sun.msv.grammar.relax.HedgeRules;
import com.sun.msv.grammar.relax.RELAXExpressionVisitorExpression;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.grammar.relax.TagClause;

/**
 * creates Expression that validates exported attPool.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class ExportedAttPoolGenerator extends ExpressionCloner implements RELAXExpressionVisitorExpression
{
    ExportedAttPoolGenerator( ExpressionPool pool ) { super(pool); }
        
    private String targetNamespace;
    public Expression create( RELAXModule module, Expression exp )
    {
        targetNamespace = module.targetNamespace;
        return exp.visit(this);
    }
        
    public Expression onAttribute( AttributeExp exp )
    {
        if(!(exp.nameClass instanceof SimpleNameClass ))
            return exp;    // leave it as is. or should we consider this as a failed assertion?
            
        SimpleNameClass nc = (SimpleNameClass)exp.nameClass;
        if( !nc.namespaceURI.equals("") )
            return exp;    // externl attributes. leave it as is.
            
        return pool.createAttribute(
            new SimpleNameClass( targetNamespace, nc.localName ),
            exp.exp );
    }
        
    // we are traversing attPools. thus these will never be possible.
    public Expression onElement( ElementExp exp )            { throw new Error(); }
    public Expression onTag( TagClause exp )                { throw new Error(); }
    public Expression onElementRules( ElementRules exp )    { throw new Error(); }
    public Expression onHedgeRules( HedgeRules exp )        { throw new Error(); }
        
    public Expression onRef( ReferenceExp exp )
    {
        // this class implements RELAXExpressionVisitorExpression.
        // So this method should never be called
        throw new Error();
    }
    public Expression onOther( OtherExp exp ) {
        // OtherExps are removed from the generated expression.
        return exp.exp.visit(this);
    }
        
    public Expression onAttPool( AttPoolClause exp )
    {// create exported version for them, too.
            
        // note that thsi exp.exp may be a AttPool of a different module.
        // In that case, calling visit method is no-op. But at least
        // it doesn't break anything.
        return exp.exp.visit(this);
    }
}
