/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.sm;

/**
 * signals an error encountered during the marshalling.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MarshallingException extends Exception {
	
	public MarshallingException( String msg, Exception nestedException ) {
		super(msg);
		this.nestedException = nestedException;
	}
	
	private Exception nestedException;
	public Exception getNestedException() {
		return nestedException;
	}
}
