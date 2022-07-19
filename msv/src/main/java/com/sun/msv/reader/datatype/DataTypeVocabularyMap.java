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

package com.sun.msv.reader.datatype;

import java.util.Map;

/**
 * a map from namespace URI to DataTypeVocabulary
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeVocabularyMap implements java.io.Serializable {
    
    /** map from namespace URI to DataTypeVocabulary */
    private final Map impl = new java.util.HashMap();
    
    /**
     * obtains an DataTypeVocabulary associated to the namespace.
     * 
     * If necessary, Vocabulary is located and instanciated.
     */
    public DataTypeVocabulary get( String namespaceURI ) {
        
        DataTypeVocabulary v = (DataTypeVocabulary)impl.get(namespaceURI);
        if(v!=null)        return v;
        
        // TODO: generic way to load a vocabulary
        if( namespaceURI.equals( com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace ) ) {
            v = new com.sun.msv.reader.datatype.xsd.XSDVocabulary();
            impl.put( com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace, v );
            impl.put( com.sun.msv.reader.datatype.xsd.XSDVocabulary.XMLSchemaNamespace2, v );
        }
        
        return v;
    }
    
    /** manually adds DataTypeVocabulary into this map. */
    public void put( String namespaceURI, DataTypeVocabulary voc ) {
        impl.put( namespaceURI, voc );
    }
}
