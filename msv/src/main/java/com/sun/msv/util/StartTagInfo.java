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

package com.sun.msv.util;

import org.xml.sax.Attributes;

import com.sun.msv.datatype.xsd.WhiteSpaceProcessor;
import com.sun.msv.grammar.IDContextProvider;
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.grammar.util.IDContextProviderWrapper;

/**
 * immutable start tag information
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StartTagInfo {
    
    public String        namespaceURI;
    public String        localName;
    public String        qName;
    public Attributes    attributes;
    /**
     * object that provides additional information which is necessary
     * for validating some datatypes
     */
    public IDContextProvider2 context;
    
    protected StartTagInfo() {}
    
    /** @deprecated */
    public StartTagInfo(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider context ) {
        reinit(namespaceURI,localName,qName,attributes,context);
    }
    
    public StartTagInfo(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider2 context ) {
        reinit(namespaceURI,localName,qName,attributes,context);
    }

    public StartTagInfo(
        String namespaceURI, String localName, String qName, Attributes attributes ) {
        reinit(namespaceURI,localName,qName,attributes,(IDContextProvider2)null);
    }

    /** @deprecated */
    public void reinit(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider context ) {
            
        reinit( namespaceURI, localName, qName, attributes, IDContextProviderWrapper.create(context) );
    }
    
    /** re-initialize the object with brand new parameters. */
    public void reinit(
        String namespaceURI, String localName, String qName,
        Attributes attributes, IDContextProvider2 context ) {
        this.namespaceURI    = namespaceURI;
        this.localName        = localName;
        this.qName            = qName;
        this.attributes        = attributes;
        this.context        = context;
    }
    
    public final boolean containsAttribute( String attrName ) {
        return containsAttribute("",attrName);
    }
    
    public final boolean containsAttribute( String namespaceURI, String attrName ) {
        return attributes.getIndex(namespaceURI,attrName)!=-1;
    }
    
    /**
     * gets value of the specified attribute.
     * 
     * @return null        attribute does not exist.
     */
    public final String getAttribute( String attrName ) {
        return getAttribute("",attrName);
    }
    
    public final String getAttribute( String namespaceURI, String attrName ) {
        return attributes.getValue(namespaceURI,attrName);
    }
    
    public final String getCollapsedAttribute( String attrName ) {
        String s = getAttribute(attrName);
        if(s==null)        return null;
        return WhiteSpaceProcessor.collapse(s);
    }
    
    public final String getDefaultedAttribute( String attrName, String defaultValue ) {
        return getDefaultedAttribute("",attrName,defaultValue);
    }
    
    public final String getDefaultedAttribute( String namespaceURI, String attrName, String defaultValue ) {
        String v = getAttribute(namespaceURI,attrName);
        if(v!=null)        return v;
        else            return defaultValue;
    }
}
