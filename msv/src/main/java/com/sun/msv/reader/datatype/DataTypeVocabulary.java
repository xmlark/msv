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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;

import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * this class is used to parse foreign datatype vocabulary.
 * 
 * Each datatype vocabulary must be associated with one namespace URI.
 * When the element with that namespace URI is first found, this object is
 * instanciated. After that, whenever the element with the namespace URI
 * is found, createTopLevelReaderState method will be used to parse the element
 * (and its descendants.)
 * 
 * And whenever a reference to this vocabulary by name (e.g., "mydt:mytypename")
 * is found, getType method is called to resolve this name into DataType object.
 * 
 * One instance of this class is used throughout the parsing of one grammar.
 * Therefore, implementations are encouraged to take advantages of this property
 * and keep context information (e.g., user-defined named datatypes).
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface DataTypeVocabulary
{
    /**
     * creates a State object that will parse the element specified
     * by tag parameter.
     * 
     * @return null
     *        if given start tag is not recognized by this object.
     * 
     * This method is called when an "island" of this vocabulary was found.
     * The state returned from this method will be used to parse the root
     * element of this island.
     * 
     * The parent state of this state must implement TypeOwner or ExpressionOwner.
     * In either case, the implementation must report its parsing result
     * by calling either interface. If both interface is implemented,
     * the implementation must notify via TypeOwner interface only and may not
     * call methods of ExpressionOwner.
     * 
     * If the parsed island is not a type definition (for example, comments or
     * inclusion), the implementation may not call TypeOwner nor ExpressionOwner.
     */
    State createTopLevelReaderState( StartTagInfo tag );
    
    /**
     * resolves a type name to Datatype object.
     * 
     * @param localTypeName
     *        local part of the qualified name, like "string" or "integer".
     *        prefix part must be removed by the caller.
     * 
     * @return
     *        a non-null valid datatype object.
     * 
     * @exception DatatypeException
     *        if the specified type name is a valid type name.
     */
    Datatype getType( String localTypeName ) throws DatatypeException;
}
