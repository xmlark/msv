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
import com.sun.msv.grammar.Expression;

/**
 * ElementExp that is used for &lt;any processContents="skip"/&gt;.
 * 
 * This is kept in the separate class so that others can see that this element
 * is used for skip element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SkipElementExp extends com.sun.msv.grammar.trex.ElementPattern {
	
	public SkipElementExp( NameClass nameClass, Expression contentModel ) {
		super(nameClass,contentModel);
	}
}
