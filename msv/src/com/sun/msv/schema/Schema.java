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

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

public abstract class Schema implements java.io.Serializable
{
		
	/** collection that stores all <code>Clauses</code> object */
	public final ClauseCollection clauses = new ClauseCollection();
	
	/** collection of HedgeRule objects that appear in this module */
	public final HedgeRuleCollection hedgeRules = new HedgeRuleCollection(this);

	/**
	 * gets the Clauses object which is pointed by 'ref' element
	 * 
	 * This method cannot return null (throw an exception instead)
	 * 
	 * @exception SchemaParseException
	 *		implementation can throw this exception if given
	 *		element is syntactically or semantically invalid.
	 */
	protected abstract Clauses resolveRoleRef( XMLElement e )
		throws SchemaParseException;
	
	/**
	 * gets the ElementRules object which is pointed by 'ref' element
	 * 
	 * This method cannot return null (throw an exception instead)
	 * 
	 * @exception SchemaParseException
	 *		implementation can throw this exception if given
	 *		element is syntactically or semantically invalid.
	 */
	protected abstract ElementRules resolveElementRuleRef( XMLElement e )
		throws SchemaParseException;
	
	/**
	 * gets the HedgeRule object which is pointed by 'hedgeRef' element
	 * 
	 * This method cannot return null (throw an exception instead)
	 * 
	 * @exception SchemaParseException
	 *		implementation can throw this exception if given
	 *		element is syntactically or semantically invalid.
	 */
	protected abstract HedgeRules resolveHedgeRuleRef( XMLElement e )
		throws SchemaParseException;
	
}
