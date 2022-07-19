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
