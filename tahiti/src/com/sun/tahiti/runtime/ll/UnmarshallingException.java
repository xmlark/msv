/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.ll;


public class UnmarshallingException extends org.xml.sax.SAXException {
	public UnmarshallingException( Exception e ) {
		super(e);
	}
	public UnmarshallingException( String msg ) {
		super(msg);
	}
}
