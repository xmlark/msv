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

package com.sun.msv.relaxns.verifier;

import org.iso_relax.dispatcher.Dispatcher;
import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.msv.relaxns.grammar.relax.AnyOtherElementExp;

/**
 * IslandVerifier that validates &lt;anyOtherElement /&gt; of RELAX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyOtherElementVerifier
    extends DefaultHandler
    implements IslandVerifier {
    
    /** this Verifier validates these expressions.
     * 
     * during validation, failed expression is removed.
     */
    private final AnyOtherElementExp[] exps;
    
    public AnyOtherElementVerifier( AnyOtherElementExp[] exps ) {
        this.exps = exps;
    }
    
    protected Dispatcher dispatcher;
    
    public void setDispatcher( Dispatcher disp ) {
        this.dispatcher = disp;
    }
    
    public void startElement( String namespaceURI,
        String localName, String qName, Attributes atts )
        throws SAXException {

        IslandSchema is = dispatcher.getSchemaProvider().getSchemaByNamespace(namespaceURI);
        if( is!=null ) {
            // find an island that has to be validated.
            // switch to the new IslandVerifier.
            IslandVerifier iv = is.createNewVerifier( namespaceURI, is.getElementDecls() );
            dispatcher.switchVerifier(iv);
            iv.startElement(namespaceURI,localName,qName,atts);
            return;
        }
        
        boolean atLeastOneIsValid = false;
        
        for( int i=0; i<exps.length; i++ )
            if( exps[i]!=null ) {
                if( exps[i].getNameClass().accepts( namespaceURI, localName ) )
                    atLeastOneIsValid = true;
                else
                    exps[i] = null;    // this one is no longer valid.
            }

        if(!atLeastOneIsValid)
            // none is valid. report an error.
            dispatcher.getErrorHandler().error(
                new SAXParseException(
                    Localizer.localize( ERR_UNEXPECTED_NAMESPACE, new Object[]{namespaceURI} ),
                    locator ) );
        
    }
    
    public void endChildIsland( String namespaceURI, ElementDecl[] rules ) {
        // error report should have done by child verifier, if any.
        // so just do nothing.
    }
    
    public ElementDecl[] endIsland() {
        // collect satisfied AnyOtherElements and return it as Rules
        int i,j;
        int len=0;
        for( i=0; i<exps.length; i++ )
            if( exps[i]!=null )        len++;
        
        ElementDecl[] r = new ElementDecl[len];
        for( i=0,j=0; i<exps.length; i++ )
            if( exps[i]!=null )    r[j++]=exps[i];
        
        return r;
    }
    
    protected Locator locator;
    
    public void setDocumentLocator( Locator loc ) {
        this.locator = loc;
    }
    
    
    public static final String ERR_UNEXPECTED_NAMESPACE = 
        "AnyOtherElementVerifier.UnexpectedNamespace";
}
