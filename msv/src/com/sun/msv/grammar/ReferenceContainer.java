/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar;

import java.util.Map;
import java.util.Iterator;

/**
 * Container of ReferenceExp. a map from name to ReferenceExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ReferenceContainer
{
	protected final Map impl = new java.util.HashMap();
	
	/**
	 * gets or creates ReferenceExp object.
	 * 
	 * Derived class should provide type-safe accesser methods.
	 * 
	 * Usually, this method is only necessary for some kind of grammar loader.
	 * If you are programming an application over Tranquilo,
	 * {@link _get} method is probably what you need.
	 */
	protected final ReferenceExp _getOrCreate( String name )
	{
		Object o = impl.get(name);
		if(o!=null)	return (ReferenceExp)o;
		
		// this is the first time this name is used.
		// so create a ReferenceExp here.
		ReferenceExp exp = createReference(name);
		impl.put(name,exp);
		return exp;
	}
	
	/** creates a new reference object with given name */
	protected abstract ReferenceExp createReference( String name );

	
	/** gets a referenced expression
	 * 
	 * Derived class should provide type-safe accesser methods.
	 * 
	 * @return null
	 *		if no expression is defined with the given name.
	 */
	public final ReferenceExp _get( String name )
	{
		Object o = impl.get(name);
		if(o!=null)	return (ReferenceExp)o;
		else		return null;	// not found	
	}
	
	/** iterates all ReferenceExp in this container */
	public final Iterator iterator()
	{
		return impl.values().iterator();
	}
	
	/** obtains all items in this container. */
	public final ReferenceExp[] getAll()
	{
		ReferenceExp[] r = new ReferenceExp[size()];
		impl.values().toArray(r);
		return r;
	}
	
	/** gets the number of ReferenceExps in this container. */
	public final int size()	{ return impl.size(); }
}
