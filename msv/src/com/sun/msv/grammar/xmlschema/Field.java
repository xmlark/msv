/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.xmlschema;

import com.sun.msv.grammar.NameClass;

/**
 * represents one field of an identity constraint.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Field implements java.io.Serializable {
	
	/**
	 * XPath that characterizes this field.
	 * 'A|B' is represented by using two FieldPath objects.
	 */
	public FieldPath[]	paths;
	
	/**
	 * Path expression for field.
	 * Field can use an attribute at the last of a path expression,
	 * which is hed by attributeStep field.
	 */
	public class FieldPath extends XPath {
		public NameClass		attributeStep;
	}
}
