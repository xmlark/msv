package com.sun.tranquilo.relaxns.verifier;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.iso_relax.dispatcher.IslandVerifier;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.impl.AbstractSchemaProviderImpl;
import com.sun.tranquilo.relaxns.grammar.RELAXGrammar;
import com.sun.tranquilo.relaxns.grammar.RuleImpl;
import com.sun.tranquilo.grammar.Grammar;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.trex.TREXPatternPool;
import com.sun.tranquilo.verifier.regexp.trex.TREXDocumentDeclaration;
import java.util.Iterator;

public class SchemaProviderImpl extends AbstractSchemaProviderImpl {
	
	private final RELAXGrammar grammar;
	private final RuleImpl[] topLevel;
	protected final TREXDocumentDeclaration docDecl;
	
	public IslandVerifier createTopLevelVerifier() {
		return new IslandVerifierImpl(
			new RulesAcceptor( docDecl, topLevel ) );
	}
	
	/**
	 * creates SchemaProvider from existing RELAXGrammar.
	 * 
	 * Since bind method is already called by RELAXNSReader,
	 * the application should not call bind method.
	 */
	public SchemaProviderImpl( RELAXGrammar grammar ) {
		this( grammar, new TREXDocumentDeclaration(grammar) ); 
	}
	
	/**
	 * creates SchemaProvider from generic Grammar (including TREX/RELAX Core)
	 */
	public static SchemaProviderImpl fromGrammar( Grammar grammar ) {
		if( grammar instanceof RELAXGrammar )
			return new SchemaProviderImpl( (RELAXGrammar)grammar );
		
		RELAXGrammar g = new RELAXGrammar(grammar.getPool());
		g.topLevel = grammar.getTopLevel();
		
		return new SchemaProviderImpl( g );
	}
	
	public SchemaProviderImpl( RELAXGrammar grammar, TREXDocumentDeclaration docDecl ) {
		this.grammar = grammar;
		this.docDecl = docDecl;
		this.topLevel = new RuleImpl[]{new RuleImpl("##start",grammar.topLevel)};
		
		// add all parsed modules into the provider.
		Iterator itr = grammar.moduleMap.keySet().iterator();
		while( itr.hasNext() ) {
			String namespaceURI = (String)itr.next();
			addSchema(
				namespaceURI, (IslandSchema)grammar.moduleMap.get(namespaceURI) );
		}
	}

	
	/** binds all IslandSchemata. */
	public boolean bind( ErrorHandler handler ) {
		ErrorHandlerFilter filter = new ErrorHandlerFilter(handler);
		
		try {
			Iterator itr = schemata.values().iterator();
			while( itr.hasNext() )
				((IslandSchema)itr.next()).bind( this, filter );
		} catch( SAXException e ) {
			// bind method may throw SAXException.
			return false;
		}
		
		return !filter.hadError;
	}
	
	private static class ErrorHandlerFilter implements ErrorHandler {
		private final ErrorHandler core;
		boolean hadError = false;
		
		ErrorHandlerFilter( ErrorHandler handler ) { this.core=handler; }
		
		public void fatalError( SAXParseException spe ) throws SAXException {
			error(spe);
		}
		
		public void error( SAXParseException spe ) throws SAXException {
			core.error(spe);
			hadError = true;
		}

		public void warning( SAXParseException spe ) throws SAXException {
			core.warning(spe);
		}
	}
}
