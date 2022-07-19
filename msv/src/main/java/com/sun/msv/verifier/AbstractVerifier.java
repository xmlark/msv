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

package com.sun.msv.verifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;

import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.verifier.regexp.StringToken;

/**
 * Base implementation for various Verifier implementations.
 * 
 * This implementation provides common service like:
 * 
 * <ol>
 *  <li>collecting ID/IDREFs.
 *  <li>storing Locator.
 * 
 * <p>
 *    By setting <code>performIDcheck</code> variable, the ID/IDREF checking
 *  can be either turned on or turned off.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class AbstractVerifier implements
    ContentHandler, DTDHandler, IDContextProvider2 {
    
    /**
     * Document Locator that is given by XML reader.
     * Sometimes it doesn't set the locator, so start with a dummy instance.
     */
    protected Locator locator = dummyLocator;
    public final Locator getLocator() { return locator; }
    
    protected static final Locator dummyLocator = new LocatorImpl();
    
    /**
     * set this flag to true to perform ID/IDREF validation.
     * this value cannot be modified in the middle of the validation.
     */
    protected boolean performIDcheck = true;
    
    /** this map remembers every ID token encountered in this document */
    protected final Map ids = new HashMap();
    /** this map remembers every IDREF token encountered in this document */
    protected final Set idrefs = new java.util.HashSet();
    
    public void setDocumentLocator( Locator loc ) {
        this.locator = loc;
    }
    public void skippedEntity(String p) {}
    public void processingInstruction(String name,String data) {}
    
    private boolean contextPushed = false;
    public void startPrefixMapping( String prefix, String uri ) {
        if( !contextPushed ) {
            namespaceSupport.pushContext();
            contextPushed = true;
        }
        namespaceSupport.declarePrefix( prefix, uri );
    }
    public void endPrefixMapping( String prefix )    {}
    
    public void startElement( String namespaceUri, String localName, String qName, Attributes atts ) throws SAXException {
        if( !contextPushed )
            namespaceSupport.pushContext();
        contextPushed = false;
    }
    
    public void endElement( String namespaceUri, String localName, String qName ) throws SAXException {
        namespaceSupport.popContext();
    }
    
    protected void init() {
        ids.clear();
        idrefs.clear();
    }
    
    public void notationDecl( String name, String publicId, String systemId ) {
        notations.add(name);
    }
    public void unparsedEntityDecl( String name, String publicId, String systemId, String notationName ) {
        // store name of unparsed entities to implement ValidationContextProvider
        unparsedEntities.add(name);
    }
                                    
    
    /**
     * namespace prefix to namespace URI resolver.
     * 
     * this object memorizes mapping information.
     */
    protected final NamespaceSupport namespaceSupport = new NamespaceSupport();

    /** unparsed entities found in the document. */
    private final Set unparsedEntities = new java.util.HashSet();
    
    /** declared notations. */
    private final Set notations = new java.util.HashSet();
    
    // methods of ValidationContextProvider
    public String resolveNamespacePrefix(String prefix) {
        String uri = namespaceSupport.getURI(prefix);
        if(uri==null && prefix.length()==0)  return "";
        else                                return uri;
    }
    public boolean isUnparsedEntity(String entityName) {
        return unparsedEntities.contains(entityName);
    }
    public boolean isNotation(String notationName) {
        return notations.contains(notationName);
    }
    public String getBaseUri() {
        // TODO: Verifier should implement the base URI
        return null;
    }
    
    /** this method is called when a duplicate id value is found. */
    protected abstract void onDuplicateId( String id );
    
    public void onID(Datatype dt, StringToken token) {
        if (!performIDcheck)
            return;

        int idType = dt.getIdType();
        if (idType == Datatype.ID_TYPE_ID) {
            String literal = token.literal.trim();
            StringToken existing = (StringToken)ids.get(literal);
            if( existing==null ) {
                // the first tiem this ID is used
                ids.put(literal,token);
            } else
            if( existing!=token ) {
                // duplicate id value
                onDuplicateId(literal);
            }
            return;
        }
        if (idType == Datatype.ID_TYPE_IDREF) {
            idrefs.add(token.literal.trim());
            return;
        }
        if (idType == Datatype.ID_TYPE_IDREFS) {
            StringTokenizer tokens = new StringTokenizer(token.literal);
            while (tokens.hasMoreTokens())
                idrefs.add(tokens.nextToken());
            return;
        }

        throw new Error(); // assertion failed. unknown Id type.
    }
}
