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
