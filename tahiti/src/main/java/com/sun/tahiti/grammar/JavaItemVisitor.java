/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

/**
 * visits JavaItem.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface JavaItemVisitor {
	Object onClass( ClassItem item );
	Object onField( FieldItem item );
	Object onIgnore( IgnoreItem item );
	Object onInterface( InterfaceItem item );
	Object onPrimitive( PrimitiveItem item );
	Object onSuper( SuperClassItem item );
}
