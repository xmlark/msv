/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.*;
import com.sun.msv.datatype.ValidationContextProvider;

/**
 * represents attribute and its value.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class AttributeToken extends Token
{
	protected final String				namespaceURI;
	protected final String				localName;
	protected final StringToken			value;
	protected final ExpressionPool		pool;
	protected final ResidualCalculator	residual;
	
	protected AttributeToken( ExpressionPool pool,
		String namespaceURI, String localName, String value,
		ValidationContextProvider context,
		ResidualCalculator residual )
	{
		this( pool, namespaceURI, localName, new StringToken(value,context), residual );
	}
	protected AttributeToken( ExpressionPool pool,
		String namespaceURI, String localName,
		StringToken value,
		ResidualCalculator residual )
	{
		this.namespaceURI	= namespaceURI;
		this.localName		= localName;
		this.value			= value;
		this.pool			= pool;
		this.residual		= residual;
	}
	
	/**
	 * creates a special AttributeToken which matchs any content restrictions.
	 * 
	 * This token acts like a wild card for the attribute. This method is
	 * used for error recovery.
	 */
	final AttributeRecoveryToken createRecoveryAttToken()
	{
		return new AttributeRecoveryToken( pool, namespaceURI, localName, value, residual );
	}
	
	boolean match( AttributeExp exp )
	{
		// Attribute name must meet the constraint of NameClass
		if(!exp.nameClass.accepts(namespaceURI,localName))	return false;
		
		// content model of the attribute must consume the value
		if(residual.calcResidual(exp.exp, value).isEpsilonReducible())
			return true;
		
		return false;
	}

}
