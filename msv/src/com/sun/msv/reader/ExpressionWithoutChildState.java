package com.sun.tranquilo.reader;

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * Base implementation for those states who cannot have any children.
 * (e.g., RELAX: empty, null, etc.   TREX: anyString, notAllowed, etc.)
 * 
 * Note that this class does not prohibit children from different namespace.
 * Those "foreign" elements are just ignored.
 */
public abstract class ExpressionWithoutChildState extends ExpressionState
{
	protected final State createChildState(StartTagInfo tag)
	{
		// return null to indicate that this element does not accept a child.
		return null;
	}
}
