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

import java.util.StringTokenizer;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses &lt;simpleType&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeState extends TypeWithOneChildState
{
    protected State createChildState( StartTagInfo tag ) {
        
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        final String name = startTag.getAttribute("name");
        String uri = getTargetNamespaceUri();
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("restriction") )    return new RestrictionState(uri,name);
        if( tag.localName.equals("list") )            return new ListState(uri,name);
        if( tag.localName.equals("union") )        return new UnionState(uri,name);
        
        return null;    // unrecognized
    }

    protected XSDatatypeExp annealType( final XSDatatypeExp dt ) {
        final String finalValueStr = startTag.getAttribute("final");
        if(finalValueStr!=null) {
            final int finalValue = getFinalValue(finalValueStr);
            
            // create a new type by adding final constraint.
            return dt.createFinalizedType(finalValue,reader);
        } else
            return dt;
    }


    /** parses final attribute */
    public int getFinalValue( String list ) {
        int finalValue = 0;
        StringTokenizer tokens = new StringTokenizer(list);
        while(tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            
            if( token.equals("#all") )
                finalValue |=    XSDatatype.DERIVATION_BY_LIST|
                                XSDatatype.DERIVATION_BY_RESTRICTION|
                                XSDatatype.DERIVATION_BY_UNION;
            else
            if( token.equals("restriction") )
                finalValue |= XSDatatype.DERIVATION_BY_RESTRICTION;
            else
            if( token.equals("list") )
                finalValue |= XSDatatype.DERIVATION_BY_LIST;
            else
            if( token.equals("union") )
                finalValue |= XSDatatype.DERIVATION_BY_UNION;
            else {
                reader.reportError( 
                    GrammarReader.ERR_ILLEGAL_FINAL_VALUE, token );
                return 0;    // abort
            }
        }
        return finalValue;
    }


}
