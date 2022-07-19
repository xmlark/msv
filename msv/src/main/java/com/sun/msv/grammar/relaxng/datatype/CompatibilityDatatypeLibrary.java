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
import org.relaxng.datatype.DatatypeLibrary;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.IDREFType;
import com.sun.msv.datatype.xsd.IDType;

/**
 * RELAX NG DTD compatibility datatype library.
 * 
 * This implementation relies on Sun XML Datatypes Library.
 * Compatibility datatypes library available through
 * <code>http://relaxng.org/ns/compatibility/datatypes/1.0</code>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CompatibilityDatatypeLibrary implements DatatypeLibrary {
    
    /** namespace URI of the compatibility datatypes library. */
    public static final String namespaceURI = "http://relaxng.org/ns/compatibility/datatypes/1.0";
    
    public Datatype createDatatype( String name ) throws DatatypeException {
        if( name.equals("ID") )
            return IDType.theInstance;
        if( name.equals("IDREF") )
            return IDREFType.theInstance;
        if( name.equals("IDREFS") )
            return DatatypeFactory.getTypeByName("IDREFS");
        
        throw new DatatypeException("undefined built-in type:"+name);
    }
    
    public DatatypeBuilder createDatatypeBuilder( String name ) throws DatatypeException {
        return new DatatypeBuilderImpl( createDatatype(name) );
    }
}
