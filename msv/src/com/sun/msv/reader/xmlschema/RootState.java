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

import com.sun.tranquilo.reader.State;

// used also for import.
public class RootState extends RootIncludedSchemaState {
	
	protected RootState( State topLevelState ) {
		super(topLevelState);
	}
	
	protected void endSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		// perform final wrap up.
		reader.wrapUp();
		super.endSelf();
	}
}
