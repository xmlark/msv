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

import java.util.Set;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.reader.ChildlessState;
import com.sun.msv.reader.GrammarReader;

/**
 * state that reads facets.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FacetState extends ChildlessState
{
    /** set of recognizable facet names */
    public static final Set facetNames = initFacetNames();
    
    static private Set initFacetNames()
    {
        Set s = new java.util.HashSet();
        s.add("length");
        s.add("minLength");
        s.add("maxLength");
        s.add("pattern");
        s.add("enumeration");
        s.add("maxInclusive");
        s.add("minInclusive");
        s.add("maxExclusive");
        s.add("minExclusive");
        s.add("whiteSpace");
        s.add("fractionDigits");
        s.add("totalDigits");
        return s;
    }
    
    protected void startSelf()
    {
        super.startSelf();
        final String value = startTag.getAttribute("value");
        
        if( value==null )
        {
            reader.reportError( GrammarReader.ERR_MISSING_ATTRIBUTE, startTag.localName, "value" );
            // recover by ignoring this facet.
        } else {
            try {
                ((FacetStateParent)parentState).getIncubator().addFacet(
                    startTag.localName, value, "true".equals(startTag.getAttribute("fixed")), reader );
            } catch( DatatypeException e ) {
                reader.reportError( e, GrammarReader.ERR_BAD_TYPE, e.getMessage() );
                // recover by ignoring this facet
            }
        }
    }
}
