package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.util.StartTagInfo;

// used also for import.
public class RootState extends SimpleState {
	
	protected State topLevelState;
	
	protected RootState( State topLevelState ) {
		this.topLevelState = topLevelState;
	}
	
	protected State createChildState( StartTagInfo tag ) {
		if(tag.localName.equals("schema"))
			return topLevelState;
		
		return null;
	}
	
	protected void endSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		reader.wrapUp();
		super.endSelf();
	}
}
