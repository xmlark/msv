package com.sun.tranquilo.reader;

import com.sun.tranquilo.util.StartTagInfo;

/**
 * state that has no children
 */
public class ChildlessState extends SimpleState
{
	protected final State createChildState( StartTagInfo tag )
	{
		return null;
	}
}
	
	
