package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.reader.State;

// used also for import.
public class RootState extends RootIncludedSchemaState {
	
	protected RootState( State topLevelState ) {
		super(topLevelState);
	}
	
	protected void endSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		// perform final wrap up.
		reader.wrapUp();
		super.endSelf();
	}
}
