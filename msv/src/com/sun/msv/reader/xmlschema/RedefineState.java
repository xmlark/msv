/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.reader.ChildlessState;
import com.sun.msv.reader.datatype.xsd.SimpleTypeState;

/**
 * used to parse &lt;redefine&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RedefineState extends GlobalDeclState {
	
	// TODO: elementDecl/attributeDecl are prohibited in redefine.
	// TODO: it probably is an error to redefine undefined components.
	
	// TODO: it is NOT an error to fail to load the specified schema (see 4.2.3)
	
	protected void startSelf() {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		super.startSelf();
	
		// parse included grammar first.
		reader.switchSource( this,
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
	
	
	public void onEndChild( XSDatatype type ) {
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
		exp.setType((XSDatatype)type,reader.pool);
		reader.setDeclaredLocationOf(exp);
	}

}
