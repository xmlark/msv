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
import java.util.Iterator;

public class HedgeRuleCollection
{
	/** actual storage */
	private final Map impl = new java.util.HashMap();
	/** parent Schema object to which this object belongs */
	public final Schema parentSchema;
	
	/**
	 * gets a HedgeRules object that corresponds to the specified label name
	 * 
	 * @return null		if no HedgeRules object is associated with the name
	 */
	public HedgeRules get( String label )		{ return (HedgeRules)impl.get(label); }
		
	/**
	 * gets or creates HedgeRules object
	 * 
	 * this method creates new HedgeRules object if no object is associated with
	 * given label name
	 */
	protected HedgeRules getOrCreate( String label )
	{
		HedgeRules hr = get(label);
		if( hr==null )
			impl.put( label, hr=new HedgeRules() );
		return hr;
	}
	
	/**
	 * works the same as get method, but throws an exception instead of returning null
	 * 
	 * This method should be used only during loading schema.
	 * 
	 * @exception SchemaParseException
	 *		if corresponding HedgeRules object is not found.
	 */
	protected HedgeRules getWithCheck( String labelName, XMLElement refElement )
		throws SchemaParseException
	{
		// TODO : should report different error? so that user can distinguish elementRule ref and hedgeRule ref
		HedgeRules hr = get(labelName);
		if( hr==null )
			SchemaParseException.raise( refElement,
				SchemaParseException.ERR_UNDEFINED_LABEL, labelName );
		return hr;
	}
	
	/**
	 * adds new HedgeRule to this collection
	 */
	protected void add( String label, HedgeRuleX hr )
		throws SchemaParseException
	{
		HedgeRules set = get(label);
		if( set==null )		// create new HedgeRules first
			impl.put( label, set = new HedgeRules() );
		
		// HedgeRule with the same label exists: merge it
		set.add(hr);
	}
	
	/**
	 * iterates all HedgeRule objects contained this object
	 */
	public Iterator iterator()
	{
		return impl.values().iterator();
	}
	
	protected HedgeRuleCollection( Schema parentSchema )
	{
		this.parentSchema = parentSchema;
	}
}
