/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.grammar;

import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.ExpressionPool;
import com.sun.tranquilo.grammar.Expression;
import org.xml.sax.Locator;

/**
 * place holder for imported attributes declaration.
 * 
 * This class also provides stub methods so that programs who are not aware to
 * divide&validate can gracefully degrade.
 * 
 * <p>
 * In Tranquilo, importing AttributesDecl from different implementations is
 * not supported. ExternalAttributeExp is always replaced by their target Expression
 * before validation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ExternalAttributeExp extends ReferenceExp {
	
	public ExternalAttributeExp(
		ExpressionPool pool, String namespaceURI, String role, Locator loc ) {
		
		super(namespaceURI+":"+role);
		this.source = loc;
		this.namespaceURI = namespaceURI;
		this.role = role;
		this.exp = Expression.epsilon;
	}
	
	/** namespace URI that this object belongs to. */
	public final String namespaceURI;
	
	/** name of the imported AttributesDecl */
	public final String role;
	
	/**
	 * where did this reference is written in the source file.
	 * can be set to null (to reduce memory usage) at anytime.
	 */
	public Locator source;
}
