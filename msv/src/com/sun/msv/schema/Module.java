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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;

/**
 * RELAX module
 */
public class Module extends Schema
{
	// TODO : implement this
	
	/** collection of ElementRule objects that appear in this module */
	public final ElementRuleCollection elementRules = new ElementRuleCollection();
	
	/** targetNamespace of this module */
	protected String targetNamespace;
	/** gets target namespace of this module */
	public String getTargetNamespace()	{ return targetNamespace; }
	
	/** module version
	 * 
	 * this variable will never be used for verification.
	 */
	public String moduleVersion;
	
	/** systemId from which this schema was loaded */
	protected String systemId;
	/** gets systemId from which this schema was loaded */
	public String getSystemId() { return systemId; }
	
	/** Grammar object to which this Module belongs to
	 * 
	 * Even if Module is used as a stand-alone module,
	 * this variable has valid reference to some Grammar object.
	 * 
	 * While parsing module, this variable can be null.
	 * (this happens when parsing stand-alone module)
	 */
	protected Grammar parentGrammar;
	
	public Grammar getParentGrammar() { return parentGrammar; }
	
	
	
	
	
	protected Clauses resolveRoleRef( XMLElement refElement )
		throws SchemaParseException
	{
		return clauses.getWithCheck( refElement.getRequiredAttribute("role"), refElement );
	}
	
	protected ElementRules resolveElementRuleRef( XMLElement refElement )
		throws SchemaParseException
	{
		return elementRules.getWithCheck( refElement.getRequiredAttribute("label"), refElement );
	}
	
	protected HedgeRules resolveHedgeRuleRef( XMLElement refElement )
		throws SchemaParseException
	{
		return hedgeRules.getWithCheck( refElement.getRequiredAttribute("label"), refElement );
	}
}
