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

import java.util.StringTokenizer;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses &lt;simpleType&gt; element and its children.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeState extends TypeWithOneChildState
{
    protected State createChildState( StartTagInfo tag ) {
        
        // accepts elements from the same namespace only.
        if( !startTag.namespaceURI.equals(tag.namespaceURI) )    return null;
        
        final String name = startTag.getAttribute("name");
        String uri = getTargetNamespaceUri();
        
        if( tag.localName.equals("annotation") )    return new IgnoreState();
        if( tag.localName.equals("restriction") )    return new RestrictionState(uri,name);
        if( tag.localName.equals("list") )            return new ListState(uri,name);
        if( tag.localName.equals("union") )        return new UnionState(uri,name);
        
        return null;    // unrecognized
    }

    protected XSDatatypeExp annealType( final XSDatatypeExp dt ) {
        final String finalValueStr = startTag.getAttribute("final");
        if(finalValueStr!=null) {
            final int finalValue = getFinalValue(finalValueStr);
            
            // create a new type by adding final constraint.
            return dt.createFinalizedType(finalValue,reader);
        } else
            return dt;
    }


    /** parses final attribute */
    public int getFinalValue( String list ) {
        int finalValue = 0;
        StringTokenizer tokens = new StringTokenizer(list);
        while(tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            
            if( token.equals("#all") )
                finalValue |=    XSDatatype.DERIVATION_BY_LIST|
                                XSDatatype.DERIVATION_BY_RESTRICTION|
                                XSDatatype.DERIVATION_BY_UNION;
            else
            if( token.equals("restriction") )
                finalValue |= XSDatatype.DERIVATION_BY_RESTRICTION;
            else
            if( token.equals("list") )
                finalValue |= XSDatatype.DERIVATION_BY_LIST;
            else
            if( token.equals("union") )
                finalValue |= XSDatatype.DERIVATION_BY_UNION;
            else {
                reader.reportError( 
                    GrammarReader.ERR_ILLEGAL_FINAL_VALUE, token );
                return 0;    // abort
            }
        }
        return finalValue;
    }


}
