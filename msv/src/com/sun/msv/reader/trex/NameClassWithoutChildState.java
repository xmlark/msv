package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * Base implementation for those states who cannot have any child name class.
 * (e.g., nsName, anyName)
 * 
 * Note that this class does not prohibit children from different namespace.
 * Those "foreign" elements are just ignored.
 */
public abstract class NameClassWithoutChildState extends NameClassState
{
	protected final State createChildState(StartTagInfo tag)
	{
		// return null to indicate that this element does not accept a child.
		return null;
	}
}
