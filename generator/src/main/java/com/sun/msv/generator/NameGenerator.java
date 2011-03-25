/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
