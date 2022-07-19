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
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.xmlschema.AttributeDeclExp;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;

/**
 * used to parse &lt;anyAttribute &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyAttributeState extends AnyState {

    protected Expression createExpression( final String namespace, final String process ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final XMLSchemaSchema currentSchema = reader.currentSchema;

        int mode;
        
        if(process.equals("skip"))        mode = AttributeWildcard.SKIP;
        else
        if(process.equals("lax"))        mode = AttributeWildcard.LAX;
        else
        if(process.equals("strict"))    mode = AttributeWildcard.STRICT;
        else {
            reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "processContents", process );
            mode = AttributeWildcard.SKIP;
        }
        
        ((AnyAttributeOwner)parentState).setAttributeWildcard(
            new AttributeWildcard( getNameClass(namespace,currentSchema), mode ) );
        
        return Expression.epsilon;
    }

    protected NameClass getNameClassFrom( ReferenceExp exp ) {
        return ((AttributeDeclExp)exp).self.nameClass;
    }
}
