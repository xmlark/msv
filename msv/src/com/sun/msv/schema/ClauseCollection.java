/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.schema;

import org.xml.sax.Locator;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * Map from role name to Clauses object
 */
public class ClauseCollection
{
	/** this map stores named clauses */
	private Map named = new java.util.HashMap();
	/** this set stores anonymous clauses */
	private Set anonymous = new java.util.HashSet();
						
	/**
	 * gets the Clauses object which is associated with the specified role name
	 * 
	 * @return null
	 *		if no Clauses object is associated with the role name
	 */
	public Clauses get( String roleName )
	{
		return (Clauses)named.get(roleName);
	}
	
	/**
	 * works the same as get method, but throws an exception instead of returning null
	 * 
	 * This method should be used only during loading schema.
	 * 
	 * @exception SchemaParseException
	 *		if corresponding Clauses object is not found.
	 */
	protected Clauses getWithCheck( String roleName, XMLElement ref )
		throws SchemaParseException
	{
		Clauses c = get(roleName);
		if( c==null )
			SchemaParseException.raise( ref,
				SchemaParseException.ERR_UNDEFINED_ROLE, roleName );
		return c;
	}
	
	/**
	 * gets or creates the Clauses object which is associated with the specified role name
	 * 
	 * if no Clauses object is associated with the role name,
	 * this method will create one.
	 */
	protected Clauses getOrCreate( String roleName )
	{
		Clauses c = get(roleName);
		if( c==null )
			named.put( roleName, c=new Clauses(roleName) );
		return c;
	}
	
	/**
	 * instanciates a new Clauses obejct and returns it.
	 * 
	 * Returned Clauses object will be only accessible through iteration.
	 */
	protected Clauses createAnonymous()
	{
		Clauses c = new Clauses(null);
		anonymous.add(c);
		return c;
	}
	
	/** iterates all Clauses object, including anonymous ones */
	public Iterator iterator()
	{
		return new MergeIterator( named.values().iterator(), anonymous.iterator() );
	}
	
	/** calls bind method of all Clauses objects */
	protected void bind()
		throws SchemaParseException
	{
		Iterator itr = iterator();
		while( itr.hasNext() )
			((Clauses)itr.next()).bind();
	}
}
