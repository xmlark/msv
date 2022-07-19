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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.relax.ElementRules;
import com.sun.msv.grammar.relax.TagClause;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * Base implementation for ElementRuleWithHedgeState and ElementRuleWithTypeState.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class ElementRuleBaseState extends SimpleState
{
    protected TagClause clause;
    
    /** gets reader in type-safe fashion */
    protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }
    
    /** gets content model of this elementRule */
    protected abstract Expression getContentModel();
    
    /** notification of inline tag element.
     * 
     * this method is called by InlineTagState after it is parsed
     */
    protected void onEndInlineClause( TagClause inlineTag )
    {
        if(clause!=null)
        {// more than one inline tag was specified
            reader.reportError( RELAXCoreReader.ERR_MORE_THAN_ONE_INLINE_TAG );
            // recover by ignoring previous local tag.
        }
        clause = inlineTag;
    }
    
    protected void endSelf() {
        String role = startTag.getAttribute("role");
        String label = startTag.getAttribute("label");
        
        if(role==null && label==null) {
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE_2,
                                "elementRule", "role", "label" );
            // recover from error by supplying dummy label
            label = "<undefined>";
        }
        
        if( label==null )    label=role;    // label attribute defaults to role attribute.
        
        if( clause==null ) {
            // inline <tag> element was not found.
            // role element must point to some TagClause
            if( role==null ) {
                reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE,
                                    "elementRule","role");
                // recover by assuming a harmless Clause
                clause = new TagClause();
                clause.nameClass = NameClass.ALL;
                clause.exp = Expression.nullSet;
            } else {
                clause = getReader().module.tags.getOrCreate(role);
            }
        }
        
        ElementRules er = getReader().module.elementRules.getOrCreate(label);
        getReader().setDeclaredLocationOf(er);    // remember where this ElementRules is defined
        
        er.addElementRule( reader.pool, new ElementRule( reader.pool, clause, getContentModel() ) );
        
        super.endSelf();
    }

    
    protected State createChildState( StartTagInfo tag )
    {
        if( tag.localName.equals("tag") )
            return getReader().getStateFactory().tagInline(this,tag);
        
        return null;    // otherwise unknown
    }
}
