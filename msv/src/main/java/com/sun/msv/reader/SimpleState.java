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

package com.sun.msv.reader;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.AttributesImpl;

import com.sun.msv.util.StartTagInfo;

/**
 * base interface of the most of parsing states.
 * 
 * <p>
 * In this level of inheritance, contract is as follows.
 * 
 * <ol>
 *  <li>startElement(<x>) event is received by the parent state.
 *        It usually creates a child state by this event.
 * 
 *  <li>startSelf method of the child SimpleState is called.
 *        derived classes should perform necessary things
 *        by reading start tag information.
 * 
 *  <li>Whenever startElement method is received by
 *        SimpleState object, createChildState method is
 *        called to create a child state.
 *        Derived classes are responsible for providing
 *        appropriate child state objects.
 * 
 *  <li>Child state handles descendants. Usually, it finishes
 *        parsing when it sees endElement.
 * 
 *  <li>When endElement(</x>) event is received by this object,
 *        it calls endSelf method and reverts to the parent state.
 *        Derived classes are responsible for doing anything
 *        necessary within endSelf method.
 * </ol>
 * 
 * In other words, this state is only active for one hierarchy of XML elements
 * and derived classes are responsible for three abstract methods.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class SimpleState extends State
{
    /** checks if this element belongs to the grammar. */
    protected boolean isGrammarElement( StartTagInfo tag ) {
        return reader.isGrammarElement(tag);
    }
    
    public void startElement( String namespaceURI, String localName, String qName, Attributes atts ) {
        final StartTagInfo tag = new StartTagInfo(
            namespaceURI,localName,qName,new AttributesImpl(atts));
        // we have to copy Attributes, otherwise it will be mutated by SAX parser
            
        if( isGrammarElement(tag) ) {
            // this is a grammar element.
            // creates appropriate child state for it.
            
            State nextState = createChildState(tag);
            if(nextState!=null) {
                reader.pushState(nextState,this,tag);
                return;
            }
            
            // unacceptable element
            reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName );
            // try to recover from error by just ignoring it.
        } else {
            // usually, foreign elements are silently ignored.
            // However, for the document element, we have to report an error
            if( parentState==null ) {
                reader.reportError(GrammarReader.ERR_MALPLACED_ELEMENT, tag.qName );
                // probably user is using a wrong namespace.
                reader.reportError(GrammarReader.WRN_MAYBE_WRONG_NAMESPACE, tag.namespaceURI );
            }
        }
        
        // element of a foreign namespace. skip subtree
        reader.pushState(new IgnoreState(),this,tag);
    }
    
    /** creates appropriate child state object for this element */
    abstract protected State createChildState( StartTagInfo tag );
    
        
    public final void endElement( String namespaceURI, String localName, String qName ) {
        // while processing endSelf, error should be reported for its start tag.
        Locator prevLoc = reader.getLocator();
        try {
            reader.setLocator(this.location);
            endSelf();
        } finally {
            reader.setLocator(prevLoc);
        }
        
        reader.popState();
    }
    
    public final void endDocument() {
        // top-level state receives endDocument event instead of endElement event.
        endSelf();
        reader.popState();
    }


    /**
     * this method is called in endElement method
     * when the state is about to be removed.
     * 
     * derived-class should perform any wrap-up job 
     */
    protected void endSelf() {}
    
}
