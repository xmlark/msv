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

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.relaxng.datatype.helpers.DatatypeLibraryLoader;

import com.sun.msv.grammar.relaxng.datatype.BuiltinDatatypeLibrary;
import com.sun.msv.grammar.relaxng.datatype.CompatibilityDatatypeLibrary;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;

/**
 * Default implementation of Datatype
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class DefaultDatatypeLibraryFactory implements DatatypeLibraryFactory {

    private final DatatypeLibraryFactory loader = new DatatypeLibraryLoader();
    
    private DatatypeLibrary xsdlib;
    
    private DatatypeLibrary compatibilityLib;

    /**
     * @see org.relaxng.datatype.DatatypeLibraryFactory#createDatatypeLibrary(java.lang.String)
     */
    public DatatypeLibrary createDatatypeLibrary(String namespaceURI) {
        
        DatatypeLibrary lib = loader.createDatatypeLibrary(namespaceURI);
        if(lib!=null)       return lib;
        
        // if failed to dynamically locate the library, use static ones.
        
        if( namespaceURI.equals("") )
            return BuiltinDatatypeLibrary.theInstance;
        
        // We have the built-in support for XML Schema Part 2.
        if( namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace)
        ||  namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace2) ) {
            if(xsdlib==null)
                xsdlib = new com.sun.msv.datatype.xsd.ngimpl.DataTypeLibraryImpl();
            return xsdlib;
        }
        
        // RELAX NG compatibiltiy datatypes library is also supported
        if( namespaceURI.equals(CompatibilityDatatypeLibrary.namespaceURI) ) {
            if( compatibilityLib==null )
                compatibilityLib = new CompatibilityDatatypeLibrary();
            return compatibilityLib;
        }
        
        return null;
    }
    
}
