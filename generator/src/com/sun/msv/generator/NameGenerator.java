/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.generator;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.TREXNameClassVisitor;
import com.sun.tranquilo.grammar.trex.DifferenceNameClass;
import com.sun.tranquilo.util.StringPair;
import java.util.Vector;
import java.util.Stack;
import java.util.Random;

/**
 * generates a namespaceURI/localName pair that satisfies given {@link NameClass}
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class NameGenerator implements TREXNameClassVisitor
{
	private final Random random;
	NameGenerator( Random random ) { this.random = random; }
	
	public Object onNsName( NamespaceNameClass nc )
	{
		return new StringPair( nc.namespaceURI, getRandomName() );
	}
	
	public Object onSimple( SimpleNameClass nc )
	{
		return new StringPair( nc.namespaceURI, nc.localName );
	}
	
	public Object onAnyName( AnyNameClass nc )
	{
		return new StringPair( getRandomURI(), getRandomName() );
	}
	
	public Object onChoice( ChoiceNameClass nc )
	{
		// collect all choice items.
		Stack jobs = new Stack();
		Vector candidates = new Vector();
		jobs.push(nc);
		
		while(!jobs.isEmpty())
		{
			nc = (ChoiceNameClass)jobs.pop();
			if( nc.nc1 instanceof ChoiceNameClass )	jobs.push(nc.nc1);
			else									candidates.add(nc.nc1);
			if( nc.nc2 instanceof ChoiceNameClass )	jobs.push(nc.nc2);
			else									candidates.add(nc.nc2);
		}
		
		// pick one
		return ((NameClass)candidates.get( random.nextInt(candidates.size()) )).visit(this);
	}
	
	public Object onNot( NotNameClass nc )
	{
		throw new Error("not name class is not supported");
	}
	
	public Object onDifference( DifferenceNameClass nc )
	{
		throw new Error("not name class is not supported");
	}
	
	private String getRandomName()
	{
		// TODO: should be better implemented
		return "tagName";
	}
	private String getRandomURI()
	{
		// TODO: should be better implemented
		return "randomURI";
	}
}
