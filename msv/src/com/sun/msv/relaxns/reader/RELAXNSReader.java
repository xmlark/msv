/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.reader;

import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.reader.relax.RELAXReader;
import com.sun.tranquilo.reader.*;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.relaxns.grammar.ExternalElementExp;
import com.sun.tranquilo.relaxns.grammar.RELAXGrammar;
import com.sun.tranquilo.relaxns.reader.relax.RELAXCoreIslandSchemaReader;
import com.sun.tranquilo.relaxns.reader.trex.TREXIslandSchemaReader;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.LocatorImpl;
import org.iso_relax.dispatcher.IslandSchemaReader;
import org.iso_relax.dispatcher.SchemaProvider;

/**
 * parses RELAX Namespace XML and constructs a SchemaProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNSReader
	extends RELAXReader
{
	/** namespace URI of RELAX Namespace. */
	public static final String RELAXNamespaceNamespace = "http://www.xml.gr.jp/xmlns/relaxNamespace";
	
	/** loads RELAX grammar */
	public static RELAXGrammar parse( String moduleURL,
		SAXParserFactory factory, GrammarReaderController controller, TREXPatternPool pool )
	{
		RELAXNSReader reader = new RELAXNSReader(controller,factory,pool);
		reader.parse(moduleURL);
		
		return reader.getResult();
	}

	/** loads RELAX grammar */
	public static RELAXGrammar parse( InputSource module,
		SAXParserFactory factory, GrammarReaderController controller, TREXPatternPool pool )
	{
		RELAXNSReader reader = new RELAXNSReader(controller,factory,pool);
		reader.parse(module);
		
		return reader.getResult();
	}
	
	public RELAXNSReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		TREXPatternPool pool )
	{
		super(controller,parserFactory,pool,new RootGrammarState());
		grammar = new RELAXGrammar(pool);
	}
	
	/** RELAX grammar that is currentlt being loaded */
	public final RELAXGrammar grammar;

	/** obtains parsed grammar object only if parsing was successful. */
	public final RELAXGrammar getResult() {
		if(hadError)	return null;
		else			return grammar;
	}
	
	protected SchemaProvider schemaProvider;
	/** obtains parsed grammar object as SchemaProvider
	 * only if parsing was successful. */
	public final SchemaProvider getSchemaProvider() {
		if(hadError)	return null;
		else			return schemaProvider;
	}
	
	
	/**
	 * creates an {@link IslandSchemaReader} that can parse the specified language.
	 * 
	 * This method can be overrided by the derived class to incorporate other
	 * language implementations.
	 * 
	 * @return
	 *		return null if the given language is unrecognized.
	 *		error will be handled by the caller. So this method should not attempt
	 *		to report nor recover from error.
	 */
	public IslandSchemaReader getIslandSchemaReader(
		String language, String expectedTargetNamespace ) {
		
		try {
			if( language.equals( RELAXCoreNamespace ) )	// RELAX Core
				return new com.sun.tranquilo.relaxns.reader.relax.RELAXCoreIslandSchemaReader(
					controller,parserFactory,(TREXPatternPool)pool,expectedTargetNamespace);
			if( language.equals( com.sun.tranquilo.reader.trex.TREXGrammarReader.TREXNamespace ) ) // TREX
				return new com.sun.tranquilo.relaxns.reader.trex.TREXIslandSchemaReader(
					new com.sun.tranquilo.reader.trex.TREXGrammarReader(
						controller,parserFactory,(TREXPatternPool)pool) );

		} catch( javax.xml.parsers.ParserConfigurationException e ) {
			reportError( e, ERR_XMLPARSERFACTORY_EXCEPTION, e.getMessage() );
		} catch( SAXException e ) {
			reportError( e, ERR_SAX_EXCEPTION, e.getMessage() );
		}

		return null;
	}
	
	public DataType resolveDataType( String typeName ) {
		// should never be called.
		// because in top-level content model, datatype reference can never occur.
		throw new Error();
	}
	
	protected boolean isGrammarElement( StartTagInfo tag )
	{
		if( !RELAXNamespaceNamespace.equals(tag.namespaceURI) )
			return false;
		
		// annotation is ignored at this level.
		// by returning false, the entire subtree will be simply ignored.
		if(tag.localName.equals("annotation"))	return false;
		
		return true;
	}
	
	protected Expression resolveElementRef( String namespace, String label ) {
		return resolveRef(namespace,label,"ref");
	}
	
	protected Expression resolveHedgeRef( String namespace, String label ) {
		return resolveRef(namespace,label,"hedgeRef");
	}
	
	private Expression resolveRef( String namespace, String label, String tagName ) {
		if( namespace==null ) {
			reportError( ERR_MISSING_ATTRIBUTE, tagName, "namespace" );
			return Expression.nullSet;
		}
		return new ExternalElementExp( pool, namespace, label, new LocatorImpl(locator) );
	}
	
	
	
	
	public static final String WRN_ILLEGAL_RELAXNAMESPACE_VERSION	// arg:1
		= "RELAXNSReader.Warning.IllegalRelaxNamespaceVersion";
	public static final String ERR_TOPLEVEL_PARTICLE_MUST_BE_RELAX_CORE	// arg:0
		= "RELAXNSReader.TopLevelParticleMustBeRelaxCore";
	public static final String ERR_INLINEMODULE_NOT_FOUND	// arg:0
		= "RELAXNSReader.InlineModuleNotFound";
	public static final String ERR_UNKNOWN_LANGUAGE
		= "RELAXNSReader.UnknownLanguage";	// arg:1
	public static final String ERR_NAMESPACE_COLLISION	// arg:1
		= "RELAXNSReader.NamespaceCollision";
}
