/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.reader;

import com.sun.msv.reader.State;
import com.sun.msv.reader.IgnoreState;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.grammar.relax.RELAXModule;
import org.iso_relax.dispatcher.impl.IgnoredSchema;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandSchemaReader;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;
import java.util.Enumeration;

/**
 * parses &lt;namespace&gt; element of RELAX Namespace.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NamespaceState extends State
{
	/** this flag indicates this object expects a module element to
	 * appear as the child.
	 */
	private boolean inlineModuleExpected = false;
	
	/** this flag indicates we are in bail-out mode.
	 * any contents of &lt;namespace&gt; element will be ignored.
	 * 
	 * this flag is used for error recovery.
	 */
	private boolean bailOut = false;
	
	/** inline module should have this primary namespace */
	private String namespace;
	
	/** this object will parse inlined grammar. */
	IslandSchemaReader moduleReader;
	
	protected void startSelf()
	{
		super.startSelf();
		
		namespace = startTag.getAttribute("name");
		
		if(namespace==null) {
			reader.reportError( RELAXNSReader.ERR_MISSING_ATTRIBUTE, "namespace","name" );
			// recover by ignoring this element.
			return;
		}
		
		if(getReader().grammar.moduleMap.containsKey(namespace)) {
			reader.reportError( RELAXNSReader.ERR_NAMESPACE_COLLISION, namespace );
			return;	// recovery by ignoring this element.
		}
		
		
		// TODO: prevent duplicate definitions
		
		final String validation = startTag.getAttribute("validation");
		if( "false".equals(validation) )
		{// this module will not be validated.
			
			// create stub module.
			getReader().grammar.moduleMap.put(
				namespace, new IgnoredSchema() );
			
			return;	// done.
		}

		String language = startTag.getAttribute("language");
		if(language==null)
			language = RELAXNSReader.RELAXCoreNamespace;	// assume RELAX Core if none is specified
		
		moduleReader = getReader().getIslandSchemaReader(language,namespace);
		if( moduleReader==null ) {
			// unrecognized language
			reader.reportError( RELAXNSReader.ERR_UNKNOWN_LANGUAGE, language );
			bailOut = true;
			return;	// ignore this element.
		}
		
		final String moduleLocation = startTag.getAttribute("moduleLocation");
		if(moduleLocation!=null)
		{// parse a module from external resource.
			try {
				InputSource is = reader.resolveLocation(moduleLocation);
				XMLReader parser = reader.parserFactory.newSAXParser().getXMLReader();
				parser.setContentHandler(moduleReader);
				parser.parse(is);
			} catch( javax.xml.parsers.ParserConfigurationException e ) {
				reader.reportError( e, reader.ERR_XMLPARSERFACTORY_EXCEPTION, e.getMessage() );
			} catch( java.io.IOException e ) {
				reader.reportError( e, reader.ERR_IO_EXCEPTION, e.getMessage() );
			} catch( SAXException e ) {
				reader.reportError( e, reader.ERR_SAX_EXCEPTION, e.getMessage() );
			}
			
			getSchema(moduleReader);
			
			return;	// done.
		}
		
		// moduleLocation is not specified, and validation="false" is not specified.
		// module element should be appeared inline.
		inlineModuleExpected = true;
	}
	
	private void getSchema( IslandSchemaReader moduleReader ) {
		IslandSchema schema = moduleReader.getSchema();
		if( schema==null ) {
			// failed to load a module.
			reader.hadError = true;
			schema = new IgnoredSchema();	// use a dummy schema
		}
			
		getReader().grammar.moduleMap.put( namespace, schema );
	}
	
	public void startElement( String namespace, String localName, String qName, Attributes atts )
		throws SAXException
	{
		if(bailOut) {
			// in bail-out mode, ignore all children.
			reader.pushState( new IgnoreState(), this, new StartTagInfo(namespace,localName,qName,atts,reader) );
			return;
		}
		
		if(!inlineModuleExpected) {
			// expecets nothing
			reader.reportError(reader.ERR_MALPLACED_ELEMENT, qName );
			bailOut=true;	// so that we don't issue errors for every child.
			return;
		}
		
		// feed moduleReader.
		//----------------------
		moduleReader.startDocument();	// simulate SAX events
		moduleReader.setDocumentLocator(reader.locator);
		
		Enumeration itr = reader.namespaceSupport.getDeclaredPrefixes();
		while(itr.hasMoreElements())
		{// simulate prefix mappings
			String prefix = (String)itr.nextElement();
			moduleReader.startPrefixMapping( prefix, reader.namespaceSupport.getURI(prefix) );
		}
		
		moduleReader.startElement( namespace, localName, qName, atts );
		// delegate SAX events to module reader
		// after this element is finished, cut in filter will take control back.
		CutInFilter cutInFilter = new CutInFilter();
		cutInFilter.setContentHandler(moduleReader);
		reader.setContentHandler(cutInFilter);
		
		inlineModuleExpected = false;	// we expects one element only.
	}
	
	public void endElement( String namespace, String localName, String qName )
	{
		if(inlineModuleExpected)
			// inline module was not found.
			reader.reportError( RELAXNSReader.ERR_INLINEMODULE_NOT_FOUND );
			// recover by do nothing
			// effectively ignoring this namespace element.
		
		reader.popState();
	}
	public void endDocument() { throw new Error(); }	// this object shall never see endDocument.
	
	private class CutInFilter extends XMLFilterImpl
	{
		private int depth = 0;
		
		public void startElement( String a, String b, String c, Attributes d )
			throws SAXException
		{
			depth++;
			super.startElement(a,b,c,d);
		}
		
		public void endElement( String a, String b, String c ) throws SAXException
		{
			super.endElement(a,b,c);
			if( depth==0 )
			{// parsing should be finished.
				super.endDocument();	// simulate SAX event.
				
				// take control back to this state.
				getReader().setContentHandler( NamespaceState.this );
				
				// get parsed object.
				getSchema(moduleReader);
				return;
			}
			depth--;
		}
	}
	
	/** gets reader in type-safe fashion */
	protected RELAXNSReader getReader() { return (RELAXNSReader)reader; }
}
