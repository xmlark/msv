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


public class Grammar extends Schema
{
	private class NamespaceInfo
	{
		/** hedge rules that are defined in this grammar for this namespace */
		public final HedgeRuleCollection hedgeRules
			= new HedgeRuleCollection(Grammar.this);
		/** attPools that are defined in this grammar for this namespace */
		public final ClauseCollection clauses
			= new ClauseCollection();
		/** Module that defines this namespace */
		public final Module module;
		
		NamespaceInfo( Module module ) { this.module = module; }
	}

	/** map from namespaceUri (string) to NamespaceInfo */
	private Map namespaces = new java.util.HashMap();
	
	/** gets NamespaceInfo object for this namespace.
	 * 
	 * @return null		if undefined namespace is passed as a parameter
	 */
	private NamespaceInfo get( String namespaceUri )
	{
		return (NamespaceInfo)namespaces.get(namespaceUri);
	}
	
	/** gets NamespaceInfo object for this namespace.
	 * 
	 * @exception SchemaParseException
	 *		if undefined namespace is passed as a parameter
	 */
	private NamespaceInfo getWithCheck( String namespaceUri, XMLElement referenceSource )
		throws SchemaParseException
	{	
		NamespaceInfo ni = get(namespaceUri);
		if(ni!=null)	return ni;
		
		SchemaParseException.raise( referenceSource,
			SchemaParseException.ERR_UNDEFINED_NAMESPACE, namespaceUri );
		return null;	// just for assuring compiler
	}
	
	public HedgeRuleCollection hedgeRules( String namespaceUri, XMLElement referenceSource )
		throws SchemaParseException
	{
		return getWithCheck(namespaceUri,referenceSource).hedgeRules;
	}
	
	public ClauseCollection clauses( String namespaceUri, XMLElement referenceSource )
		throws SchemaParseException
	{
		return getWithCheck(namespaceUri,referenceSource).clauses;
	}
	
	public Module modules( String namespaceUri, XMLElement referenceSource )
		throws SchemaParseException
	{
		return getWithCheck(namespaceUri,referenceSource).module;
	}

	
	
	protected Clauses resolveRoleRef( XMLElement refElement )
		throws SchemaParseException
	{
		final String namespace = refElement.getRequiredAttribute("namespace");
		final String roleName = refElement.getRequiredAttribute("role");
		
		return modules(namespace,refElement).clauses.getWithCheck( roleName, refElement );
	}
	
	protected ElementRules resolveElementRuleRef( XMLElement refElement )
		throws SchemaParseException
	{
		final String namespace = refElement.getRequiredAttribute("namespace");
		final String labelName = refElement.getRequiredAttribute("label");
		
		return modules(namespace,refElement).elementRules.getWithCheck( labelName, refElement );
	}
	
	protected HedgeRules resolveHedgeRuleRef( XMLElement refElement )
		throws SchemaParseException
	{
		final String namespace = refElement.getRequiredAttribute("namespace");
		final String labelName = refElement.getRequiredAttribute("label");
		
		return modules(namespace,refElement).hedgeRules.getWithCheck( labelName, refElement );
	}
}
