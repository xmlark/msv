/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
