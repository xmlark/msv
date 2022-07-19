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

import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierFactoryLoader;

import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.xmlschema.XMLSchemaReader;
import com.sun.msv.relaxns.reader.RELAXNSReader;
    
public class FactoryLoaderImpl implements VerifierFactoryLoader {
    public FactoryLoaderImpl() {
    }

    public VerifierFactory createFactory(String language) {

        // supported language
        if (language.equals(RELAXNGReader.RELAXNGNamespace))
            return new RELAXNGFactoryImpl();
        if (language.equals(RELAXCoreReader.RELAXCoreNamespace))
            return new RELAXCoreFactoryImpl();
        if (language.equals(TREXGrammarReader.TREXNamespace))
            return new TREXFactoryImpl();
        if (language.equals(XMLSchemaReader.XMLSchemaNamespace)
            || language.equals(XMLSchemaReader.XMLSchemaNamespace_old))
            return new XSFactoryImpl();
        if(language.equals(RELAXNSReader.RELAXNamespaceNamespace))
            return new TheFactoryImpl();
        if(language.equals("http://www.w3.org/XML/1998/namespace"))
            return new DTDFactoryImpl();
        
        // backward compatibility
        if (language.equals("relax"))
            return new TheFactoryImpl();
        if (language.toUpperCase().equals("DTD"))
            return new DTDFactoryImpl();

        return null;
    }
}
