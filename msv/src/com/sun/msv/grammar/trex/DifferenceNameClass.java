/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;

/**
 * &lt;difference&gt; name class of TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DifferenceNameClass implements NameClass {
	public final NameClass nc1;
	public final NameClass nc2;
	
	public boolean accepts( String namespaceURI, String localPart ) {
		return nc1.accepts(namespaceURI,localPart)
			&& !nc2.accepts(namespaceURI,localPart);
	}
	
	public Object visit( NameClassVisitor visitor ) { return ((TREXNameClassVisitor)visitor).onDifference(this); }
	
	public DifferenceNameClass( NameClass nc1, NameClass nc2 ) {
		this.nc1 = nc1;
		this.nc2 = nc2;
	}
}
