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

import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.ErrorDatatypeLibrary;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringPair;

/**
 * parses &lt;data&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataState extends ExpressionState implements ExpressionOwner {
    
    protected State createChildState( StartTagInfo tag ) {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        if( tag.localName.equals("except") )
            return reader.getStateFactory().dataExcept(this,tag);
        if( tag.localName.equals("param") )
            return reader.getStateFactory().dataParam(this,tag);
        
        return null;
    }
    
    /** type incubator object to be used to create a type. */
    protected DatatypeBuilder typeBuilder;
    
    /** the name of the base type. */
    protected StringPair baseTypeName;
    
    protected void startSelf() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        super.startSelf();
        
        final String localName = startTag.getCollapsedAttribute("type");
        if( localName==null ) {
            reader.reportError( RELAXNGReader.ERR_MISSING_ATTRIBUTE, "data", "type" );
        } else {
            // create a type incubator
            baseTypeName = new StringPair( reader.datatypeLibURI, localName );
            try {
                typeBuilder = reader.getCurrentDatatypeLibrary().createDatatypeBuilder(localName);
            } catch( DatatypeException dte ) {
                reader.reportError( RELAXNGReader.ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage() );
            }
        }
        
        if( typeBuilder==null ) {
            // if an error is encountered, then typeIncubator field is left null.
            // In that case, set a dummy implementation so that the successive param
            // statements are happy.
            typeBuilder = ErrorDatatypeLibrary.theInstance;
        }
    }
    
    /** the 'except' clause. Null if nothing was specified */
    protected Expression except = null;
    
    public void onEndChild( Expression child ) {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        // this method receives the 'except' clause, if any.
        if( except!=null )
            reader.reportError( RELAXNGReader.ERR_MULTIPLE_EXCEPT );
        
        except = child;
    }
    
    protected Expression makeExpression() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        try {
            if( except==null )    except=Expression.nullSet;
            
            return reader.pool.createData(
                typeBuilder.createDatatype(), baseTypeName, except );
                
        } catch( DatatypeException dte ) {
            reader.reportError( RELAXNGReader.ERR_INVALID_PARAMETERS, dte.getMessage() );
            // recover by returning something.
            return Expression.nullSet;
        }
    }
}
