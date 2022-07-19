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
