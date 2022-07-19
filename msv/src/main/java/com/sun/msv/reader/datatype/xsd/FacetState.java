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
