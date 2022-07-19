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

package com.sun.msv.reader.datatype.xsd;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses &lt;union&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnionState extends TypeState implements XSTypeOwner {

    protected final String newTypeUri;
    protected final String newTypeName;
    
    protected UnionState( String newTypeUri, String newTypeName ) {
        this.newTypeUri  = newTypeUri;
        this.newTypeName = newTypeName;
    }
    
    private final ArrayList memberTypes = new ArrayList();
                                                  
    protected State createChildState( StartTagInfo tag ) {
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("simpleType") )    return new SimpleTypeState();
        
        return null;    // unrecognized
    }
    
    protected void startSelf() {
        super.startSelf();
        
        // if memberTypes attribute is used, load it.
        String memberTypes = startTag.getAttribute("memberTypes");
        if(memberTypes!=null) {
            StringTokenizer tokens = new StringTokenizer(memberTypes);
            while( tokens.hasMoreTokens() )
                onEndChild( ((XSDatatypeResolver)reader)
                    .resolveXSDatatype(tokens.nextToken()) );
        }
    }
    
    public void onEndChild( XSDatatypeExp type ) {
        memberTypes.add(type);
    }
    
    
    protected final XSDatatypeExp makeType() throws DatatypeException {
        return XSDatatypeExp.makeUnion( newTypeUri, newTypeName, memberTypes, reader );
    }

}
