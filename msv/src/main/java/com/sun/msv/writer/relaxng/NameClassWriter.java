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

package com.sun.msv.writer.relaxng;

import java.util.Stack;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.writer.XMLWriter;

/**
 * Visits NameClass and writes its XML representation.
 * 
 * this class can only handle canonicalized name class.
 */
public class NameClassWriter implements NameClassVisitor {
    
    public NameClassWriter( Context ctxt ) {
        this.writer = ctxt.getWriter();
        this.defaultNs = ctxt.getTargetNamespace();
    }
    private final XMLWriter writer;
    
    /**
     * Namespace URI of the inherited "ns" attribute, if any.
     * Otherwise null.
     */
    private final String defaultNs;
    
    public Object onAnyName(AnyNameClass nc) {
        writer.element("anyName");
        return null;
    }
        
    protected void startWithNs( String name, String ns ) {
        if( ns.equals(defaultNs) )
            writer.start(name);
        else
            writer.start(name, new String[]{"ns",ns});
    }
        
    public Object onSimple( SimpleNameClass nc ) {
        startWithNs( "name", nc.namespaceURI );
        writer.characters(nc.localName);
        writer.end("name");
        return null;
    }
        
    public Object onNsName( NamespaceNameClass nc ) {
        startWithNs( "nsName", nc.namespaceURI );
        writer.end("nsName");
        return null;
    }
        
    public Object onNot( NotNameClass nc ) {
        // should not be called.
        throw new Error();
    }
        
    public Object onChoice( ChoiceNameClass nc ) {
        writer.start("choice");
        processChoice(nc);
        writer.end("choice");
        return null;
    }
            
    private void processChoice( ChoiceNameClass nc ) {
        Stack s = new Stack();
        s.push(nc.nc1);
        s.push(nc.nc2);
            
        while(!s.empty()) {
            NameClass n = (NameClass)s.pop();
            if(n instanceof ChoiceNameClass ) {
                s.push( ((ChoiceNameClass)n).nc1 );
                s.push( ((ChoiceNameClass)n).nc2 );
                continue;
            }
                
            n.visit(this);
        }
    }
        
    public Object onDifference( DifferenceNameClass nc ) {
        if( nc.nc1 instanceof AnyNameClass ) {
            writer.start("anyName");
            writer.start("except");
            if( nc.nc2 instanceof ChoiceNameClass )
                processChoice( (ChoiceNameClass)nc.nc2 );
            else
                nc.nc2.visit(this);
            writer.end("except");
            writer.end("anyName");
        }
        else
        if( nc.nc1 instanceof NamespaceNameClass ) {
            startWithNs("nsName", ((NamespaceNameClass)nc.nc1).namespaceURI );
            writer.start("except");
            if( nc.nc2 instanceof ChoiceNameClass )
                processChoice( (ChoiceNameClass)nc.nc2 );
            else
                nc.nc2.visit(this);
            writer.end("except");
            writer.end("nsName");
        }
        else
            throw new Error();
            
        return null;
    }
}
