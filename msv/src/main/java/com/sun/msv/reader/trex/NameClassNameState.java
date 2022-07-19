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

package com.sun.msv.reader.trex;

import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * parses &lt;name&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassNameState extends NameClassWithoutChildState {
    protected final StringBuffer text = new StringBuffer();
    
    public void characters( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    public void ignorableWhitespace( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }

    protected NameClass makeNameClass() {
        
        String name = WhiteSpaceProcessor.collapse(new String(text));
        
        int idx = name.indexOf(':');
        if(idx<0)
            // if the name is NCName
            return new SimpleNameClass( getPropagatedNamespace(), name );
        
        // if it's a QName, resolve it.
        String[] qname 
            = reader.splitQName(name);
        
        return new SimpleNameClass( qname[0], qname[1] );
    }
}
