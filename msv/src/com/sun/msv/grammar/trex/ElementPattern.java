/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.*;

public class ElementPattern extends ElementExp
{
	public final NameClass nameClass;
	public final NameClass getNameClass() { return nameClass; }
	
	public ElementPattern( NameClass nameClass, Expression contentModel )
	{
		super(contentModel);
		this.nameClass = nameClass;
	}
}
