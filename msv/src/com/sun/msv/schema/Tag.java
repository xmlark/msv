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

/**
 * 'tag' declaration in RELAX
 */
public class Tag extends Clause
{
	/** tag name constraint.
	 * 
	 * Any valid start tag must have this tagName as its local name */
	private String tagName;
	
	private Schema parentSchema;
	
	/** instanciation for non-local tag element
	 * 
	 * @param roleName
	 *   role name of this Tag. Can be null if this tag is anonymous.
	 * @param tagName
	 *	 name of this tag.
	 */
	protected Tag( String roleName, String tagName )
	{
		super( roleName );
		setTagName( tagName );
	}
	
	public String getTagName() { return tagName; }
	public void setTagName( String tagName )
	{
		// TODO : detect unsuitable tagName, like null or "<".
		if( tagName==null )	throw new IllegalArgumentException();
		this.tagName = tagName;
	}
	
	protected boolean verifyTagName( String namespaceUri, String localName )
	{
		if( tagName != localName )								return false;
		if( getParentModule().targetNamespace != namespaceUri )	return false;
		
		return true;
	}
	
	/** returns parentSchema as Module object */
	private Module getParentModule()
	{
		return (Module)parentSchema;
	}
}
