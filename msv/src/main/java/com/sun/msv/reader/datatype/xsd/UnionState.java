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

package com.sun.msv.reader.datatype.xsd;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses &lt;union&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnionState extends TypeState implements XSTypeOwner {

    protected final String newTypeUri;
    protected final String newTypeName;
    
    protected UnionState( String newTypeUri, String newTypeName ) {
        this.newTypeUri  = newTypeUri;
        this.newTypeName = newTypeName;
    }
    
    private final ArrayList memberTypes = new ArrayList();
                                                  
    protected State createChildState( StartTagInfo tag ) {
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("simpleType") )    return new SimpleTypeState();
        
        return null;    // unrecognized
    }
    
    protected void startSelf() {
        super.startSelf();
        
        // if memberTypes attribute is used, load it.
        String memberTypes = startTag.getAttribute("memberTypes");
        if(memberTypes!=null) {
            StringTokenizer tokens = new StringTokenizer(memberTypes);
            while( tokens.hasMoreTokens() )
                onEndChild( ((XSDatatypeResolver)reader)
                    .resolveXSDatatype(tokens.nextToken()) );
        }
    }
    
    public void onEndChild( XSDatatypeExp type ) {
        memberTypes.add(type);
    }
    
    
    protected final XSDatatypeExp makeType() throws DatatypeException {
        return XSDatatypeExp.makeUnion( newTypeUri, newTypeName, memberTypes, reader );
    }

}
