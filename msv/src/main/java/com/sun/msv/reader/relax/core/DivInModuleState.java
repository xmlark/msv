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
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSTypeOwner;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;div&gt; element under &lt;module&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DivInModuleState extends SimpleState implements ExpressionOwner, XSTypeOwner
{
    /** gets reader in type-safe fashion */
    protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }

    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("div"))            return getReader().getStateFactory().divInModule(this,tag);
        if(tag.localName.equals("hedgeRule"))    return getReader().getStateFactory().hedgeRule(this,tag);
        if(tag.localName.equals("tag"))            return getReader().getStateFactory().tag(this,tag);
        if(tag.localName.equals("attPool"))        return getReader().getStateFactory().attPool(this,tag);
        if(tag.localName.equals("include"))        return getReader().getStateFactory().include(this,tag);
        if(tag.localName.equals("interface"))    return getReader().getStateFactory().interface_(this,tag);
        if(tag.localName.equals("elementRule")) return getReader().getStateFactory().elementRule(this,tag);
        if(tag.localName.equals("simpleType"))    return getReader().getStateFactory().simpleType(this,tag);
        
        return null;
    }
    
    // do nothing. declarations register themselves by themselves.
    public void onEndChild( Expression exp ) {}
    public String getTargetNamespaceUri() { return ""; }
    public void onEndChild( XSDatatypeExp type ) {
        // user-defined simple types
        
        final String typeName = type.name;
        
        if( typeName==null ) {
            // top-level simpleType must define a named type
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
            return;    // recover by ignoring this declaration
        }
        
        // memorize this type.
        getReader().addUserDefinedType(type);
    }
}
