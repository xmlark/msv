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

package com.sun.msv.relaxns.reader;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;
import com.sun.msv.relaxns.verifier.SchemaProviderImpl;
import com.sun.msv.util.StartTagInfo;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used only one when starting parsing a RELAX schema.
 * For included module/grammar, different states are used.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootGrammarState extends SimpleState implements ExpressionOwner
{
    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("grammar") ) 
            // it is a grammar.
            return new GrammarState();
        
        return null;
    }
    
    protected void endSelf()
    {// wrap-up.
        final RELAXNSReader reader = (RELAXNSReader)this.reader;
        
        SchemaProviderImpl schemaProvider = new SchemaProviderImpl(reader.grammar);
        reader.schemaProvider = schemaProvider;
        
        if(!reader.controller.hadError()) {
            // abort further wrap up if there was an error.
            
            // then bind it as the final wrap-up.
            if( !schemaProvider.bind(reader.controller) )
                reader.controller.setErrorFlag();
        
            // also bind top-level expression
            if( reader.grammar.topLevel!=null )
                // this 'if' clause is necessary when
                // <topLevel> is not specified (which is an error, and already reported.)
                reader.grammar.topLevel = 
                    reader.grammar.topLevel.visit(
                        new IslandSchemaImpl.Binder(schemaProvider, reader.controller, reader.pool ) );
        }
    }
    
    // GrammarState implements ExpressionState,
    // so RootState has to implement ExpressionOwner.
    public final void onEndChild(Expression exp) {}
}
