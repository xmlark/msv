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

import java.util.Iterator;
import java.util.Set;
import org.xml.sax.Attributes;

/**
 * set of Clause objects that share the same role name
 */
public class Clauses implements Exportable
{
	private final Set clauses = new java.util.HashSet();
	
	boolean verify( String namespaceUri, String localName, Attributes a, boolean domesticValidation )
	{
		final Iterator itr = iterator();
		while(itr.hasNext())
		{
			final Clause c = (Clause)itr.next();
			if( c.verify(namespaceUri,localName,a,domesticValidation) )
				return true;	// if any one of Clause accepts, then the entire verification succeeds
		}
		
		return false;
	}
	
	/** iterates all Clause objects in this object */
	public Iterator iterator()
	{
		return clauses.iterator();
	}
	
	/** calls bind method of all Clause object */
	protected void bind()
		throws SchemaParseException
	{
		Iterator itr = iterator();
		while( itr.hasNext() )
			((Clause)itr.next()).bind();
	}
	
	/** this flag indicates that whether the specified label name is exported */
	protected boolean exported;
	/** examines whether this label is exported or not */
	public boolean isExported() { return exported; }
	
	/** instanciation is only possible within this package */
	protected Clauses( String roleName ) { this.roleName = roleName; }
	
	/** role name of the clauses that this object contains */
	protected final String roleName;
	
	/** gets the role name of this clauses
	 * 
	 * This method can return null if no role name is given to this clause.
	 */
	public String getRoleName() { return roleName; }
	
	/** adds a Clause object */
	protected void add( Clause newClause ) { clauses.add(newClause); }
}
