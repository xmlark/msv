/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.XMLFilterImpl;
import org.relaxng.datatype.DataType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.util.StartTagInfo;

/**
 * base implementation of grammar readers that read grammar from SAX2 stream.
 * 
 * GrammarReader class can be used as a ContentHandler that parses a grammar.
 * So the typical usage is
 * <PRE><XMP>
 * 
 * GrammarReader reader = new RELAXGrammarReader(...);
 * XMLReader parser = .... // create a new XMLReader here
 * 
 * parser.setContentHandler(reader);
 * parser.parse(whateverYouLike);
 * return reader.grammar;  // obtain parsed grammar.
 * </XMP></PRE>
 * 
 * Or you may want to use several pre-defined static "parse" methods for
 * ease of use.
 * 
 * @seealso com.sun.msv.reader.relax.RELAXReader#parse
 * @seealso com.sun.msv.reader.trex.TREXGrammarReader#parse
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class GrammarReader
	extends XMLFilterImpl
	implements IDContextProvider
{
	/** document Locator that is given by XML reader */
	public Locator locator;
	
	/** this object receives errors and warnings */
	public final GrammarReaderController controller;
	
	/** Reader may create another SAXParser from this factory */
	public final SAXParserFactory parserFactory;

	/** this object must be used to create a new expression */
	public final ExpressionPool pool;
	
	/** constructor that should be called from parse method. */
	protected GrammarReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		ExpressionPool pool,
		State initialState ) {
		
		this.controller = controller;
		this.parserFactory = parserFactory;
		if( !parserFactory.isNamespaceAware() )
			throw new IllegalArgumentException("parser factory must be namespace-aware");
		this.pool = pool;
		pushState( initialState, null, null );
	}
	
	
	
	
	
	/** checks if given element is that of the grammar elements. */
	protected abstract boolean isGrammarElement( StartTagInfo tag );
	
	/**
	 * namespace prefix to URI conversion map.
	 * this variable is evacuated to InclusionContext when the parser is switched.
	 */
	public NamespaceSupport namespaceSupport = new NamespaceSupport();
	
	/**
	 * calls processName method of NamespaceSupport.
	 * Therefore this method returns null if it fails to process QName.
	 */
	public final String[] splitQName( String qName ) {
		return namespaceSupport.processName(qName, new String[3], false );
	}
	


	/**
	 * intercepts an expression made by ExpressionState
	 * before it is passed to the parent state.
	 * 
	 * derived class can perform further wrap-up before it is received by the parent.
	 * This mechanism is used by RELAXReader to handle occurs attribute.
	 */
	protected Expression interceptExpression( ExpressionState state, Expression exp ) {
		return exp;
	}
	
	/**
	 * gets DataType object from type name.
	 * 
	 * If undefined type name is specified, this method is responsible
	 * to report an error, and recover.
	 * 
	 * @param typeName
	 *		For RELAX, this is unqualified type name. For TREX,
	 *		this is a QName.
	 */
	public abstract DataType resolveDataType( String typeName );

	/**
	 * map from type name of Candidate Recommendation to the current type.
	 */
	private static final Map deprecatedTypes = initDeprecatedTypes();
	private static Map initDeprecatedTypes() {
		Map m = new java.util.HashMap();
		m.put("uriReference",		com.sun.msv.datatype.AnyURIType.theInstance );
		m.put("number",				com.sun.msv.datatype.NumberType.theInstance );
		m.put("timeDuration",		com.sun.msv.datatype.DurationType.theInstance );
		m.put("CDATA",				com.sun.msv.datatype.NormalizedStringType.theInstance );
		m.put("year",				com.sun.msv.datatype.GYearType.theInstance );
		m.put("yearMonth",			com.sun.msv.datatype.GYearMonthType.theInstance );
		m.put("month",				com.sun.msv.datatype.GMonthType.theInstance );
		m.put("monthDay",			com.sun.msv.datatype.GMonthDayType.theInstance );
		m.put("day",				com.sun.msv.datatype.GDayType.theInstance );
		return m;
	}
	/**
	 * tries to obtain a DataType object by resolving obsolete names.
	 * this method is useful for backward compatibility purpose.
	 */
	public DataType getBackwardCompatibleType( String typeName ) {
		DataType dt = (DataType)deprecatedTypes.get(typeName);
		if( dt!=null )
			reportWarning( WRN_DEPRECATED_TYPENAME, typeName, dt.displayName() );
		return dt;
	}
	

	
// parsing and related services
//======================================================
	
	/**
	 * information that must be sheltered before switching InputSource
	 * (typically by inclusion).
	 * 
	 * It is chained by previousContext field and used as a stack.
	 */
	private class InclusionContext {
		final NamespaceSupport	nsSupport;
		final Locator			locator;
		final String			systemId;

		final InclusionContext	previousContext;

		InclusionContext( NamespaceSupport ns, Locator loc, String sysId, InclusionContext prev ) {
			this.nsSupport = ns;
			this.locator = loc;
			this.systemId = sysId;
			this.previousContext = prev;
		}
	}
	
	/** current inclusion context */
	private InclusionContext pendingIncludes;
	
	private void pushInclusionContext( ) {
		pendingIncludes = new InclusionContext(
			namespaceSupport, locator, locator.getSystemId(),
			pendingIncludes );
		
		namespaceSupport = new NamespaceSupport();
		locator = null;
	}
	
	private void popInclusionContext() {
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
	 * resolve relative URL to the absolute URL.
	 * 
	 * Also this method allows GrammarReaderController to redirect or
	 * prohibit inclusion.
	 * 
	 * @return
	 *		return null if an error occurs.
	 */
	public final InputSource resolveLocation( String url ) {
		// resolve a relative URL to an absolute one
		try {
			url = new URL( new URL(locator.getSystemId()), url ).toExternalForm();
		} catch( MalformedURLException e ) {}
	
		try {
			InputSource source = controller.resolveEntity(null,url);
			if(source==null)	return new InputSource(url);	// default handling
			else				return source;
		} catch( IOException ie ) {
			reportError( ie, ERR_IO_EXCEPTION );
			return null;
		} catch( SAXException se ) {
			reportError( se, ERR_SAX_EXCEPTION );
			return null;
		}
	}
	
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
	public void switchSource( String url, State newState ) {
		
		final InputSource source = resolveLocation(url);
		if(source==null)		return;	// recover by ignoring this.
		
		url = source.getSystemId();
		
		for( InclusionContext ic = pendingIncludes; ic!=null; ic=ic.previousContext )
			if( ic.systemId.equals(url) ) {
				// recursive include.
				// computes what files are recurisve.
				String s="";
				for( int i = includeStack.indexOf(url); i<includeStack.size(); i++ )
					s += includeStack.elementAt(i) + " > ";
				s += url;
				
				reportError( ERR_RECURSIVE_INCLUDE, url );
				return;	// recover by ignoring this include.
			}
		
		pushInclusionContext();
		State currentState = getCurrentState();
		try {
			// this state will receive endDocument event.
			pushState( newState, null, null );
			_parse( source, currentState.location );
		} finally {
			// restore the current state.
			super.setContentHandler(currentState);
			popInclusionContext();
		}
	}
	
	/** parses a grammar from the specified source */
	public final void parse( String source ) {
		_parse(source,null);
	}
	
	/** parses a grammar from the specified source */
	public final void parse( InputSource source ) {
		_parse(source,null);
	}
	
	/** parses a grammar from the specified source */
	private void _parse( Object source, Locator errorSource ) {
		try {
			XMLReader reader = parserFactory.newSAXParser().getXMLReader();
			reader.setContentHandler(this);
			
			// invoke XMLReader
			if( source instanceof InputSource )		reader.parse((InputSource)source);
			if( source instanceof String )			reader.parse((String)source);
		} catch( ParserConfigurationException e ) {
			reportError( ERR_XMLPARSERFACTORY_EXCEPTION,
				new Object[]{e.getMessage()},
				e, new Locator[]{errorSource} );
		} catch( IOException e ) {
			reportError( ERR_IO_EXCEPTION,
				new Object[]{e.getMessage()},
				e, new Locator[]{errorSource} );
		} catch( SAXException e ) {
			reportError( ERR_SAX_EXCEPTION,
				new Object[]{e.getMessage()},
				e, new Locator[]{errorSource} );
		}
	}
	
	
	

	
	
	
	/**
	 * memorizes what declarations are referenced from where.
	 * 
	 * this information is used to report the source of errors.
	 */
	public class BackwardReferenceMap {
		private final Map impl = new java.util.HashMap();
														 
		/** memorize a reference to an object. */
		public void memorizeLink( Object target ) {
			ArrayList list;
			if( impl.containsKey(target) )	list = (ArrayList)impl.get(target);
			else {
				// new target.
				list = new ArrayList();
				impl.put(target,list);
			}
			
			list.add(new LocatorImpl(locator));
		}
		
		/**
		 * gets all the refer who have a reference to this object.
		 * @return null
		 *		if no one refers it.
		 */
		public Locator[] getReferer( Object target ) {
			// TODO: does anyone want to get all of the refer?
			if( impl.containsKey(target) ) {
				ArrayList lst = (ArrayList)impl.get(target);
				Locator[] locs = new Locator[lst.size()];
				lst.toArray(locs);
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
	 * some ReferenceExp can be defined
	 * in more than one location.
	 * In those cases, the last one is always memorized.
	 * This behavior is essential to correctly implement
	 * TREX constraint that no two &lt;define&gt; is allowed in the same file.
	 */
	private final Map declaredLocations = new java.util.HashMap();
	
	public void setDeclaredLocationOf( Object o ) {
		declaredLocations.put(o, new LocatorImpl(locator) );
	}
	public Locator getDeclaredLocationOf( Object o ) {
		return (Locator)declaredLocations.get(o);
	}

	/**
	 * detects undefined ReferenceExp and reports it as an error.
	 * 
	 * this method is used in the final wrap-up process of parsing.
	 */
	public void detectUndefinedOnes( ReferenceContainer container, String errMsg ) {
		Iterator itr = container.iterator();
		while( itr.hasNext() ) {
			// ReferenceExp object is created when it is first referenced or defined.
			// its exp field is supplied when it is defined.
			// therefore, ReferenceExp with its exp field null means
			// it is referenced but not defined.
			
			ReferenceExp ref = (ReferenceExp)itr.next();
			if( !ref.isDefined() ) {
				reportError( backwardReference.getReferer(ref),
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
	public void pushState( State newState, State parentState, StartTagInfo startTag )
	{
		super.setContentHandler(newState);
		newState.init( this, parentState, startTag );
		
		// this order of statements ensures that
		// getCurrentState can be implemented by using getContentHandler()
	}
	
	/** pops the previous state from the stack */
	public void popState()
	{
		State currentState = getCurrentState();
		
		if( currentState.parentState!=null )
			super.setContentHandler( currentState.parentState );
		else	// if the root state is poped, supply a dummy.
			super.setContentHandler( new org.xml.sax.helpers.DefaultHandler() );
	}

	/** gets current State object. */
	public final State getCurrentState() { return (State)super.getContentHandler(); }

	/**
	 * this method must be implemented by the derived class to create
	 * language-default expresion state.
	 * 
	 * @return null if the start tag is an error.
	 */
	public abstract State createExpressionChildState( State parent, StartTagInfo tag );
	
	
// SAX events interception
//============================================
	public void startElement( String a, String b, String c, Attributes d ) throws SAXException
	{
		namespaceSupport.pushContext();
		super.startElement(a,b,c,d);
	}
	public void endElement( String a, String b, String c ) throws SAXException
	{
		super.endElement(a,b,c);
		namespaceSupport.popContext();
	}
	public void setDocumentLocator( Locator loc )
	{
		super.setDocumentLocator(loc);
		this.locator = loc;
	}
	public void startPrefixMapping(String prefix, String uri ) throws SAXException
	{
		super.startPrefixMapping(prefix,uri);
		namespaceSupport.declarePrefix(prefix,uri);
	}
	public void endPrefixMapping(String prefix) {}


	
// validation context provider
//============================================
	// implementing ValidationContextProvider is neccessary
	// to correctly handle facets.
	
	public String resolveNamespacePrefix( String prefix ) {
		return namespaceSupport.getURI(prefix);
	}
	
	public boolean isUnparsedEntity( String entityName ) {
		// we have to allow everything here?
		return true;
	}
	
	// when the user uses enumeration over ID type,
	// this method will be called.
	// To make it work, simply allow everything.
	public boolean onID( String symbolSpace, Object token ) { return true; }
	public void onIDREF( String symbolSpace, Object token ) {}

	/**
	 * returns a persistent ValidationContextProvider.
	 * this context provider is necessary to late-bind simple types. 
	 */
/*	public IDContextProvider getPersistentVCP() {
		// dump prefix->URI mapping into a map.
		final Map prefixes = new java.util.HashMap();
		Enumeration e = namespaceSupport.getDeclaredPrefixes();
		while( e.hasMoreElements() ) {
			String prefix = (String)e.nextElement();
			prefixes.put( prefix, namespaceSupport.getURI(prefix) );
		}
		
		return new IDContextProvider(){
			public String resolveNamespacePrefix( String prefix ) {
				return (String)prefixes.get(prefix);
			}
			public boolean isUnparsedEntity( String entityName ) {
				return true;
			}
			public boolean onID( String token ) {
				return true;
			}
			public void onIDREF( String token ) {}
		};
	}
*/	
	
	
	
	
// back patching
//===========================================
/*
	several things cannot be done at the moment when the declaration is seen.
	These things have to be postponed until all the necessary information is
	prepared.
	(e.g., generating the expression that matches to <any />).
	
	those jobs are queued here and processed after the parsing is completed.
	Note that there is no mechanism in this class to execute jobs.
	The derived class has to decide its own timing to perform jobs.
*/
	
	public static interface BackPatch {
		/** do back-patching. */
		void patch();
		/** gets State object who has submitted this patch job. */
		State getOwnerState();
	}
	
	protected final Vector backPatchJobs = new Vector();
	public void addBackPatchJob( BackPatch job ) {
		backPatchJobs.add(job);
	}
	
	
	
	

// error related services
//========================================================
	
	
	/** this flag is set to true if reportError method is called.
	 * 
	 * Derived classes must check this flag to determine whether the parsing
	 * was successful or not.
	 */
	public boolean hadError = false;
	
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

	private Locator[] prepareLocation( Locator[] param ) {
		// if null is given, use the current location.
		if( param!=null ) {
			int cnt=0;
			for( int i=0; i<param.length; i++ )
				if( param[i]!=null )	cnt++;
			
			if( param.length==cnt ) return param;
			
			// remove null from the array.
			Locator[] locs = new Locator[cnt];
			cnt=0;
			for( int i=0; i<param.length; i++ )
				if( param[i]!=null )	locs[cnt++] = param[i];
			
			return locs;
		}
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
	public static final String ERR_DISALLOWED_ATTRIBUTE = // arg:2
		"GrammarReader.DisallowedAttribute";
	public static final String ERR_MISSING_ATTRIBUTE = // arg:2
		"GrammarReader.MissingAttribute";
	public static final String ERR_BAD_ATTRIBUTE_VALUE = // arg:2
		"GrammarReader.BadAttributeValue";
	public static final String ERR_MISSING_ATTRIBUTE_2 = // arg:3
		"GrammarReader.MissingAttribute.2";
	public static final String ERR_CONFLICTING_ATTRIBUTES = // arg:2
		"GrammarReader.ConflictingAttribute";
	public static final String ERR_RECURSIVE_INCLUDE = // arg:1
		"GrammarReader.RecursiveInclude";
	public static final String ERR_UNDEFINED_DATATYPE = // arg:1
		"GrammarReader.UndefinedDataType";
	public static final String ERR_DATATYPE_ALREADY_DEFINED =	// arg:1
		"GrammarReader.DataTypeAlreadyDefined";
	public static final String ERR_MISSING_CHILD_EXPRESSION =	// arg:none
		"GrammarReader.Abstract.MissingChildExpression";
	public static final String ERR_MORE_THAN_ONE_CHILD_EXPRESSION =	// arg:none
		"GrammarReader.Abstract.MoreThanOneChildExpression";
	public static final String ERR_MORE_THAN_ONE_CHILD_TYPE = // arg:none
		"GrammarReader.Abstract.MoreThanOneChildType";
	public static final String ERR_MISSING_CHILD_TYPE = // arg:none
		"GrammarReader.Abstract.MissingChildType";
	public static final String ERR_ILLEGAL_FINAL_VALUE =
		"GrammarReader.IllegalFinalValue";
	public static final String ERR_RUNAWAY_EXPRESSION = // arg:1
		"GrammarReader.Abstract.RunAwayExpression";
	public static final String ERR_MISSING_TOPLEVEL	= // arg:0
		"GrammarReader.Abstract.MissingTopLevel";
	public static final String WRN_MAYBE_WRONG_NAMESPACE = // arg:1
		"GrammarReader.Warning.MaybeWrongNamespace";
	public static final String WRN_DEPRECATED_TYPENAME = // arg:2
		"GrammarReader.Warning.DeprecatedTypeName";
	public static final String ERR_BAD_TYPE	=	// arg:1
		"GrammarReader.BadType";
}
