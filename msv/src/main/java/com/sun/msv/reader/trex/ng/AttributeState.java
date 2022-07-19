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

package com.sun.msv.reader.trex.ng;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * parses &lt;attribute&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends com.sun.msv.reader.trex.AttributeState
{
    private static final String infosetURI = "http://www.w3.org/2000/xmlns";
    protected void endSelf() {
        super.endSelf();
        
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        reader.restrictionChecker.checkNameClass(nameClass);
        
        nameClass.visit( new NameClassVisitor() {
            public Object onAnyName( AnyNameClass nc ) { return null; }
            public Object onSimple(SimpleNameClass nc) {
                if(nc.namespaceURI.equals(infosetURI))
                    reader.reportError( RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE );
                
                if(nc.namespaceURI.length()==0 && nc.localName.equals("xmlns"))
                    reader.reportError( RELAXNGReader.ERR_XMLNS_ATTRIBUTE );
                return null;
            }
            public Object onNsName( NamespaceNameClass nc ) {
                if(nc.namespaceURI.equals(infosetURI))
                    reader.reportError( RELAXNGReader.ERR_INFOSET_URI_ATTRIBUTE );
                return null;
            }
            public Object onNot( NotNameClass nc ) {
                nc.child.visit(this);
                return null;
            }
            public Object onDifference( DifferenceNameClass nc ) {
                nc.nc1.visit(this); nc.nc2.visit(this);
                return null;
            }
            public Object onChoice( ChoiceNameClass nc ) {
                nc.nc1.visit(this); nc.nc2.visit(this);
                return null;
            }
        });
    }
}
