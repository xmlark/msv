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

import com.sun.msv.datatype.xsd.NmtokenType;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.datatype.xsd.DatatypeFactory;
import org.relaxng.datatype.ValidationContext;

/**
 * very limited 'IDREF' type of XML Schema Part 2.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IDREFType extends NmtokenType {
	
	public static final IDREFType theInstance = new IDREFType();
	public static final XSDatatypeImpl theIDREFSinstance = createIDREFS();
		
	private static XSDatatypeImpl createIDREFS() {
		try {
			return (XSDatatypeImpl)DatatypeFactory.deriveByList("IDREFS",IDREFType.theInstance );
		} catch( Exception e ) {
			// not possible
			throw new Error();
		}
	}
	
	protected IDREFType()	{ super("IDREF"); }
	
	protected Object readResolve() {
		// prevent serialization from breaking the singleton.
		return theInstance;
	}
	
	public Object convertToValue( String content, ValidationContext context ) {
		Object o = super.convertToValue(content,context);
		if(o==null)		return null;

		((IDContextProvider)context).onIDREF("","",content);
		return o;
	}
}
