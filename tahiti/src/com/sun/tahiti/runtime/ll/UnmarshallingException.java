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

/**
 * signals an error encountered during the unmarshalling.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnmarshallingException extends org.xml.sax.SAXException {
	public UnmarshallingException( Exception e ) {
		super(e);
	}
	public UnmarshallingException( String msg ) {
		super(msg);
	}
}
