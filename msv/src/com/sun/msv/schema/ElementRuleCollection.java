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

import org.xml.sax.Locator;

/**
 * Typed-map from label name to ElementRules
 */
public class ElementRuleCollection
{
	/** actual storage */
	private final Map impl = new java.util.HashMap();
	
	/**
	 * gets a ElementRules object that
	 * contains all <code>ElementRule</code> object
	 * which corresponds to the specified label name
	 * 
	 * @return null		if no ElementRule object is associated with the name
	 */
	public ElementRules get( String label )		{ return (ElementRules)impl.get(label); }
		
	/**
	 * works the same as get method, but throws an exception instead of returning null
	 * 
	 * This method should be used only during loading schema.
	 * 
	 * @exception SchemaParseException
	 *		if corresponding ElementRules object is not found.
	 */
	protected ElementRules getWithCheck( String labelName, XMLElement ref )
		throws SchemaParseException
	{
		ElementRules er = get(labelName);
		if( er==null )
			SchemaParseException.raise( ref,
				SchemaParseException.ERR_UNDEFINED_LABEL, labelName );
		return er;
	}
	
	/**
	 * gets or creates ElementRules object
	 * 
	 * this method creates new ElementRules object if no object is associated with
	 * given label name
	 */
	protected ElementRules getOrCreate( String label )
	{
		ElementRules er = get(label);
		if( er==null )
			impl.put( label, er=new ElementRules() );
		return er;
	}
	
	/**
	 * iterates all ElementRules objects contained this object
	 */
	public Iterator iterator()
	{
		return impl.values().iterator();
	}
}
