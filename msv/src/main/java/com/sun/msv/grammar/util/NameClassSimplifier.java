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

package com.sun.msv.grammar.util;

import java.util.Iterator;
import java.util.Set;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.util.StringPair;

/**
 * Minimizes a name class.
 * 
 * Sometimes, a name class could become unnecessarily big. For example,
 * 
 * <PRE><XMP>
 * <choice>
 *   <anyName/>
 *   <anyName/>
 *   <anyName/>
 * </choice>
 * </XMP></PRE>
 * 
 * This procedure converts those name classes to the equivalent small name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NameClassSimplifier {
    
    public static NameClass simplify( NameClass nc ) {
        final Set possibleNames = PossibleNamesCollector.calc(nc);
        final String MAGIC = PossibleNamesCollector.MAGIC;
        
        Set uris = new java.util.HashSet();
        
        Iterator itr = possibleNames.iterator();
        while( itr.hasNext() ) {
            StringPair name = (StringPair)itr.next();
            if( name.localName!=MAGIC ) {
                // a simple name.
                if( nc.accepts(name)==nc.accepts( name.namespaceURI, MAGIC ) ) {
                    itr.remove();
                    continue;
                }
            } else
            if( name.namespaceURI!=MAGIC ) {
                // a ns name
                if( nc.accepts(name)==nc.accepts(MAGIC,MAGIC) ) {
                    itr.remove();
                    continue;
                }
            }
            
            // collect the remainig namespace URIs.
            if( name.namespaceURI!=MAGIC )
                uris.add(name.namespaceURI);
        }
        
        if( !nc.accepts(MAGIC,MAGIC) )
            possibleNames.remove( new StringPair(MAGIC,MAGIC) );
        
        NameClass result = null;
        Iterator jtr = uris.iterator();
        while( jtr.hasNext() ) {
            final String uri = (String)jtr.next();
            
            NameClass local = null;
            itr = possibleNames.iterator();
            while( itr.hasNext() ) {
                final StringPair name = (StringPair)itr.next();
                
                if(!name.namespaceURI.equals(uri))        continue;
                if(name.localName==MAGIC)                continue;
                
                if(local==null)    local = new SimpleNameClass(name);
                else            local = new ChoiceNameClass(local,new SimpleNameClass(name));
            }
            if(possibleNames.contains(new StringPair(uri,MAGIC))) {
                if(local==null)
                    local = new NamespaceNameClass(uri);
                else
                    local = new DifferenceNameClass(new NamespaceNameClass(uri),local);
            }
            
            if(local!=null) {
                if(result==null)    result = local;
                else                result = new ChoiceNameClass(result,local);
            }
        }
        
        if( nc.accepts(MAGIC,MAGIC) ) {
            if(result==null)        result = NameClass.ALL;
            else                    result = new NotNameClass(result);
        }
        
        if( result==null )
            result = AnyNameClass.NONE;
        
        return result;
    }
}
