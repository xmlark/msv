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

package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.ValidationContext;


/**
 * "language" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#language for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LanguageType extends TokenType {
    
    public static final LanguageType theInstance = new LanguageType();
    private LanguageType() { super("language",false); }
    
    final public XSDatatype getBaseType() {
        return TokenType.theInstance;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        /*    RFC1766 defines the following BNF
        
             Language-Tag = Primary-tag *( "-" Subtag )
             Primary-tag = 1*8ALPHA
             Subtag = 1*8ALPHA

            Whitespace is not allowed within the tag.
            All tags are to be treated as case insensitive.
        */
        
        final int len = content.length();
        int i=0; int tokenSize=0;
        
        while( i<len ) {
            final char ch = content.charAt(i++);
            if( ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') ) {
                tokenSize++;
                if( tokenSize==9 )
                    return null;    // maximum 8 characters are allowed.
            } else
            if( ch=='-' ) {
                if( tokenSize==0 )    return null;    // at least one alphabet preceeds '-'
                tokenSize=0;
            } else
                return null;    // invalid characters
        }
        
        if( tokenSize==0 )    return null;    // this means either string is empty or ends with '-'
        
        return content.toLowerCase();
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
