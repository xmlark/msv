package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.reader.ChildlessState;

/**
 * &lt;include&gt; element of RELAX Namespace.
 */
public class IncludeGrammarState extends ChildlessState
{
	protected void startSelf()
	{
		super.startSelf();
	
		final String href = startTag.getAttribute("grammarLocation");

		if(href==null)
		{// name attribute is required.
			reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE,
				"include","grammarLocation");
			// recover by ignoring this include element
		}
		else
		{
			((RELAXReader)reader).switchSource(href,new RootGrammarMergeState());
		}
	}
}
