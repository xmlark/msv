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
import com.sun.msv.util.DatatypeRef;

/**
 * represents attribute and its value.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeToken extends Token
{
	public final String					namespaceURI;
	public final String					localName;
	public final StringToken			value;
	protected final REDocumentDeclaration	docDecl;
	
	/**
	 * holds a reference to the assigned type.
	 * 
	 * If this AttributeToken is successfully consumed, then this field
	 * contains the AttributeExp which consumed this token.
	 * 
	 * If this token is not consumed or several different AttributeExps
	 * consumed this token, then null.
	 */
	public AttributeExp matchedExp = null;
	/**
	 * If this value is false, the "matched" field must always null. This indicates
	 * that no AttributeExp has consumed this token yet.
	 * If this value is true and the "matched" field is non-null, then it means
	 * that AttributeExp has consumed this token.
	 * If this value is true and the "matched" field is null, then more than
	 * one AttributeExps have consumed this token.
	 */
	private boolean saturated = false;
	
	protected AttributeToken( REDocumentDeclaration docDecl,
			String namespaceURI, String localName, String value, IDContextProvider context ) {
		this( docDecl, namespaceURI, localName,
			new StringToken(docDecl,value,context,new DatatypeRef()) );
	}
	protected AttributeToken( REDocumentDeclaration docDecl,
			String namespaceURI, String localName, StringToken value ) {
		this.namespaceURI	= namespaceURI;
		this.localName		= localName;
		this.value			= value;
		this.docDecl		= docDecl;
	}
	
	/**
	 * creates a special AttributeToken which matchs any content restrictions.
	 * 
	 * This token acts like a wild card for the attribute. This method is
	 * used for error recovery.
	 */
	final AttributeRecoveryToken createRecoveryAttToken() {
		return new AttributeRecoveryToken( docDecl, namespaceURI, localName, value );
	}
	
	boolean match( AttributeExp exp ) {
		// Attribute name must meet the constraint of NameClass
		if(!exp.nameClass.accepts(namespaceURI,localName))	return false;
		
		// content model of the attribute must consume the value
		if(docDecl.resCalc.calcResidual(exp.exp, value).isEpsilonReducible()) {
			// store the expression who consumed this token.
			if( !saturated || exp==matchedExp )		matchedExp=exp;
			else									matchedExp=null;
		/*	the above is the shortened form of:
			if( !saturated )
				matchedExp = exp;
			else
				if( exp!=matchedExp )
					matchedExp = null;
		 */
			saturated = true;
			return true;
		}
		
		return false;
	}

}
