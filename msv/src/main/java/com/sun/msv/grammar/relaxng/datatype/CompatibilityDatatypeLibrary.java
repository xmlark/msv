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
