/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax;

import java.util.Map;
import org.xml.sax.Locator;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.*;
import com.sun.tranquilo.reader.datatype.TypeOwner;

public class DivInModuleState extends SimpleState implements ExpressionOwner, TypeOwner
{
	/** gets reader in type-safe fashion */
	protected RELAXReader getReader() { return (RELAXReader)reader; }

	protected State createChildState( StartTagInfo tag )
	{
		if(!RELAXReader.RELAXCoreNamespace.equals(tag.namespaceURI) )	return null;
		
		if(tag.localName.equals("div"))			return new DivInModuleState();
		if(tag.localName.equals("hedgeRule"))	return new HedgeRuleState();
		if(tag.localName.equals("tag"))			return new TagState();
		if(tag.localName.equals("attPool"))		return new AttPoolState();
		if(tag.localName.equals("include"))		return new IncludeModuleState();
		if(tag.localName.equals("interface"))	return new InterfaceState();
		if(tag.localName.equals("elementRule"))
		{
			if(tag.containsAttribute("type"))	return new ElementRuleWithTypeState();
			else								return new ElementRuleWithHedgeState();
		}
		if(tag.localName.equals("simpleType"))
		{
			return ((RELAXReader)reader).currentModule.userDefinedTypes.createTopLevelReaderState(tag);
		}
		return null;
	}
	
	// do nothing. declarations register themselves by themselves.
	public void onEndChild( Expression exp ) {}
	
	public void onEndChild( DataType type )
	{// user-defined simple types
		final String typeName = type.getName();
		
		if( typeName==null )
		{// top-level simpleType must define a named type
			reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
			return;	// recover by ignoring this declaration
		}

		// type is memorized by XSDVocabulary. just let it go.
	}
}
