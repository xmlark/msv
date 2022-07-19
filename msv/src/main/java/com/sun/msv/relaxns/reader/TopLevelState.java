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

package com.sun.msv.relaxns.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.State;
import com.sun.msv.reader.relax.HedgeRuleBaseState;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;topLevel&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TopLevelState extends HedgeRuleBaseState
{
    protected void endSelf( Expression contentModel ) {
        ((RELAXNSReader)reader).grammar.topLevel = contentModel;
    }

    protected State createChildState( StartTagInfo tag )
    {
        // user tends to forget to specify RELAX Core namespace for
        // topLevel elements. see if this is the case
        if( tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace))
        {// bingo.
            reader.reportError( RELAXNSReader.ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE );
            // return null so that user will also receive "malplaced element" error.
            return null;
        }
        
        return super.createChildState(tag);
    }

    protected boolean isGrammarElement( StartTagInfo tag ) {
        // children of <topLevel> must be RELAXCore.
        if( tag.namespaceURI.equals(RELAXCoreReader.RELAXCoreNamespace) )
            return true;
        
        // for better error message, allow RELAX Namespace elements.
        // this error is handled at createChildState method.
        if( tag.namespaceURI.equals(RELAXNSReader.RELAXNamespaceNamespace) )
            return true;
        
        return false;
    }
}
