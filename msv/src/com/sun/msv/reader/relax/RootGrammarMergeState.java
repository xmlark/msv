package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;

/**
 * invokes State object that parses the document element.
 * 
 * this state is used for parsing included grammar.
 */
class RootGrammarMergeState extends SimpleState
{
	protected State createChildState( StartTagInfo tag )
	{
		if(tag.namespaceURI.equals(RELAXReader.RELAXNamespaceNamespace)
		&& tag.localName.equals("grammar"))
			return new GrammarState();
		
		return null;
	}
}
