package com.sun.tranquilo.reader.xmlschema;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.grammar.xmlschema.SimpleTypeExp;
import com.sun.tranquilo.reader.ChildlessState;
import com.sun.tranquilo.reader.datatype.xsd.SimpleTypeState;

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
	
	
	public void onEndChild( DataType type ) {
		// handle redefinition of simpleType.
		
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		final String typeName = type.getName();
		
		if( typeName==null ) {
			// top-level simpleType must define a named type
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
			return;	// recover by ignoring this declaration
		}
		
		// memorize this type.
		SimpleTypeExp exp = reader.currentSchema.simpleTypes.get(typeName);
		if(exp==null ) {
			reader.reportError( reader.ERR_REDEFINE_UNDEFINED, typeName );
			return;
			// recover by ignoring this declaration
		}
		
		// overwrite
		exp.setType(type,reader.pool);
		reader.setDeclaredLocationOf(exp);
	}

}
