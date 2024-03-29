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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.reader.ExpressionWithChildState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;complexContent&gt; element.
 *
 * the expression created by this state is used as ComplexTypeExp.self field.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexContentState extends ExpressionWithChildState {

    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp parentDecl;

    protected ComplexContentState( ComplexTypeExp decl ) {
        this.parentDecl = decl;
    }

    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;

        if( super.exp!=null )
            // we have already parsed restriction/extension.
            return null;

        if( tag.localName.equals("restriction") )    return reader.sfactory.complexRst(this,tag,parentDecl);
        if( tag.localName.equals("extension") )        return reader.sfactory.complexExt(this,tag,parentDecl);

        return super.createChildState(tag);
    }

    protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
        if( halfCastedExpression!=null )
            // assertion failed.
            // this situation should be prevented by createChildState method.
            throw new Error();

        return newChildExpression;
    }

    /* Patch to address issue #1, [http://github.com/kohsuke/msv/issues#issue/1].
     */
    @Override
    protected Expression annealExpression(Expression contentType) {
      String mixed = startTag.getAttribute("mixed");

      if ("true".equals(mixed)) {
        contentType = reader.pool.createMixed(contentType);
      } else {
        if (mixed != null && !"false".equals(mixed)) {
          reader.reportError(XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "mixed", mixed);
          // recover by ignoring this error.
        }
      }

      return contentType;
    }
}
