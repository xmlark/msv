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

import org.relaxng.datatype.DataType;
import com.sun.msv.datatype.DataTypeImpl;
import com.sun.msv.reader.State;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.datatype.TypeOwner;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.util.StartTagInfo;

/**
 * State that parses global declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GlobalDeclState extends SimpleState
	implements ExpressionOwner,TypeOwner {
	
	protected State createChildState( StartTagInfo tag ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		
		if(tag.localName.equals("include"))			return reader.sfactory.include(this,tag);
		if(tag.localName.equals("import"))			return reader.sfactory.import_(this,tag);
		if(tag.localName.equals("redefine"))		return reader.sfactory.redefine(this,tag);
		if(tag.localName.equals("simpleType"))		return reader.sfactory.simpleType(this,tag);
		if(tag.localName.equals("complexType"))		return reader.sfactory.complexTypeDecl(this,tag);
		if(tag.localName.equals("group"))			return reader.sfactory.group(this,tag);
		if(tag.localName.equals("attributeGroup"))	return reader.sfactory.attributeGroup(this,tag);
		if(tag.localName.equals("element"))			return reader.sfactory.elementDecl(this,tag);
		if(tag.localName.equals("attribute"))		return reader.sfactory.attribute(this,tag);
		if(tag.localName.equals("notation"))		return reader.sfactory.notation(this,tag);
		
		return null;
	}
	
	// do nothing. declarations register themselves by themselves.
	public void onEndChild( Expression exp ) {}
	
	public void onEndChild( DataType type ) {
		final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
		final DataTypeImpl dti = (DataTypeImpl)type;
		final String typeName = dti.getName();
		
		if( typeName==null ) {
			// top-level simpleType must define a named type
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
			return;	// recover by ignoring this declaration
		}
		
		// memorize this type.
		SimpleTypeExp exp = reader.currentSchema.simpleTypes.getOrCreate(typeName);
		if(exp.getType()!=null ) {
			reader.reportError( reader.ERR_DATATYPE_ALREADY_DEFINED, typeName );
			return;
			// recover by ignoring this declaration
		}
		
		exp.setType(dti,reader.pool);
		reader.setDeclaredLocationOf(exp);
	}
}
