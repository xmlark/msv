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
