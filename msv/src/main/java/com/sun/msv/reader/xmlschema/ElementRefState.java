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
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.ExpressionWithoutChildState;

/**
 * used to parse &lt;element &gt; element with ref attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementRefState extends ExpressionWithoutChildState {

    protected Expression makeExpression() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        // TODO: what attributes can be used with @ref?
        
        if( !startTag.containsAttribute("ref") )
            // existance of @ref must be checked before instanciation of this object.
            throw new Error();
        
        // this this tag has @ref.
        Expression exp = reader.resolveQNameRef(
            startTag, "ref",
            new XMLSchemaReader.RefResolver() {
                public ReferenceContainer get( XMLSchemaSchema g ) {
                    return g.elementDecls;
                }
            } );
        if( exp==null )        return Expression.epsilon;    // couldn't resolve QName.
        
        // minOccurs/maxOccurs is processed through interception
        return exp;
    }
}
