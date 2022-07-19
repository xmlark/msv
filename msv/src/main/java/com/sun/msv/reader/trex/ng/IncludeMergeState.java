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

import java.util.Set;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;include&gt; element as a child of &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeMergeState extends com.sun.msv.reader.trex.IncludeMergeState
            implements ExpressionOwner {
    
    protected State createChildState( StartTagInfo tag ) {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        if(tag.localName.equals("define"))    return reader.getStateFactory().redefine(this,tag);
        if(tag.localName.equals("start"))    return reader.getStateFactory().redefineStart(this,tag);
        return null;
    }
    
    /** set of ReferenceExps which are redefined by this inclusion. */
    private final Set redefinedPatterns = new java.util.HashSet();
    
    // this class has to implement ExpressionOwner because
    // <define> state requires this interface.
    public void onEndChild( Expression child ) {
        // if child <define> element has an error,
        // then it may not return ReferenceExp.
        if(!(child instanceof ReferenceExp))    return;
        
        redefinedPatterns.add(child);
    }
    
    public void endSelf() {
        final RELAXNGReader reader = (RELAXNGReader)this.reader;
        
        ReferenceExp[] patterns = (ReferenceExp[])redefinedPatterns.toArray(new ReferenceExp[0]);
        RELAXNGReader.RefExpParseInfo[] old = new RELAXNGReader.RefExpParseInfo[patterns.length];
        
        // back-up the current values of RefExpParseInfo,
        // and reset the values.
        for( int i=0; i<patterns.length; i++ ) {
            RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(patterns[i]);
            
            old[i] = new RELAXNGReader.RefExpParseInfo();
            old[i].set( info );
            info.haveHead = false;
            info.combineMethod = null;
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalNotFoundYet;
        }
        
        // process inclusion.
        super.endSelf();
        
        // make sure that originals are found.
        for( int i=0; i<patterns.length; i++ ) {
            RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(patterns[i]);
            
            if( info.redefinition==RELAXNGReader.RefExpParseInfo.originalNotFoundYet )
                // the original definition was not found.
                reader.reportError( RELAXNGReader.ERR_REDEFINING_UNDEFINED, patterns[i].name );

            // then restore the values.
            reader.getRefExpParseInfo(patterns[i]).set( old[i] );
        }
    }
}
