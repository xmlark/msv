package com.sun.tranquilo.reader;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.SAXParserFactory;
import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.datatype.DataType;

/**
 * base implementation of grammar readers that read grammar from SAX2 stream.
 * 
 * GrammarReader class shouldn't be created directly by the client applications.
 * Instead, they should use parse static method of this class.
 */
public abstract class GrammarReader
	implements IDContextProvider
{
	/** document Locator that is given by XML reader */
	public Locator locator;
	
	/** this object receives errors and warnings */
	protected final GrammarReaderController controller;
	
	/** Reader may create another SAXParser from this factory */
	protected final SAXParserFactory parserFactory;

	/** this object must be used to create a new expression */
	public final ExpressionPool pool;
	
	/** constructor that should be called from parse method. */
	protected GrammarReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		ExpressionPool pool )
	{
		this.controller = controller;
		this.parserFactory = parserFactory;
		if( !parserFactory.isNamespaceAware() )
			throw new IllegalArgumentException("parser factory must be namespace-aware");
		this.pool = pool;
	}
	
	
	
	
	
	/** checks if given element is that of the grammar elements. */
	protected abstract boolean isGrammarElement( StartTagInfo tag );
	
	/** SAX2 parser currently in use. */
	private XMLReader parser;
	public final XMLReader getParser() { return parser; }
	
	/** namespace prefix to URI conversion map.
	 * 
	 * this variable is evacuated to InclusionContext when the parser is switched.
	 */
	private NamespaceSupport namespaceSupport = new NamespaceSupport();
	
	protected void declareNamespacePrefix( String prefix, String uri )
	{ namespaceSupport.declarePrefix(prefix,uri); }
	
	/**
	 * calls processName method of NamespaceSupport
	 */
	protected final String[] splitNamespacePrefix( String qName )
	{
		return namespaceSupport.processName(qName, new String[3], false );
	}
	


	/**
	 * intercepts an expression made by ExpressionState
	 * before it is passed to the parent state.
	 * 
	 * derived class can perform further wrap-up before it is received by the parent.
	 * This mechanism is used by RELAXReader to handle occurs attribute.
	 */
	protected Expression interceptExpression( ExpressionState state, Expression exp )
	{
		return exp;
	}
	
	/**
	 * gets DataType object from type name.
	 * 
	 * If undefined type name is specified, this method is responsible
	 * to report an error, and recovers.
	 * 
	 * @param typeName
	 *		For RELAX, this is unqualified type name. For TREX,
	 *		this is a QName.
	 */
	public abstract DataType resolveDataType( String typeName );


	
// parsing and related services
//======================================================
	
	/**
	 * information that must be sheltered before switching InputSource
	 * (typically by inclusion).
	 * 
	 * It is chained by previousContext field and used as a stack.
	 */
	private class InclusionContext
	{
		final XMLReader			parser;
		final NamespaceSupport	nsSupport;
		final Locator			locator;
		final String			systemId;

		final InclusionContext	previousContext;

		InclusionContext( XMLReader r, NamespaceSupport ns, Locator loc, String sysId, InclusionContext prev )
		{
			this.parser = r;
			this.nsSupport = ns;
			this.locator = loc;
			this.systemId = sysId;
			this.previousContext = prev;
		}
	}
	
	/** current inclusion context */
	private InclusionContext pendingIncludes;
	
	private void pushInclusionContext( )
	{
		pendingIncludes = new InclusionContext(
			parser, namespaceSupport, locator, locator.getSystemId(),
			pendingIncludes );
		
		parser = null;	// XMLReader is created in _parse method.
		namespaceSupport = new NamespaceSupport();
		locator = null;
	}
	
	private void popInclusionContext()
	{
		parser				= pendingIncludes.parser;
		namespaceSupport	= pendingIncludes.nsSupport;
		locator				= pendingIncludes.locator;
		
		pendingIncludes = pendingIncludes.previousContext;
	}
	/** stores all URL of grammars currently being parsed.
	 * 
	 * say "foo.rlx" includes "bar.rlx" and "bar.rlx" include "joe.rlx".
	 * Then this stack hold "foo.rlx","bar.rlx","joe.rlx" when parsing "joe.rlx".
	 */
	private Stack includeStack = new Stack();
		
	/**
	 * switchs InputSource to the specified URL and
	 * parses it by the specified state.
	 * 
	 * derived classes can use this method to realize semantics of 'include'.
	 * 
	 * @param newState
	 *		this state will parse top-level of new XML source.
	 *		this state receives document element by its createChildState method.
	 */
	public void switchSource( String url, State newState )
	{
		// resolve a relative URL to an absolute one
		try
		{
			url = new URL( new URL(locator.getSystemId()), url ).toExternalForm();
		}
		catch( MalformedURLException e ) { }
	
		// let EntityResolver redirects this URL to whatever it likes
		EntityResolver er = parser.getEntityResolver();
		InputSource source = null;
		try
		{
			if(er!=null)
				source = er.resolveEntity( null, url );
			if(source==null)
				source = new InputSource(url);	// default handling
		}
		catch( IOException ie )
		{
			reportError( ie, ERR_IO_EXCEPTION );
			return;
		}
		catch( SAXException se )
		{
			reportError( se, ERR_SAX_EXCEPTION );
			return;	// recover by ignoring this include
		}
		
		url = source.getSystemId();
		
		for( InclusionContext ic = pendingIncludes; ic!=null; ic=ic.previousContext )
			if( ic.systemId.equals(url) )
			{// recursive include.
				// computes what files are recurisve.
				String s="";
				for( int i = includeStack.indexOf(url); i<includeStack.size(); i++ )
					s += includeStack.elementAt(i) + " > ";
				s += url;
				
				reportError( ERR_RECURSIVE_INCLUDE, url );
				return;	// recover by ignoring this include.
			}
		
		pushInclusionContext();
		try
		{
			_parse( source, newState );
		}
		finally
		{
			popInclusionContext();
		}
	}
	
	/**
	 * kicks the parser to start reading a grammar from the specified location.
	 */
	protected final void _parse( Object source, State initialState )
	{
		try
		{
			this.parser = parserFactory.newSAXParser().getXMLReader();
		}
		catch( Exception e )
		{
			reportError( e, ERR_XMLPARSERFACTORY_EXCEPTION, e.getMessage() );
			return;	// cannot recover from this error.
		}
		
		pushState( initialState, null );
		// initialState will receive endDocument event.
		
		try
		{
			if( source instanceof InputSource )		parser.parse((InputSource)source);
			if( source instanceof String )			parser.parse((String)source);
		}
		catch( IOException e )
		{
			reportError( e, ERR_IO_EXCEPTION, e.getMessage() );
		}
		catch( SAXException e )
		{
			reportError( e, ERR_SAX_EXCEPTION, e.getMessage() );
		}
	}
	
	
	

	
	
	
	public class BackwardReferenceMap
	{
		private final Map[] impl = new Map[]
				{new java.util.HashMap(),new java.util.HashMap()};
														 
		public void memorizeLink( Object target, boolean externalLink )
		{
			ArrayList list; int idx = externalLink?1:0;
			if( impl[idx].containsKey(target) )	list = (ArrayList)impl[idx].get(target);
			else
			{// new target.
				list = new ArrayList();
				impl[idx].put(target,list);
			}
			
			list.add(new LocatorImpl(locator));
		}
		
		// TODO: does anyone want to get all of the refer?
		public Locator[] getReferer( Object target, boolean externalLink )
		{
			int idx = externalLink?1:0;
			if( impl[idx].containsKey(target) )
			{
				ArrayList lst = (ArrayList)impl[idx].get(target);
				Locator[] locs = new Locator[lst.size()];
				for( int i=0; i<locs.length; i++ )
					locs[i] = (Locator)lst.get(i);
				return locs;
			}
			else							return null;
		}
	}
	/** keeps track of all backward references to every ReferenceExp.
	 * 
	 * this map should be used to report the source of error
	 * of undefined-something.
	 */
	public final BackwardReferenceMap backwardReference = new BackwardReferenceMap();
	
	

	
	/** this map remembers where ReferenceExps are defined,
	 * and where user defined types are defined.
	 * 
	 * some ReferenceExp in RELAX (e.g., ElementRules) can be defined
	 * in more than one location.
	 * In those cases, all but the first definition are ignored.
	 */
	private final Map declaredLocations = new java.util.HashMap();
	
	public void setDeclaredLocationOf( ReferenceExp exp )
	{
		if( !declaredLocations.containsKey(exp) )
			declaredLocations.put(exp, new LocatorImpl(locator) );
	}
	public void setDeclaredLocationOf( DataType dt )
	{
		declaredLocations.put(dt, new LocatorImpl(locator) );
	}
	public Locator getDeclaredLocationOf( ReferenceExp exp )
	{ return (Locator)declaredLocations.get(exp); }
	public Locator getDeclaredLocationOf( DataType dt )
	{ return (Locator)declaredLocations.get(dt); }

	/**
	 * detects undefined ReferenceExp and reports it as an error.
	 * 
	 * this method is used in the final wrap-up process of parsing.
	 */
	public void detectUndefinedOnes( ReferenceContainer container, String errMsg )
	{
		Iterator itr = container.iterator();
		while( itr.hasNext() )
		{
			// ReferenceExp object is created when it is first referenced or defined.
			// its exp field is supplied when it is defined.
			// therefore, ReferenceExp with its exp field null means
			// it is referenced but not defined.
			
			ReferenceExp ref = (ReferenceExp)itr.next();
			if( ref.exp==null )
			{
				reportError( backwardReference.getReferer(ref,false),
							errMsg, new Object[]{ref.name} );
				ref.exp=Expression.nullSet;
				// recover by assuming a null definition.
			}
		}
	}

	
	
	
	
//
// stack of State objects and related services
//============================================================
	
	/** pushs the current state into the stack and sets new one */
	public void pushState( State newState, StartTagInfo startTag )
	{
		namespaceSupport.pushContext();
		
		// ASSERT : parser.getContentHandler()==newState.parentState
		State parentState = (State)parser.getContentHandler();
		parser.setContentHandler(newState);
		newState.init( this, parentState, startTag );
		
		// this order of statements ensures that
		// getCurrentState can be implemented by using getContentHandler()
	}
	
	/** pops the previous state from the stack */
	public void popState()
	{
		State currentState = (State)parser.getContentHandler();
		
		if( currentState.parentState!=null )
			parser.setContentHandler( currentState.parentState );
		else	// if the root state is poped, supply a dummy.
			parser.setContentHandler( new org.xml.sax.helpers.DefaultHandler() );
		
		namespaceSupport.popContext();
	}

	/** gets current State object. */
	public State getCurrentState() { return (State)parser.getContentHandler(); }
	
	/**
	 * creates an appropriate State object for parsing particle/pattern.
	 */
	public abstract State createExpressionChildState( StartTagInfo tag );


	
// validation context provider
//============================================
	// implementing ValidationContextProvider is neccessary
	// to correctly handle facets.
	
	public String resolveNamespacePrefix( String prefix )
	{
		return namespaceSupport.getURI(prefix);
	}
	
	public boolean isUnparsedEntity( String entityName )
	{
		// we have to allow everything here?
		return true;
	}
	
	// when the user uses enumeration over ID type,
	// this method will be called.
	// To make it work, simply allow everything.
	public boolean onID( String token ) { return true; }
	public void onIDREF( String token ) {}

	
	

// error related services
//========================================================
	
	
	/** this flag is set to true if reportError method is called.
	 * 
	 * Derived classes must check this flag to determine whether the parsing
	 * was successful or not.
	 */
	protected boolean hadError = false;
	
	public final void reportError( String propertyName )
	{ reportError( propertyName, null, null, null ); }
	public final void reportError( String propertyName, Object arg1 )
	{ reportError( propertyName, new Object[]{arg1}, null, null ); }
	public final void reportError( String propertyName, Object arg1, Object arg2 )
	{ reportError( propertyName, new Object[]{arg1,arg2}, null, null ); }
	public final void reportError( String propertyName, Object arg1, Object arg2, Object arg3 )
	{ reportError( propertyName, new Object[]{arg1,arg2,arg3}, null, null ); }
	public final void reportError( Exception nestedException, String propertyName )
	{ reportError( propertyName, null, nestedException, null ); }
	public final void reportError( Exception nestedException, String propertyName, Object arg1 )
	{ reportError( propertyName, new Object[]{arg1}, nestedException, null ); }
	public final void reportError( Locator[] locs, String propertyName, Object[] args )
	{ reportError( propertyName, args, null, locs ); }

	public final void reportWarning( String propertyName, Object arg1 )
	{ reportWarning( propertyName, new Object[]{arg1}, null ); }
	public final void reportWarning( String propertyName, Object arg1, Object arg2 )
	{ reportWarning( propertyName, new Object[]{arg1,arg2}, null ); }

	private Locator[] prepareLocation( Locator[] param )
	{
		// if null is given, use start tag of the current state.
		if( param!=null )	return param;
		if( locator!=null )		return new Locator[]{locator};
		else					return new Locator[0];
	}
	
	/** reports an error to the controller */
	public final void reportError( String propertyName, Object[] args, Exception nestedException, Locator[] errorLocations )
	{
		hadError = true;
		controller.error(
			prepareLocation(errorLocations),
			localizeMessage(propertyName,args), nestedException );
	}
	
	
	/** reports a warning to the controller */
	public final void reportWarning( String propertyName, Object[] args, Locator[] locations )
	{
		controller.warning( prepareLocation(locations),
							localizeMessage(propertyName,args) );
	}
	
	
	/** formats localized message with arguments */
	protected abstract String localizeMessage( String propertyName, Object[] args );

	
	public static final String ERR_MALPLACED_ELEMENT =	// arg:1
		"GrammarReader.MalplacedElement";
	public static final String ERR_IO_EXCEPTION =	// arg:1
		"GrammarReader.IOException";
	public static final String ERR_SAX_EXCEPTION =	// arg:1
		"GrammarReader.SAXException";
	public static final String ERR_XMLPARSERFACTORY_EXCEPTION =	// arg:1
		"GrammarReader.XMLParserFactoryException";
	public static final String ERR_CHARACTERS =		// arg:1
		"GrammarReader.Characters";
	public static final String ERR_MISSING_ATTRIBUTE = // arg:2
		"GrammarReader.MissingAttribute";
	public static final String ERR_MISSING_ATTRIBUTE_2 = // arg:3
		"GrammarReader.MissingAttribute.2";
	public static final String ERR_RECURSIVE_INCLUDE = // arg:1
		"GrammarReader.RecursiveInclude";
	public static final String ERR_UNDEFINED_DATATYPE = // arg:1
		"GrammarReader.UndefinedDataType";
	public static final String ERR_DATATYPE_ALREADY_DEFINED =	// arg:0
		"GrammarReader.DataTypeAlreadyDefined";
	public static final String ERR_MISSING_CHILD_EXPRESSION =	// arg:none
		"GrammarReader.Abstract.MissingChildExpression";
	public static final String ERR_MORE_THAN_ONE_CHILD_EXPRESSION =	// arg:none
		"GrammarReader.Abstract.MoreThanOneChildExpression";
	public static final String ERR_MORE_THAN_ONE_CHILD_TYPE = // arg:none
		"GrammarReader.Abstract.MoreThanOneChildType";
	public static final String ERR_MISSING_CHILD_TYPE = // arg:none
		"GrammarReader.Abstract.MissingChildType";
	public static final String ERR_RUNAWAY_EXPRESSION = // arg:1
		"GrammarReader.Abstract.RunAwayExpression";
	public static final String ERR_MISSING_TOPLEVEL	= // arg:0
		"GrammarReader.Abstract.MissingTopLevel";
	public static final String WRN_MAYBE_WRONG_NAMESPACE = // arg:1
		"GrammarReader.Warning.MaybeWrongNamespace";
	public static final String ERR_BAD_TYPE	=	// arg:1
		"GrammarReader.BadType";
}
