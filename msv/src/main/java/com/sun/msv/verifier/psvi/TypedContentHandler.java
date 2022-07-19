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
