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

package com.sun.msv.reader.trex.ng.comp;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.trex.ng.AttributeState;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CompAttributeState extends AttributeState {
    protected Expression annealExpression( Expression contentModel ) {
        
        final Expression exp = super.annealExpression(contentModel);
        
        String defaultValue = startTag.getAttribute(
            RELAXNGCompReader.AnnotationNamespace, "defaultValue" );
        if(defaultValue!=null && (exp instanceof AttributeExp)) {
//            if(!nameClass instanceof SimpleNameClass)
//                // attribute with a:defaultValue must be a simple name.
//                reader.reportError(
//                    RELAXNGCompReader.ERR_NAME_IS_NOT_SIMPLE_FOR_DEFAULTEDATTRIBUTE );
//        
            // remember that a default value is specified for this attribute.
            // Since AttributeExps are not unified, it is safe to use it as a key.
            // all checks are performed later.
            RELAXNGCompReader reader = (RELAXNGCompReader)this.reader;
            reader.addDefaultValue((AttributeExp)exp,defaultValue);
        }
                
        return exp;
    }
}
