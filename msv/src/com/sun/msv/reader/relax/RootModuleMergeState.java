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

/**
 * invokes State object that parses the document element.
 * 
 * this state is used for parsing included module.
 */
class RootModuleMergeState extends SimpleState
{
	protected State createChildState( StartTagInfo tag )
	{
		if(tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)
		&& tag.localName.equals("module"))
			return new ModuleMergeState(
				((RELAXReader)reader).currentModule.targetNamespace );
			// included module must have the same target namespace
			// or it must be a chameleon module.
		
		return null;
	}
}
