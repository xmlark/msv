/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.xmlschema;

import com.sun.tranquilo.grammar.ReferenceExp;

/**
 * attribute declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeDeclExp extends ReferenceExp {
	
	public AttributeDeclExp( String typeLocalName ) {
		super(typeLocalName);
	}
}
