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

package com.sun.msv.relaxns.grammar;

import org.xml.sax.SAXNotRecognizedException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Implementation of ElementDecl interface by MSV grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DeclImpl
    implements    org.iso_relax.dispatcher.ElementDecl,
                org.iso_relax.dispatcher.AttributesDecl,
                java.io.Serializable {
    /** "meat" of this Rule. */
    public final Expression exp;
    
    /** name of this rule */
    protected final String name;
    
    public DeclImpl( ReferenceExp exp ) {
        this( exp.name, exp.exp );
    }
    public DeclImpl( String name, Expression exp ) {
        this.exp=exp;
        this.name=name;
    }
    
    public String getName() { return name; }
    
    public boolean getFeature( String feature ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(feature);
    }
    
    public Object getProperty( String property ) throws SAXNotRecognizedException {
        throw new SAXNotRecognizedException(property);
    }
}
