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
import org.relaxng.datatype.ValidationContext;

/**
 * very limited 'ID' type of XML Schema Part 2.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDType extends NmtokenType {
	
	public static final IDType theInstance = new IDType();
	protected IDType()	{ super("ID"); }
	
	protected Object readResolve() {
		// prevent serialization from breaking the singleton.
		return theInstance;
	}
	
	public Object convertToValue( String content, ValidationContext context ) {
		Object o = super.convertToValue(content,context);
		if(o==null)		return null;

		if(!((IDContextProvider)context).onID("",content))	return null;
		return o;
	}
}
