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

package com.sun.msv.verifier.psvi;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;

/**
 * Receives notification of the typed content of the document.
 * 
 * <p>
 * This interface can be considered as the SAX ContentHandler plus type-information.
 * It is intended to help applications to interpret the incoming document.
 * 
 * <p>
 * Consider a following RELAX NG pattern and instance:
 * 
 * <PRE><XMP>
 * <element name="root">
 *   <optional>
 *     <attribute name="foo">
 *       <choice>
 *         <data type="boolean"/>
 *         <data type="date"/>
 *       </choice>
 *     </attribute>
 *   </optional>
 *   <element name="child">
 *     <list><zeroOrMore>
 *       <data type="NMTOKEN"/>
 *     </zeroOrMore></list>
 *   </element>
 * </element>
 * 
 * <root foo="true">
 *   <child> A B </child>
 * </root>
 * </XMP></PRE>
 * 
 * Events are reported in the following order:
 * <pre>
 * startDocument()
 *  startElement(root)
 *   startAttribute(foo)
 *    characterChunk("true", com.sun.msv.datatype.xsd.BooleanType)
 *   endAttribute(foo)
 *   endAttributePart()
 *   startElement(child)
 *    characterChunk("A", com.sun.msv.datatype.xsd.NMTOKENType)
 *    characterChunk("B", com.sun.msv.datatype.xsd.NMTOKENType)
 *   endElement(child, MSV's internal object that represents the child element)
 *  endElement(root, MSV's internal object that represents the root element)
 * endDocument()
 * </pre>
 * 
 * @see
 *        TypeDetector
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface TypedContentHandler {
    
    /**
     * receives notification of the start of a document.
     * 
     * @param context
     *        This ValidationContext object is effective through the entire document.
     */
    void startDocument( ValidationContext context ) throws SAXException;
    
    /**
     * receives notification of the end of a document.
     */
    void endDocument() throws SAXException;
    
    /**
     * receives notification of a string.
     * 
     * @param literal
     *        the contents.
     * @param type
     *        assigned type. The validator assigns this type for this literal.
     */
    void characterChunk( String literal, Datatype type ) throws SAXException;
    
    /**
     * receives notification of the start of an element.
     * 
     * If this element has attributes, the start/endAttribute methods are
     * called after this method.
     */
    void startElement( String namespaceURI, String localName, String qName ) throws SAXException;
    
    /**
     * receives notification of the end of an element.
     * 
     * @param type
     *        the type of this element.
     */
    void endElement( String namespaceURI, String localName, String qName, ElementExp type ) throws SAXException;

    /**
     * receives notification of the start of an attribute.
     * 
     * the value of the attribute is reported through the characterChunk method.
     */
    void startAttribute( String namespaceURI, String localName, String qName ) throws SAXException;
    
    /**
     * receives notification of the end of an attribute.
     * 
     * @param type
     *        assigned type.
     */
    void endAttribute( String namespaceURI, String localName, String qName, AttributeExp type ) throws SAXException;

    /**
     * this method is called after the start/endAttribute method are called
     * for all attributes.
     */
    void endAttributePart() throws SAXException;
}
