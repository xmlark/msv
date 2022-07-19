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
