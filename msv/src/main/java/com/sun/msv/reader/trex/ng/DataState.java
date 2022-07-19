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
