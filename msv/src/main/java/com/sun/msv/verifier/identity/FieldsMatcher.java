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

package com.sun.msv.verifier.identity;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import com.sun.msv.grammar.xmlschema.KeyConstraint;
import com.sun.msv.grammar.xmlschema.KeyRefConstraint;

/**
 * Coordinator of FieldMatcher.
 * 
 * This object is created when SelectorMatcher finds a match.
 * This object then creates FieldMatcher for each field, and
 * let them find their field matchs.
 * When leaving the element that matched the selector, it collects
 * field values and registers a key value to IDConstraintChecker.
 * 
 * <p>
 * Depending on the type of the constraint, it works differently.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldsMatcher extends MatcherBundle {
    
    /**
     * location of the start tag.
     * It is usually preferable as a source of error.
     */
    protected final Locator startTag;
    
    /**
     * the parent SelectorMatcher.
     */
    protected final SelectorMatcher selector;
    
    protected FieldsMatcher( SelectorMatcher selector, String namespaceURI, String localName ) throws SAXException {
        super(selector.owner);
        
        this.selector = selector;
        if(owner.getLocator()==null)
            this.startTag = null;
        else
            this.startTag = new LocatorImpl(owner.getLocator());
        
        children = new Matcher[selector.idConst.fields.length];
        for( int i=0; i<selector.idConst.fields.length; i++ )
            children[i] = new FieldMatcher(
                this,selector.idConst.fields[i], namespaceURI,localName);
    }
    
    protected void onRemoved() throws SAXException {
        Object[] values = new Object[children.length];
            
        // copy matched values into "values" variable,
        // while checking any unmatched fields.
        for( int i=0; i<children.length; i++ )
            if( (values[i]=((FieldMatcher)children[i]).value) == null ) {
                if(!(selector.idConst instanceof KeyConstraint))
                    // some fields didn't match to anything.
                    // In case of KeyRef and Unique constraints,
                    // we can ignore this node.
                    return;
                    
                // if this is the key constraint, it is an error
                owner.reportError(
                    startTag, null, 
                    IDConstraintChecker.ERR_UNMATCHED_KEY_FIELD,
                    new Object[]{
                        selector.idConst.namespaceURI,
                        selector.idConst.localName,
                        new Integer(i+1)} );
                return;
            }

        if( com.sun.msv.driver.textui.Debug.debug )
            System.out.println("fields collected for "+selector.idConst.localName);
        
        KeyValue kv = new KeyValue(values,startTag);
        if(owner.addKeyValue( selector, kv ))
            return;
        
        // the same value already exists.
        
        if( selector.idConst instanceof KeyRefConstraint )
            // multiple reference to the same key value.
            // not a problem.
            return;
        
        // find a value that collides with kv
        Object[] items = owner.getKeyValues(selector);
        int i;
        for( i=0; i<values.length; i++ )
            if( items[i].equals(kv) )
                break;
        
        // violates uniqueness constraint.
        // this set already has this value.
        owner.reportError(
            startTag, null,
            IDConstraintChecker.ERR_NOT_UNIQUE,
            new Object[]{
                selector.idConst.namespaceURI, selector.idConst.localName} );
        owner.reportError(
            ((KeyValue)items[i]).locator, null,
            IDConstraintChecker.ERR_NOT_UNIQUE_DIAG,
            new Object[]{
                selector.idConst.namespaceURI, selector.idConst.localName} );
    }
    
}
