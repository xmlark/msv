/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.util.text;

/**
 * provides replacement strings for {@link Formatter}.
 */
public interface Model {
	/**
	 * computes the replacement string for the specified parameter.
	 * When a format string contains &lt;%abc>, then "abc" is passed
	 * as a parameter.
	 */
	String getParameter( String parameter );
}
