/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.SequenceState;

/**
 * parses &lt;start&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StartState extends SequenceState
{
	protected final TREXGrammarReader getReader() { return (TREXGrammarReader)reader; }
	
	protected Expression annealExpression( Expression exp )
	{
		if(startTag.containsAttribute("name"))
		{// name attribute is optional.
			final String name = startTag.getAttribute("name");
			ReferenceExp ref = getReader().grammar.namedPatterns.getOrCreate(name);
			ref.exp = exp;
		}
		
		getReader().grammar.start = exp;
		return null;	// return value is meaningless.
	}
}
