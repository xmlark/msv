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

import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.util.StartTagInfo;

public class RootIncludedSchemaState extends SimpleState {
	
	protected State topLevelState;
	
	protected RootIncludedSchemaState( State topLevelState ) {
		this.topLevelState = topLevelState;
	}
	
	protected State createChildState( StartTagInfo tag ) {
		if(tag.localName.equals("schema"))
			return topLevelState;
		
		return null;
	}
}
