/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.grammar.trex;

import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.SchemaProvider;
import org.iso_relax.dispatcher.ElementDecl;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import java.util.Iterator;
import java.util.Map;

/**
 * IslandSchema implementation for TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXIslandSchema extends IslandSchemaImpl
{
	/** underlying TREX pattern which this IslandSchema is representing */
	protected final TREXGrammar grammar;
	
	public TREXIslandSchema( TREXGrammar grammar )
	{
		super( new REDocumentDeclaration( grammar ) );

		this.grammar = grammar;
		
		// export all named patterns.
		// TODO: modify to export only those element declarations.
		ReferenceExp[] refs = grammar.namedPatterns.getAll();
		for( int i=0; i<refs.length; i++ )
			elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
	}
	
	public void bind( SchemaProvider provider, ErrorHandler handler )
	{
		Binder binder = new Binder(provider,handler,docDecl.pool);
		bind( grammar.namedPatterns, binder );
	}
}
