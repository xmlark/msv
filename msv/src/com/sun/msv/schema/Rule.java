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

import jp.gr.xml.formal.automaton.Automaton;
import org.xml.sax.Attributes;
import java.util.Vector;


/**
 * base class of HedgeRule and ElementRule
 * 
 * Object of this class is denoted by a label
 */
public abstract class Rule extends ModelParent
{
	/** label name of this rule */
	protected String label;
	/** gets label name of this rule
	 * 
	 * For anonymous Rules, this method returns null
	 */
	public String getLabel() { return label; }
	
	protected Rule( String labelName, HedgeModel contentModel )
	{
		super(contentModel);
		this.label = labelName;
	}
	
	/** parsing state that gathers child particles
	 *
	 * "ref", "hedgeRef", "choice", "sequence", "element", "none" and "empty"
	 * are the particles.
	 */
/*	protected class ParticleState extends StateImpl
	{
		/** vector of Particle objects that are found */
/*		private final Vector childModels = new Vector();
		
		protected ParticleState( SchemaReader reader ) { super(reader); }
		
		public State startChild( XMLElement e )
			throws SchemaParseException
		{
			State s = super.startChild(e);
			if(s!=null)		return s;	// recognized by the base class
			
			if( e.tagName.equals("mixed") )
			{// mixed is allowed only as a immediate child of elementRule
				if( reader.getParentState() instanceof ElementRule.ElementRuleBaseState )
					return reader.factory.createMixedState(e);
				else
					SchemaParseException.raise( e,
						SchemaParseException.ERR_ILLEGAL_MIXED, null );
			}
			if( e.tagName.equals("sequence")
			||  e.tagName.equals("choice") )
				; // return reader.factory.createParticleState(e);
			// TODO : implement the reset
			
			return s;
		}
	}
*/
}
