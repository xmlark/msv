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

import com.sun.tranquilo.reader.ChildlessState;

/**
 * &lt;include&gt; element as an immediate child of &lt;grammar&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IncludeMergeState extends ChildlessState
{
	protected void startSelf()
	{
		super.startSelf();
	
		final String href = startTag.getAttribute("href");

		if(href==null)
		{// name attribute is required.
			reader.reportError( TREXGrammarReader.ERR_MISSING_ATTRIBUTE,
				"include","href");
			// recover by ignoring this include element
		}
		else
			// parse specified file
			reader.switchSource(href,new RootMergedGrammarState());
	}
}
