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

import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.reader.State;
import com.sun.msv.reader.trex.NameClassWithChildState;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;anyName&gt; name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NGNameState extends NameClassWithChildState {
    
    NGNameState() {
        allowNullChild = true;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        // <except> tag is allowed only once.
        if( super.nameClass==null && tag.localName.equals("except") )
            return ((RELAXNGReader)reader).getStateFactory().nsExcept(this,tag);
        return null;
    }
    
    protected NameClass castNameClass( NameClass halfCastedNameClass, NameClass newChildNameClass ) {
        // error check is done by the createChildState method.
        return newChildNameClass;
    }
    
    /**
     * performs final wrap-up and returns a fully created NameClass object
     * that represents this element.
     */
    protected NameClass annealNameClass( NameClass nameClass ) {
        NameClass r = getMainNameClass();
        if( nameClass!=null )
            r = new DifferenceNameClass( r, nameClass );
        return r;
    }
    
    /** this method should return the name class that is used as the base. */
    protected abstract NameClass getMainNameClass();
    
    /** Parsing state for &lt;anyName&gt; */
    public static class AnyNameState extends NGNameState {
        protected NameClass getMainNameClass() {
            return NameClass.ALL;
        }
    }
    
    /** Parsing state for &lt;nsName&gt; */
    public static class NsNameState extends NGNameState {
        protected NameClass getMainNameClass() {
            return new NamespaceNameClass( getPropagatedNamespace() );
        }
    }
    
}
