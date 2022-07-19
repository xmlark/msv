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

import java.util.StringTokenizer;

import org.relaxng.datatype.Datatype;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.ErrorInfo;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.ComplexAcceptor;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.regexp.SimpleAcceptor;

/**
 * assign types to the incoming SAX2 events and reports them to
 * the application handler through TypedContentHandler.
 * 
 * This class "augment" infoset by adding type information. The application can
 * receive augmented infoset by implementing TypedContentHandler.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypeDetector extends Verifier {
    
    
    /** characters that were read (but not processed)  */
    private StringBuffer text = new StringBuffer();
    
    protected TypedContentHandler handler;
    
    public TypeDetector( DocumentDeclaration documentDecl, ErrorHandler errorHandler ) {
        super(documentDecl,errorHandler);
    }
    
    public TypeDetector( DocumentDeclaration documentDecl, TypedContentHandler handler, ErrorHandler errorHandler ) {
        this(documentDecl,errorHandler);
        setContentHandler(handler);
    }
    
    /**
     * sets the TypedContentHandler which will received the type-augmented
     * infoset.
     */
    public void setContentHandler( TypedContentHandler handler ) {
        this.handler = handler;
    }

    private final DatatypeRef characterType = new DatatypeRef();
    
    protected void verifyText() throws SAXException {
        if(text.length()!=0) {
            final String txt = new String(text);
            if(!current.onText2( txt, this, null, characterType )) {
                // error
                // diagnose error, if possible
                StringRef err = new StringRef();
                current.onText2( txt, this, err, null );
                    
                // report an error
                errorHandler.error( new ValidityViolation(locator,
                    localizeMessage( ERR_UNEXPECTED_TEXT, null ),
                    new ErrorInfo.BadText(txt) ) );
            }
            
            // characters are validated. report to the handler.
            reportCharacterChunks( txt, characterType.types );
            
            text = new StringBuffer();
        }
    }

    private void reportCharacterChunks( String text, Datatype[] types ) throws SAXException {
        
        if( types==null )
            // unable to assign type.
            throw new AmbiguousDocumentException();
        
        switch( types.length ) {
        case 0:
            return;    // this text is ignored.
        case 1:
            handler.characterChunk( text, types[0] );
            return;
        default:
            StringTokenizer tokens = new StringTokenizer(text);
            for( int i=0; i<types.length; i++ )
                handler.characterChunk( tokens.nextToken(), types[i] );
                
            if( tokens.hasMoreTokens() )    throw new Error();    // assertion failed
        }
    }
    
    
    protected Datatype[] feedAttribute( Acceptor child, String uri, String localName, String qName, String value ) throws SAXException {
        
        // thanks to Damian Gajda <zwierzem@ngo.pl> for the patch.
        // the startAttribute method should be called before the feedAttribute.
        // 
        // this makes the error report consistent with the startAttribute event.
        handler.startAttribute( uri, localName, qName );    
        
        Datatype[] result = super.feedAttribute(child,uri,localName,qName,value);
        
        reportCharacterChunks( value, result );    
        handler.endAttribute( uri, localName, qName,
            ((REDocumentDeclaration)docDecl).attToken.matchedExp );
        
        return result;
    }
    
    public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
        throws SAXException {
        
        super.startElement( namespaceUri, localName, qName, atts );
        
        handler.endAttributePart();
    }

    protected void onNextAcceptorReady( StartTagInfo sti, Acceptor nextAcceptor ) throws SAXException {
        /*
            You cannot call handler.startElement before super.startElement invocation
            because unconsumed text maybe processed here.
        */
        handler.startElement( sti.namespaceURI, sti.localName, sti.qName );
    }
    
    public void endElement( String namespaceUri, String localName, String qName )
        throws SAXException {
        
        Acceptor child = current;
        
        super.endElement(namespaceUri,localName,qName);
        
        {// report to the handler
            ElementExp type;
            if( child instanceof SimpleAcceptor ) {
                type = ((SimpleAcceptor)child).owner;
            } else
            if( child instanceof ComplexAcceptor ) {
                ElementExp[] exps = ((ComplexAcceptor)child).getSatisfiedOwners();
                if(exps.length!=1)
                    throw new AmbiguousDocumentException();
                type = exps[0];
            } else
                throw new Error();    // assertion failed. not supported.
            
            handler.endElement( namespaceUri, localName, qName, type );
        }
    }
    
    public void characters( char[] buf, int start, int len ) throws SAXException {
        text.append(buf,start,len);
    }
    public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
        text.append(buf,start,len);
    }
    
    public void startDocument() throws SAXException {
        super.startDocument();
        handler.startDocument(this);
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        handler.endDocument();
    }
    
    /**
     * signals that the document is ambiguous.
     * This exception is thrown when
     * <ol>
     *  <li>we cannot uniquely assign the type for given characters.
     *  <li>or we cannot uniquely determine the type for the element
     *        when we reached the end element.
     * </ol>
     * 
     * The formar case happens for patterns like:
     * <PRE><XMP>
     * <choice>
     *   <data type="xsd:string"/>
     *   <data type="xsd:token"/>
     * </choice>
     * </XMP></PRE>
     * 
     * The latter case happens for patterns like:
     * <PRE><XMP>
     * <choice>
     *   <element name="foo">
     *     <text/>
     *   </element>
     *   <element>
     *     <anyName/>
     *     <text/>
     *   </element>
     * </choice>
     * </XMP></PRE>
     */
    public class AmbiguousDocumentException extends SAXException {
        public AmbiguousDocumentException() {
            super("");
        }
        /** returns the source of the error. */
        Locator getLocation() { return TypeDetector.this.getLocator(); }
    };
}
