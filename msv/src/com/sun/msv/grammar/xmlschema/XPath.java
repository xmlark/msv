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
 * internal representation of 'aaa/bbb/ccc/ ... /eee'.
 * Note that 'A|B' is repsented by using two Path instances.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XPath implements java.io.Serializable {
	public boolean			isAnyDescendant;
	public NameClass[]		steps;
}
