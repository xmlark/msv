/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex.ng;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.Stack;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.relaxng.datatype.*;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.relaxng.NGTypedStringExp;
import com.sun.msv.grammar.relaxng.datatype.BuiltinDataTypeLibrary;
import com.sun.msv.reader.*;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.reader.trex.TREXBaseReader;
import com.sun.msv.reader.trex.AnyStringState;
import com.sun.msv.reader.trex.RootState;
import com.sun.msv.reader.trex.RefState;
import com.sun.msv.reader.trex.IncludePatternState;
import com.sun.msv.reader.datatype.DataTypeVocabulary;
import com.sun.msv.util.StartTagInfo;

/**
 * reads RELAX NG grammar from SAX2 and constructs abstract grammar model.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGReader extends TREXBaseReader {
	
	/** loads RELAX NG pattern */
	public static TREXGrammar parse( String grammarURL,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		RELAXNGReader reader = new RELAXNGReader(controller,factory);
		reader.parse(grammarURL);
		
		return reader.getResult();
	}
	
	/** loads RELAX NG pattern */
	public static TREXGrammar parse( InputSource grammar,
		SAXParserFactory factory, GrammarReaderController controller )
	{
		RELAXNGReader reader = new RELAXNGReader(controller,factory);
		reader.parse(grammar);
		
		return reader.getResult();
	}

	/** easy-to-use constructor. */
	public RELAXNGReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory) {
		this(controller,parserFactory,new StateFactory(),new ExpressionPool());
	}
	
	/** full constructor */
	public RELAXNGReader(
		GrammarReaderController controller,
		SAXParserFactory parserFactory,
		StateFactory stateFactory,
		ExpressionPool pool ) {
		
		super( controller, parserFactory, pool, stateFactory, new RootState() );
	}
	
	protected String localizeMessage( String propertyName, Object[] args ) {
		String format;
		
		try {
			format = ResourceBundle.getBundle("com.sun.msv.reader.trex.ng.Messages").getString(propertyName);
		} catch( Exception e ) {
			return super.localizeMessage(propertyName,args);
		}
		
	    return MessageFormat.format(format, args );
	}
	
	protected TREXGrammar getGrammar() {
		return grammar;
	}
	
	/**
	 * a map from ReferenceExps to strings that represents the combine method.
	 * (e.g., "choice", "interleave".)
	 * 
	 * This map is used to ensure that a named pattern is combined in the consistent way.
	 */
	protected final Map combineMethodMap = new java.util.HashMap();
	
	/**
	 * a set that contains all ReferenceExps that have their head declarations.
	 * A head declaration is a define element without the combine attribute.
	 * It is an error that two head declarations share the same name.
	 * This set is used to detect this error.
	 */
	protected final Set headRefExps = new java.util.HashSet();

	
	/** Namespace URI of RELAX NG */
	public static final String RELAXNGNamespace = "http://relaxng.org/ns/structure/0.9";

	protected boolean isGrammarElement( StartTagInfo tag ) {
		return RELAXNGNamespace.equals(tag.namespaceURI);
	}
	
	/**
	 * creates various State object, which in turn parses grammar.
	 * parsing behavior can be customized by implementing custom StateFactory.
	 */
	public static class StateFactory extends TREXBaseReader.StateFactory {
		public State text			( State parent, StartTagInfo tag ) { return new AnyStringState(); }
		public State data			( State parent, StartTagInfo tag ) { return new DataState(); }
		public State dataParam		( State parent, StartTagInfo tag ) { return new DataParamState(); }
		public State value			( State parent, StartTagInfo tag ) { return new ValueState(); }
		public State list			( State parent, StartTagInfo tag ) { return new ListState(); }
		public State define			( State parent, StartTagInfo tag ) { return new DefineState(); }
		public State redefine		( State parent, StartTagInfo tag ) { return new RedefineState(); }
		public State includeGrammar	( State parent, StartTagInfo tag ) { return new IncludeMergeState(); }
		public State externalRef	( State parent, StartTagInfo tag ) { return new IncludePatternState(); }
		
		public State ref		( State parent, StartTagInfo tag ) {
			if( tag.containsAttribute("parent") ) {
				// this might be a TREX user...
				// provide an error message.
				parent.reader.reportError( ERR_DISALLOWED_ATTRIBUTE, "ref", "parent");
				return new NullSetState();	// recovery
			}
			return new RefState(false);	// always local reference
		}
		public State parentRef	( State parent, StartTagInfo tag ) {
			return new RefState(true);	// parent reference
		}

		/**
		 * gets DataTypeLibrary object that is specified by the namespace URI.
		 * 
		 * If no vocabulary is known to have that namespace URI, then simply
		 * return null without issuing an error message.
		 */
		public DataTypeLibrary getDataTypeLibrary( String namespaceURI ) {
			if( namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace) )
				return xsdlib;
			if( namespaceURI.equals(XSDVocabulary.XMLSchemaNamespace2) )
				return xsdlib;
			return null;
		}
		private final DataTypeLibrary xsdlib = new com.sun.msv.datatype.DataTypeLibraryImpl();
	}
	protected StateFactory getStateFactory() {
		return (StateFactory)super.sfactory;
	}
	
	public State createExpressionChildState( State parent, StartTagInfo tag ) {
		
		if(tag.localName.equals("text"))		return getStateFactory().text(parent,tag);
		if(tag.localName.equals("data"))		return getStateFactory().data(parent,tag);
		if(tag.localName.equals("value"))		return getStateFactory().value(parent,tag);
		if(tag.localName.equals("list"))		return getStateFactory().list(parent,tag);
		if(tag.localName.equals("externalRef"))	return getStateFactory().externalRef(parent,tag);
		if(tag.localName.equals("parentRef"))	return getStateFactory().parentRef(parent,tag);
		
		return super.createExpressionChildState(parent,tag);
	}
	
	/** obtains a named DataType object referenced by a local name. */
	public DataType resolveDataType( String localName ) {
		
		if(datatypeLib==null)
			// silently recover from an error
			return com.sun.msv.datatype.StringType.theInstance;
		
		try {
			DataType dt = datatypeLib.getType(localName);
			if(dt==null) {
				reportError( ERR_UNDEFINED_DATATYPE, localName );
				return com.sun.msv.datatype.StringType.theInstance;
			}
		
			return dt;
		} catch( DataTypeException dte ) {
			reportError( ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage() );
			return com.sun.msv.datatype.StringType.theInstance;
		}
	}
	
	/**
	 * obtains the DataTypeLibrary that represents the specified namespace URI.
	 * 
	 * If the specified URI is undefined, then this method issues an error to
	 * the user and returns null.
	 */
	public DataTypeLibrary resolveDataTypeLibrary( String uri ) {
		DataTypeLibrary lib = getStateFactory().getDataTypeLibrary(uri);
		if(lib!=null)		return lib;
		
		// issue an error
		reportError( ERR_UNKNOWN_DATATYPE_VOCABULARY, uri );
		return null;
	}

	/**
	 * set of RELAXNGTypedStringExps that are found during parsing.
	 * 
	 * This set is used to detect undefined keys.
	 */
	protected final Set keyKeyrefs = new java.util.HashSet();
	
	public void wrapUp() {
		super.wrapUp();
		
		{// detect undefined keys
			Map keys = new java.util.HashMap();
			
			NGTypedStringExp[] keyKeyrefs = (NGTypedStringExp[])
				this.keyKeyrefs.toArray( new NGTypedStringExp[0] );
			
			// enumerate all keys.
			for( int i=0; i<keyKeyrefs.length; i++ )
				if( keyKeyrefs[i].keyName!=null ) {
					NGTypedStringExp pred = (NGTypedStringExp)keys.get(keyKeyrefs[i].keyName);
					if(pred!=null) {
						// check the type consistency.
						// if baseTypeName==null, then it means there was an error in that declaration.
						// In that case, this error check is meaningless.
						if( pred.baseTypeName!=null && keyKeyrefs[i].baseTypeName!=null
							&&  !pred.baseTypeName.equals(keyKeyrefs[i].baseTypeName) ) {
							reportError( 
								new Locator[]{
									getDeclaredLocationOf(pred),
								getDeclaredLocationOf(keyKeyrefs[i]) },
								ERR_INCONSISTENT_KEY_TYPE, new Object[]{pred.keyName} );
							// suppress excessive error messages by setting
							// baseTypeName fields null.
							pred.baseTypeName=null;
							keyKeyrefs[i].baseTypeName=null;
						}
					}
						
					keys.put( keyKeyrefs[i].keyName, keyKeyrefs[i] );
				}
			
			// then detect undefined keys.
			for( int i=0; i<keyKeyrefs.length; i++ ) {
				if( keyKeyrefs[i].keyrefName!=null ) {
					
					NGTypedStringExp pred = (NGTypedStringExp)keys.get(keyKeyrefs[i].keyrefName);
					
					if( pred==null )
						reportError(
							new Locator[]{getDeclaredLocationOf(keyKeyrefs[i])},
							ERR_UNDEFINED_KEY,
							new Object[]{keyKeyrefs[i].keyrefName} );
					else
					{
						// check the type consistency.
						// if baseTypeName==null, then it means there was an error in that declaration.
						// In that case, this error check is meaningless.
						if( pred.baseTypeName!=null && keyKeyrefs[i].baseTypeName!=null
							&&  !pred.baseTypeName.equals(keyKeyrefs[i].baseTypeName) ) {
							reportError( 
								new Locator[]{
									getDeclaredLocationOf(pred),
								getDeclaredLocationOf(keyKeyrefs[i]) },
								ERR_INCONSISTENT_KEY_TYPE, new Object[]{pred.keyName} );
							// suppress excessive error messages by setting
							// baseTypeName fields null.
							pred.baseTypeName=null;
							keyKeyrefs[i].baseTypeName=null;
						}
					}
				}					
			}
		}
	}

	
	
	
// propagatable attributes
//--------------------------------
	/**
	 * currently active datatype library.
	 * This field is maintained by this class.
	 * In case of an error, this field may be set to null after issuing an error.
	 * So care should be taken not to report the same error again.
	 */
	protected DataTypeLibrary datatypeLib = new BuiltinDataTypeLibrary();
	private final Stack dtLibStack = new Stack();
	
	public void startElement( String a, String b, String c, Attributes d ) throws SAXException {
		// handle 'datatypeLibrary' attribute propagation
		dtLibStack.push(datatypeLib);
		if( d.getIndex("datatypeLibrary")!=-1 )
			datatypeLib = resolveDataTypeLibrary(d.getValue("datatypeLibrary"));
		
		// if nothing specified, datatype library stays the same.
		super.startElement(a,b,c,d);
	}
	public void endElement( String a, String b, String c ) throws SAXException {
		super.endElement(a,b,c);
		datatypeLib = (DataTypeLibrary)dtLibStack.pop();
	}
	
	
	
	
	// error messages
	public static final String ERR_BAD_FACET = // arg:2
		"RELAXNGReader.BadFacet";
	public static final String ERR_INVALID_PARAMETERS = // arg:1
		"RELAXNGReader.InvalidParameters";
	public static final String ERR_BAD_DATA_VALUE = // arg:2
		"RELAXNGReader.BadDataValue";
	public static final String ERR_UNDEFINED_KEY = // arg:1
		"RELAXNGReader.UndefinedKey";
	public static final String ERR_UNDEFINED_DATATYPE_1 = // arg:2
		"RELAXNGReader.UndefinedDataType1";
	public static final String ERR_INCONSISTENT_KEY_TYPE = // arg:1
		"RELAXNGReader.InconsistentKeyType";
	public static final String ERR_INCONSISTENT_COMBINE = // arg:1
		"RELAXNGReader.InconsistentCombine";
	public static final String ERR_REDEFINING_UNDEFINED = // arg:1
		"RELAXNGReader.RedefiningUndefined";
}
