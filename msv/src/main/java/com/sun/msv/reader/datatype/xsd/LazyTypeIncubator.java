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

import java.util.Iterator;
import java.util.List;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.GrammarReader;

/**
 * Lazy XSTypeIncubator
 * 
 * <p>
 * This incubator is used to add facets to lazily created XSDatatypeExp object.
 * Since the actual Datatype object is not available when facets are parsed,
 * this object merely stores all facets when the addFacet method is called.
 * 
 * <p>
 * Once the actual Datatype is provided, this class uses ordinary
 * TypeIncubator and builds a real type object.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class LazyTypeIncubator implements XSTypeIncubator { // package local
    
    public LazyTypeIncubator( XSDatatypeExp base, GrammarReader reader ) {
        this.baseType = base;
        this.reader = reader;
    }
    
    /** base object. */
    private final XSDatatypeExp baseType;
    
    private final GrammarReader reader;
    
    /**
     * applied facets.
     * Order between facets are possibly significant.
     */
    private final List facets = new java.util.LinkedList();
    
    public void addFacet( String name, String strValue, boolean fixed,
                     ValidationContext context ) {
        facets.add( new Facet(name,strValue,fixed,context) );
    }

    public XSDatatypeExp derive( final String nsUri, final String localName ) throws DatatypeException {
        
        // facets might be further added, so remember the size of the facet.
        final int facetSize = facets.size();
        
        if(facetSize==0)    return baseType;
        
        return new XSDatatypeExp(nsUri,localName,reader,new XSDatatypeExp.Renderer(){
            public XSDatatype render( XSDatatypeExp.RenderingContext context )
                    throws DatatypeException {
                
                TypeIncubator ti = new TypeIncubator( baseType.getType(context) );
                
                Iterator itr = facets.iterator();
                for( int i=0; i<facetSize; i++ ) {
                    Facet f = (Facet)itr.next();
                    ti.addFacet( f.name, f.value, f.fixed, f.context );
                }
                return ti.derive(nsUri,localName);
            }
        });
    }
    
    /** store the information about one added facet. */
    private class Facet {
        String name;
        String value;
        boolean fixed;
        ValidationContext context;
        public Facet( String name, String value, boolean fixed, ValidationContext context ) {
            this.name=name; this.value=value; this.fixed=fixed; this.context=context;
        }
    }
}
