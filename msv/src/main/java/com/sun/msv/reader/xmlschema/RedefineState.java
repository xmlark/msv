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

import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.reader.AbortException;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.util.StartTagInfo;


/**
 * used to parse &lt;redefine&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RedefineState extends GlobalDeclState {
    
    // TODO: elementDecl/attributeDecl are prohibited in redefine.
    // TODO: it probably is an error to redefine undefined components.
    
    // TODO: it is NOT an error to fail to load the specified schema (see 4.2.3)

    /**
     * When a simple type is being redefined, the original declaration
     * will be stored here.
     */
    private SimpleTypeExp oldSimpleTypeExp;
    
    protected State createChildState( StartTagInfo tag ) {
        // SimpleType parsing is implemented in reader.datatype.xsd,
        // and therefore it doesn't support redefinition by itself.
        // so we need to take care of redefinition for them.
        // for detail, see RedefinableDeclState
        
        if( tag.localName.equals("simpleType") ) {
            final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            String name = tag.getAttribute("name");
            
            SimpleTypeExp sexp = reader.currentSchema.simpleTypes.get(name);
            if( sexp==null ) {
                reader.reportError( XMLSchemaReader.ERR_REDEFINE_UNDEFINED, name );
                // recover by using an empty declaration
                sexp = reader.currentSchema.simpleTypes.getOrCreate(name);
            }
            
            reader.currentSchema.simpleTypes.redefine( name, sexp.getClone() );
            
            oldSimpleTypeExp = sexp;    // memorize this declaration
        }
        
        return super.createChildState(tag);
    }
    
    
    protected void startSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        super.startSelf();
    
        try {// parse included grammar first.
            reader.switchSource( this,
                new RootIncludedSchemaState(
                    reader.sfactory.schemaIncluded(this,reader.currentSchema.targetNamespace) ) );
        } catch( AbortException e ) {
            // recover by ignoring the error
        }
        
        // disable duplicate definition check.
        prevDuplicateCheck = reader.doDuplicateDefinitionCheck;
    }
    
    /** previous value of reader#doDuplicateDefinitionCheck. */
    private boolean prevDuplicateCheck;
    
    protected void endSelf() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.doDuplicateDefinitionCheck = prevDuplicateCheck;
        super.endSelf();
    }
    
    
    public void onEndChild( XSDatatypeExp type ) {
        // handle redefinition of simpleType.
        
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final String typeName = type.name;
        
        if( typeName==null ) {
            // top-level simpleType must define a named type
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
            return;    // recover by ignoring this declaration
        }
        
        oldSimpleTypeExp.set(type);
        reader.setDeclaredLocationOf(oldSimpleTypeExp);
        
        // restore the association
        reader.currentSchema.simpleTypes.redefine( oldSimpleTypeExp.name, oldSimpleTypeExp );
        
        oldSimpleTypeExp = null;
    }

}
