package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.reader.ChildlessState;

public class RedefineState extends GlobalDeclState {
	
	// TODO: elementDecl/attributeDecl are prohibited in redefine.
	// TODO: it probably is an error to redefine undefined components.
	
	// TODO: it is NOT an error to fail to load the specified schema (see 4.2.3)
	
	protected void startSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		super.startSelf();
	
		// parse included grammar first.
		reader.switchSource( startTag,
			new RootIncludedSchemaState(
				reader.sfactory.schemaIncluded(this,reader.currentSchema.targetNamespace) ) );
		
		// disable duplicate definition check.
		prevDuplicateCheck = reader.doDuplicateDefinitionCheck;
	}
	
	/** previous value of reader#doDuplicateDefinitionCheck. */
	private boolean prevDuplicateCheck;
	
	protected void endSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		reader.doDuplicateDefinitionCheck = prevDuplicateCheck;
		super.endSelf();
	}
}
