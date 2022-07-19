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
