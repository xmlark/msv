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

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;

/**
 * Validation unit that corresponds with start tag of XML
 */
public abstract class Clause implements java.io.Serializable
{
	/** <code>Clauses</code> that are referenced from this clause */
	public final Set refs = new java.util.HashSet();
	
	/** <code>Attribute</code> objects that are refered from this clause */
	public final Attributes attributes = new Attributes();
	
	public class Attributes
	{
		private final Map impl = new java.util.HashMap();
		
		private String encode( String namespaceUri, String localName )
		{
			return "{"+namespaceUri+"}"+localName;
		}
		
		public void add( Attribute a )		{ impl.put( encode(a.namespace,a.localName), a ); }
		public Iterator iterator()			{ return impl.values().iterator(); }
	}
	
	/** role name given to this clause
	 * 
	 * null for locally declared tag clause.
	 */
	protected String role;
	
	
	
	/**
	 * actual constructor of this class.
	 * 
	 * Sometimes, computation of role name is so complex that it need some statements.
	 * Thus we need an alternative way to construct object.
	 * 
	 * This method performs 1st half of construction.
	 * The 2nd half of construction, which is to parse 'attribute's and 'ref's,
	 * will be done in bind method after 1st half construction is done for all
	 * tags and attPools.
	 */
	protected Clause( String roleName )
	{
		this.role = roleName;
	}
	
	/**
	 * checks if this clause accepts given start tag
	 * 
	 * @param domesticValidation
	 *		"true" indicates that validation should be done for local,
	 *		unprefixed attribute. "false" indicates that it should be done
	 *		for exported, prefixed attribute.
	 * 
	 * @return
	 *		true	if accepted
	 *		false	otherwise
	 */
	public final boolean verify( String namespaceUri, String localName, org.xml.sax.Attributes a, boolean domesticValidation )
	{
		// verifies tag name
		if(!verifyTagName(namespaceUri,localName))
			return false;
		
		// verifies attributes
		Iterator itr = attributes.iterator();
		while(itr.hasNext())
			if( ! ((Attribute)itr.next()).verify(a,domesticValidation) )
				return false;		// if one of Attribute fails, then the entire clause fails
		
		return true;
	}
	
	/**
	 * verifies tag name (both namespace and local name)
	 * 
	 * @return	true		if arguments are accepted by the clause
	 *			false		if not accepted
	 */
	abstract protected boolean verifyTagName( String namespaceUri, String localName );

	// TODO : add diagnose method here
	
	protected void bind()
	{
		// TODO : implement this
	}
}
