/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler;

/**
 * used to resolve various objects to its id so that
 * they can be serialized.
 */
public interface Symbolizer {
	String getId( Object o );
}
