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

import java.util.Set;
import java.util.Iterator;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

/**
 * Set of ElementRule objects that share the same label name
 * 
 * acts as an alphabet
 */
public class ElementRules implements Exportable, Particle
{
	/** actual storage that keeps ElementRule objects */
	private final Set impl = new java.util.HashSet();
	
	/** adds new ElementRule */
	protected void add( ElementRule rule )
	{
		// ASSERT : rule.label==label
		impl.add(rule);
	}
	
	/** iterates all ElementRule object in this object */
	public Iterator iterator()				{ return impl.iterator(); }
	
	protected ElementRules() {}
	
	/** this flag indicates that whether the specified label name is exported */
	protected boolean exported;
	/** examines whether this label is exported or not */
	public boolean isExported() { return exported; }
	
	/**
	 * returns an single symbol automaton that accepts this object as a sole character
	 */
	public Automaton getAutomaton( AutomatonFactory factory )
	{
		return factory.createSingleSymbolAutomaton(this);
	}
}
