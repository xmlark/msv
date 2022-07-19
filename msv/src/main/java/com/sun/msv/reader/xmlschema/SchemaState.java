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

import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;

/**
 * used to parse &lt;schema&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaState extends SchemaIncludedState {

    protected SchemaState( String expectedTargetNamespace ) {
        super(expectedTargetNamespace);
    }
    
    private XMLSchemaSchema old;
    
    protected void onTargetNamespaceResolved( String targetNs, boolean ignoreContents ) {
        super.onTargetNamespaceResolved(targetNs, ignoreContents);
    	XMLSchemaReader reader = (XMLSchemaReader)this.reader;        
        
        // sets new XMLSchemaGrammar object.
        old = reader.currentSchema;
        reader.currentSchema = reader.getOrCreateSchema(targetNs);
        /*
         * Don't check for errors if this is a redundant read that we are ignoring.
         */
        if (ignoreContents) {
        	return;
        }
        
        if( reader.isSchemaDefined(reader.currentSchema) )  {
            reader.reportError( XMLSchemaReader.ERR_DUPLICATE_SCHEMA_DEFINITION, targetNs );
            // recover by providing dummy grammar object.
            // this object is not registered to the map,
            // so it cannot be referenced.
            reader.currentSchema = new XMLSchemaSchema(targetNs,reader.grammar);
        }
        
        reader.markSchemaAsDefined(reader.currentSchema);
    }
    
    protected void endSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.currentSchema = old;
        super.endSelf();
    }
}
