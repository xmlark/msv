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

package com.sun.msv.generator;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.util.StringPair;

/**
 * generates a namespaceURI/localName pair that satisfies given {@link NameClass}
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class NameGenerator {
	
	private final Random random;
	NameGenerator( Random random ) { this.random = random; }
	
	
	
	public StringPair generate( NameClass nc ) {
		if( nc instanceof SimpleNameClass ) {
			// 90% is SimpleNameClass. So this check makes
			// the computation faster.
			return new StringPair(
				((SimpleNameClass)nc).namespaceURI,
				((SimpleNameClass)nc).localName );
        }
		
		final String MAGIC = ".";
		final Set possibleNames = new java.util.HashSet();
		
		// collect possible names
		nc.visit( new NameClassVisitor(){
			public Object onNsName( NamespaceNameClass nc ) {
				possibleNames.add( new StringPair(nc.namespaceURI, MAGIC) );
				return null;
			}
	
			public Object onSimple( SimpleNameClass nc ) {
				possibleNames.add( new StringPair(nc.namespaceURI, nc.localName) );
				return null;
			}
	
			public Object onAnyName( AnyNameClass nc ){
				possibleNames.add( new StringPair(MAGIC, MAGIC) );
				return null;
			}
	
			public Object onChoice( ChoiceNameClass nc ) {
				nc.nc1.visit(this);
				nc.nc2.visit(this);
				return null;
			}
	
			public Object onNot( NotNameClass nc ) {
				possibleNames.add( new StringPair(MAGIC, MAGIC) );
				nc.child.visit(this);
				return null;
			}
	
			public Object onDifference( DifferenceNameClass nc ) {
				nc.nc1.visit(this);
				nc.nc2.visit(this);
				return null;
			}
		});
		
		// remove failed items
		Iterator itr = possibleNames.iterator();
		while( itr.hasNext() ) {
			StringPair p = (StringPair)itr.next();
			if( !nc.accepts( p.namespaceURI, p.localName ) )
				itr.remove();
		}
		
		if( possibleNames.size()==0 )
			throw new Error("name class that accepts nothing");
		
		// randomly pick one.
		StringPair model = (StringPair)
			possibleNames.toArray()[ random.nextInt(possibleNames.size()) ];
		
		StringPair answer;
		do {
			// expand wild card
			answer = new StringPair(
				model.namespaceURI==MAGIC?getRandomURI():model.namespaceURI,
				model.localName==MAGIC?getRandomName():model.localName );
		}while( !nc.accepts(answer.namespaceURI,answer.localName) );
		
		return answer;
	}
	
	private String getRandomName() {
		int len = random.nextInt(8)+2;
		StringBuffer tagName = new StringBuffer();
		for( int i=0; i<len; i++ )
			tagName.append( (char)('A'+random.nextInt(26)) );
		return tagName.toString();
	}
	
	private String getRandomURI() {
		// TODO: should be better implemented
		return getRandomName();
	}
}
