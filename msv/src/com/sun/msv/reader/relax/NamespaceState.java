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

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.relax.RELAXModule;

/**
 * namespace element of RELAX Namespace.
 */
public class NamespaceState extends SimpleState
{
	/** this flag indicates this object expects module element to
	 * appear as the child.
	 */
	private boolean inlineModuleExpected = false;
	
	/** inline module should have this namespace */
	private String namespace;
	
	protected void startSelf()
	{
		super.startSelf();
		
		namespace = startTag.getAttribute("name");
		
		if(namespace==null)
		{
			reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE, "namespace", "name" );
			// recover by keeping name null.
			// by keeping name null, target namespace of the module will be used.
		}
		
		final String language = startTag.getAttribute("language");
		if(language!=null)
		{
			// TODO: support TREX-interoperation
			reader.reportError( RELAXReader.LANGUAGE_NOT_SUPPORTED );
			return;	// recover by ignoring this namespace element.
		}
		
		final String validation = startTag.getAttribute("validation");
		if( "false".equals(validation) )
		{// this module will not be validated.
			
			// create stub module.
			RELAXModule m = getReader().getOrCreateModule(namespace);
			getReader().markAsInitialized(m);	// make it initialized
			getReader().markAsStub(m);
			
			return;	// done.
		}
		
		final String moduleLocation = startTag.getAttribute("moduleLocation");
		if(moduleLocation!=null)
		{
			getReader().switchSource( moduleLocation, new RootModuleState(namespace) );
			// namespace URI specified by name attribute is expected.
			
			return;	// done.
		}
		
		// moduleLocation is not specified, and validation="false" is not specified.
		// module element should be appeared inline.
		inlineModuleExpected = true;
	}
	
	protected State createChildState( StartTagInfo tag )
	{
		if(!inlineModuleExpected)	return null;	// expects nothing
		
		if( tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)
		&&	tag.localName.equals("module") )
		{// found module.
			inlineModuleExpected = false;	// only one module is allowed
			return new ModuleState(namespace);
		}
		
		return null;	// unrecognized
	}
	
	protected void endSelf()
	{
		if(inlineModuleExpected)
			// inline module was not found.
			reader.reportError( RELAXReader.ERR_INLINEMODULE_NOT_FOUND );
			// recover by do nothing
			// effectively ignoring this namespace element.
	}

	/** gets reader in type-safe fashion */
	protected RELAXReader getReader() { return (RELAXReader)reader; }
}
