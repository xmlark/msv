/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.grammar.Expression;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used to parse RELAX module referenced by RELAX Namespace.
 */
class RootModuleState extends SimpleState
{
	protected final String expectedNamespace;
	
	RootModuleState( String expectedNamespace )
	{ this.expectedNamespace = expectedNamespace; }
	
	protected State createChildState( StartTagInfo tag )
	{
		if(tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)
		&& tag.localName.equals("module"))
			return new ModuleState(expectedNamespace);
		
		return null;
	}
}
