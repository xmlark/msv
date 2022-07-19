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

package com.sun.msv.reader.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses name class that has child name classes
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassWithChildState extends NameClassState implements NameClassOwner
{
    /**
     * name class object that is being created.
     * See {@link #castNameClass(NameClass, NameClass)} and {@link #annealNameClass(NameClass)} methods
     * for how a pattern will be created.
     */
    protected NameClass nameClass = null;

    /**
     * if this flag is true, then it is OK not to have any children.
     */
    protected boolean allowNullChild = false;
    
    /**
     * receives a Pattern object that is contained in this element.
     */
    public final void onEndChild( NameClass childNameClass ) {
        nameClass = castNameClass( nameClass, childNameClass );
    }
    
    protected final NameClass makeNameClass() {
        if( nameClass==null && !allowNullChild ) {
            reader.reportError( TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS );
            nameClass = NameClass.ALL;
            // recover by assuming some name class.
        }
        return annealNameClass(nameClass);
    }
    
    protected State createChildState( StartTagInfo tag ) {
        return ((TREXBaseReader)reader).createNameClassChildState(this,tag);
    }

        
    /**
     * combines half-made name class and newly found child name class into the name class.
     * 
     * <p>
     * Say this container has three child name class n1,n2, and n3.
     * Then, the name class of this container will be made by the following method
     * invocations.
     * 
     * <pre>
     *   annealNameClass( castNameClass( castNameClass( castNameClass(null,p1), p2), p3 ) )
     * </pre>
     */
    protected abstract NameClass castNameClass(
        NameClass halfCastedNameClass, NameClass newChildNameClass );
    
    /**
     * performs final wrap-up and returns a fully created NameClass object
     * that represents this element.
     */
    protected NameClass annealNameClass( NameClass nameClass ) {
        // default implementation does nothing.
        return nameClass;
    }
}
