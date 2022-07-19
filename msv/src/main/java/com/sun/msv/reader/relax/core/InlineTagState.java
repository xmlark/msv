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

package com.sun.msv.reader.relax.core;

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.TagClause;

/**
 * parses &lt;tag&gt; element inlined in &lt;elementRule&gt;
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InlineTagState extends ClauseState
{
    protected void endSelf()
    {
        super.endSelf();
        
        String name = startTag.getAttribute("name");
        
        if(name==null)
        {// then it defaults to the label of parent state
            
            name = parentState.getStartTag().getAttribute("label");
            if(name==null)    // label attribute of the parent itself defaults to role attribute.
                name = parentState.getStartTag().getAttribute("role");
            
            if(name==null)
                // this is an error of elementRule.
                // so user will receive an error by ElementRuleBaseState.
                // silently ignore this error here.
                name = "<undefined>";
        }
        
        
        if(!(parentState instanceof ElementRuleBaseState ))
            // inline element must be used as a child of elementRule
            throw new Error();    // assertion failed.
        
        TagClause c = new TagClause();
        
        c.nameClass = new SimpleNameClass(
            getReader().module.targetNamespace,
            name );
        c.exp = exp;    // exp holds a sequence of AttributeExp
        
        ((ElementRuleBaseState)parentState).onEndInlineClause(c);
        
        return;
    }
}
