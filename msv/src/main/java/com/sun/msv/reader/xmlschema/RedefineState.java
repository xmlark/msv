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
