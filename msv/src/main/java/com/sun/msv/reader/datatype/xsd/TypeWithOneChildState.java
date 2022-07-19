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

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.reader.GrammarReader;

/**
 * State which has at most one TypeState as its child.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class TypeWithOneChildState extends TypeState implements XSTypeOwner
{
    protected XSDatatypeExp type;

    /** receives a Pattern object that is contained in this element. */
    public void onEndChild( XSDatatypeExp child ) {
        if( type!=null )
            reader.reportError( GrammarReader.ERR_MORE_THAN_ONE_CHILD_TYPE );
            // recover by ignoring this child
        else
            type = child;
    }
    
    
    protected final XSDatatypeExp makeType() throws DatatypeException {
        if( type==null ) {
            reader.reportError( GrammarReader.ERR_MISSING_CHILD_TYPE );
            // recover by supplying a dummy DataType
            return new XSDatatypeExp( StringType.theInstance, reader.pool );
        }
        return annealType(type);
    }

    /**
     * performs final wrap-up and returns a fully created DataType object
     * that represents this element.
     */
    protected XSDatatypeExp annealType( XSDatatypeExp dt ) throws DatatypeException {
        // default implementation do nothing.
        return dt;
    }
}
