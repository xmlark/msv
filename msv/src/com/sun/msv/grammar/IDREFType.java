/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

import com.sun.msv.datatype.NmtokenType;
import com.sun.msv.datatype.ValidationContextProvider;

/**
 * very limited 'IDREF' type of XML Schema Part 2.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDREFType extends NmtokenType {
	
	public static final IDREFType theInstance = new IDREFType();
	
	protected IDREFType()	{ super("IDREF"); }
	
	
	public Object convertToValue( String content, ValidationContextProvider context ) {
		Object o = super.convertToValue(content,context);
		if(o==null)		return null;

		((IDContextProvider)context).onIDREF(content);
		return o;
	}
}
