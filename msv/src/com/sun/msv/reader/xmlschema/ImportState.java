/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.reader.ChildlessState;

/**
 * used to parse &lt;import&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ImportState extends ChildlessState {
	
	protected void startSelf() {
		super.startSelf();
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		// TODO: @schemaLocation is optional.
		// TODO: @namespace is also optional, but I couldn't get what shall we do if it's absent.
		String namespace = startTag.getAttribute("namespace");
		if( namespace==null ) {
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "import", "namespace" );
			return;	// recover by ignoring this import.
		}
		
		if( namespace.equals(reader.currentSchema.targetNamespace) ) {
			reader.reportError( reader.ERR_IMPORTING_SAME_NAMESPACE );
			return;
		}
		
		if( reader.isSchemaDefined( reader.getOrCreateSchema(namespace) ) )
			// this grammar is already defined.
			// so ignore it.
			return;
		
		
		reader.switchSource( startTag,
			new RootIncludedSchemaState(reader.sfactory.schemaHead(namespace)) );
	}
}
