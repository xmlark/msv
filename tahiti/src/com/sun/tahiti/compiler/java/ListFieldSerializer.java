/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.java;

import com.sun.tahiti.grammar.*;

/**
 * serializes a collection field by using a Vector.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class ListFieldSerializer extends VectorFieldSerializer
{
	ListFieldSerializer( ClassSerializer parent, FieldUse fu ) {
		super(parent,fu);
	}

	
	String getTypeStr() {
		return "java.util.LinkedList /* of " + parent.toPrintName(fu.type) +" */";
	}
}
