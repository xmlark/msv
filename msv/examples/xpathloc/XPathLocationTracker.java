/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package xpathloc;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Monitors the SAX events and compute the XPath expression
 * that points to the current location.
 * 
 * <p>
 * The XPath computed by this tool is of the form:
 * /foo/bar[3]/zot[2] ...
 * 
 * <p>
 * This can be used for example to point to the location of the error.
 * To do this, set up SAX pipeline as follows:
 * 
 * <pre>
 * EventSource -> XPathLocationTracker -> Verifier -> ...
 * </pre>
 * 
 * <p>
 * Then when you receive an error from a verifier, query this component
 * about the XPath location of the error.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XPathLocationTracker extends XMLFilterImpl {
    
    public XPathLocationTracker( XMLReader r ) {
        super(r);
    }
    
    public XPathLocationTracker( ContentHandler handler ) {
        setContentHandler(handler);
    }
    
    /**
     * Captures the occurrences of elements among siblings.
     */
    private static final class State {
        /**
         * Counts the occurence of element names.
         * Counting is done by using rawName.
         */
        private final Map counters = new HashMap();
        
        /**
         * Parent state, or null if this state governs the document element.
         */
        private final State parent;
        
        /**
         * Child state, or null if it's not yet created.
         * 
         * <p>
         * To reuse {@link State} objects, we only create one child state.
         */
        private State child;
        
        /**
         * Name of the current element which we are parsing right now.
         */
        private String currentName;
        
        State( State parent ) {
            this.parent = parent;
        }
        
        /**
         * Accounts a new cihld element and then
         * returns a new child state.
         */
        protected State push( String rawName ) {
            count(rawName);
            currentName = rawName;
            if(child==null)
                child = new State(this);
            else
                child.reset();
            return child;
        }
        
        /**
         * Goes back to the parent state.
         */
        protected State pop() {
            parent.currentName = null;
            return parent;
        }
        
        private void count( String rawName ) {
            Integer i = (Integer)counters.get(rawName);
            if(i==null)
                i = getInt(1);
            else
                i = getInt(i.intValue()+1);
            counters.put(rawName,i);
        }
        
        void reset() {
            counters.clear();
            currentName = null;
        }
        
        String getXPath() {
            String r;
            if(parent==null) {
                // root state
                r = "/";
                if(currentName!=null)
                    r += currentName;
            } else {
                // child state
                r = parent.getXPath();
                if(currentName!=null) {
                    r += '/' + currentName;
                    Integer i = (Integer)counters.get(currentName);
                    r += '[' + i.toString() + ']';
                }
            }
            return r;
        }
    }
    
    /**
     * Current state.
     */
    private State state;
    
    public void startDocument() throws SAXException {
        state = new State(null);
        super.startDocument();
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        state = null;
    }
    
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        state = state.push(qName);
        super.startElement(uri, localName, qName, atts);
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        state = state.pop();
    }
    
    /**
     * Gets the XPath expression that points to the current location.
     * 
     * @throws IllegalStateException
     *      If the component is not parsing a document.
     */
    public final String getXPath() {
        if(state==null)
            throw new IllegalStateException("startDocument event is not invoked");
        return state.getXPath();
    }
    
    
    /**
     * Effectively the same as <pre>Integer.valueOf(i)</pre>
     */
    private static Integer getInt(int i) {
        if(i<ints.length)
            return ints[i];
        else
            return Integer.valueOf(i);
    }
    
    private static final Integer[] ints = new Integer[] {
        Integer.valueOf(0),
        Integer.valueOf(1),
        Integer.valueOf(2),
        Integer.valueOf(3),
        Integer.valueOf(4)
    };
}
