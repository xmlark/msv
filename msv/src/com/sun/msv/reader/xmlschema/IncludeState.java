package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.reader.ChildlessState;

public class IncludeState extends ChildlessState {
	
	protected void startSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		super.startSelf();
		reader.switchSource( startTag,
			new RootIncludedSchemaState(
				reader.sfactory.schemaIncluded(this,reader.currentSchema.targetNamespace) ) );
	}
}
