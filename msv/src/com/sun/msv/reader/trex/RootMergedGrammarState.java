/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.State;

/**
 * parses root state of a merged grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootMergedGrammarState extends RootState
{
	protected State createChildState( StartTagInfo tag )
	{// expects "grammar" element only, and creates MergeGrammarState
		if( tag.localName.equals("grammar") )	return new MergeGrammarState();
		return null;
	}
}
