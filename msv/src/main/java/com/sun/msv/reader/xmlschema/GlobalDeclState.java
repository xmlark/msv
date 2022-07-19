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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.reader.datatype.xsd.XSTypeOwner;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses global declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GlobalDeclState extends SimpleState
    implements ExpressionOwner,XSTypeOwner {
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if(tag.localName.equals("include"))            return reader.sfactory.include(this,tag);
        if(tag.localName.equals("import"))            return reader.sfactory.import_(this,tag);
        if(tag.localName.equals("redefine"))        return reader.sfactory.redefine(this,tag);
        if(tag.localName.equals("simpleType"))        return reader.sfactory.simpleType(this,tag);
        if(tag.localName.equals("complexType"))        return reader.sfactory.complexTypeDecl(this,tag);
        if(tag.localName.equals("group"))            return reader.sfactory.group(this,tag);
        if(tag.localName.equals("attributeGroup"))    return reader.sfactory.attributeGroup(this,tag);
        if(tag.localName.equals("element"))            return reader.sfactory.elementDecl(this,tag);
        if(tag.localName.equals("attribute"))        return reader.sfactory.attribute(this,tag);
        if(tag.localName.equals("notation"))        return reader.sfactory.notation(this,tag);
        
        return null;
    }

    public String getTargetNamespaceUri() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.currentSchema.targetNamespace;
    }
    
    // do nothing. declarations register themselves by themselves.
    public void onEndChild( Expression exp ) {}
    
    public void onEndChild( XSDatatypeExp type ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
//        final XSDatatypeImpl dti = (XSDatatypeImpl)type;
//        final String typeName = dti.getName();
        
        String typeName = reader.getCurrentState().getStartTag().getAttribute("name");
        
        if( typeName==null ) {
            // top-level simpleType must define a named type
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
            return;    // recover by ignoring this declaration
        }
        
        // memorize this type.
        final SimpleTypeExp exp = reader.currentSchema.simpleTypes.getOrCreate(typeName);
        if(exp.getType()!=null ) {
            reader.reportError( XMLSchemaReader.ERR_DATATYPE_ALREADY_DEFINED, typeName );
            return;
            // recover by ignoring this declaration
        }
        
        exp.set(type);
        reader.setDeclaredLocationOf(exp);
    }
}
