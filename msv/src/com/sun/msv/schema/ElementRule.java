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


/**
 * represents &lt;elementRule&gt;
 */
public class ElementRule extends Rule
{
	protected Clauses clauses;
			
	/** gets role name that is specified with this rule */
	public String getRoleName()
	{
		return clauses.getRoleName();
	}
	
	/** gets the Clauses object which is used by this object */
	public Clauses getClauses()
	{
		return clauses;
	}
	
	/**
	 * @param labelName
	 *		If this ElementRule is named one, set its name.
	 *		If the ElementRule is anonymous one (this could be happen when using
	 *		'element' shorthand notation), set null.
	 */
	protected ElementRule( String labelName, Clauses clauses, HedgeModel contentModel )
		throws SchemaParseException
	{
		super(labelName,contentModel);
		this.clauses = clauses;
	}
}
