/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.relax.core;

import java.util.Map;
import org.xml.sax.Locator;
//import com.sun.msv.datatype.xsd.XSDatatype;
//import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.*;
import com.sun.msv.reader.datatype.xsd.XSTypeOwner;

/**
 * parses &lt;div&gt; element under &lt;module&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DivInModuleState extends SimpleState implements ExpressionOwner, XSTypeOwner
{
	/** gets reader in type-safe fashion */
	protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }

	protected State createChildState( StartTagInfo tag ) {
		if(tag.localName.equals("div"))			return getReader().getStateFactory().divInModule(this,tag);
		if(tag.localName.equals("hedgeRule"))	return getReader().getStateFactory().hedgeRule(this,tag);
		if(tag.localName.equals("tag"))			return getReader().getStateFactory().tag(this,tag);
		if(tag.localName.equals("attPool"))		return getReader().getStateFactory().attPool(this,tag);
		if(tag.localName.equals("include"))		return getReader().getStateFactory().include(this,tag);
		if(tag.localName.equals("interface"))	return getReader().getStateFactory().interface_(this,tag);
		if(tag.localName.equals("elementRule")) return getReader().getStateFactory().elementRule(this,tag);
		if(tag.localName.equals("simpleType"))	return getReader().getStateFactory().simpleType(this,tag);
		
		return null;
	}
	
	// do nothing. declarations register themselves by themselves.
	public void onEndChild( Expression exp ) {}
	
	public void onEndChild( XSDatatypeExp type ) {
        // user-defined simple types
		
		final String typeName = type.name;
		
		if( typeName==null ) {
            // top-level simpleType must define a named type
			reader.reportError( reader.ERR_MISSING_ATTRIBUTE, "simpleType", "name" );
			return;	// recover by ignoring this declaration
		}
		
		// memorize this type.
        getReader().addUserDefinedType(type);
	}
}
