/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.schema;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import com.sun.tranquilo.datatype.DataTypeFactory;

/**
 * parses RELAX module
 * 
 * this class reads RELAX Module by using SAX2. It performs the first part
 * of loading schema.
 */
class ModuleReader extends SchemaReader
{
	ModuleReader()
	{
		this( new Module() );
	}
	
	/** constructor for merging module into the existing module.
	 * 
	 * this constructor is used for processing "include" element.
	 */
	private ModuleReader( Module module )
	{
		super(module,"http://xml.gr.jp/xmlns/relaxCore/" );
	}
	

	public Module getModule() { return (Module)schema; }
	
	protected State createRootState()	{ return new ModuleState(); }
	
	protected class ModuleState extends DocumentElementState
	{
		protected void startSelf()
			throws SchemaParseException
		{
			getModule().moduleVersion = startTag.getOptionalAttribute("moduleVersion",null);
			
			if( !startTag.getRequiredAttribute("relaxCoreVersion").equals("1.0") )
				SchemaParseException.raise( startTag,
					SchemaParseException.ERR_UNRECOGNIZED_CORE_VERSION, null );
			
			getModule().targetNamespace = startTag.getRequiredAttribute("targetNamespace");
		}
		
		/** returns Module object that is parsed */
		public Object getResult() { return schema; }
		
		protected State createChildState( XMLElement startTag ) throws SchemaParseException
		{
			if( startTag.tagName.equals("interface") )	return new InterfaceState();
			if( startTag.tagName.equals("include") )	return new IncludeState();
			return super.createChildState(startTag);
		}
	}
	
	protected class IncludeState extends ModelState
	{
		protected void startSelf() throws SchemaParseException
		{
			String moduleURL = Util.combineUrl(
				getSystemId(),	// calls SchemaReader.getSytemId method
				startTag.getRequiredAttribute("moduleLocation") );
			
			/** how to obtain another parser */
			(new ModuleReader(getModule())).parse( null, moduleURL );
		}
		// this class returns nothing as a result.
		protected Object getResult() throws SchemaParseException
		{ throw new UnsupportedOperationException(); }
	}
	
	protected class InterfaceState extends DivSkipState
	{
		protected State createChildState( XMLElement startTag ) throws SchemaParseException
		{
			if( startTag.tagName.equals("export") )			return new ElementOrClauseExportState();
			if( startTag.tagName.equals("hedgeExport") )	return new HedgeExportState();
			if( startTag.tagName.equals("import") )			return new RoleImportState();
//quick hack
//			if( startTag.tagName.equals("hedgeImport") )	return new HedgeImportState();
			return super.createChildState(startTag);
		}
		// this class returns nothing as a result.
		protected Object getResult() throws SchemaParseException
		{ throw new UnsupportedOperationException(); }
	}
	
	/** State that doesn't support getResult method. */
	protected class ResultlessModelState extends ModelState
	{
		// this class returns nothing as a result.
		protected Object getResult() throws SchemaParseException
		{ throw new UnsupportedOperationException(); }
	}
	
	protected class ElementOrClauseExportState extends ResultlessModelState
	{
		protected void startSelf() throws SchemaParseException
		{
			if( startTag.hasAttribute("label") )
			{// export of ElementRules
				getModule().elementRules.getOrCreate( startTag.getAttribute("label") ).exported = true;
			}
			else
			if( startTag.hasAttribute("role") )
			{// export of ElementRules
				getModule().clauses.getOrCreate( startTag.getAttribute("role") ).exported = true;
			}
			else	// error. label or role must be present.
				SchemaParseException.raise( startTag,
					SchemaParseException.ERR_EXPORT_WITHOUT_LABEL_NOR_ROLE, null );
		}
	}
	
	protected class HedgeExportState extends ResultlessModelState
	{
		protected void startSelf() throws SchemaParseException
		{// set specified hedgeRule as exported
			getModule().hedgeRules.getOrCreate( startTag.getRequiredAttribute("label") ).exported = true;
		}
	}
	
	protected class RoleImportState extends ResultlessModelState
	{
		protected void startSelf() throws SchemaParseException
		{// set specified hedgeRule as exported
			final String roleName = startTag.getRequiredAttribute("role");
			getModule().getParentGrammar().clauses( getModule().targetNamespace, startTag ).getOrCreate(roleName);
		}
	}
}
