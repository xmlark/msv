/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.reader.ChildlessState;

public class IncludeState extends ChildlessState {
	
	protected void startSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		super.startSelf();
		reader.switchSource( startTag,
			new RootIncludedSchemaState(
				reader.sfactory.schemaIncluded(this,reader.currentSchema.targetNamespace) ) );
	}
}
