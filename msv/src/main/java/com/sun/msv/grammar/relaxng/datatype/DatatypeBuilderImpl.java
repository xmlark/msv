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

package com.sun.msv.grammar.relaxng.datatype;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * DataTypeBuilder implementation.
 * 
 * There is no paramater for any built-in and compatibility types.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class DatatypeBuilderImpl implements DatatypeBuilder {
    
    private final Datatype baseType;
    DatatypeBuilderImpl( Datatype baseType ) {
        this.baseType = baseType;
    }
    
    public Datatype createDatatype() {
        return baseType;
    }
    
    public void addParameter( String name, String value, ValidationContext context ) 
            throws DatatypeException {
        throw new DatatypeException(
            localize(ERR_PARAMETER_UNSUPPORTED,null));
    }


    protected String localize( String propertyName, Object[] args ) {
        String format = java.util.ResourceBundle.getBundle(
            "com.sun.msv.grammar.relaxng.Messages").getString(propertyName);
        
        return java.text.MessageFormat.format(format, args );
    }
    
    protected final static String ERR_PARAMETER_UNSUPPORTED = // arg:0
        "DataTypeBuilderImpl.ParameterUnsupported";
}
