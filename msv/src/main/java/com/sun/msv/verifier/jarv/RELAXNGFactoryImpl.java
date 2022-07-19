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

package com.sun.msv.verifier.jarv;

import org.relaxng.datatype.DatatypeLibraryFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.trex.ng.RELAXNGReader;

/**
 * VerifierFactory implementation of RELAX NG.
 * 
 * This implementation supports the "datatypeLibraryFactory" property
 * which configures RELAX NG parser with a datatype library factory.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGFactoryImpl extends FactoryImpl {
    
    private DatatypeLibraryFactory datatypeLibraryFactory = null;
    
    private static final String PROP_NAME = "datatypeLibraryFactory";
    
    protected Grammar parse( InputSource is, GrammarReaderController controller ) {
        RELAXNGReader reader = new RELAXNGReader(controller,factory);
        if( datatypeLibraryFactory!=null )
            reader.setDatatypeLibraryFactory(datatypeLibraryFactory);
        reader.parse(is);
        
        return reader.getResult();
    }
    
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals(PROP_NAME) )
            return datatypeLibraryFactory;
        return super.getProperty(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if( name.equals(PROP_NAME) ) {
            datatypeLibraryFactory = (DatatypeLibraryFactory)value;
            return;
        }
        super.setProperty(name, value);
    }

}
