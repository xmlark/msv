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

package com.sun.msv.reader.trex.ng;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithoutChildState;
import com.sun.msv.util.StringPair;

/**
 * parses &lt;value&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValueState extends ExpressionWithoutChildState {
    
    protected final StringBuffer text = new StringBuffer();
    
    public void characters( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    public void ignorableWhitespace( char[] buf, int from, int len ) {
        text.append(buf,from,len);
    }
    
    protected Expression makeExpression() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        String typeName = startTag.getCollapsedAttribute("type");
        
        Datatype type;
        
        StringPair typeFullName;
        
        if(typeName==null) {
            try {
                // defaults to built-in token type.
                type = reader.resolveDataTypeLibrary("").createDatatype("token");
                typeFullName = new StringPair("","token");
            } catch( DatatypeException e ) {
                // since token is the built-in datatype,
                // this can't happen
                e.printStackTrace();
                throw new InternalError();
            }
        } else {
            type = reader.resolveDataType(typeName);
            typeFullName = new StringPair(reader.datatypeLibURI,typeName);
        }
        
        Object value = type.createValue(text.toString(),reader);
        if( value==null ) {
            // this is not a good value for this type.
            reader.reportError( RELAXNGReader.ERR_BAD_DATA_VALUE, typeName, text.toString().trim() );
            return Expression.nullSet;    // recover by returning something.
        }
        
        return reader.pool.createValue( type, typeFullName, value );
    }
}
