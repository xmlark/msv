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

package com.sun.msv.reader.xmlschema;

import javax.xml.transform.Source;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.util.Util;

/**
 * An utility class that
 * reads multiple XML Schema documents and combines them into one schema object.
 * 
 * <h2>Usage</h2>
 * <p>
 * Creates a new instance of {@link XMLSchemaReader}, then pass it to the
 * constructor of this class. Then call the parse method as many times as you want.
 * Finally, call the finish method.
 * </p><p>
 * The parsed grammar can be obtained from the underlying XMLSchemaReader object.
 * </p>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MultiSchemaReader
{
    public MultiSchemaReader( XMLSchemaReader _reader ) {
        this.reader = _reader;
        reader.setDocumentLocator(new LocatorImpl());
    }
    
    private final XMLSchemaReader reader;
    
    private boolean finalized = false;
    
    /**
     * @deprecated
     */
    public final XMLSchemaReader getReader() { return reader; }
    
    /**
     * Obtains the parsed grammar.
     */
    public final XMLSchemaGrammar getResult() {
        finish();
        return reader.getResult();
    }

    /**
     * Parse a schema supplied by a javax.xml.transform Source.
     * @param source
     */
    public void parse(Source source) {
        reader.switchSource(source,
                new RootIncludedSchemaState(
                    reader.sfactory.schemaHead(null)) );
    }
    
    public final void parse( String source ) {
        parse(Util.getInputSource(source));
    }
    
    /**
     * Parses an additional schema.
     * 
     * The caller can call this method many times to parse
     * multiple schemas.
     * 
     * If possible, the caller should set the system Id to the InputSource.
     */
    public void parse( InputSource is ) {
        reader.switchSource( is,
            new RootIncludedSchemaState(
                reader.sfactory.schemaHead(null)) );
    }
    
    /**
     * This method should be called when all the schemas are parsed.
     */
    public void finish() {
        if( !finalized ) {
            finalized = true;
            reader.wrapUp();
        }
    }
}
