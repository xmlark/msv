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

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * &lt;topLevel&gt; element
 */
public class TopLevelState extends HedgeRuleBaseState
{
	protected void endSelf( Expression contentModel )
	{
		((RELAXReader)reader).grammar.topLevel = contentModel;
	}

	protected State createChildState( StartTagInfo tag )
	{
		State s = super.createChildState(tag);
		if(s!=null)		return s;
		
		// user tends to forget to specify RELAX Core namespace for
		// topLevel elements. see if this is the case
		if( tag.namespaceURI.equals(RELAXReader.RELAXNamespaceNamespace))
		{// bingo.
			reader.reportError( RELAXReader.ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE );
			// return null so that user will also receive "malplaced element" error.
		}
		return null;	
	}
}
