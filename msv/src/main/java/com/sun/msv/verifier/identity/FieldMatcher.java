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

package com.sun.msv.verifier.identity;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.xmlschema.Field;

/**
 * XPath matcher that tests one field of a key.
 * 
 * This object is created by a FieldsMatcher when a SelectorMathcer
 * finds a match to its selector. This object is responsible for finding
 * a match to one field of the constraint.
 * 
 * A field XPath may consist of "A|B|C". Each sub case A,B, and C is
 * tested by a child FieldPathMatcher object. This class coordinates
 * the work of those children and collects actual text that matches
 * the given XPath.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FieldMatcher extends PathMatcher {
    
    protected Field field;
    
    /**
     * the matched value. If this field is null, then it means
     * nothing is matched yet.
     */
    protected Object    value;
    
    /** parent FieldsMatcher object. */
    protected final FieldsMatcher parent;
    
    /**
     * this field is set to non-null if it's found that an element
     * is matched to this XPath. This field is then used to collect
     * the contents of the matched element until it encounters
     * the endElement method.
     */
    protected StringBuffer elementText = null;
    
    FieldMatcher( FieldsMatcher parent, Field field, String namespaceURI, String localName ) throws SAXException {
        super( parent.owner, field.paths );
        this.parent = parent;
        
        // test the initial match
        super.start(namespaceURI,localName);
    }


    /**
     * this method is called when the element matches the XPath.
     */
    protected void onElementMatched( String namespaceURI, String localName ) throws SAXException {
        if( com.sun.msv.driver.textui.Debug.debug )
            System.out.println("field match for "+ parent.selector.idConst.localName );
        
        // this field matches this element.
        // wait for the corresponding endElement call and
        // obtain text.
        elementText = new StringBuffer();
    }

    /**
     * this method is called when the attribute matches the XPath.
     */
    protected void onAttributeMatched(
        String namespaceURI, String localName, String value, Datatype type ) throws SAXException {
        
        if( com.sun.msv.driver.textui.Debug.debug )
            System.out.println("field match for "+ parent.selector.idConst.localName );
        
        setValue( value, type );
    }
    
    protected void startElement( String namespaceURI, String localName, Attributes attributes ) 
                                throws SAXException {
        if( elementText!=null ) {
            // this situation is an error because a matched element
            // cannot contain any child element.
            // But what I don't know is how to treat this situation.
            
            // 1. to make the document invalid?
            // 2. cancel the match?
            
            // the current implementation choose the 2nd.
            elementText = null;
        }
        super.startElement(namespaceURI,localName);
    }
    
    protected void endElement( Datatype type ) throws SAXException {
        super.endElement(type);
        // double match error is already checked in the corresponding
        // startElement method.
        if( elementText!=null ) {
            setValue( elementText.toString(), type );
            elementText = null;
        }
    }

    protected void characters( char[] buf, int start, int len ) throws SAXException {
        super.characters(buf,start,len);
        if( elementText!=null )
            // collect text
            elementText.append(buf,start,len);
    }
    
    /** sets the value field. */
    private void setValue( String lexical, Datatype type ) throws SAXException {
        if(value!=null) {
            // not the first match.
            doubleMatchError();
            return;
        }
        
        if(type==null) {
            // this is possible only when we are recovering from errors.
            value = lexical;
            if(com.sun.msv.driver.textui.Debug.debug)
                System.out.println("no type info available");
        } else
            value = type.createValue(lexical,owner);
    }
    
    /** this field matches more than once. */
    private void doubleMatchError() throws SAXException {
        int i;
        // compute the index number of this field.
        for( i=0; i<parent.children.length; i++ )
            if( parent.children[i]==this )
                break;
        
        owner.reportError( null, IDConstraintChecker.ERR_DOUBLE_MATCH,
            new Object[]{
                parent.selector.idConst.namespaceURI,
                parent.selector.idConst.localName,
                new Integer(i+1)} );
    }
}
